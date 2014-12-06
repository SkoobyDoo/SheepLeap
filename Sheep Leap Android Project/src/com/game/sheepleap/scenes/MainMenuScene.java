package com.game.sheepleap.scenes;

import java.util.LinkedList;
import java.util.List;

import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
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
import com.game.sheepleap.ResourcesManager.MusicType;
import com.game.sheepleap.SceneManager;
import com.game.sheepleap.SceneManager.SceneType;

public class MainMenuScene extends BaseScene implements IOnMenuItemClickListener{
	
	ITextureRegion options_region;
	ITextureRegion play_region;
	ITextureRegion menu_logo_region;
	ITextureRegion menu_background_region;
	
	private MenuScene menuChildScene;
	private final int MENU_PLAY = 0;
	private final int MENU_OPTIONS = 1;
	
	public static void displayNew() {
		ResourcesManager.getInstance().engine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() 
        {
            @Override
			public void onTimePassed(final TimerHandler pTimerHandler) 
            {
            	ResourcesManager.getInstance().engine.unregisterUpdateHandler(pTimerHandler);
                //ResourcesManager.getInstance().loadMenuTextures();
            	//MainMenuScene scene = new MainMenuScene();
            	//ResourcesManager.getInstance().loadSceneResources(scene);
                SceneManager.getInstance().transitionTo(new MainMenuScene());
            }
        }));
	}

	private void createMenuChildScene()
	{
	    menuChildScene = new MenuScene(camera);
	    menuChildScene.setPosition(0, 0);
	    
	    final IMenuItem playMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_PLAY, play_region, vbom), 1.2f, 1);
	    final IMenuItem optionsMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_OPTIONS, options_region, vbom), 1.2f, 1);
	    
	    menuChildScene.addMenuItem(playMenuItem);
	    menuChildScene.addMenuItem(optionsMenuItem);
	    
	    menuChildScene.buildAnimations();
	    menuChildScene.setBackgroundEnabled(false);
	    
	    playMenuItem.setPosition(playMenuItem.getX(), playMenuItem.getY() + 100);
	    optionsMenuItem.setPosition(optionsMenuItem.getX(), optionsMenuItem.getY() + 120);
	    
	    menuChildScene.setOnMenuItemClickListener(this);
	    
	    setChildScene(menuChildScene);
	}

	private void createBackground()
	{
	    attachChild(new Sprite(0, 0, menu_background_region, vbom)
	    {
	        @Override
	        protected void preDraw(GLState pGLState, Camera pCamera) 
	        {
	            super.preDraw(pGLState, pCamera);
	            pGLState.enableDither();
	        }
	    });
	    Sprite logo = new Sprite(0, 0, menu_logo_region, vbom);
	    logo.setScale(0.5f, 0.5f);
	    logo.setPosition(0f,-70f);
	    attachChild(logo);
	}
	
	@Override
	public void createScene() {
		createBackground();
		createMenuChildScene();
		resourcesManager.PlayMusic(MusicType.MENU);
	}

	@Override
	public void onBackKeyPressed()
	{
	    System.exit(0);
	}

	@Override
	public SceneType getSceneType()
	{
	    return SceneType.SCENE_MENU;
	}

	@Override
	public void disposeScene() {
	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		switch(pMenuItem.getID()){
	        case MENU_PLAY:
	        	//SceneManager.getInstance().loadLevelSelectScene(engine);
	        	LevelSelectScene.displayNew();
	            return true;
	        case MENU_OPTIONS:
	        	//SceneManager.getInstance().loadOptionsScene(engine);
	        	OptionsScene.displayNew();
	            return true;
	        default:
	            return false;
	    }
	}

	@Override
	public ITextureBuilder getTextureBuilder() {
		return new ITextureBuilder() {

			@Override
			public List<BuildableBitmapTextureAtlas> prepareGraphics() {
				BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
				BuildableBitmapTextureAtlas menuTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
				// load textures
				menu_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "background.png");
				menu_logo_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "logo.png");
				play_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "start.png");
				options_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "options.png");
		    	
		    	
		    	// prepare atlas list
		    	// most scenes only have one atlas.
		    	List<BuildableBitmapTextureAtlas> graphicsEntry = new LinkedList<BuildableBitmapTextureAtlas>();

		    	
		    	try 
		    	{
		    	    menuTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
		    	} 
		    	catch (final TextureAtlasBuilderException e)
		    	{
		    	        Debug.e(e);
		    	}
		    	graphicsEntry.add(menuTextureAtlas);
		    	return graphicsEntry;
			}
			
		};
	}
}