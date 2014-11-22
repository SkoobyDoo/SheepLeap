package com.game.sheepleap;

import java.util.Random;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.ZoomCamera;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePack;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePackLoader;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePackTextureRegionLibrary;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.exception.TexturePackParseException;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import com.game.sheepleap.GameActivity;

public class ResourcesManager {
	
    private static final ResourcesManager INSTANCE = new ResourcesManager();
    
    public Engine engine;
    public GameActivity activity;
    public ZoomCamera camera;
    public VertexBufferObjectManager vbom;
    
    public ITextureRegion splash_region;
    private BitmapTextureAtlas splashTextureAtlas;
    
    private BuildableBitmapTextureAtlas levelSelectTextureAtlas;
    public ITextureRegion levelSelect_background_region;
    public ITextureRegion lock_region;
    public ITextureRegion light_region;
    
    public ITextureRegion menu_background_region;
    public ITextureRegion play_region;
    public ITextureRegion options_region;
    
    private BuildableBitmapTextureAtlas victoryTextureAtlas;
    
    public ITextureRegion nextLevel_region;
    public ITextureRegion sheepUsed_region;
    public ITextureRegion time_region;
    public ITextureRegion winning_region;
    
    //public ITextureRegion logo_region;
    private BuildableBitmapTextureAtlas menuTextureAtlas;
    
    public Font font;
    public Music menu_music;
    public Music ground_music;
    public Music space_music;
    private Music bleat_death0;
    private Music bleat_death1;
    private Music bleat_death2;
    private Music bleat_death3;
    private Music bleat_death4;
    private Music bleat_death5;
    private Music bleat_standard0;
    private Music bleat_standard1;
    private Music bleat_standard2;
    private Music bleat_standard3;
    private Music bleat_standard4;
    private Music bleat_standard5;
    private Music bleat_nervous0;
    private Music bleat_nervous1;
    private Music bleat_nervous2;
    private Music bleat_nervous3;
    private Music bleat_nervous4;
    private Music bleat_nervous5;
    
    public Random r;
    
    private BuildableBitmapTextureAtlas optionsTextureAtlas;
    
    public ITextureRegion checkBox_region;
    public ITextureRegion checkedCheckBox_region;
    
    private TexturePackTextureRegionLibrary texturePackLibrary;
    private TexturePackTextureRegionLibrary texturePackLibrary2;
    private TexturePack texturePack;
    private TexturePack texturePack2;
    
    //public BuildableBitmapTextureAtlas gameTextureAtlas;
    public BitmapTextureAtlas gameGrassTextureAtlas;
    public ITextureRegion grass_region;
    
    public BitmapTextureAtlas gameWindTextureAtlas;
    public ITiledTextureRegion wind_region;
    
    public BitmapTextureAtlas gameSpaceTextureAtlas;
    public ITextureRegion space_region;
    
    public ITiledTextureRegion sheep_region;

	public ITiledTextureRegion start_region;
	
	public ITiledTextureRegion goalCloud_region;
	public ITiledTextureRegion goalAsteroid_region;
	public ITiledTextureRegion goalPillow_region;
	
	public ITiledTextureRegion bird_region;
	
	//public ITextureRegion wind_region;
	
	public ITiledTextureRegion treeLarge_region;
	public ITiledTextureRegion treeMedium_region;
	public ITiledTextureRegion treeSmall_region;
	
	//public ITextureRegion realTree_region;
	
	public ITiledTextureRegion cloudLarge_region;
	public ITiledTextureRegion cloudMedium_region;
	public ITiledTextureRegion cloudSmall_region;
	
	public ITiledTextureRegion blackHole_region;
	
	public ITiledTextureRegion asteroidLarge_region;
	public ITiledTextureRegion asteroidMedium_region;
	public ITiledTextureRegion asteroidSmall_region;
	
	public ITiledTextureRegion cloudThunder_region;
	
	public ITiledTextureRegion sheepHead;
	public ITiledTextureRegion sheepTorso;
	public ITiledTextureRegion sheepLeftLeg;
	public ITiledTextureRegion sheepRightLeg;
	
	public ITiledTextureRegion flower0;
	public ITiledTextureRegion flower1;
	public ITiledTextureRegion flower2;
	public ITiledTextureRegion flower3;
	public ITiledTextureRegion flower4;
	public ITiledTextureRegion flower5;
	
	public enum BleatType {DEATH, NERVOUS, STANDARD};
	

    public void loadMenuResources()
    {
        loadMenuGraphics();
        loadMenuAudio();
        loadMenuFonts();
    }
    
    public void loadLevelSelectResources()
    {
    	loadLevelSelectGraphics();
    	//loadMenuFonts();
    }
    
    public void unloadMenuTextures()
    {
        menuTextureAtlas.unload();
    }
        
    public void unloadLevelSelectTextures()
    {
    	levelSelectTextureAtlas.unload();
    }
    
    public void loadMenuTextures()
    {
        menuTextureAtlas.load();
    }
    
    public void unloadGameTextures()
    {
        texturePack.unloadTexture();
        texturePack2.unloadTexture();
        gameGrassTextureAtlas.unload();
    }
    
    public void loadGameResources()
    {
        loadGameGraphics();
        loadGameFonts();
        loadGameAudio();
    }
    
    public void loadVictoryResources()
    {
    	loadVictoryGraphics();
    }
    
    private void loadMenuFonts(){
        FontFactory.setAssetBasePath("font/");
        final ITexture mainFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

        font = FontFactory.createStrokeFromAsset(activity.getFontManager(), mainFontTexture, activity.getAssets(), "RAVIE.TTF", 50, true, Color.WHITE.getABGRPackedInt(), 2, Color.BLACK.getABGRPackedInt());
        font.load();
    }
    
    private void loadLevelSelectGraphics()
    {
    	BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
    	levelSelectTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
    	levelSelect_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(levelSelectTextureAtlas, activity, "background_simple.png");
    	lock_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(levelSelectTextureAtlas, activity, "lock2.png");
    	light_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(levelSelectTextureAtlas, activity, "lighticon2.png");
    	
    	try 
    	{
    	    this.levelSelectTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
    	    this.levelSelectTextureAtlas.load();
    	} 
    	catch (final TextureAtlasBuilderException e)
    	{
    	        Debug.e(e);
    	}
    }
    
    public void unloadVictoryTextures(){
    	victoryTextureAtlas.unload();
    }

    
    private void loadVictoryGraphics()
    {
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
    }
    
    private void loadMenuGraphics()
    {
    	BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
    	menuTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
    	menu_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "background.png");
    	play_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "start.png");
    	options_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "options.png");
    	//logo_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "logo.png");
    	       
    	try 
    	{
    	    this.menuTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
    	    this.menuTextureAtlas.load();
    	} 
    	catch (final TextureAtlasBuilderException e)
    	{
    	        Debug.e(e);
    	}

    }
    
    public void loadOptionsResources()
    {
    	loadOptionsGraphics();
    }
    
    private void loadOptionsGraphics()
    {
    	BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
    	optionsTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
    	
    	checkBox_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(optionsTextureAtlas, activity, "checkbox.png");
    	checkedCheckBox_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(optionsTextureAtlas, activity, "check.png");
    	
    	try
    	{
    		this.optionsTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
    		this.optionsTextureAtlas.load();
    	}
    	catch(final TextureAtlasBuilderException e)
    	{
    		Debug.e(e);
    	}
    }
    
    public void unloadOptionsTextures()
    {
    	this.optionsTextureAtlas.unload();
    }
    
    private void loadMenuAudio()
    {
    	try
    	{
    	    menu_music = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity,"mfx/menu_music.ogg");
    	    menu_music.setLooping(true);
    	}
    	catch (Exception e)
    	{
    	    Debug.e(e);
    	}
    }

    private void loadGameGraphics()
    {
        try 
        {
            texturePack = new TexturePackLoader(activity.getTextureManager(), "gfx/").loadFromAsset(activity.getAssets(), "entities.xml");
            texturePack.loadTexture();
            
            texturePack2 = new TexturePackLoader(activity.getTextureManager(), "gfx/").loadFromAsset(activity.getAssets(), "entities2.xml");
            texturePack2.loadTexture();
            
            texturePackLibrary = texturePack.getTexturePackTextureRegionLibrary();
            texturePackLibrary2 = texturePack2.getTexturePackTextureRegionLibrary();
        } 
        catch (final TexturePackParseException e) 
        {
            Debug.e(e);
        }
        
        sheep_region = texturePackLibrary.get(EntityStictchIdOne.SHEEPLAYERED4_ID, 4, 1);
        bird_region = texturePackLibrary.get(EntityStictchIdOne.BIRDFULL_ID, 3, 1);
        goalCloud_region = texturePackLibrary.get(EntityStictchIdOne.GOALCLOUDFULL_ID, 3, 1);
        cloudLarge_region = texturePackLibrary.get(EntityStictchIdOne.CLOUD_LARGE_ID, 1, 1);
        cloudMedium_region = texturePackLibrary.get(EntityStictchIdOne.CLOUD_MEDIUM_ID, 1, 1);
        cloudSmall_region = texturePackLibrary.get(EntityStictchIdOne.CLOUD_SMALL_ID, 1, 1);
        cloudThunder_region = texturePackLibrary.get(EntityStictchIdOne.RAINCLOUD2_ID, 1, 1);
        start_region = texturePackLibrary.get(EntityStictchIdOne.STARTLOC_ID, 1, 1);
        treeLarge_region = texturePackLibrary.get(EntityStictchIdOne.TREE_LARGE_NEST_ID, 1, 1);
        treeMedium_region = texturePackLibrary.get(EntityStictchIdOne.TREE_MEDIUM_ID, 1, 1);
        treeSmall_region = texturePackLibrary.get(EntityStictchIdOne.TREE_SMALL_ID, 1, 1);
        
    	sheepHead = texturePackLibrary.get(EntityStictchIdOne.SHEEP_DEADHEAD2_ID, 1, 1);
    	sheepTorso = texturePackLibrary.get(EntityStictchIdOne.SHEEP_BODY2_ID, 1, 1);
    	sheepLeftLeg = texturePackLibrary.get(EntityStictchIdOne.SHEEP_LEFT2_ID, 1, 1);
    	sheepRightLeg = texturePackLibrary.get(EntityStictchIdOne.SHEEP_RIGHT2_ID, 1, 1);
    	
    	flower0 = texturePackLibrary.get(EntityStictchIdOne.FLOWER_0_ID, 1, 1);
    	flower1 = texturePackLibrary.get(EntityStictchIdOne.FLOWER_1_ID, 1, 1);
    	flower2 = texturePackLibrary.get(EntityStictchIdOne.FLOWER_2_ID, 1, 1);
    	flower3 = texturePackLibrary.get(EntityStictchIdOne.FLOWER_3_ID, 1, 1);
    	flower4 = texturePackLibrary.get(EntityStictchIdOne.FLOWER_4_ID, 1, 1);
    	flower5 = texturePackLibrary.get(EntityStictchIdOne.FLOWER_5_ID, 1, 1);
        
        
        goalAsteroid_region = texturePackLibrary2.get(EntityStitchIdTwo.GOALASTEROIDFULL_ID, 3, 1);     
        goalPillow_region = texturePackLibrary2.get(EntityStitchIdTwo.PILLOW_ID, 1, 1);
        asteroidLarge_region = texturePackLibrary2.get(EntityStitchIdTwo.ASTEROID_LARGE_ID, 1, 1);
        asteroidMedium_region = texturePackLibrary2.get(EntityStitchIdTwo.ASTEROID_MEDIUM_ID, 1, 1);
        asteroidSmall_region = texturePackLibrary2.get(EntityStitchIdTwo.ASTEROID_SMALL_ID, 1, 1);
        blackHole_region = texturePackLibrary2.get(EntityStitchIdTwo.BLACKHOLE_ID, 1, 1);
        
    	BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
    		
    	gameGrassTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 1024, 256, TextureOptions.REPEATING_NEAREST_PREMULTIPLYALPHA);	
    	grass_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameGrassTextureAtlas, activity, "grass.png", 0, 0);
    	
    	gameWindTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.NEAREST_PREMULTIPLYALPHA);
    	wind_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameWindTextureAtlas, activity, "windStacked.png", 0, 0, 1, 5);
    	
    	gameSpaceTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 1024, 768, TextureOptions.NEAREST_PREMULTIPLYALPHA);
    	space_region =  BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameSpaceTextureAtlas, activity, "space.png", 0, 0);

	    try 
	    {
	        this.gameGrassTextureAtlas.load();
	        this.gameWindTextureAtlas.load();
	        this.gameSpaceTextureAtlas.load();
	    } 
	    catch (Exception e)
	    {
	        Debug.e(e);
	    }
    }
    
    private void loadGameFonts()
    {
        
    }
    
    private void loadGameAudio()
    {
    	r = new Random();
    	
    	try
    	{
    	    ground_music = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity,"mfx/ground_music.ogg");
    	    menu_music.setLooping(true);
    	    space_music = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity,"mfx/space_music.ogg");
    	    space_music.setLooping(true);
    	    
    	    bleat_death0 = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity,"mfx/bleat_death_00.ogg");
    	    bleat_death1 = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity,"mfx/bleat_death_01.ogg");
    	    bleat_death2 = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity,"mfx/bleat_death_02.ogg");
    	    bleat_death3 = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity,"mfx/bleat_death_03.ogg");
    	    bleat_death4 = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity,"mfx/bleat_death_04.ogg");
    	    bleat_death5 = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity,"mfx/bleat_death_05.ogg");
    	    
    	    bleat_nervous0 = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity,"mfx/bleat_nervous_00.ogg");
    	    bleat_nervous1 = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity,"mfx/bleat_nervous_01.ogg");
    	    bleat_nervous2 = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity,"mfx/bleat_nervous_02.ogg");
    	    bleat_nervous3 = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity,"mfx/bleat_nervous_03.ogg");
    	    bleat_nervous4 = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity,"mfx/bleat_nervous_04.ogg");
    	    bleat_nervous5 = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity,"mfx/bleat_nervous_05.ogg");
    	    
    	    bleat_standard0 = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity,"mfx/bleat_standard_00.ogg");
    	    bleat_standard1 = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity,"mfx/bleat_standard_01.ogg");
    	    bleat_standard2 = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity,"mfx/bleat_standard_02.ogg");
    	    bleat_standard3 = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity,"mfx/bleat_standard_03.ogg");
    	    bleat_standard4 = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity,"mfx/bleat_standard_04.ogg");
    	    bleat_standard5 = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity,"mfx/bleat_standard_05.ogg");
    	}
    	catch (Exception e)
    	{
    	    Debug.e(e);
    	}
    }
    
    public Music sheepBleat(BleatType type){
    	Music theBleat = null;
    	final int number = r.nextInt(6);
    	
    	if(type == BleatType.DEATH){
    		switch(number){
    		case 0:
    			theBleat = bleat_death0;
    			break;
    		case 1:
    			theBleat = bleat_death1;
    			break;
    		case 2:
    			theBleat = bleat_death2;
    			break;
    		case 3:
    			theBleat = bleat_death3;
    			break;
    		case 4:
    			theBleat = bleat_death4;
    			break;
    		case 5:
    			theBleat = bleat_death5;
    			break;
    		default:
    			Debug.e("gave 6 :(");
    			break;
    		}
    		
    	}
    	else if(type == BleatType.NERVOUS){
    		switch(number){
    		case 0:
    			theBleat = bleat_nervous0;
    			break;
    		case 1:
    			theBleat = bleat_nervous1;
    			break;
    		case 2:
    			theBleat = bleat_nervous2;
    			break;
    		case 3:
    			theBleat = bleat_nervous3;
    			break;
    		case 4:
    			theBleat = bleat_nervous4;
    			break;
    		case 5:
    			theBleat = bleat_nervous5;
    			break;
    		default:
    			Debug.e("gave 6 :(");
    			break;
    		}
    	}
    	else if(type == BleatType.STANDARD){
    		switch(number){
    		case 0:
    			theBleat = bleat_standard0;
    			break;
    		case 1:
    			theBleat = bleat_standard1;
    			break;
    		case 2:
    			theBleat = bleat_standard2;
    			break;
    		case 3:
    			theBleat = bleat_standard3;
    			break;
    		case 4:
    			theBleat = bleat_standard4;
    			break;
    		case 5:
    			theBleat = bleat_standard5;
    			break;
    		default:
    			Debug.e("gave 6 :(");
    			break;
    		}
    	}
    	
    	return theBleat;
    }
    
    public void loadSplashScreen()
    {
    	BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
    	splashTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
    	splash_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas, activity, "splash2.png", 0, 0);
    	splashTextureAtlas.load();
    }
    
    public void unloadSplashScreen()
    {
    	splashTextureAtlas.unload();
    	splash_region = null;
    }
    

    public static void prepareManager(Engine engine, GameActivity activity, ZoomCamera camera, VertexBufferObjectManager vbom)
    {
        getInstance().engine = engine;
        getInstance().activity = activity;
        getInstance().camera = camera;
        getInstance().vbom = vbom;
    }
    
    public static ResourcesManager getInstance()
    {
        return INSTANCE;
    }
}
