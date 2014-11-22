package com.game.sheepleap.entities;

import java.util.Iterator;

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
import com.game.sheepleap.ResourcesManager;

public class BlackHoleEntity extends PhysicsEntity implements ThoughtfulEntity {
	public static final float ROTATION_SPEED = .0008f;

	private static final FixtureDef BLACK_HOLE_FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 1, 0, true);
	
	float mRange;
	float mForce;

	public BlackHoleEntity(float x, float y, float radius, float range, float force) {
		super(x, y, ResourcesManager.getInstance().blackHole_region);
		mScene.registerThoughtfulEntity(this);
		mRange = range;
		mForce = force;
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
		if(other instanceof SheepEntity)
			((SheepEntity) other).kill();
	}

	@Override
	public void doneColldingWith(PhysicsEntity other) {
		// do nothing
	}

	@Override
	public void think(float timeElapsed) {
		mSprite.setRotation(mSprite.getRotation()-timeElapsed*10);
		Vector2 pos = mBody.getPosition();
		
		Iterator<Body> b = mPhysWorld.getBodies();
		Body i;
		while (b.hasNext()) {
			i=b.next();
			if(i.getMass() == 0 || i.getType() != BodyType.DynamicBody)
				continue;
			Vector2 diff = pos.sub(i.getPosition());
			float sqdiff = diff.len2();
			if(sqdiff < mRange) {
				float ratio = sqdiff / mRange;//percentage of force to apply
				diff = diff.div(diff.len());
				diff = diff.mul((1-ratio) * mForce);
				i.applyForce(diff, i.getPosition());
				Debug.e("Applying force: " + diff.toString());
			}
		}
	}

}
