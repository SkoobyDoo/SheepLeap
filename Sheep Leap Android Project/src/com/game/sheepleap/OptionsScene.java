package com.game.sheepleap;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.util.GLState;
import org.andengine.util.debug.Debug;
import com.game.sheepleap.SceneManager.SceneType;

public class OptionsScene extends BaseScene implements IOnAreaTouchListener, IOnSceneTouchListener{

	private Sprite checkBox;
	private Sprite checkedBox;
	
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
	public void createScene() {
		createBackground();
		
		checkBox = new Sprite(300, 150, resourcesManager.checkBox_region, vbom);
		
		
		checkedBox = new Sprite(300, 150, resourcesManager.checkedCheckBox_region, vbom);
		
		if(GameSettings.hippieMode){
			this.attachChild(checkedBox);
			this.registerTouchArea(checkedBox);
		}
		else{
			this.attachChild(checkBox);
			this.registerTouchArea(checkBox);
		}
		
		this.setOnAreaTouchListener(this);
		
	}

	@Override
	public void onBackKeyPressed() {
		SceneManager.getInstance().loadMenuScene(engine);
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_OPTIONS;
	}

	@Override
	public void disposeScene() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
			ITouchArea pTouchArea, float pTouchAreaLocalX,
			float pTouchAreaLocalY) {
		if(pSceneTouchEvent.isActionDown()) {
			final Sprite face = (Sprite) pTouchArea;
			
			flip(face);
			Debug.e("touched");
			
			return true;
		}
		
		Debug.e("wat");

		return false;
	}

	private void flip(Sprite face) {
		if(face.getTextureRegion().equals(resourcesManager.checkBox_region)){
			this.detachChild(face);
			this.unregisterTouchArea(checkBox);
			this.attachChild(checkedBox);
			this.registerTouchArea(checkedBox);
			GameSettings.hippieMode = true;
		}
		else if(face.getTextureRegion().equals(resourcesManager.checkedCheckBox_region)){
			this.detachChild(face);
			this.unregisterTouchArea(checkedBox);
			this.attachChild(checkBox);
			this.registerTouchArea(checkBox);
			GameSettings.hippieMode = false;
		}
		
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		// TODO Auto-generated method stub
		return false;
	}

}
