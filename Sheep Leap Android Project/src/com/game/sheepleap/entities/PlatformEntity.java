package com.game.sheepleap.entities;

import java.util.Collection;
import java.util.HashSet;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.game.sheepleap.ResourcesManager;

public class PlatformEntity extends PhysicsEntity {

	private static final FixtureDef PLATFORM_FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0, 0.5f, false);

	public enum PlatformType {TREE, CLOUD, ASTEROID};
	public enum PlatformSize {SMALL, MEDIUM, LARGE};
	
	boolean mSticky;
	
	private static int platformWidth;
	private static int platformHeight;
	
	private Collection<SheepEntity> connected = new HashSet<SheepEntity>();
	
	public static PlatformEntity createPlatform(float x, float y, PlatformType type, PlatformSize size, boolean sticky) {
		PlatformEntity g=null;
		switch(type) {
		case TREE:
			switch(size) {
			case SMALL:
				platformWidth = 151;
				platformHeight = 89;
				g = new PlatformEntity(x-75, y-44, ResourcesManager.getInstance().treeSmall_region);
				break;
			case MEDIUM:
				platformWidth = 270;
				platformHeight = 143;
				g = new PlatformEntity(x-130, y-71, ResourcesManager.getInstance().treeMedium_region);
				break;
			case LARGE:
				platformWidth = 349;
				platformHeight = 168;
				g = new PlatformEntity(x-174, y-89, ResourcesManager.getInstance().treeLarge_region);
				break;
			}
			break;
		case CLOUD:
			switch(size) {
			case SMALL:
				platformWidth = 151;
				platformHeight = 89;
				g = new PlatformEntity(x-75, y-44, ResourcesManager.getInstance().cloudSmall_region);
				break;
			case MEDIUM:
				platformWidth = 270;
				platformHeight = 143;
				g = new PlatformEntity(x-130, y-71, ResourcesManager.getInstance().cloudMedium_region);
				break;
			case LARGE:
				platformWidth = 349;
				platformHeight = 168;
				g = new PlatformEntity(x-174, y-89, ResourcesManager.getInstance().cloudLarge_region);
				break;
			}
			break;
		case ASTEROID:
			switch(size) {
			case SMALL:
				platformWidth = 208;
				platformHeight = 179;
				g = new PlatformEntity(x-104, y-89, ResourcesManager.getInstance().asteroidSmall_region);
				break;
			case MEDIUM:
				platformWidth = 345;
				platformHeight = 298;
				g = new PlatformEntity(x-172, y-149, ResourcesManager.getInstance().asteroidMedium_region);
				break;
			case LARGE:
				platformWidth = 400;
				platformHeight = 287;
				g = new PlatformEntity(x-200, y-143, ResourcesManager.getInstance().asteroidLarge_region);
				break;
			}
			break;
		}
		g.mSticky = sticky;
		return g;
	}
	
	private PlatformEntity(float x, float y, ITiledTextureRegion tex) {
		super(x, y, tex);
	}

	@Override
	protected Body createBody(float x, float y) {
		Body bod;
		bod = PhysicsFactory.createBoxBody(mPhysWorld, x + platformWidth/2, y + platformHeight/2, platformWidth * .9f, platformHeight * .9f, BodyType.StaticBody, PLATFORM_FIXTURE_DEF);
		bod.setUserData(this);

		mPhysWorld.registerPhysicsConnector(new PhysicsConnector(mSprite,bod, false, false));

		return bod;
	}
	
	public void connectTo(SheepEntity other) {
		final RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
		revoluteJointDef.collideConnected = true;
		revoluteJointDef.initialize(mBody, other.mBody, mBody.getWorldCenter());
		mPhysWorld.createJoint(revoluteJointDef);
		connected.add(other);
	}

	@Override
	protected Sprite createSprite(float x, float y, ITiledTextureRegion pTiledTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		AnimatedSprite s = new AnimatedSprite(x, y, platformWidth, platformHeight, pTiledTextureRegion, pVertexBufferObjectManager);
		return s;
	}

	@Override
	public void collidedWith(PhysicsEntity other) {
		if(!mSticky)
			return;
		if(other instanceof SheepEntity && !connected.contains(other))
			connectTo((SheepEntity) other);
	}

	@Override
	public void doneColldingWith(PhysicsEntity other) {
		// do nothing
	}

}
