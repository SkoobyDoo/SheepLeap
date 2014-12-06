package com.game.sheepleap.entities.ui;

import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;

import com.game.sheepleap.entities.base.ScreenEntity;
import com.game.sheepleap.scenes.BaseScene;

public abstract class OptionEntity extends ScreenEntity {
	private double state;
	IOptionUpdater link;
	
	public OptionEntity(float x, float y, double defaultState, IOptionUpdater newLink,
			ITextureRegion tex, BaseScene scene) {
		super(x, y, tex, scene);
		state = defaultState;
		link = newLink;
		mScene.registerTouchArea(mSprite);
	}
	
	public double getState() {
		return state;
	}
	
	protected void setState(double newState) {
		state = newState;
		if(link != null)
			link.update(newState);
	}
	
	public abstract boolean onAreaTouch(final TouchEvent pSceneTouchEvent, 
			final float pTouchAreaLocalX, final float pTouchAreaLocalY);
	
}
