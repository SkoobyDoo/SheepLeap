package com.game.sheepleap.scenes;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.ZoomCamera;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.BaseGameActivity;

import android.app.Activity;

import com.game.sheepleap.ResourcesManager;
import com.game.sheepleap.ResourcesManager.ITextureBuilder;
import com.game.sheepleap.SceneManager.SceneType;

public abstract class BaseScene extends Scene{
	protected Engine engine;
	protected BaseGameActivity activity;
	protected ResourcesManager resourcesManager;
	
	// every subclass must assign a value to this, not sure how to best enforce that
	// nvm i just made the getter abstract
	protected ITextureBuilder mTextureBuilder;
	
	protected BaseScene() {
		this.resourcesManager = ResourcesManager.getInstance();
        this.engine = resourcesManager.engine;
        this.activity = resourcesManager.activity;
        this.vbom = resourcesManager.vbom;
        this.camera = resourcesManager.camera;
		ResourcesManager.getInstance().loadSceneResourcesS(this);
        createScene();
	}
	
	public Engine getEngine() {
		return engine;
	}

	public Activity getActivity() {
		return activity;
	}

	public ResourcesManager getResourcesManager() {
		return resourcesManager;
	}

	public VertexBufferObjectManager getVbom() {
		return vbom;
	}

	public ZoomCamera getCamera() {
		return camera;
	}

	protected VertexBufferObjectManager vbom;
    protected ZoomCamera camera;
    
    public abstract ITextureBuilder getTextureBuilder();
    
    public abstract void createScene();
    
    public abstract void onBackKeyPressed();
    
    public abstract SceneType getSceneType();
    
    // should call ResourcesManager.unloadSceneResources(this)
    public abstract void disposeScene();
}
