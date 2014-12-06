package com.game.sheepleap.scenes;

import java.util.LinkedList;
import java.util.List;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.util.color.Color;

import com.game.sheepleap.ResourcesManager;
import com.game.sheepleap.ResourcesManager.ITextureBuilder;
import com.game.sheepleap.SceneManager;
import com.game.sheepleap.SceneManager.SceneType;

public class LoadingScene extends BaseScene {

	public static void displayNew() {
		ResourcesManager.getInstance().engine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() {
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler) {
				ResourcesManager.getInstance().engine.unregisterUpdateHandler(pTimerHandler);
				SceneManager.getInstance().transitionTo(new LoadingScene());
			}
		}));
	}

	@Override
	public void createScene() {
		ResourcesManager.getInstance().loadFont();
		ResourcesManager.getInstance().loadMusic();
		
		
		setBackground(new Background(Color.BLACK));
		attachChild(new Text(250, 200, resourcesManager.font, "Loading...", vbom));
	}

	@Override
	public void onBackKeyPressed() {
		return;
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_LOADING;
	}

	@Override
	public void disposeScene() {

	}

	@Override
	public ITextureBuilder getTextureBuilder() {
		return new ITextureBuilder() {

			@Override
			public List<BuildableBitmapTextureAtlas> prepareGraphics() {
				return new LinkedList<BuildableBitmapTextureAtlas>();
			}
		};
	}

}
