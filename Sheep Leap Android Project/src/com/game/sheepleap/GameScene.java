package com.game.sheepleap;

import java.io.IOException;
import java.util.ArrayList;
//import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.extension.debugdraw.DebugRenderer;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.ClickDetector;
import org.andengine.input.touch.detector.PinchZoomDetector;
import org.andengine.input.touch.detector.PinchZoomDetector.IPinchZoomDetectorListener;
import org.andengine.input.touch.detector.SurfaceScrollDetector;
import org.andengine.input.touch.detector.ClickDetector.IClickDetectorListener;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.SAXUtils;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;
import org.andengine.util.level.IEntityLoader;
import org.andengine.util.level.LevelLoader;
import org.andengine.util.level.constants.LevelConstants;
import org.xml.sax.Attributes;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.game.sheepleap.SceneManager.SceneType;
import com.game.sheepleap.entities.BirdEntity;
import com.game.sheepleap.entities.BlackHoleEntity;
import com.game.sheepleap.entities.GoalEntity;
import com.game.sheepleap.entities.PhysicsEntity;
import com.game.sheepleap.entities.PlatformEntity;
import com.game.sheepleap.entities.ThunderCloudEntity;
import com.game.sheepleap.entities.PlatformEntity.PlatformSize;
import com.game.sheepleap.entities.PlatformEntity.PlatformType;
import com.game.sheepleap.entities.SheepEntity;
import com.game.sheepleap.entities.StartEntity;
import com.game.sheepleap.entities.ThoughtfulEntity;
import com.game.sheepleap.entities.WindEntity;

public class GameScene extends BaseScene implements IOnSceneTouchListener, IScrollDetectorListener, IClickDetectorListener,
		IPinchZoomDetectorListener {

	private boolean WIN_DEBUG = true; // bool to hide debug text for win
										// condition
	private static final float MIN_ZOOM = .5f;
	private static final float MAX_ZOOM = 2f;
	private static int BORDER_WIDTH = 300;

	private Collection<ThoughtfulEntity> thoughtfulEntities = new LinkedList<ThoughtfulEntity>();
	public Collection<ThoughtfulEntity> toRemove = new LinkedList<ThoughtfulEntity>();

	public void registerThoughtfulEntity(ThoughtfulEntity t) {
		thoughtfulEntities.add(t);
	}

	public void deregisterThoughtfulEntity(ThoughtfulEntity t) {
		thoughtfulEntities.remove(t);
	}

	private HUD gameHUD;
	private Text sheepText;
	private int sheepLeft;
	private PhysicsWorld physicsWorld;

	private static GameScene singleton;

	public static GameScene get() {
		return singleton;
	}

	public PhysicsWorld getPhysicsWorld() {
		return physicsWorld;
	}

	private boolean isPaused;

	private SurfaceScrollDetector mScrollDetector;
	private ClickDetector mClickDetector;
	private PinchZoomDetector mPinchZoomDetector;
	private float mPinchZoomStartedCameraZoomFactor;

	// all sheep colliding with start at any given moment
	Set<SheepEntity> startSheep = new HashSet<SheepEntity>();

	// all sheep colliding with goal at any given moment
	Set<SheepEntity> goalSheep = new HashSet<SheepEntity>();

	public Set<SheepEntity> getStartSheep() {
		return startSheep;
	}

	public Set<SheepEntity> getGoalSheep() {
		return goalSheep;
	}

	private float timeElapsed = 0f;
	private float secondsToWin = 5f;
	private int totalSheep;

	private Collection<ArrayList<PhysicsEntity>> newContacts = new LinkedList<ArrayList<PhysicsEntity>>();
	private Collection<ArrayList<PhysicsEntity>> finishedContacts = new LinkedList<ArrayList<PhysicsEntity>>();

	public GameScene(int level) {
		super();
		singleton = this;
		this.loadLevel(level);
	}

	public void setSheepLeft(int i) {
		sheepLeft = i;
		sheepText.setText("Sheep Remaining: " + this.sheepLeft);
	}

	private void createPhysics() {
		physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, 10), false);
		physicsWorld.setContactListener(createContactListener());
	}

	private void createHUD() {
		gameHUD = new HUD();

		sheepText = new Text(20, 420, resourcesManager.font, "Sheep Remaining: 0123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
		// sheepText.setPosition(0, 0);
		sheepText.setText("Sheep Remaining: 0");
		gameHUD.attachChild(sheepText);

		camera.setHUD(gameHUD);
	}

	/*
	 * private void createBackground() { setBackground(new
	 * Background(Color.BLUE)); }
	 */

	private void loadLevel(int levelID) {
		final LevelLoader levelLoader = new LevelLoader();

		isPaused = true;

		// final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0,
		// 0.01f, 0.5f);

		levelLoader.registerEntityLoader(LevelConstants.TAG_LEVEL, new IEntityLoader() {
			@Override
			public IEntity onLoadEntity(final String pEntityName, final Attributes pAttributes) {
				final int width = SAXUtils.getIntAttributeOrThrow(pAttributes, LevelConstants.TAG_LEVEL_ATTRIBUTE_WIDTH); // 800
				final int height = SAXUtils.getIntAttributeOrThrow(pAttributes, LevelConstants.TAG_LEVEL_ATTRIBUTE_HEIGHT); // 1200

				final String stageType = SAXUtils.getAttributeOrThrow(pAttributes, "stagetype");

				final int pixelWidth = width * 50;
				final int pixelHeight = height * 50;
				final float xBound = pixelWidth / 2.0f;

				final int maxSheep = SAXUtils.getIntAttributeOrThrow(pAttributes, "maxsheep");

				totalSheep = maxSheep;
				GameScene.this.setSheepLeft(maxSheep);
				// GameScene.this.setBackground(new Background(.231f,
				// .5255f, .9216f));
				GameScene.this.setBackground(new Background((SAXUtils.getIntAttributeOrThrow(pAttributes, "red") / 255.0f), (SAXUtils
						.getIntAttributeOrThrow(pAttributes, "green") / 255.0f),
						(SAXUtils.getIntAttributeOrThrow(pAttributes, "blue") / 255.0f)));
				GameScene.this.setBackgroundEnabled(true);

				camera.setBounds(-xBound - BORDER_WIDTH, -pixelHeight - BORDER_WIDTH, xBound + BORDER_WIDTH, 0);
				camera.setBoundsEnabled(true);

				camera.setCenter(0, -pixelHeight / 2);

				final Rectangle ground = new Rectangle(-xBound - BORDER_WIDTH, -100, pixelWidth + BORDER_WIDTH * 2, BORDER_WIDTH, vbom);
				final Rectangle roof = new Rectangle(-xBound, -pixelHeight - BORDER_WIDTH, pixelWidth, BORDER_WIDTH, vbom);
				final Rectangle left = new Rectangle(-xBound - BORDER_WIDTH, -pixelHeight - BORDER_WIDTH, BORDER_WIDTH, pixelHeight
						+ BORDER_WIDTH, vbom);
				final Rectangle right = new Rectangle(xBound, -pixelHeight - BORDER_WIDTH, BORDER_WIDTH, pixelHeight + BORDER_WIDTH, vbom);

				ground.setColor(Color.BLACK);
				roof.setColor(Color.BLACK);
				left.setColor(Color.BLACK);
				right.setColor(Color.BLACK);
				
				GameScene.this.attachChild(ground);

				if (stageType.equals("grass")) {
					resourcesManager.grass_region.setTextureWidth(pixelWidth);
					resourcesManager.grass_region.setTextureHeight(182);
					final Sprite grass = new Sprite(-xBound, -150, resourcesManager.grass_region, vbom);
					GameScene.this.attachChild(grass);
					
					if(!resourcesManager.ground_music.isPlaying())
						resourcesManager.ground_music.play();
				}
				
				if(stageType.equals("sky")){
					if(!resourcesManager.ground_music.isPlaying())
						resourcesManager.ground_music.play();
				}
				
				if (stageType.equals("space")) {
					if(!resourcesManager.space_music.isPlaying())
						resourcesManager.space_music.play();
					
					physicsWorld.setGravity(new Vector2(0, 0));
					
					final Sprite space = new Sprite(-xBound, -pixelHeight, pixelWidth, pixelHeight, resourcesManager.space_region, vbom);
					
					GameScene.this.attachChild(space);
				}

				final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(1, 0, 1.0f);
				Body groundBody = PhysicsFactory.createBoxBody(GameScene.this.physicsWorld, ground, BodyType.StaticBody, wallFixtureDef);
				Body roofBody = PhysicsFactory.createBoxBody(GameScene.this.physicsWorld, roof, BodyType.StaticBody, wallFixtureDef);
				Body leftBody = PhysicsFactory.createBoxBody(GameScene.this.physicsWorld, left, BodyType.StaticBody, wallFixtureDef);
				Body rightBody = PhysicsFactory.createBoxBody(GameScene.this.physicsWorld, right, BodyType.StaticBody, wallFixtureDef);

				groundBody.setUserData("ground");
				roofBody.setUserData("wall");
				leftBody.setUserData("wall");
				rightBody.setUserData("wall");

				// GameScene.this.attachChild(ground);

				GameScene.this.attachChild(roof);
				GameScene.this.attachChild(left);
				GameScene.this.attachChild(right);

				return GameScene.this;
			}
		});

		levelLoader.registerEntityLoader("entity", new IEntityLoader() {
			@Override
			public IEntity onLoadEntity(final String pEntityName, final Attributes pAttributes) {
				final int x = SAXUtils.getIntAttributeOrThrow(pAttributes, "x");
				final int y = SAXUtils.getIntAttributeOrThrow(pAttributes, "y");
				final String type = SAXUtils.getAttributeOrThrow(pAttributes, "type");

				PhysicsEntity levelObject = null;

				if (type.equals("goal")) {
					final String goalType = SAXUtils.getAttributeOrThrow(pAttributes, "goaltype");
					if (goalType.equals("cloud")) {
						levelObject = GoalEntity.createGoal(x, y, GoalEntity.GoalType.CLOUD);
					} else if (goalType.equals("asteroid")) {
						levelObject = GoalEntity.createGoal(x, y, GoalEntity.GoalType.ASTEROID);
					} else if (goalType.equals("pillow")) {
						levelObject = GoalEntity.createGoal(x, y, GoalEntity.GoalType.PILLOW);
					} else {
						throw new IllegalArgumentException();
					}
				} else if (type.equals("bird")) {
					final int fromX = SAXUtils.getIntAttributeOrThrow(pAttributes, "fromx");
					final int toX = SAXUtils.getIntAttributeOrThrow(pAttributes, "tox");
					final boolean goingLeft = SAXUtils.getBooleanAttributeOrThrow(pAttributes, "left");
					levelObject = new BirdEntity(x, y, fromX, toX, goingLeft);
					// levelObject = new ThunderCloudEntity(x, y);
				} else if (type.equals("wind")) {
					final int width = SAXUtils.getIntAttributeOrThrow(pAttributes, "width");
					final int height = SAXUtils.getIntAttributeOrThrow(pAttributes, "height");
					final float force = SAXUtils.getFloatAttributeOrThrow(pAttributes, "force");
					levelObject = new WindEntity(x, y, width, height, force);
				}

				else if (type.equals("tree")) {
					final int treeType = SAXUtils.getIntAttributeOrThrow(pAttributes, "treetype");
					final boolean sticky = SAXUtils.getBooleanAttributeOrThrow(pAttributes, "sticky");

					switch (treeType) {
					case 0:
						levelObject = PlatformEntity.createPlatform(x, y, PlatformType.TREE, PlatformSize.SMALL, sticky);
						break;
					case 1:
						levelObject = PlatformEntity.createPlatform(x, y, PlatformType.TREE, PlatformSize.MEDIUM, sticky);
						break;
					case 2:
						levelObject = PlatformEntity.createPlatform(x, y, PlatformType.TREE, PlatformSize.LARGE, sticky);
						break;
					default:
						throw new IllegalArgumentException();
					}
				}

				else if (type.equals("cloud")) {
					final int treeType = SAXUtils.getIntAttributeOrThrow(pAttributes, "treetype");
					final boolean sticky = SAXUtils.getBooleanAttributeOrThrow(pAttributes, "sticky");

					switch (treeType) {
					case 0:
						levelObject = PlatformEntity.createPlatform(x, y, PlatformType.CLOUD, PlatformSize.SMALL, sticky);
						break;
					case 1:
						levelObject = PlatformEntity.createPlatform(x, y, PlatformType.CLOUD, PlatformSize.MEDIUM, sticky);
						break;
					case 2:
						levelObject = PlatformEntity.createPlatform(x, y, PlatformType.CLOUD, PlatformSize.LARGE, sticky);
						break;
					default:
						throw new IllegalArgumentException();
					}
				}

				else if (type.equals("thunder")) {
					levelObject = new ThunderCloudEntity(x, y);
				}
				
				else if (type.equals("asteroid")) {
					final int treeType = SAXUtils.getIntAttributeOrThrow(pAttributes, "treetype");
					final boolean sticky = SAXUtils.getBooleanAttributeOrThrow(pAttributes, "sticky");

					switch (treeType) {
					case 0:
						levelObject = PlatformEntity.createPlatform(x, y, PlatformType.ASTEROID, PlatformSize.SMALL, sticky);
						break;
					case 1:
						levelObject = PlatformEntity.createPlatform(x, y, PlatformType.ASTEROID, PlatformSize.MEDIUM, sticky);
						break;
					case 2:
						levelObject = PlatformEntity.createPlatform(x, y, PlatformType.ASTEROID, PlatformSize.LARGE, sticky);
						break;
					default:
						throw new IllegalArgumentException();
					}
				}
				
				else if (type.equals("blackhole")){
					final int radius = SAXUtils.getIntAttributeOrThrow(pAttributes, "radius");
					final int range = SAXUtils.getIntAttributeOrThrow(pAttributes, "range");
					final int force = SAXUtils.getIntAttributeOrThrow(pAttributes, "force");
					
					
					levelObject = new BlackHoleEntity(x, y, radius, range, force);
				}
				else if (type.equals("start")) {
					levelObject = new StartEntity(x, y);
				}

				else {
					throw new IllegalArgumentException();
				}

				return levelObject.getSprite();
			}
		});

		try {
			levelLoader.loadLevelFromAsset(activity.getAssets(), "worlds/" + levelID + ".wldx");
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.isPaused = false;
	}

	@Override
	public void createScene() {
		// createBackground();
		this.setOnSceneTouchListener(this);

		this.mScrollDetector = new SurfaceScrollDetector(this);
		this.mClickDetector = new ClickDetector(125, this);
		this.mPinchZoomDetector = new PinchZoomDetector(this);

		// this.birdList = new LinkedList<OldBirdEntity>();

		createHUD();
		createPhysics();
		// loadLevel(2);

		this.registerUpdateHandler(this.physicsWorld);
		this.registerUpdateHandler(getCollisionUpdateHandler());

		this.attachChild(new DebugRenderer(physicsWorld, vbom));
	}

	@Override
	public void onBackKeyPressed() {
		SceneManager.getInstance().loadLevelSelectScene(engine);
		// SheepEntity.number = 0;
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_GAME;
	}

	@Override
	public void disposeScene() {
		camera.setHUD(null);
		camera.setBoundsEnabled(false);
		camera.setCenter(400, 240);

		camera.setZoomFactor(1);
		
		if(resourcesManager.ground_music.isPlaying()){
			resourcesManager.ground_music.pause();
		}
		if(resourcesManager.space_music.isPlaying()){
			resourcesManager.space_music.pause();
		}

		// TODO remove game objects, dispose scene

	}

	@Override
	public void onClick(ClickDetector pClickDetector, int pPointerID, float pSceneX, float pSceneY) {
		this.addSheep(pSceneX - 50, pSceneY - 49);
	}

	private void addSheep(float pSceneX, float pSceneY) {
		if (this.sheepLeft > 0) {
			setSheepLeft(sheepLeft - 1);

			new SheepEntity(pSceneX, pSceneY);
		}
	}

	private ContactListener createContactListener() {
		ContactListener contactListener = new ContactListener() {
			@Override
			public void beginContact(Contact contact) {
				final Fixture x1 = contact.getFixtureA();
				final Fixture x2 = contact.getFixtureB();
				if ((x1.getBody().getUserData() instanceof PhysicsEntity) && (x2.getBody().getUserData() instanceof PhysicsEntity)) {
					final PhysicsEntity e1 = (PhysicsEntity) x1.getBody().getUserData();
					final PhysicsEntity e2 = (PhysicsEntity) x2.getBody().getUserData();
					ArrayList<PhysicsEntity> thisPair = new ArrayList<PhysicsEntity>(2);
					thisPair.add(e1);
					thisPair.add(e2);
					newContacts.add(thisPair);
				}

			}

			@Override
			public void endContact(Contact contact) {
				final Fixture x1 = contact.getFixtureA();
				final Fixture x2 = contact.getFixtureB();
				if ((x1.getBody().getUserData() instanceof PhysicsEntity) && (x2.getBody().getUserData() instanceof PhysicsEntity)) {
					final PhysicsEntity e1 = (PhysicsEntity) x1.getBody().getUserData();
					final PhysicsEntity e2 = (PhysicsEntity) x2.getBody().getUserData();
					ArrayList<PhysicsEntity> thisPair = new ArrayList<PhysicsEntity>(2);
					thisPair.add(e1);
					thisPair.add(e2);
					finishedContacts.add(thisPair);
				}
			}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {

			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {

			}

		};
		return contactListener;
	}

	public IUpdateHandler getCollisionUpdateHandler() {
		return new IUpdateHandler() {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				if (!isPaused) {
					for (ArrayList<PhysicsEntity> c : newContacts) {
						PhysicsEntity x1 = c.get(0), x2 = c.get(1);
						x1.collidedWith(x2);
						x2.collidedWith(x1);
					}
					newContacts.clear();
					for (ArrayList<PhysicsEntity> c : finishedContacts) {
						PhysicsEntity x1 = c.get(0), x2 = c.get(1);
						x1.doneColldingWith(x2);
						x2.doneColldingWith(x1);
					}
					finishedContacts.clear();
					for (ThoughtfulEntity e : thoughtfulEntities) {
						e.think(pSecondsElapsed);
					}
					for (ThoughtfulEntity t : toRemove) {
						thoughtfulEntities.remove(t);
						if (t instanceof SheepEntity) ((SheepEntity) t).kill();
					}
					toRemove.clear();

					if (isStartToGoalLink()) { // if there is any connection
												// from
												// start to goal
						// TODO make a countdown timer/set it visible if it
						// already
						// exists maybe?
						// probably just create here destroy below
						if (secondsToWin == 5f && WIN_DEBUG) Debug.e("Win Link Established");
						secondsToWin -= pSecondsElapsed;
					} else {
						if (secondsToWin != 5 && WIN_DEBUG) Debug.e("Resetting Wintimer...");
						secondsToWin = 5f;// TODO Hide/remove aforementioned
											// timer
					}
					if (secondsToWin <= 0) {
						SceneManager.getInstance().loadVictoryScene(engine, totalSheep - sheepLeft, totalSheep, timeElapsed);
						// TODO go to the "You Won!" screen, and pass in
						// timeElapsed
						// for completion time and sheep remaining
						if (secondsToWin >= -0.1 && WIN_DEBUG) Debug.e("You won!");
					}

					timeElapsed += pSecondsElapsed;
				}
			}

			private boolean isStartToGoalLink() {
				for (SheepEntity s : startSheep)
					for (SheepEntity g : goalSheep)
						if (s.isConnectedTo(g)) return true;
				return false;
			}

			@Override
			public void reset() {

			}
		};

	}

	@Override
	public void onScrollStarted(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
		final float zoomFactor = this.camera.getZoomFactor();
		this.camera.offsetCenter(-pDistanceX / zoomFactor, -pDistanceY / zoomFactor);
	}

	@Override
	public void onScroll(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
		final float zoomFactor = this.camera.getZoomFactor();
		this.camera.offsetCenter(-pDistanceX / zoomFactor, -pDistanceY / zoomFactor);
	}

	@Override
	public void onScrollFinished(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
		final float zoomFactor = this.camera.getZoomFactor();
		this.camera.offsetCenter(-pDistanceX / zoomFactor, -pDistanceY / zoomFactor);
	}

	@Override
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		this.mPinchZoomDetector.onTouchEvent(pSceneTouchEvent);
		if (this.physicsWorld != null) {
			if (this.mPinchZoomDetector.isZooming()) {
				this.mScrollDetector.setEnabled(false);
				this.mClickDetector.setEnabled(false);
			} else {
				if (pSceneTouchEvent.isActionDown()) {
					this.mScrollDetector.setEnabled(true);
					this.mClickDetector.setEnabled(true);
				}
				this.mScrollDetector.onTouchEvent(pSceneTouchEvent);
				this.mClickDetector.onTouchEvent(pSceneTouchEvent);
			}

			return true;
		}
		return false;
	}

	@Override
	public void onPinchZoomStarted(final PinchZoomDetector pPinchZoomDetector, final TouchEvent pTouchEvent) {
		this.mPinchZoomStartedCameraZoomFactor = this.camera.getZoomFactor();
	}

	@Override
	public void onPinchZoom(final PinchZoomDetector pPinchZoomDetector, final TouchEvent pTouchEvent, final float pZoomFactor) {
		if (this.mPinchZoomStartedCameraZoomFactor * pZoomFactor <= MAX_ZOOM
				&& this.mPinchZoomStartedCameraZoomFactor * pZoomFactor > MIN_ZOOM) {
			this.camera.setZoomFactor(this.mPinchZoomStartedCameraZoomFactor * pZoomFactor);
		}
	}

	@Override
	public void onPinchZoomFinished(final PinchZoomDetector pPinchZoomDetector, final TouchEvent pTouchEvent, final float pZoomFactor) {
		if (this.mPinchZoomStartedCameraZoomFactor * pZoomFactor <= MAX_ZOOM
				&& this.mPinchZoomStartedCameraZoomFactor * pZoomFactor > MIN_ZOOM) {
			this.camera.setZoomFactor(this.mPinchZoomStartedCameraZoomFactor * pZoomFactor);
		}
	}

}
