package com.game.sheepleap;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.util.GLState;

import com.game.sheepleap.SceneManager.SceneType;

public class VictoryScene extends BaseScene implements IOnAreaTouchListener{

	private float timeUsed;
	private int sheepUsed;
	private int totalSheep;
	
	private Sprite nextLevelSprite;
	
	/*public VictoryScene(float time, int sheep, int total){	
		timeUsed = time;
		sheepUsed = sheep;
		totalSheep = total;
		
		super();
	}*/
	
	@Override
	public void createScene() {
		createBackground();
		
		sheepUsed = SceneManager.getInstance().sheep;
		totalSheep = SceneManager.getInstance().maxSheep;
		timeUsed = SceneManager.getInstance().time;
		
		nextLevelSprite = new Sprite(450, 300, resourcesManager.nextLevel_region, vbom);
		
		this.attachChild(new Sprite(140, 20, resourcesManager.winning_region, vbom));
		this.attachChild(new Sprite(140, 150, resourcesManager.time_region, vbom));
		this.attachChild(new Sprite(20, 290, resourcesManager.sheepUsed_region, vbom));
		
		this.attachChild(nextLevelSprite);
		this.registerTouchArea(nextLevelSprite);
		
		this.attachChild(new Text(400, 150, resourcesManager.font, String.format("%.3f seconds", timeUsed), vbom));
		this.attachChild(new Text(450, 290, resourcesManager.font, "" + sheepUsed + "/" + totalSheep, vbom));
		
		this.setOnAreaTouchListener(this);
	}

	@Override
	public void onBackKeyPressed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_VICTORY;
	}

	@Override
	public void disposeScene() {
		// TODO Auto-generated method stub
		
	}
	
	private void createBackground()
	{
	    attachChild(new Sprite(0, 0, resourcesManager.menu_background_region, vbom)
	    {
	        @Override
	        protected void preDraw(GLState pGLState, Camera pCamera) 
	        {
	            super.preDraw(pGLState, pCamera);
	            pGLState.enableDither();
	        }
	    });
	}

	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		if(pSceneTouchEvent.isActionDown()) {
			final Sprite button = (Sprite) pTouchArea;
			
			if(button.getTextureRegion().equals(resourcesManager.nextLevel_region)){
				SceneManager.getInstance().loadGameScene(engine);
			}
			
			return true;
		}
		return false;
	}

}
