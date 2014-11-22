package com.game.sheepleap.entities;

import java.util.Collection;
import java.util.LinkedList;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.game.sheepleap.ResourcesManager;

public class WindEntity extends PhysicsEntity implements ThoughtfulEntity {
	private static final FixtureDef WIND_FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0, 0, true);
	
	private static final float WIND_FORCE_CONSTANT = 20f;
	
	public float mForce;
	
	Collection<PhysicsEntity> touchedThings = new LinkedList<PhysicsEntity>();

	public WindEntity(float x, float y, int width, int height, float force) {
		super(x - width / 2.0f, y - height / 2.0f, width, height, ResourcesManager.getInstance().wind_region);
		mForce = force * WIND_FORCE_CONSTANT;
		mScene.registerThoughtfulEntity(this);
	}

	@Override
	protected Body createBody(float x, float y) {
		final Body body = PhysicsFactory.createBoxBody(mPhysWorld, mSprite, BodyType.StaticBody, WIND_FIXTURE_DEF);
		body.setUserData(this);
		mPhysWorld.registerPhysicsConnector(new PhysicsConnector(mSprite, body, false, false));
		if (mForce < 0) {
			mSprite.setFlippedHorizontal(true);
		}
		return body;
	}

	@Override
	protected Sprite createSprite(float x, float y, ITiledTextureRegion pTiledTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		AnimatedSprite s = new AnimatedSprite(x, y, 1024, 137, pTiledTextureRegion, mVertBufObjMan);
		s.animate(new long[] { 500, 500, 500, 500, 500 }, 0, 4, true);
		return s;
	}

	@Override
	public void collidedWith(PhysicsEntity other) {
		touchedThings.add(other);
	}

	@Override
	public void doneColldingWith(PhysicsEntity other) {
		touchedThings.remove(other);
	}

	@Override
	public void think(float timeElapsed) {
		for(PhysicsEntity p : touchedThings)
			p.mBody.applyForceToCenter(mForce, 0);
	}
	
	

}
