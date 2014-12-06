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
import com.game.sheepleap.scenes.GameScene;

public class StartEntity extends PhysicsEntity {

	private static final FixtureDef START_FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0, 0, true);

	public StartEntity(float x, float y, GameScene scene) {
		super(x, y, scene.start_region);
	} 

	@Override
	protected Body createBody(float x, float y) {
		Body bod;
		bod = PhysicsFactory.createBoxBody(mPhysWorld, mSprite, BodyType.StaticBody, START_FIXTURE_DEF);
		bod.setUserData(this);

		mPhysWorld.registerPhysicsConnector(new PhysicsConnector(mSprite, bod, false, false));

		return bod;
	}

	@Override
	protected Sprite createSprite(float x, float y, ITiledTextureRegion pTiledTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		AnimatedSprite s = new AnimatedSprite(x - 15, y - 15, 30, 30, pTiledTextureRegion, pVertexBufferObjectManager);
		return s;
	}

	@Override
	public void collidedWith(PhysicsEntity other) {
		if(other instanceof SheepEntity)
			mScene.getStartSheep().add((SheepEntity) other);
	}

	@Override
	public void doneColldingWith(PhysicsEntity other) {
		if(other instanceof SheepEntity)
			mScene.getStartSheep().remove(other);
	}

}
