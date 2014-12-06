package com.game.sheepleap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.ZoomCamera;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import com.game.sheepleap.GameActivity;
import com.game.sheepleap.scenes.BaseScene;

public class ResourcesManager {	
    private static final ResourcesManager INSTANCE = new ResourcesManager();
    
    public Engine engine;
    public GameActivity activity;
    public ZoomCamera camera;
    public VertexBufferObjectManager vbom;
    
    private Map< SceneManager.SceneType, List<BuildableBitmapTextureAtlas> > mSceneAtlases = new HashMap< SceneManager.SceneType, List<BuildableBitmapTextureAtlas> >();
    
    public Font font;
    
    private Map<BleatType, List<Sound>> mBleatCollection = new HashMap<BleatType, List<Sound>>();
    boolean mSoundsLoaded = false;
    //TODO look into sheep nervous sound lag
    private Map<MusicType, Music> mMusicList = new HashMap<MusicType, Music>();
    boolean mMusicLoaded = false;
    private MusicType mMusicNowPlaying = null;
	
	public enum BleatType {
		STANDARD, DEATH, NERVOUS;
		
		public String getBaseFileName() {
			switch (this) {
			case NERVOUS:
				return "bleat_nervous_";
			case DEATH:
				return "bleat_death_";
			case STANDARD:
			default:
				return "bleat_standard_";
			}
		}
	}
	
	public enum MusicType {
		SPACE, GROUND, MENU;
	}
	
    public static void prepareManager(Engine engine, GameActivity activity, ZoomCamera camera, VertexBufferObjectManager vbom) {
        getInstance().engine = engine;
        getInstance().activity = activity;
        getInstance().camera = camera;
        getInstance().vbom = vbom;
    }
    
    public static ResourcesManager getInstance() {
        return INSTANCE;
    }
	
	public void loadSceneResourcesS(BaseScene scene) {
		List<BuildableBitmapTextureAtlas> list = mSceneAtlases.get(scene.getSceneType());
		if(list == null) {
			// textures have not been initialized yet. initialize them
			mSceneAtlases.put(scene.getSceneType(), scene.getTextureBuilder().prepareGraphics());
		}
		
		// at this point textures should be initialized. just need to load.
		list = mSceneAtlases.get(scene.getSceneType());
		//if (list != null) // this should never fire, commenting out and crossing fingers
		for(BuildableBitmapTextureAtlas atlas : list)
			atlas.load();
	}
	
	public void unloadSceneResources(BaseScene scene) {
		// calls TextureAtlas.unload() for the scene's resources, if they exist already
		List<BuildableBitmapTextureAtlas> list = mSceneAtlases.get(scene.getSceneType());
		if (list!= null)
			for(BuildableBitmapTextureAtlas atlas : list)
				atlas.unload();
	}
	
	public void disposeSceneResources(BaseScene scene) {
		// fully removes TextureAtlas/ITextureRegions from the mSceneAtlases map, forcing them to be fully reloaded next scene load of same type as scene
		//List<Pair<TextureAtlas<ITextureAtlasSource>, List<ITextureRegion>>> list = mSceneAtlases.get(scene.getSceneType());
		//if (list!= null)
		mSceneAtlases.remove(scene.getSceneType());
	}
    
    public void loadFont(){
        FontFactory.setAssetBasePath("font/");
        final ITexture mainFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

        font = FontFactory.createStrokeFromAsset(activity.getFontManager(), mainFontTexture, activity.getAssets(), "RAVIE.TTF", 50, true, Color.WHITE.getABGRPackedInt(), 2, Color.BLACK.getABGRPackedInt());
        font.load();
    }
    
    public boolean loadMusic() {
    	if(mMusicLoaded)
    		return false;
	    try {
			mMusicList.put(MusicType.GROUND, MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity,"mfx/ground_music.ogg"));
		    mMusicList.put(MusicType.SPACE, MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity,"mfx/space_music.ogg"));
		    mMusicList.put(MusicType.MENU, MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity,"mfx/menu_music.ogg"));
		} catch (Exception e) {
			Debug.e(e);
		}
	    for(Music m : mMusicList.values())
	    	m.setLooping(true);
	    
	    return true;
    }
    
    public boolean loadGameSounds() {
    	if (mSoundsLoaded)
    		return false;
    	try {
    	    for(BleatType type : BleatType.values()) {
    	    	List<Sound> theseBleats = new ArrayList<Sound>(6);
    	    	for(int i=0; i<6; i++) {
    	    		// TODO: fix code so that it works beyond 10 different bleats. or stop caring because there will never be more than 10 bleats.
    	    		theseBleats.add(SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity, "mfx/" + type.getBaseFileName() + "0" + i + ".ogg"));
    	    	}
    	    	mBleatCollection.put(type, theseBleats);
    	    }
    	}
    	catch (Exception e) {
    	    Debug.e(e);
    	}
    	mSoundsLoaded = true;
    	return true;
    }
    
    public boolean PlayMusic(MusicType type) {
    	// returns true if the music was changed as a result of this function call.
    	// this function assumes all music is loaded. (at time of writing, loading code does not exist; no idea where I'll put it to make sure this is true)
    	if(mMusicNowPlaying == type)
    		return false;
    	if(mMusicNowPlaying != null) {
    		// stop() function breaks the music requiring it to be released and reloaded. pause and seek to 0 seems to work. weird.
    		mMusicList.get(mMusicNowPlaying).pause();
    		mMusicList.get(mMusicNowPlaying).seekTo(0);
    	}
    	mMusicList.get(type).play();
    	mMusicNowPlaying = type;
    	return true;
    }
    
    public boolean stopMusic() {
    	if (mMusicNowPlaying == null)
    		return false;
    	mMusicList.get(mMusicNowPlaying).stop();
    	mMusicNowPlaying = null;
    	return true;
    }
    
    public Sound randomTypedSheepBleat(BleatType type) {
    	List<Sound> bleats = mBleatCollection.get(type);
    	return bleats.get(GameSettings.random.nextInt(bleats.size()));
    }
    
    public interface ITextureBuilder {
    	// TODO: possibly add a wrapper class around BuildableBitmapTextureAtlas in case some scenes use non-BuildableBitmapTextureAtlas texture methods (AHEM GameScene)
    	// might just remove those from GameScene so everything conforms to our BuildableBitmapTextureAtlas overlords.
    	public List<BuildableBitmapTextureAtlas> prepareGraphics();
    }

}
