package com.game.sheepleap.entities.ui;

import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;

import com.game.sheepleap.entities.base.ScreenEntity;
import com.game.sheepleap.scenes.BaseScene;

public class CheckBoxEntity extends OptionEntity {
	private ScreenEntity mCheckEntity;

	public static ITextureRegion CHECKED_BOX;
	public static ITextureRegion UNCHECKED_BOX;

	public CheckBoxEntity(float x, float y, boolean defaultState, IOptionUpdater link, BaseScene scene) {
		super(x, y,(defaultState)?1.:0., link, UNCHECKED_BOX, scene);
		mCheckEntity = new ScreenEntity(x, y, CHECKED_BOX, scene);
		if(!defaultState)
			mCheckEntity.hide();
	}
	
	public boolean isChecked() {
		return (getState() == 1.);
	}

	@Override
	public boolean onAreaTouch(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		if (pSceneTouchEvent.isActionDown()) {
			if (getState() == 0.) {
				setState(1.);
				mCheckEntity.show();
			} else {
				setState(0.);
				mCheckEntity.hide();
			}
			return true;
		}
		return false;
	}
	
	public static void updateTextureRegions(ITextureRegion unchecked, ITextureRegion checked) {
		CHECKED_BOX = checked;
		UNCHECKED_BOX = unchecked;
	}
	
	public static void clearTextureRegions() {
		CHECKED_BOX = null;
		UNCHECKED_BOX = null;
	}
}