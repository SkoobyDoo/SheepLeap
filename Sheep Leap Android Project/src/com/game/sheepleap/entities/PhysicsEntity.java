package com.game.sheepleap.entities;

import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.Body;
import com.game.sheepleap.GameScene;
import com.game.sheepleap.ResourcesManager;

public abstract class PhysicsEntity {
	protected PhysicsWorld mPhysWorld;
	protected GameScene mScene;
	protected ResourcesManager mResourceManager;
	VertexBufferObjectManager mVertBufObjMan;
	protected Sprite mSprite;

	protected Body mBody;

	public PhysicsEntity(float x, float y, ITiledTextureRegion pTiledTextureRegion) {
		mScene = GameScene.get();
		mPhysWorld = mScene.getPhysicsWorld();
		mResourceManager = ResourcesManager.getInstance();
		mVertBufObjMan = mResourceManager.vbom;
		mSprite = createSprite(x, y, pTiledTextureRegion, mVertBufObjMan);
		mBody = createBody(x, y);
		//mScene.attachChild(mSprite);
	}
	
	public PhysicsEntity(float x, float y, int width, int height, ITiledTextureRegion pTiledTextureRegion) {
		mScene = GameScene.get();
		mPhysWorld = mScene.getPhysicsWorld();
		mResourceManager = ResourcesManager.getInstance();
		mVertBufObjMan = mResourceManager.vbom;
		mSprite = createSprite(x, y, pTiledTextureRegion, mVertBufObjMan);
		mSprite.setWidth(width);
		mSprite.setHeight(height);
		mBody = createBody(x, y);
		mSprite.setCullingEnabled(true);
		//mScene.attachChild(mSprite);
	}

	protected abstract Body createBody(float x, float y);

	protected abstract Sprite createSprite(float x, float y, ITiledTextureRegion pTiledTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager);

	public abstract void collidedWith(PhysicsEntity other);
	
	public abstract void doneColldingWith(PhysicsEntity other);

	public Sprite getSprite() {
		return mSprite;
	}
}
