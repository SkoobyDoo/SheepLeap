package com.game.sheepleap.entities;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.game.sheepleap.GameSettings;

public class TimedLifeDynamicEntity extends PhysicsEntity implements ThoughtfulEntity {
	private static final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0, 0.2f, false, GameSettings.CATEGORYBIT_BODYPART,
			GameSettings.MASKBITS_BODYPART, (short) 0);

	public float mDeathTime;

	public TimedLifeDynamicEntity(float x, float y, float duration, ITiledTextureRegion skin) {
		super(x, y, skin);
		mScene.attachChild(mSprite);
		mScene.registerThoughtfulEntity(this);
		mDeathTime = duration;
	}

	@Override
	protected Body createBody(float x, float y) {
		final Body body = PhysicsFactory.createBoxBody(mPhysWorld, mSprite, BodyType.DynamicBody, FIXTURE_DEF);
		body.setUserData(this);
		mPhysWorld.registerPhysicsConnector(new PhysicsConnector(mSprite, body));
		return body;
	}

	@Override
	protected Sprite createSprite(float x, float y, ITiledTextureRegion pTiledTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		AnimatedSprite s = new AnimatedSprite(x, y, pTiledTextureRegion, mVertBufObjMan);
		return s;
	}

	@Override
	public void collidedWith(PhysicsEntity other) {
		if (other instanceof SheepEntity) ((SheepEntity) other).scare(3f);
	}

	@Override
	public void doneColldingWith(PhysicsEntity other) {
	}

	@Override
	public void think(float timeElapsed) {
		mDeathTime -= timeElapsed;
		if (mDeathTime <= 0) {
			final PhysicsConnector physicsConnector = this.mPhysWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(mSprite);
			if (physicsConnector != null) {
				mPhysWorld.unregisterPhysicsConnector(physicsConnector);
				mBody.setActive(false);
				mPhysWorld.destroyBody(mBody);
				mScene.detachChild(mSprite);
			}
		}
	}

}
