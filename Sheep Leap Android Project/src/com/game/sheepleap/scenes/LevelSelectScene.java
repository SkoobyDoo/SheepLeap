package com.game.sheepleap.scenes;

import java.util.LinkedList;
import java.util.List;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.ClickDetector;
import org.andengine.input.touch.detector.ClickDetector.IClickDetectorListener;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.util.GLState;
import org.andengine.util.debug.Debug;

import com.game.sheepleap.ResourcesManager;
import com.game.sheepleap.ResourcesManager.ITextureBuilder;
import com.game.sheepleap.ResourcesManager.MusicType;
import com.game.sheepleap.SceneManager;
import com.game.sheepleap.SceneManager.SceneType;

public class LevelSelectScene extends BaseScene implements IOnSceneTouchListener, IClickDetectorListener{
	
	ITextureRegion levelSelect_background_region;
	ITextureRegion lock_region;
	ITextureRegion light_region;
	
	private static final int CAMERA_WIDTH = 800;
	private static final int CAMERA_HEIGHT = 480;
	
	private static final int LEVELS = 21;
	private static final int LEVEL_COLUMNS = 7;
	private static final int LEVEL_ROWS = 3;
	private static final int LEVEL_PADDING = 50;
	
	
	private int levelClicked = -1;
	private int maxLevelReached = 21;
	private int levelNumber;

	private ClickDetector mClickDetector;
	
	public static void displayNew() {
		ResourcesManager.getInstance().engine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() 
        {
            @Override
			public void onTimePassed(final TimerHandler pTimerHandler) 
            {
            	ResourcesManager.getInstance().engine.unregisterUpdateHandler(pTimerHandler);
                //ResourcesManager.getInstance().loadMenuTextures();
            	LevelSelectScene scene = new LevelSelectScene();
                SceneManager.getInstance().transitionTo(scene);
            }
        }));
	}

	@Override
	public void createScene() {
		createBackground();
		this.mClickDetector = new ClickDetector(this);
		this.setOnSceneTouchListener(this);
		this.setTouchAreaBindingOnActionDownEnabled(true);
		this.setOnSceneTouchListenerBindingOnActionDownEnabled(true);
		
		resourcesManager.PlayMusic(MusicType.MENU);
		
		maxLevelReached = 21;
		
		CreateLevelBoxes();
		
	}

	@Override
	public void onBackKeyPressed() {
		MainMenuScene.displayNew();
		//SceneManager.getInstance().loadMenuScene(engine);
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_LEVEL_SELECT;
	}

	@Override
	public void disposeScene() {
	}
	
	private void createBackground()
	{
	    attachChild(new Sprite(0, 0, levelSelect_background_region, vbom)
	    {
	        @Override
	        protected void preDraw(GLState pGLState, Camera pCamera) 
	        {
	            super.preDraw(pGLState, pCamera);
	            pGLState.enableDither();
	        }
	    });
	}
	
	private void CreateLevelBoxes() {
		 
        int totalRows = (LEVELS / LEVEL_COLUMNS) + 1;

        int spaceBetweenRows = (CAMERA_HEIGHT / LEVEL_ROWS) - LEVEL_PADDING;
        int spaceBetweenColumns = (CAMERA_WIDTH / LEVEL_COLUMNS) - LEVEL_PADDING;

        levelNumber = 1;
        
        int boxX = 30;
        int boxY = LEVEL_PADDING;
        
        Sprite s;
        
        for (int y = 0; y < totalRows; y++) {
                for (int x = 0; x < LEVEL_COLUMNS; x++) {
                	
                	//Debug.e("" + maxLevelReached);
                	
                	if(levelNumber <= maxLevelReached){
                		s = new Sprite(boxX, boxY, 50, 50, light_region, vbom){
           
                			final int numberHolder = levelNumber;
                			
                			@Override
                            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                				levelClicked = numberHolder;
                				return false;
                			}
                		};
                	}
                	else{
                		s = new Sprite(boxX, boxY, 50, 50, lock_region, vbom){
                			@Override
                            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                				levelClicked = -1;
                				return false;
                			}
                		};
                	}

                   /* Rectangle box = new Rectangle(boxX, boxY, 50, 50, vbom) {
                    		
                    		final int wtf = levelNumber;
                    	
                            @Override
                            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                                    if (wtf > maxLevelReached){
                                            levelClicked = -1;
                                            Debug.e("ln " + wtf + " mLR " + maxLevelReached);
                                    }
                                    else{
                                            levelClicked = wtf;
                                    }
                                    return false;
                            }
                    };

                    if (levelNumber <= maxLevelReached)
                            box.setColor(Color.GREEN);
                    else
                            box.setColor(Color.RED);
                            */

                    this.attachChild(s);
                    if (levelNumber <= maxLevelReached){
	                    if (levelNumber < 10) {
	                            this.attachChild(new Text(boxX + 7, boxY + 5, resourcesManager.font, "" + levelNumber, vbom));
	                    }
	                    else {
	                            this.attachChild(new Text(boxX - 7, boxY + 5, resourcesManager.font, "" + levelNumber, vbom));
	                    }
                    }
                   
                   
                    this.registerTouchArea(s);

                    levelNumber++;
                    boxX += spaceBetweenColumns + LEVEL_PADDING;

                    if (levelNumber > LEVELS)
                            break;
            }

                if (levelNumber > LEVELS)
                        break;

                boxY += spaceBetweenRows + LEVEL_PADDING;
                boxX = 30;
        }
	}

	@Override
	public void onClick(ClickDetector pClickDetector, int pPointerID, float pSceneX, float pSceneY) {
		Debug.e("click click click");
		
		if(levelClicked != -1){
			Debug.e("Clicked Level: " + levelClicked);
			
			GameScene.displayNew(levelClicked);
			//SceneManager.getInstance().levelToLoad = levelClicked;
			//SceneManager.getInstance().loadGameScene(engine);
		}	
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		this.mClickDetector.onTouchEvent(pSceneTouchEvent);
		
		return true;
	}

	@Override
	public ITextureBuilder getTextureBuilder() {
		return new ITextureBuilder() {

			@Override
			public List<BuildableBitmapTextureAtlas> prepareGraphics() {
				BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
				BuildableBitmapTextureAtlas levelSelectTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
				// load textures
				levelSelect_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(levelSelectTextureAtlas, activity, "background_simple.png");
		    	lock_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(levelSelectTextureAtlas, activity, "lock2.png");
		    	light_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(levelSelectTextureAtlas, activity, "lighticon2.png");
		    	
		    	
		    	// prepare atlas list
		    	// most scenes only have one atlas.
		    	List<BuildableBitmapTextureAtlas> graphicsEntry = new LinkedList<BuildableBitmapTextureAtlas>();

		    	
		    	try 
		    	{
		    		levelSelectTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
		    	} 
		    	catch (final TextureAtlasBuilderException e)
		    	{
		    	        Debug.e(e);
		    	}
		    	graphicsEntry.add(levelSelectTextureAtlas);
		    	return graphicsEntry;
			}
			
		};
	}
	
}
