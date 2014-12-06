package com.game.sheepleap.entities;

import java.util.HashSet;
import java.util.Set;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.game.sheepleap.entities.base.PhysicsEntity;
import com.game.sheepleap.entities.base.IThoughtfulEntity;
import com.game.sheepleap.scenes.GameScene;

public class BlackHoleEntity extends PhysicsEntity implements IThoughtfulEntity {
	public static final float ROTATION_SPEED = .0008f;
	public static final float FORCE_MULTIPLIER = 100f;

	private static final FixtureDef BLACK_HOLE_FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 1, 0, true);

	PhysicsEntity mSensor;

	float mRange;
	float mForce;

	public BlackHoleEntity(float x, float y, float radius, float range, float force, GameScene scene) {
		super(x, y, scene.blackHole_region);
		mScene.registerThoughtfulEntity(this);
		mRange = range;
		mForce = force;

		mSensor = new BlackHoleGravityWellEntity(x, y, radius, force);

	}

	@Override
	protected Body createBody(float x, float y) {
		Body bod;
		bod = PhysicsFactory.createCircleBody(mPhysWorld, x, y, 30, BodyType.StaticBody, BLACK_HOLE_FIXTURE_DEF);
		bod.setUserData(this);

		mPhysWorld.registerPhysicsConnector(new PhysicsConnector(mSprite, bod, false, false));

		return bod;
	}

	@Override
	protected Sprite createSprite(float x, float y, ITiledTextureRegion pTiledTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		AnimatedSprite s = new AnimatedSprite(x - 200, y - 172, pTiledTextureRegion, pVertexBufferObjectManager);
		return s;
	}

	@Override
	public void collidedWith(PhysicsEntity other) {
		if (other instanceof SheepEntity) ((SheepEntity) other).kill();
	}

	@Override
	public void doneColldingWith(PhysicsEntity other) {
		// do nothing
	}

	@Override
	public void think(float timeElapsed) {
		mSprite.setRotation(mSprite.getRotation() - timeElapsed * 20);
	}

	private class BlackHoleGravityWellEntity extends PhysicsEntity implements IThoughtfulEntity {
		private Set<PhysicsEntity> spaghetti = new HashSet<PhysicsEntity>();
		
		public BlackHoleGravityWellEntity(float x, float y, float radius, float force) {
			super(x, y);
			mScene.registerThoughtfulEntity(this);
		}

		@Override
		protected Body createBody(float x, float y) {
			Body bod = PhysicsFactory.createCircleBody(mPhysWorld, x, y, mRange, BodyType.StaticBody, BLACK_HOLE_FIXTURE_DEF);
			bod.setUserData(this);
			// mPhysWorld.registerPhysicsConnector(new PhysicsConnector(mSprite,
			// bod, false, false));
			return bod;
		}

		@Override
		protected Sprite createSprite(float x, float y, ITiledTextureRegion pTiledTextureRegion,
				VertexBufferObjectManager pVertexBufferObjectManager) {
			// this physentity should never call this
			Debug.e("This should never run.");
			return null;
		}

		@Override
		public void collidedWith(PhysicsEntity other) {
			if (other.getBody().getMass() == 0 || other.getBody().getType() != BodyType.DynamicBody) return;
			spaghetti.add(other);
		}

		@Override
		public void doneColldingWith(PhysicsEntity other) {
			spaghetti.remove(other);
		}

		@Override
		public void think(float timeElapsed) {
			for(PhysicsEntity p : spaghetti) {
				Vector2 here = super.mBody.getPosition();
				Vector2 there = p.getBody().getPosition();
				Vector2 diff = here.sub(there);
				float len2 = diff.len2();
				p.applyForceToCenter(diff.nor().mul(mForce * FORCE_MULTIPLIER / len2));
			}
		}
	}

}
