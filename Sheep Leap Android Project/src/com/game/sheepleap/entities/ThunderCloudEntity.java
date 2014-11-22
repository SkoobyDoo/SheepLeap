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
import com.game.sheepleap.ResourcesManager;

public class ThunderCloudEntity extends PhysicsEntity {

	private static final FixtureDef THUNDER_CLOUD_FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 1, 0, true);

	public ThunderCloudEntity(float x, float y) {
		super(x, y, ResourcesManager.getInstance().cloudThunder_region);
	}

	@Override
	protected Body createBody(float x, float y) {
		Body bod;
		bod = PhysicsFactory.createBoxBody(mPhysWorld, x, y, 314 * .9f, 160 * .9f, BodyType.StaticBody, THUNDER_CLOUD_FIXTURE_DEF);
		bod.setUserData(this);

		mPhysWorld.registerPhysicsConnector(new PhysicsConnector(mSprite, bod, false, false));

		return bod;
	}

	@Override
	protected Sprite createSprite(float x, float y, ITiledTextureRegion pTiledTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		AnimatedSprite s = new AnimatedSprite(x - 157, y - 80, pTiledTextureRegion, pVertexBufferObjectManager);
		return s;
	}

	@Override
	public void collidedWith(PhysicsEntity other) {
		if(other instanceof SheepEntity)
			((SheepEntity) other).electrocute();
	}

	@Override
	public void doneColldingWith(PhysicsEntity other) {
		// do nothing
	}

}
