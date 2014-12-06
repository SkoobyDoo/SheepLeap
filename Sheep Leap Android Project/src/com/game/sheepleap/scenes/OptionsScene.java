package com.game.sheepleap.scenes;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
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

import com.game.sheepleap.GameSettings;
import com.game.sheepleap.ResourcesManager;
import com.game.sheepleap.SceneManager;
import com.game.sheepleap.ResourcesManager.ITextureBuilder;
import com.game.sheepleap.ResourcesManager.MusicType;
import com.game.sheepleap.SceneManager.SceneType;
import com.game.sheepleap.entities.base.ScreenEntity;
import com.game.sheepleap.entities.ui.CheckBoxEntity;
import com.game.sheepleap.entities.ui.IOptionUpdater;
import com.game.sheepleap.entities.ui.OptionEntity;
import com.game.sheepleap.entities.ui.SliderEntity;

public class OptionsScene extends BaseScene {
	ITextureRegion slider_region;
	ITextureRegion deadSheepHead_region;
	ITextureRegion normalSheepHead_region;
	ITextureRegion scaredSheepHead_region;
	ITextureRegion music_region;
	ITextureRegion sound_region;
	ITextureRegion highlow_region;
	ITextureRegion checkBox_region;
	ITextureRegion checkedCheckBox_region;
	ITextureRegion menu_background_simple_region;
	
	Sound testSound;
	
	private ArrayList<OptionEntity> optionEntities;
	
	public static void displayNew() {
		ResourcesManager.getInstance().engine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() 
        {
            @Override
			public void onTimePassed(final TimerHandler pTimerHandler) 
            {
            	ResourcesManager.getInstance().engine.unregisterUpdateHandler(pTimerHandler);
                SceneManager.getInstance().transitionTo(new OptionsScene());
            }
        }));
	}
	
	private void createBackground()
	{
	    attachChild(new Sprite(0, 0, menu_background_simple_region, vbom)
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
		
		try {
			testSound = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity,"mfx/bleat_standard_00.ogg");
		}
		catch (Exception e) {
			Debug.e(e);
		}

		SliderEntity.updateTextureRegions(slider_region, deadSheepHead_region, normalSheepHead_region, scaredSheepHead_region);
		CheckBoxEntity.updateTextureRegions(checkBox_region, checkedCheckBox_region);
		
		optionEntities = new ArrayList<OptionEntity>();
		
		optionEntities.add(new CheckBoxEntity(25, 350, GameSettings.hippieMode, new IOptionUpdater() {

			@Override
			public void update(double value) {
				GameSettings.hippieMode = (value == 0f) ? false : true;
			}
		}, this));
		
		optionEntities.add(new SliderEntity(25, 250, GameSettings.musicVolume, new IOptionUpdater() {

			@Override
			public void update(double value) {
				GameSettings.musicVolume = (float) value;
				engine.getMusicManager().setMasterVolume((float) value);
			}
		}, this));
		
		optionEntities.add(new SliderEntity(25, 100, GameSettings.effectVolume, new IOptionUpdater() {
			long lastTest = 0;
			
			@Override
			public void update(double value) {
				GameSettings.effectVolume = (float) value;
				engine.getSoundManager().setMasterVolume((float) value);
				if(System.currentTimeMillis() > lastTest + 1000) {
					lastTest = System.currentTimeMillis();
					testSound.play();
				}
			}
		}, this));

		//attachChild(new Sprite(5, 275, resourcesManager.highlow_region, vbom));
		new ScreenEntity(5, 275, highlow_region, this); // equivalent to previous commented line
		attachChild(new Sprite(5, 180, music_region, vbom));
		attachChild(new Sprite(5, 125, highlow_region, vbom));
		attachChild(new Sprite(5, 30, sound_region, vbom));
		resourcesManager.PlayMusic(MusicType.MENU);
		
	}

	@Override
	public void onBackKeyPressed() {
		MainMenuScene.displayNew();
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_OPTIONS;
	}

	@Override
	public void disposeScene() {
		ResourcesManager.getInstance().unloadSceneResources(this);
		SliderEntity.clearTextureRegions();
		CheckBoxEntity.clearTextureRegions();
	}
	
	@Override
	public ITextureBuilder getTextureBuilder() {
		return new ITextureBuilder() {

			@Override
			public List<BuildableBitmapTextureAtlas> prepareGraphics() {
				BuildableBitmapTextureAtlas optionsTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
				// load textures
				BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/options/");
		    	slider_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(optionsTextureAtlas, activity, "slider.png");
		    	deadSheepHead_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(optionsTextureAtlas, activity, "dead_slider.png");
		    	normalSheepHead_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(optionsTextureAtlas, activity, "normal_slider.png");
		    	scaredSheepHead_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(optionsTextureAtlas, activity, "scared_slider.png");
		        music_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(optionsTextureAtlas, activity, "music.png");
		        sound_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(optionsTextureAtlas, activity, "sound.png");
		        highlow_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(optionsTextureAtlas, activity, "highlow.png");
		    	checkBox_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(optionsTextureAtlas, activity, "checkbox.png");
		    	checkedCheckBox_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(optionsTextureAtlas, activity, "check.png");
		    	
		    	BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
		    	menu_background_simple_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(optionsTextureAtlas, activity, "background_simple.png");
		    	
		    	
		    	// prepare atlas list
		    	// most scenes only have one atlas.
		    	List<BuildableBitmapTextureAtlas> graphicsEntry = new LinkedList<BuildableBitmapTextureAtlas>();
		    	
		    	try 
		    	{
		    		optionsTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
		    	} 
		    	catch (final TextureAtlasBuilderException e)
		    	{
		    	        Debug.e(e);
		    	}
		    	
		    	graphicsEntry.add(optionsTextureAtlas);
		    	return graphicsEntry;
			}
			
		};
	}

}