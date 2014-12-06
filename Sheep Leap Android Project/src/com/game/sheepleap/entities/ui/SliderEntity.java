package com.game.sheepleap.entities.ui;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;

import com.game.sheepleap.entities.base.ScreenEntity;
import com.game.sheepleap.scenes.BaseScene;

public class SliderEntity extends OptionEntity {
	ScreenEntity dead;
	ScreenEntity normal;
	ScreenEntity scared;

	public static ITextureRegion SLIDER;
	public static ITextureRegion DEAD;
	public static ITextureRegion NORMAL;
	public static ITextureRegion SCARED;

	public SliderEntity(int x, int y, double defaultState, IOptionUpdater link, BaseScene scene) {
		super(x, y, defaultState, link, SLIDER, scene);
		dead = new ScreenEntity(x - 32f, mSprite.getY() - 32f, DEAD, scene);
		normal = new ScreenEntity(x - 32f, mSprite.getY() - 32f, NORMAL, scene);
		scared = new ScreenEntity(x - 32f, mSprite.getY() - 32f, SCARED, scene);
		
		float barTouchThicknessFactor = 3f;
		
		Rectangle touchArea = new Rectangle(x, y, mSprite.getWidth(), mSprite.getHeight()*barTouchThicknessFactor, scene.getVbom()) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				return onAreaTouch(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		//touchArea.setColor(1f, 0f, 1f); // debug color magenta for touch area
		mSprite.attachChild(touchArea);
		touchArea.setAlpha(0f);
		touchArea.setPosition(0, -barTouchThicknessFactor/2* mSprite.getHeight());
		mScene.registerTouchArea(touchArea);

		updateSlider();
	}
	
	public void updateSlider() {
		if (dead.isVisible()) dead.hide();
		if (normal.isVisible()) normal.hide();
		if (scared.isVisible()) scared.hide();
		dead.getSprite().setPosition((float) (mSprite.getX() + mSprite.getWidth() * getState() - 32f), mSprite.getY() - 32);
		normal.getSprite().setPosition((float) (mSprite.getX() + mSprite.getWidth() * getState() - 32f), mSprite.getY() - 32);
		scared.getSprite().setPosition((float) (mSprite.getX() + mSprite.getWidth() * getState() - 32f), mSprite.getY() - 32);
		if (getState() < .05)
			dead.show();
		else if (getState() > .85)
			scared.show();
		else normal.show();
	}

	public boolean onAreaTouch(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		setState(pTouchAreaLocalX / mSprite.getWidth());
		updateSlider();
		return true;
	}
	
	public static void updateTextureRegions(ITextureRegion slider, ITextureRegion dead, ITextureRegion normal, ITextureRegion scared) {
		SLIDER = slider;
		DEAD = dead;
		NORMAL = normal;
		SCARED = scared;
	}
	
	public static void clearTextureRegions() {
		SLIDER = null;
		DEAD = null;
		NORMAL = null;
		SCARED = null;
	}

}