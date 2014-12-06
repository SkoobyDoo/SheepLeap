package com.game.sheepleap.scenes;

import java.util.LinkedList;
import java.util.List;

import org.andengine.entity.sprite.Sprite;
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
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;

import com.game.sheepleap.ResourcesManager;
import com.game.sheepleap.ResourcesManager.ITextureBuilder;
import com.game.sheepleap.SceneManager;
import com.game.sheepleap.SceneManager.SceneType;

public class SplashScene extends BaseScene {
	ITextureRegion splash_texture;

	private Sprite splash;

	public static void displayNew() {
		ResourcesManager.getInstance().engine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() {
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler) {
				ResourcesManager.getInstance().engine.unregisterUpdateHandler(pTimerHandler);
				SplashScene splash = new SplashScene();
				// ResourcesManager.getInstance().loadSceneResources(splash);
				SceneManager.getInstance().transitionTo(splash);
			}
		}));
	}

	@Override
	public void createScene() {
		splash = new Sprite(0, 0, splash_texture, vbom) {
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera) {
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};

		splash.setScale(1.2f);
		splash.setPosition(275, 140);
		attachChild(splash);
	}

	@Override
	public void onBackKeyPressed() {
		System.exit(0);
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_SPLASH;
	}

	@Override
	public void disposeScene() {
		splash.detachSelf();
		splash.dispose();
		this.detachSelf();
		this.dispose();
	}

	@Override
	public ITextureBuilder getTextureBuilder() {
		return new ITextureBuilder() {

			@Override
			public List<BuildableBitmapTextureAtlas> prepareGraphics() {
				BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
				BuildableBitmapTextureAtlas splash_atlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 256, 256,
						TextureOptions.BILINEAR);
				// load textures
				splash_texture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splash_atlas,
						ResourcesManager.getInstance().activity, "splash2.png");

				// prepare atlas list
				// most scenes only have one atlas.
				List<BuildableBitmapTextureAtlas> graphicsEntry = new LinkedList<BuildableBitmapTextureAtlas>();

				try {
					splash_atlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
				} catch (final TextureAtlasBuilderException e) {
					Debug.e(e);
				}

				graphicsEntry.add(splash_atlas);
				return graphicsEntry;
			}

		};
	}

}
