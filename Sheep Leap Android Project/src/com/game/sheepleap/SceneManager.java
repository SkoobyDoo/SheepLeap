package com.game.sheepleap;

import java.util.HashMap;
import java.util.Map;

import org.andengine.engine.Engine;
import org.andengine.ui.IGameInterface.OnCreateSceneCallback;

import com.game.sheepleap.scenes.BaseScene;
import com.game.sheepleap.scenes.LoadingScene;
import com.game.sheepleap.scenes.SplashScene;

public class SceneManager {
    
    private Map<SceneType,BaseScene> SceneList = new HashMap<SceneType,BaseScene>();
    
    private static final SceneManager INSTANCE = new SceneManager();
    
    private SceneType currentSceneType = SceneType.SCENE_SPLASH;
    
    private BaseScene currentScene;
    private BaseScene loadScene;
    
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
    
    /*
     * Scene transition Convention:
     * 
     * Each scene should implement a static method that facilitates a transition
     * to that scene. That static method should create an instance of its scene
     * type, and then call transitionTo to switch to that scene. This allows some
     * scenes to be created with certain parameters (eg GameScene takes a level num)
     * 
     * transitionTo is not a part of the BaseScene class to allow for varying parameters.
     * 
     */
    
    public BaseScene getScene(SceneType t) {
    	// returns null if no scene of that type exists
    	return INSTANCE.SceneList.get(t);
    }
    
    public void transitionTo(BaseScene s) {
    	registerScene(s);
    	loadScene(s);
    }
    
    public boolean registerScene(BaseScene s) {
    	// returns true if a scene was overwritten by s
    	return SceneList.put(s.getSceneType(), s) != null;
    }
    
    public void loadScene(BaseScene s) {
    	BaseScene prevScene = currentScene;
    	setLoadingScene();
    	if(prevScene != null) {
    		prevScene.disposeScene();
    		ResourcesManager.getInstance().disposeSceneResources(s);
    	}
    	setScene(s);
    }
    
    private void setLoadingScene() {
    	if(loadScene == null)
    		loadScene = new LoadingScene();
    	setScene(loadScene);
    }

    public void setScene(BaseScene scene)
    {
        engine.setScene(scene);
        currentScene = scene;
        currentSceneType = scene.getSceneType();
    }
    
    public void createSplashScene(OnCreateSceneCallback pOnCreateSceneCallback) {
        currentScene = new SplashScene();
        pOnCreateSceneCallback.onCreateSceneFinished(currentScene);
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
}
