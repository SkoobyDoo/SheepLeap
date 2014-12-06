package com.game.sheepleap.entities;

import java.util.Collection;
import java.util.HashSet;

import org.andengine.entity.primitive.Rectangle;
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
import com.game.sheepleap.GameSettings;
import com.game.sheepleap.entities.base.PhysicsEntity;
import com.game.sheepleap.scenes.GameScene;

public class PlatformEntity extends PhysicsEntity {

	private static final FixtureDef PLATFORM_FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0, 0.5f, false);

	public enum PlatformType {TREE, CLOUD, ASTEROID};
	public enum PlatformSize {SMALL, MEDIUM, LARGE};
	
	boolean mSticky;
	
	private static int platformWidth;
	private static int platformHeight;
	
	private Collection<SheepEntity> connected = new HashSet<SheepEntity>();
	
	public static PlatformEntity createPlatform(float x, float y, PlatformType type, PlatformSize size, boolean sticky, GameScene scene) {
		// TODO: THE MAGIC NUMBERS! THEY BURN!!!
		PlatformEntity g=null;
		float trunkWidth =0f;
		switch(type) {
		case TREE:
			switch(size) {
			case SMALL:
				platformWidth = 151;
				platformHeight = 89;
				g = new PlatformEntity(x-75, y-44, scene.treeSmall_region);
				trunkWidth = g.mSprite.getWidth()/10 + Math.abs(g.mSprite.getY()/10) * 0.6f;
				break;
			case MEDIUM:
				platformWidth = 270;
				platformHeight = 143;
				g = new PlatformEntity(x-130, y-71, scene.treeMedium_region);
				trunkWidth = g.mSprite.getWidth()/10 + Math.abs(g.mSprite.getY()/10) * 0.8f;
				break;
			case LARGE:
				platformWidth = 349;
				platformHeight = 168;
				g = new PlatformEntity(x-174, y-89, scene.treeLarge_region);
				trunkWidth = g.mSprite.getWidth()/10 + Math.abs(g.mSprite.getY()/10);
				break;
			}
			Rectangle trunkSquare = new Rectangle(g.mSprite.getWidth()/2 - trunkWidth/2, 
					-g.mSprite.getY() + g.mSprite.getHeight()/2,
					trunkWidth,
					g.mSprite.getY(),
					scene.getVbom());
			trunkSquare.setColor(.6f + (GameSettings.random.nextFloat()-0.5f)*0.1f,
					.2f + (GameSettings.random.nextFloat()-0.5f)*0.1f,
					.2f + (GameSettings.random.nextFloat()-0.5f)*0.1f);
			trunkSquare.setZIndex(-1);
			g.mSprite.sortChildren();
			g.mSprite.attachChild(trunkSquare);
			break;
		case CLOUD:
			switch(size) {
			case SMALL:
				platformWidth = 151;
				platformHeight = 89;
				g = new PlatformEntity(x-75, y-44, scene.cloudSmall_region);
				break;
			case MEDIUM:
				platformWidth = 270;
				platformHeight = 143;
				g = new PlatformEntity(x-130, y-71, scene.cloudMedium_region);
				break;
			case LARGE:
				platformWidth = 349;
				platformHeight = 168;
				g = new PlatformEntity(x-174, y-89, scene.cloudLarge_region);
				break;
			}
			break;
		case ASTEROID:
			switch(size) {
			case SMALL:
				platformWidth = 208;
				platformHeight = 179;
				g = new PlatformEntity(x-104, y-89, scene.asteroidSmall_region);
				break;
			case MEDIUM:
				platformWidth = 345;
				platformHeight = 298;
				g = new PlatformEntity(x-172, y-149, scene.asteroidMedium_region);
				break;
			case LARGE:
				platformWidth = 400;
				platformHeight = 287;
				g = new PlatformEntity(x-200, y-143, scene.asteroidLarge_region);
				break;
			}
			break;
		}
		g.mSticky = sticky;
		return g;
	}
	
	private PlatformEntity(float x, float y, ITiledTextureRegion tex) {
		super(x, y, tex);
		// setting zindex based on height. Attempting to quantize into 10 different values to prevent this from being all over the board.
		// assuming a rough world height of 1000, at the very least this should cover most if not all of levels.
		// this is a magic number that basically assigns the range from 130-139 to trees based roughly on height within a level.
		// (roughly because levels taller than 1000 get fuckt)
		final int zBaseValue = 130;
		mSprite.setZIndex((int) (zBaseValue + (y/1000 * 10)));
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
