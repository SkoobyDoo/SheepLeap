package com.game.sheepleap.entities.base;

import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;

import com.game.sheepleap.scenes.BaseScene;

public class ScreenEntity {
	
	protected Sprite mSprite;
	public Sprite getSprite() {
		return mSprite;
	}

	public BaseScene getScene() {
		return mScene;
	}

	protected BaseScene mScene;
	
	public boolean isVisible() {
		return mSprite.isVisible();
	}

	public ScreenEntity(float x, float y, ITextureRegion tex, BaseScene scene) {
		mSprite = new Sprite(x, y, tex, scene.getVbom()) {
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, 
					final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				return onAreaTouch(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		mScene = scene;
		mScene.attachChild(mSprite);
	}
	
	public void hide() {
		mSprite.setVisible(false);
	}
	
	public void show() {
		mSprite.setVisible(true);
	}
	
	public boolean onAreaTouch(final TouchEvent pSceneTouchEvent, 
			final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		return false;
	}
}
