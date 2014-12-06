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
import com.game.sheepleap.entities.base.PhysicsEntity;
import com.game.sheepleap.entities.base.IThoughtfulEntity;
import com.game.sheepleap.scenes.GameScene;

public class BirdEntity extends PhysicsEntity implements IThoughtfulEntity {

	private static final FixtureDef BIRD_FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 1, 0, true);
	private static final float BIRD_X_SPEED = 3.0f;

	enum Direction {
		LEFT, RIGHT
	};

	Direction mDir;

	float leftBound, rightBound;

	public BirdEntity(float x, float y, int left, int right, boolean goingLeft, GameScene scene) {
		super(x, y, scene.bird_region);
		mDir = (goingLeft) ? Direction.LEFT : Direction.RIGHT;
		leftBound = left / 32f;
		rightBound = right / 32f;
		mScene.registerThoughtfulEntity(this);
	}

	@Override
	protected Body createBody(float x, float y) {
		Body bod;
		bod = PhysicsFactory.createBoxBody(mPhysWorld, mSprite, BodyType.KinematicBody, BIRD_FIXTURE_DEF);
		bod.setUserData(this);

		mPhysWorld.registerPhysicsConnector(new PhysicsConnector(mSprite, bod, true, false));

		return bod;
	}

	@Override
	protected Sprite createSprite(float x, float y, ITiledTextureRegion pTiledTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		AnimatedSprite s = new AnimatedSprite(x - 32, y - 28, 65, 57, pTiledTextureRegion, pVertexBufferObjectManager);
		s.animate(new long[] { 300, 300, 300 }, 0, 2, true);
		return s;
	}

	@Override
	public void collidedWith(PhysicsEntity other) {
		if(other instanceof SheepEntity)
			((SheepEntity) other).kill();
	}

	@Override
	public void doneColldingWith(PhysicsEntity other) {
		// do nothing
	}

	@Override
	public void think(float timeElapsed) {
		float x = mBody.getPosition().x;
		if (x <= leftBound) {
			mDir = Direction.RIGHT;
		} else if (x >= rightBound) {
			mDir = Direction.LEFT;
		}

		setDir();
	}

	public void setDir() {
		if (mDir == Direction.LEFT) {
			mSprite.setFlippedHorizontal(false);
			mBody.setLinearVelocity(-BIRD_X_SPEED, 0);
		} else {
			mSprite.setFlippedHorizontal(true);
			mBody.setLinearVelocity(BIRD_X_SPEED, 0);
		}
	}

}
