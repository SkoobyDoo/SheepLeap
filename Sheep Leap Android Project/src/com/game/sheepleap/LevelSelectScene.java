package com.game.sheepleap;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.ClickDetector;
import org.andengine.input.touch.detector.ClickDetector.IClickDetectorListener;
import org.andengine.opengl.util.GLState;
import org.andengine.util.debug.Debug;

import com.game.sheepleap.SceneManager.SceneType;

public class LevelSelectScene extends BaseScene implements IOnSceneTouchListener, IClickDetectorListener{
	
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

	@Override
	public void createScene() {
		createBackground();
		this.mClickDetector = new ClickDetector(this);
		this.setOnSceneTouchListener(this);
		this.setTouchAreaBindingOnActionDownEnabled(true);
		this.setOnSceneTouchListenerBindingOnActionDownEnabled(true);
		
		if(!this.resourcesManager.menu_music.isPlaying()){
			this.resourcesManager.menu_music.play();
		}
		
		maxLevelReached = 21;
		
		CreateLevelBoxes();
		
	}

	@Override
	public void onBackKeyPressed() {
		SceneManager.getInstance().loadMenuScene(engine);
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_LEVEL_SELECT;
	}

	@Override
	public void disposeScene() {
		if(resourcesManager.menu_music.isPlaying()){
			resourcesManager.menu_music.pause();
		}
	}
	
	private void createBackground()
	{
	    attachChild(new Sprite(0, 0, resourcesManager.levelSelect_background_region, vbom)
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
                		s = new Sprite(boxX, boxY, 50, 50, resourcesManager.light_region, vbom){
           
                			final int numberHolder = levelNumber;
                			
                			@Override
                            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                				levelClicked = numberHolder;
                				return false;
                			}
                		};
                	}
                	else{
                		s = new Sprite(boxX, boxY, 50, 50, resourcesManager.lock_region, vbom){
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
			
			SceneManager.getInstance().levelToLoad = levelClicked;
			SceneManager.getInstance().loadGameScene(engine);
		}	
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		this.mClickDetector.onTouchEvent(pSceneTouchEvent);
		
		return true;
	}
	
}
