package com.game.sheepleap;

import org.andengine.engine.Engine;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.ui.IGameInterface.OnCreateSceneCallback;

public class SceneManager {

    private BaseScene splashScene;
    private BaseScene menuScene;
    private BaseScene gameScene;
    private BaseScene loadingScene;
    private BaseScene optionsScene;
    private BaseScene levelSelectScene;
    private BaseScene victoryScene;
    
    public int levelToLoad;
    
    public int sheep;
    public int maxSheep;
    public float time;
    
    private static final SceneManager INSTANCE = new SceneManager();
    
    private SceneType currentSceneType = SceneType.SCENE_SPLASH;
    
    private BaseScene currentScene;
    
    private Engine engine = ResourcesManager.getInstance().engine;
    
    public enum SceneType
    {
        SCENE_SPLASH,
        SCENE_MENU,
        SCENE_GAME,
        SCENE_LOADING,
        SCENE_OPTIONS,
        SCENE_LEVEL_SELECT,
        SCENE_VICTORY,
    }
    
    public void createMenuScene()
    {
        ResourcesManager.getInstance().loadMenuResources();
        menuScene = new MainMenuScene();
        loadingScene = new LoadingScene();
        setScene(menuScene);
        disposeSplashScene();
    }
    
    public void loadVictoryScene(final Engine mEngine, final int sheepUsed, final int totalSheep, final float timeUsed)
    {
        setScene(loadingScene);
        ResourcesManager.getInstance().unloadGameTextures();
        gameScene.disposeScene();
        levelToLoad++;
        
        sheep = sheepUsed;
        maxSheep = totalSheep;
        time = timeUsed;
        
        mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() 
        {
            public void onTimePassed(final TimerHandler pTimerHandler) 
            {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                ResourcesManager.getInstance().loadVictoryResources();
                victoryScene = new VictoryScene();
                setScene(victoryScene);
            }
        }));
    }
    
    public void loadMenuScene(final Engine mEngine)
    {
    	SceneType prevScene = getCurrentSceneType();
    	
        setScene(loadingScene);

        if(prevScene == SceneType.SCENE_GAME){
        	ResourcesManager.getInstance().unloadGameTextures();
            gameScene.disposeScene();
        }
        if(prevScene == SceneType.SCENE_OPTIONS){
        	ResourcesManager.getInstance().unloadOptionsTextures();
        	optionsScene.disposeScene();
        }
        if(prevScene == SceneType.SCENE_LEVEL_SELECT){
        	ResourcesManager.getInstance().unloadLevelSelectTextures();
        	levelSelectScene.disposeScene();
        }
        mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() 
        {
            public void onTimePassed(final TimerHandler pTimerHandler) 
            {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                ResourcesManager.getInstance().loadMenuTextures();
                setScene(menuScene);
            }
        }));
    }
    
    public void setScene(BaseScene scene)
    {
        engine.setScene(scene);
        currentScene = scene;
        currentSceneType = scene.getSceneType();
    }
    
    public void setScene(SceneType sceneType)
    {
        switch (sceneType)
        {
            case SCENE_MENU:
                setScene(menuScene);
                break;
            case SCENE_GAME:
                setScene(gameScene);
                break;
            case SCENE_SPLASH:
                setScene(splashScene);
                break;
            case SCENE_LOADING:
                setScene(loadingScene);
                break;
            default:
                break;
        }
    }
    
    public void loadGameScene(final Engine mEngine)
    {
    	SceneType prevScene = getCurrentSceneType();
        setScene(loadingScene);
        
        if(prevScene == SceneType.SCENE_LEVEL_SELECT){
        	ResourcesManager.getInstance().unloadLevelSelectTextures();
            levelSelectScene.disposeScene();
        }
        if(prevScene == SceneType.SCENE_VICTORY){
        	ResourcesManager.getInstance().unloadVictoryTextures();
        	victoryScene.disposeScene();
        }
        
        mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() 
        {
            public void onTimePassed(final TimerHandler pTimerHandler) 
            {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                ResourcesManager.getInstance().loadGameResources();
                gameScene = new GameScene(levelToLoad);
                setScene(gameScene);
            }
        }));
    }
    
	public void loadLevelSelectScene(final Engine mEngine) {
		SceneType prevScene = getCurrentSceneType();
		setScene(loadingScene);
		
        if(prevScene == SceneType.SCENE_MENU){
        	ResourcesManager.getInstance().unloadMenuTextures();
            menuScene.disposeScene();
        }
        if(prevScene == SceneType.SCENE_GAME){
        	ResourcesManager.getInstance().unloadGameTextures();
        	gameScene.disposeScene();
        }
		
		ResourcesManager.getInstance().unloadMenuTextures();
        mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() 
        {
            public void onTimePassed(final TimerHandler pTimerHandler) 
            {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                ResourcesManager.getInstance().loadLevelSelectResources();
                levelSelectScene = new LevelSelectScene();
                setScene(levelSelectScene);
            }
        }));
	}
    
    public void createSplashScene(OnCreateSceneCallback pOnCreateSceneCallback)
    {
        ResourcesManager.getInstance().loadSplashScreen();
        splashScene = new SplashScene();
        currentScene = splashScene;
        pOnCreateSceneCallback.onCreateSceneFinished(splashScene);
    }
    
    private void disposeSplashScene()
    {
        ResourcesManager.getInstance().unloadSplashScreen();
        splashScene.disposeScene();
        splashScene = null;
    }
    
    public static SceneManager getInstance()
    {
        return INSTANCE;
    }
    
    public SceneType getCurrentSceneType()
    {
        return currentSceneType;
    }
    
    public BaseScene getCurrentScene()
    {
        return currentScene;
    }

	public void loadOptionsScene(final Engine mEngine) {
		setScene(loadingScene);
		ResourcesManager.getInstance().unloadMenuTextures();
        mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() 
        {
            public void onTimePassed(final TimerHandler pTimerHandler) 
            {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                ResourcesManager.getInstance().loadOptionsResources();
                optionsScene = new OptionsScene();
                setScene(optionsScene);
            }
        }));
	}
}
