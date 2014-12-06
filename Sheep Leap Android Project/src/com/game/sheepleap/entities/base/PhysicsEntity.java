package com.game.sheepleap.entities.base;

import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.game.sheepleap.ResourcesManager;
import com.game.sheepleap.scenes.GameScene;

public abstract class PhysicsEntity {
	protected PhysicsWorld mPhysWorld;
	protected GameScene mScene;
	protected ResourcesManager mResourceManager;
	public VertexBufferObjectManager mVertBufObjMan;
	protected Sprite mSprite;

	public Body mBody;

	public PhysicsEntity(float x, float y, ITiledTextureRegion pTiledTextureRegion) {
		mScene = GameScene.get();
		mPhysWorld = mScene.getPhysicsWorld();
		mResourceManager = ResourcesManager.getInstance();
		mVertBufObjMan = mResourceManager.vbom;
		mSprite = createSprite(x, y, pTiledTextureRegion, mVertBufObjMan);
		mBody = createBody(x, y);
		//mScene.attachChild(mSprite);
	}
	
	public PhysicsEntity(float x, float y) {
		mScene = GameScene.get();
		mPhysWorld = mScene.getPhysicsWorld();
		mResourceManager = ResourcesManager.getInstance();
		mVertBufObjMan = mResourceManager.vbom;
		mSprite = null;
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
	
	public void applyForceToCenter(float forcex, float forcey) {
		mBody.applyForce(new Vector2(forcex, forcey), mBody.getPosition());
	}

	public Sprite getSprite() {
		return mSprite;
	}

	public Body getBody() {
		return mBody;
	}

	public void applyForceToCenter(Vector2 force) {
		mBody.applyForce(force, mBody.getPosition());
	}
}
