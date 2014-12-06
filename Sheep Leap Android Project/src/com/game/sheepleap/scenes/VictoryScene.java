package com.game.sheepleap.scenes;

import java.util.LinkedList;
import java.util.List;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
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

import com.game.sheepleap.ResourcesManager;
import com.game.sheepleap.SceneManager;
import com.game.sheepleap.ResourcesManager.ITextureBuilder;
import com.game.sheepleap.SceneManager.SceneType;

public class VictoryScene extends BaseScene implements IOnAreaTouchListener{
	
	ITextureRegion menu_background_region;
	ITextureRegion nextLevel_region;
	ITextureRegion sheepUsed_region;
	ITextureRegion time_region;
	ITextureRegion winning_region;

	private float mTimeUsed;
	private int mSheepUsed;
	private int mTotalSheep;
	
	private int mNextLevel;
	
	private Sprite nextLevelSprite;
	
	private VictoryScene() throws Exception { throw new Exception("Stop using this");}; // prevent 0 parameter construction
	
	private VictoryScene(int sheepUsed, int totalSheep, float timeUsed, int nextLevel) {
		this.resourcesManager = ResourcesManager.getInstance();
        this.engine = resourcesManager.engine;
        this.activity = resourcesManager.activity;
        this.vbom = resourcesManager.vbom;
        this.camera = resourcesManager.camera;
		ResourcesManager.getInstance().loadSceneResourcesS(this);
		mSheepUsed = sheepUsed;
		mTotalSheep = totalSheep;
		mTimeUsed = timeUsed;
		mNextLevel = nextLevel;
        createScene();
	}
	
	public static void displayNew(float timeConsumed, int sheepConsumed, int sheepAvailable, int nextLevel) {
		final VictoryScene v = new VictoryScene(sheepConsumed, sheepAvailable, timeConsumed, nextLevel);
		
		ResourcesManager.getInstance().engine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() 
        {
            @Override
			public void onTimePassed(final TimerHandler pTimerHandler) 
            {
            	ResourcesManager.getInstance().engine.unregisterUpdateHandler(pTimerHandler);
                SceneManager.getInstance().transitionTo(v);
            }
        }));
	}
	
	@Override
	public void createScene() {
		createBackground();
		
		nextLevelSprite = new Sprite(450, 300, nextLevel_region, vbom);
		
		this.attachChild(nextLevelSprite);
		this.registerTouchArea(nextLevelSprite);
		
		this.attachChild(new Sprite(140, 20, winning_region, vbom));
		this.attachChild(new Sprite(140, 150, time_region, vbom));
		this.attachChild(new Sprite(20, 290, sheepUsed_region, vbom));
		
		this.attachChild(new Text(400, 150, resourcesManager.font, String.format("%.3f seconds", mTimeUsed), vbom));
		this.attachChild(new Text(450, 290, resourcesManager.font, "" + mSheepUsed + "/" + mTotalSheep, vbom));
		
		this.setOnAreaTouchListener(this);
	}

	@Override
	public void onBackKeyPressed() {
		LevelSelectScene.displayNew();
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_VICTORY;
	}

	@Override
	public void disposeScene() {
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
	}

	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		if(pSceneTouchEvent.isActionDown()) {
			final Sprite button = (Sprite) pTouchArea;
			
			if(button.getTextureRegion().equals(nextLevel_region)){
				//SceneManager.getInstance().loadGameScene(engine);
				GameScene.displayNew(mNextLevel);
			}
			
			return true;
		}
		return false;
	}
	
	@Override
	public ITextureBuilder getTextureBuilder() {
		return new ITextureBuilder() {

			@Override
			public List<BuildableBitmapTextureAtlas> prepareGraphics() {

				BuildableBitmapTextureAtlas victoryTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
				// load textures
				BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
				menu_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(victoryTextureAtlas, activity, "background.png");
				nextLevel_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(victoryTextureAtlas, activity, "nextlevel.png");
				sheepUsed_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(victoryTextureAtlas, activity, "sheepused.png");
				time_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(victoryTextureAtlas, activity, "time.png");
				winning_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(victoryTextureAtlas, activity, "winning.png");
		    	
		    	
		    	// prepare atlas list
		    	// most scenes only have one atlas.
		    	List<BuildableBitmapTextureAtlas> graphicsEntry = new LinkedList<BuildableBitmapTextureAtlas>();

		    	
		    	try 
		    	{
		    		victoryTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
		    	} 
		    	catch (final TextureAtlasBuilderException e)
		    	{
		    	        Debug.e(e);
		    	}
		    	graphicsEntry.add(victoryTextureAtlas);
		    	return graphicsEntry;
			}
			
		};
	}

}
/*
BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
victoryTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);

menu_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(victoryTextureAtlas, activity, "background.png");
nextLevel_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(victoryTextureAtlas, activity, "nextlevel.png");
sheepUsed_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(victoryTextureAtlas, activity, "sheepused.png");
time_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(victoryTextureAtlas, activity, "time.png");
winning_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(victoryTextureAtlas, activity, "winning.png");

try 
{
    this.victoryTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
    this.victoryTextureAtlas.load();
} 
catch (final TextureAtlasBuilderException e)
{
        Debug.e(e);
}
*/