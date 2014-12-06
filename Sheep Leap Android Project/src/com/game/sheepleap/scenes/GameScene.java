package com.game.sheepleap.scenes;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
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
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePack;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePackLoader;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePackTextureRegionLibrary;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.exception.TexturePackParseException;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.ClickDetector;
import org.andengine.input.touch.detector.PinchZoomDetector;
import org.andengine.input.touch.detector.PinchZoomDetector.IPinchZoomDetectorListener;
import org.andengine.input.touch.detector.SurfaceScrollDetector;
import org.andengine.input.touch.detector.ClickDetector.IClickDetectorListener;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.SAXUtils;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;
import org.andengine.util.level.IEntityLoader;
import org.andengine.util.level.LevelLoader;
import org.andengine.util.level.constants.LevelConstants;
import org.xml.sax.Attributes;

import android.util.Pair;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.game.sheepleap.ResourcesManager;
import com.game.sheepleap.ResourcesManager.ITextureBuilder;
import com.game.sheepleap.ResourcesManager.MusicType;
import com.game.sheepleap.SceneManager;
import com.game.sheepleap.SceneManager.SceneType;
import com.game.sheepleap.entities.BirdEntity;
import com.game.sheepleap.entities.BlackHoleEntity;
import com.game.sheepleap.entities.GoalEntity;
import com.game.sheepleap.entities.PlatformEntity;
import com.game.sheepleap.entities.ThunderCloudEntity;
import com.game.sheepleap.entities.PlatformEntity.PlatformSize;
import com.game.sheepleap.entities.PlatformEntity.PlatformType;
import com.game.sheepleap.entities.base.PhysicsEntity;
import com.game.sheepleap.entities.base.IThoughtfulEntity;
import com.game.sheepleap.entities.SheepEntity;
import com.game.sheepleap.entities.StartEntity;
import com.game.sheepleap.entities.WindEntity;

public class GameScene extends BaseScene implements IOnSceneTouchListener, IScrollDetectorListener, IClickDetectorListener,
		IPinchZoomDetectorListener {

	// TODO these are analagous to atlases and I need to get them over to the
	// resourcemanager somehow...
	TexturePack texturePack;
	TexturePack texturePack2;

	private static final boolean WIN_DEBUG = false; // bool to hide debug text for win condition
	private static final boolean DEBUG_RENDERING = true; // enable/disable debug bounds for physics bodies
	
	private static final float MIN_ZOOM = .5f;
	private static final float MAX_ZOOM = 2f;
	private static int BORDER_WIDTH = 300;

	private Collection<IThoughtfulEntity> thoughtfulEntities = new LinkedList<IThoughtfulEntity>();
	public Collection<IThoughtfulEntity> toRemove = new LinkedList<IThoughtfulEntity>();

	public void registerThoughtfulEntity(IThoughtfulEntity t) {
		thoughtfulEntities.add(t);
	}

	public void deregisterThoughtfulEntity(IThoughtfulEntity t) {
		thoughtfulEntities.remove(t);
	}

	private HUD gameHUD;
	private Text sheepText;
	private int sheepLeft;
	private PhysicsWorld physicsWorld;

	private int level;

	private static GameScene singleton;

	public static GameScene get() {
		return singleton;
	}

	public PhysicsWorld getPhysicsWorld() {
		return physicsWorld;
	}

	public static void displayNew(int level) {
		final GameScene gs = new GameScene(level);

		ResourcesManager.getInstance().engine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() {
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler) {
				ResourcesManager.getInstance().engine.unregisterUpdateHandler(pTimerHandler);
				// ResourcesManager.getInstance().loadGameResources();
				// ResourcesManager.getInstance().loadSceneResources(gs);
				SceneManager.getInstance().transitionTo(gs);
			}
		}));
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
	private Collection<Pair<PhysicsEntity, PhysicsEntity>> newContacts = new LinkedList<Pair<PhysicsEntity, PhysicsEntity>>();
	private Collection<Pair<PhysicsEntity, PhysicsEntity>> finishedContacts = new LinkedList<Pair<PhysicsEntity, PhysicsEntity>>();

	public GameScene(int level) {
		super();
		ResourcesManager.getInstance().loadGameSounds();
		singleton = this;
		this.level = level;
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
					grass_region.setTextureWidth(pixelWidth);
					grass_region.setTextureHeight(182);
					final Sprite grass = new Sprite(-xBound, -150, grass_region, vbom);
					grass.setZIndex(1000);
					GameScene.this.attachChild(grass);
					resourcesManager.PlayMusic(MusicType.GROUND);
				}

				if (stageType.equals("sky")) {
					resourcesManager.PlayMusic(MusicType.GROUND);
				}

				if (stageType.equals("space")) {
					resourcesManager.PlayMusic(MusicType.SPACE);

					physicsWorld.setGravity(new Vector2(0, 0));

					final Sprite space = new Sprite(-xBound, -pixelHeight, pixelWidth, pixelHeight, space_region, vbom);

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
						levelObject = GoalEntity.createGoal(x, y, GoalEntity.GoalType.CLOUD, GameScene.this);
					} else if (goalType.equals("asteroid")) {
						levelObject = GoalEntity.createGoal(x, y, GoalEntity.GoalType.ASTEROID, GameScene.this);
					} else if (goalType.equals("pillow")) {
						levelObject = GoalEntity.createGoal(x, y, GoalEntity.GoalType.PILLOW, GameScene.this);
					} else {
						throw new IllegalArgumentException();
					}
				} else if (type.equals("bird")) {
					final int fromX = SAXUtils.getIntAttributeOrThrow(pAttributes, "fromx");
					final int toX = SAXUtils.getIntAttributeOrThrow(pAttributes, "tox");
					final boolean goingLeft = SAXUtils.getBooleanAttributeOrThrow(pAttributes, "left");
					levelObject = new BirdEntity(x, y, fromX, toX, goingLeft, GameScene.this);
					// levelObject = new ThunderCloudEntity(x, y);
				} else if (type.equals("wind")) {
					final int width = SAXUtils.getIntAttributeOrThrow(pAttributes, "width");
					final int height = SAXUtils.getIntAttributeOrThrow(pAttributes, "height");
					final float force = SAXUtils.getFloatAttributeOrThrow(pAttributes, "force");
					levelObject = new WindEntity(x, y, width, height, force, GameScene.this);
				}

				else if (type.equals("tree")) {
					final int treeType = SAXUtils.getIntAttributeOrThrow(pAttributes, "treetype");
					final boolean sticky = SAXUtils.getBooleanAttributeOrThrow(pAttributes, "sticky");

					switch (treeType) {
					case 0:
						levelObject = PlatformEntity.createPlatform(x, y, PlatformType.TREE, PlatformSize.SMALL, sticky, GameScene.this);
						break;
					case 1:
						levelObject = PlatformEntity.createPlatform(x, y, PlatformType.TREE, PlatformSize.MEDIUM, sticky, GameScene.this);
						break;
					case 2:
						levelObject = PlatformEntity.createPlatform(x, y, PlatformType.TREE, PlatformSize.LARGE, sticky, GameScene.this);
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
						levelObject = PlatformEntity.createPlatform(x, y, PlatformType.CLOUD, PlatformSize.SMALL, sticky, GameScene.this);
						break;
					case 1:
						levelObject = PlatformEntity.createPlatform(x, y, PlatformType.CLOUD, PlatformSize.MEDIUM, sticky, GameScene.this);
						break;
					case 2:
						levelObject = PlatformEntity.createPlatform(x, y, PlatformType.CLOUD, PlatformSize.LARGE, sticky, GameScene.this);
						break;
					default:
						throw new IllegalArgumentException();
					}
				}

				else if (type.equals("thunder")) {
					levelObject = new ThunderCloudEntity(x, y, GameScene.this);
				}

				else if (type.equals("asteroid")) {
					final int treeType = SAXUtils.getIntAttributeOrThrow(pAttributes, "treetype");
					final boolean sticky = SAXUtils.getBooleanAttributeOrThrow(pAttributes, "sticky");

					switch (treeType) {
					case 0:
						levelObject = PlatformEntity
								.createPlatform(x, y, PlatformType.ASTEROID, PlatformSize.SMALL, sticky, GameScene.this);
						break;
					case 1:
						levelObject = PlatformEntity.createPlatform(x, y, PlatformType.ASTEROID, PlatformSize.MEDIUM, sticky,
								GameScene.this);
						break;
					case 2:
						levelObject = PlatformEntity
								.createPlatform(x, y, PlatformType.ASTEROID, PlatformSize.LARGE, sticky, GameScene.this);
						break;
					default:
						throw new IllegalArgumentException();
					}
				}

				else if (type.equals("blackhole")) {
					final int radius = SAXUtils.getIntAttributeOrThrow(pAttributes, "radius");
					final int range = SAXUtils.getIntAttributeOrThrow(pAttributes, "range");
					final int force = SAXUtils.getIntAttributeOrThrow(pAttributes, "force");

					levelObject = new BlackHoleEntity(x, y, radius, range, force, GameScene.this);
				} else if (type.equals("start")) {
					levelObject = new StartEntity(x, y, GameScene.this);
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
		this.setOnSceneTouchListener(this);

		this.mScrollDetector = new SurfaceScrollDetector(this);
		this.mClickDetector = new ClickDetector(125, this);
		this.mPinchZoomDetector = new PinchZoomDetector(this);

		createHUD();
		createPhysics();

		this.registerUpdateHandler(this.physicsWorld);
		this.registerUpdateHandler(getCollisionUpdateHandler());

		if (DEBUG_RENDERING) {
			DebugRenderer dr = new DebugRenderer(physicsWorld, vbom);
			dr.setDrawBodies(true);
			dr.setDrawJoints(true);
			this.attachChild(dr);
			dr.setZIndex(1337);
		}
	}

	@Override
	public void attachChild(IEntity pEntity) {
		super.attachChild(pEntity);
		this.sortChildren(false);
	}

	@Override
	public void onBackKeyPressed() {
		LevelSelectScene.displayNew();
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_GAME;
	}

	@Override
	public void disposeScene() {
		camera.setHUD(null);
		camera.setBoundsEnabled(false);
		// TODO: no more magic number centering wtf is this
		camera.setCenter(400, 240);

		camera.setZoomFactor(1);
	}

	@Override
	public void onClick(ClickDetector pClickDetector, int pPointerID, float pSceneX, float pSceneY) {
		// TODO: remove magic numbers
		this.addSheep(pSceneX - 50, pSceneY - 49);
	}

	private void addSheep(float pSceneX, float pSceneY) {
		if (this.sheepLeft > 0) {
			setSheepLeft(sheepLeft - 1);

			new SheepEntity(pSceneX, pSceneY, this);
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
					Pair<PhysicsEntity, PhysicsEntity> p = new Pair<PhysicsEntity, PhysicsEntity>(e1, e2);
					newContacts.add(p);
				}

			}

			@Override
			public void endContact(Contact contact) {
				final Fixture x1 = contact.getFixtureA();
				final Fixture x2 = contact.getFixtureB();
				if ((x1.getBody().getUserData() instanceof PhysicsEntity) && (x2.getBody().getUserData() instanceof PhysicsEntity)) {
					final PhysicsEntity e1 = (PhysicsEntity) x1.getBody().getUserData();
					final PhysicsEntity e2 = (PhysicsEntity) x2.getBody().getUserData();
					Pair<PhysicsEntity, PhysicsEntity> p = new Pair<PhysicsEntity, PhysicsEntity>(e1, e2);
					finishedContacts.add(p);
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
			@SuppressWarnings("unused")
			@Override
			public void onUpdate(float pSecondsElapsed) {
				if (!isPaused) {
					for (Pair<PhysicsEntity, PhysicsEntity> c : newContacts) {
						PhysicsEntity x1 = c.first, x2 = c.second;
						x1.collidedWith(x2);
						x2.collidedWith(x1);
					}
					newContacts.clear();
					for (Pair<PhysicsEntity, PhysicsEntity> c : finishedContacts) {
						PhysicsEntity x1 = c.first, x2 = c.second;
						x1.doneColldingWith(x2);
						x2.doneColldingWith(x1);
					}
					finishedContacts.clear();
					for (IThoughtfulEntity e : thoughtfulEntities) {
						e.think(pSecondsElapsed);
					}
					for (IThoughtfulEntity t : toRemove) {
						thoughtfulEntities.remove(t);
						if (t instanceof SheepEntity) ((SheepEntity) t).kill();
					}
					toRemove.clear();

					if (isStartToGoalLink()) { // if there is any connection
												// from
												// start to goal
						// TODO make a countdown timer/set it visible if it
						// already exists maybe? probably just create here
						// destroy below
						if (secondsToWin == 5f && WIN_DEBUG) Debug.e("Win Link Established");
						secondsToWin -= pSecondsElapsed;
					} else {
						if (secondsToWin != 5 && WIN_DEBUG) Debug.e("Resetting Wintimer...");
						secondsToWin = 5f;// TODO Hide/remove aforementioned
											// timer
					}
					if (secondsToWin <= 0) {
						VictoryScene.displayNew(timeElapsed, totalSheep - sheepLeft, totalSheep, level + 1);
						// SceneManager.getInstance().loadVictoryScene(engine,
						// totalSheep - sheepLeft, totalSheep, timeElapsed);
						// VictoryScene v = (VictoryScene)
						// SceneManager.getInstance().getScene(SceneManager.SceneType.SCENE_VICTORY);
						// v.updateStatistics(timeElapsed, totalSheep -
						// sheepLeft, totalSheep);
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

	public ITiledTextureRegion sheep_region;
	public ITiledTextureRegion bird_region;
	public ITiledTextureRegion cloudLarge_region;
	public ITiledTextureRegion cloudMedium_region;
	public ITiledTextureRegion cloudSmall_region;
	public ITiledTextureRegion cloudThunder_region;
	public ITiledTextureRegion start_region;
	public ITiledTextureRegion treeLarge_region;
	public ITiledTextureRegion treeMedium_region;
	public ITiledTextureRegion treeSmall_region;

	public ITiledTextureRegion sheepHead;
	public ITiledTextureRegion sheepTorso;
	public ITiledTextureRegion sheepLeftLeg;
	public ITiledTextureRegion sheepRightLeg;

	public ITiledTextureRegion flower0;
	public ITiledTextureRegion flower1;
	public ITiledTextureRegion flower2;
	public ITiledTextureRegion flower3;
	public ITiledTextureRegion flower4;
	public ITiledTextureRegion flower5;

	public ITiledTextureRegion goalCloud_region;
	public ITiledTextureRegion goalAsteroid_region;
	public ITiledTextureRegion goalPillow_region;
	public ITiledTextureRegion asteroidLarge_region;
	public ITiledTextureRegion asteroidMedium_region;
	public ITiledTextureRegion asteroidSmall_region;
	public ITiledTextureRegion blackHole_region;

	public ITiledTextureRegion wind_region;
	ITextureRegion grass_region;
	ITextureRegion space_region;

	@Override
	public ITextureBuilder getTextureBuilder() {
		return new ITextureBuilder() {

			@Override
			public List<BuildableBitmapTextureAtlas> prepareGraphics() {
				BuildableBitmapTextureAtlas gameTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024,
						TextureOptions.BILINEAR);
				TexturePackTextureRegionLibrary texturePackLibrary = null;
				TexturePackTextureRegionLibrary texturePackLibrary2 = null;
				// load textures
				try {
					TexturePack texturePack = new TexturePackLoader(activity.getTextureManager(), "gfx/").loadFromAsset(
							activity.getAssets(), "entities.xml");
					texturePack.loadTexture();

					TexturePack texturePack2 = new TexturePackLoader(activity.getTextureManager(), "gfx/").loadFromAsset(
							activity.getAssets(), "entities2.xml");
					texturePack2.loadTexture();

					texturePackLibrary = texturePack.getTexturePackTextureRegionLibrary();
					texturePackLibrary2 = texturePack2.getTexturePackTextureRegionLibrary();
				} catch (final TexturePackParseException e) {
					Debug.e(e);
				}

				sheep_region = texturePackLibrary.get(EntityStictchIdOne.SHEEPLAYERED4_ID, 4, 1);
				bird_region = texturePackLibrary.get(EntityStictchIdOne.BIRDFULL_ID, 3, 1);
				goalCloud_region = texturePackLibrary.get(EntityStictchIdOne.GOALCLOUDFULL_ID, 3, 1);
				cloudLarge_region = texturePackLibrary.get(EntityStictchIdOne.CLOUD_LARGE_ID, 1, 1);
				cloudMedium_region = texturePackLibrary.get(EntityStictchIdOne.CLOUD_MEDIUM_ID, 1, 1);
				cloudSmall_region = texturePackLibrary.get(EntityStictchIdOne.CLOUD_SMALL_ID, 1, 1);
				cloudThunder_region = texturePackLibrary.get(EntityStictchIdOne.RAINCLOUD2_ID, 1, 1);
				start_region = texturePackLibrary.get(EntityStictchIdOne.STARTLOC_ID, 1, 1);
				treeLarge_region = texturePackLibrary.get(EntityStictchIdOne.TREE_LARGE_NEST_ID, 1, 1);
				treeMedium_region = texturePackLibrary.get(EntityStictchIdOne.TREE_MEDIUM_ID, 1, 1);
				treeSmall_region = texturePackLibrary.get(EntityStictchIdOne.TREE_SMALL_ID, 1, 1);

				sheepHead = texturePackLibrary.get(EntityStictchIdOne.SHEEP_DEADHEAD2_ID, 1, 1);
				sheepTorso = texturePackLibrary.get(EntityStictchIdOne.SHEEP_BODY2_ID, 1, 1);
				sheepLeftLeg = texturePackLibrary.get(EntityStictchIdOne.SHEEP_LEFT2_ID, 1, 1);
				sheepRightLeg = texturePackLibrary.get(EntityStictchIdOne.SHEEP_RIGHT2_ID, 1, 1);

				flower0 = texturePackLibrary.get(EntityStictchIdOne.FLOWER_0_ID, 1, 1);
				flower1 = texturePackLibrary.get(EntityStictchIdOne.FLOWER_1_ID, 1, 1);
				flower2 = texturePackLibrary.get(EntityStictchIdOne.FLOWER_2_ID, 1, 1);
				flower3 = texturePackLibrary.get(EntityStictchIdOne.FLOWER_3_ID, 1, 1);
				flower4 = texturePackLibrary.get(EntityStictchIdOne.FLOWER_4_ID, 1, 1);
				flower5 = texturePackLibrary.get(EntityStictchIdOne.FLOWER_5_ID, 1, 1);

				goalAsteroid_region = texturePackLibrary2.get(EntityStitchIdTwo.GOALASTEROIDFULL_ID, 3, 1);
				goalPillow_region = texturePackLibrary2.get(EntityStitchIdTwo.PILLOW_ID, 1, 1);
				asteroidLarge_region = texturePackLibrary2.get(EntityStitchIdTwo.ASTEROID_LARGE_ID, 1, 1);
				asteroidMedium_region = texturePackLibrary2.get(EntityStitchIdTwo.ASTEROID_MEDIUM_ID, 1, 1);
				asteroidSmall_region = texturePackLibrary2.get(EntityStitchIdTwo.ASTEROID_SMALL_ID, 1, 1);
				blackHole_region = texturePackLibrary2.get(EntityStitchIdTwo.BLACKHOLE_ID, 1, 1);

				BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");

				BuildableBitmapTextureAtlas gameGrassTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024,
						256, TextureOptions.REPEATING_NEAREST_PREMULTIPLYALPHA);
				// grass_region =
				// BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameGrassTextureAtlas,
				// activity, "grass.png", 0, 0);

				BuildableBitmapTextureAtlas gameWindTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024,
						1024, TextureOptions.NEAREST_PREMULTIPLYALPHA);
				// wind_region =
				// BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameWindTextureAtlas,
				// activity, "windStacked.png", 0, 0, 1, 5);

				BuildableBitmapTextureAtlas gameSpaceTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024,
						768, TextureOptions.NEAREST_PREMULTIPLYALPHA);
				// space_region =
				// BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameSpaceTextureAtlas,
				// activity, "space.png", 0, 0);

				space_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameSpaceTextureAtlas, activity, "space.png");
				wind_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameWindTextureAtlas, activity,
						"windStacked.png", 1, 5);
				grass_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameGrassTextureAtlas, activity, "grass.png");

				try {
					gameGrassTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
					gameWindTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
					gameSpaceTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
					gameGrassTextureAtlas.load();
					gameWindTextureAtlas.load();
					gameSpaceTextureAtlas.load();
				} catch (Exception e) {
					Debug.e(e);
				}

				// prepare atlas list
				// most scenes only have one atlas.
				List<BuildableBitmapTextureAtlas> graphicsEntry = new LinkedList<BuildableBitmapTextureAtlas>();
				graphicsEntry.add(gameGrassTextureAtlas);
				graphicsEntry.add(gameWindTextureAtlas);
				graphicsEntry.add(gameSpaceTextureAtlas);

				graphicsEntry.add(gameTextureAtlas);
				return graphicsEntry;
			}

		};
	}

	private interface EntityStictchIdOne {
		public static final int SHEEPLAYERED4_ID = 0;
		public static final int SHEEP_LEFT2_ID = 1;
		public static final int SHEEP_RIGHT2_ID = 2;
		public static final int BIRDFULL_ID = 3;
		public static final int CLOUD_LARGE_ID = 4;
		public static final int CLOUD_MEDIUM_ID = 5;
		public static final int CLOUD_SMALL_ID = 6;
		public static final int FLOWER_0_ID = 7;
		public static final int FLOWER_1_ID = 8;
		public static final int FLOWER_2_ID = 9;
		public static final int FLOWER_3_ID = 10;
		public static final int FLOWER_4_ID = 11;
		public static final int FLOWER_5_ID = 12;
		public static final int GOALCLOUDFULL_ID = 13;
		public static final int RAINCLOUD2_ID = 14;
		public static final int SHEEP_BODY2_ID = 15;
		public static final int SHEEP_DEADHEAD2_ID = 16;
		public static final int STARTLOC_ID = 17;
		public static final int TREE_LARGE_NEST_ID = 18;
		public static final int TREE_MEDIUM_ID = 19;
		public static final int TREE_SMALL_ID = 20;
	}

	private interface EntityStitchIdTwo {
		public static final int ASTEROID_LARGE_ID = 0;
		public static final int ASTEROID_MEDIUM_ID = 1;
		public static final int ASTEROID_SMALL_ID = 2;
		public static final int BLACKHOLE_ID = 3;
		public static final int GOALASTEROIDFULL_ID = 4;
		public static final int PILLOW_ID = 5;
	}

}
