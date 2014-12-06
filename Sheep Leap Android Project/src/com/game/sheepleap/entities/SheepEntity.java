package com.game.sheepleap.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.andengine.audio.sound.Sound;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.game.sheepleap.GameSettings;
import com.game.sheepleap.ResourcesManager.BleatType;
import com.game.sheepleap.entities.base.PhysicsEntity;
import com.game.sheepleap.entities.base.IThoughtfulEntity;
import com.game.sheepleap.scenes.GameScene;

public class SheepEntity extends PhysicsEntity implements IThoughtfulEntity {
	private static final FixtureDef SHEEP_FIXTURE_DEF = PhysicsFactory.createFixtureDef(2f, 0, 1f);
	/*
	 * private static final FixtureDef SHEEP_FIXTURE_DEF =
	 * PhysicsFactory.createFixtureDef(2f, 0, 1, false,
	 * GameSettings.CATEGORYBIT_SHEEP, GameSettings.MASKBITS_SHEEP, (short) 0);
	 * private static final FixtureDef FIXTURE_DEF_BODY_PART =
	 * PhysicsFactory.createFixtureDef(1, 0, 0.2f, false,
	 * GameSettings.CATEGORYBIT_BODYPART, GameSettings.MASKBITS_BODYPART,
	 * (short) 0);
	 */

	private Set<SheepEntity> connectedSheep = new HashSet<SheepEntity>();
	private Collection<Joint> sheepJoints = new HashSet<Joint>();

	private float mScaredTime = 0;

	private boolean mIsElectrocuted = false;

	private float mTimeElectrocuted = 0f;

	public SheepEntity(float pX, float pY, GameScene scene) {
		super(pX, pY, scene.sheep_region);
		mScene.attachChild(mSprite);
		mScene.registerThoughtfulEntity(this);
		
		Sound standardSound = mResourceManager.randomTypedSheepBleat(BleatType.STANDARD);
		standardSound.play();
	}

	@Override
	protected Body createBody(float x, float y) {
		Body bod;
		bod = PhysicsFactory.createBoxBody(mPhysWorld, x + 80 / 2, y + 80 / 2, 80, 80, BodyType.DynamicBody, SHEEP_FIXTURE_DEF);
		bod.setUserData(this);

		mPhysWorld.registerPhysicsConnector(new PhysicsConnector(mSprite, bod, true, true));

		return bod;
	}

	@Override
	protected Sprite createSprite(float pX, float pY, ITiledTextureRegion pTiledTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		AnimatedSprite s = new AnimatedSprite(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager);
		s.animate(new long[] { 1200, 300 }, 0, 1, true);
		return s;
	}

	public void connectTo(SheepEntity other) {
		connectedSheep.add(other);

		final RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
		revoluteJointDef.collideConnected = false;
		revoluteJointDef.initialize(mBody, other.mBody, mBody.getWorldCenter());
		sheepJoints.add(mPhysWorld.createJoint(revoluteJointDef));
	}

	public Joint getJointTo(SheepEntity other) {
		for (Joint j : sheepJoints) {
			if (j.getBodyB() == other.mBody) return j;
		}
		return null;
	}

	public boolean disconnectFrom(SheepEntity other) {
		// returns false if it wasn't already connected
		return (connectedSheep.remove(other));
	}

	public boolean isConnectedTo(SheepEntity other) {
		// returns true if this sheep is connected to ANY sheep in the goal
		// through any depth of chain
		return getDeepConnections().contains(other);
	}

	public Set<SheepEntity> getDeepConnections() {
		// returns every sheep this sheep is connected to through any depth
		// beware loops! This is not the most efficient method but it should
		// avoid loops
		Set<SheepEntity> encountered = new HashSet<SheepEntity>();
		encountered.add(this);
		Set<SheepEntity> frontier = new HashSet<SheepEntity>();
		frontier.addAll(connectedSheep);
		while (!frontier.isEmpty()) {
			SheepEntity temp = frontier.iterator().next();
			// this grabs one sheep without locking frontier for modification
			encountered.add(temp);
			// we encountered this sheep
			Set<SheepEntity> templist = new HashSet<SheepEntity>(temp.connectedSheep);
			// clone the new sheeps connected set. We're about to modify it
			templist.removeAll(encountered);
			// we dont want to reconsider anything we've already encountered
			frontier.addAll(templist);
			// add all the newly encountered sheep to the frontier
			frontier.remove(temp); // no longer need to deal with temp.
		}
		// frontier is empty, encountered contains every sheep this sheep was
		// connected to through any level of proxy
		return encountered;
	}

	public void kill() {
		kill(false);
	}

	public void kill(boolean stealthy) {
		Sound deathSound = mResourceManager.randomTypedSheepBleat(BleatType.DEATH);
		deathSound.play();
		
		final PhysicsConnector physicsConnector = this.mPhysWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(mSprite);
		if (physicsConnector != null) {
			mPhysWorld.unregisterPhysicsConnector(physicsConnector);
			mBody.setActive(false);
			mPhysWorld.destroyBody(mBody);
			mScene.detachChild(mSprite);
			for (SheepEntity s : connectedSheep) {
				s.disconnectFrom(this);
			}
		}
		connectedSheep.clear();

		if (!stealthy) makeBodyParts();
	}

	public void scare(float duration) {
		Sound nervousSound = mResourceManager.randomTypedSheepBleat(BleatType.NERVOUS);
		nervousSound.play();
		if (mScaredTime < duration) mScaredTime = duration;
	}

	public void makeBodyParts() {
		final float sheepX = mSprite.getX();
		final float sheepY = mSprite.getY();

		final TimedLifeDynamicEntity sheepHead;
		final TimedLifeDynamicEntity sheepTorso;
		final TimedLifeDynamicEntity sheepLeftLeg1;
		final TimedLifeDynamicEntity sheepLeftLeg2;
		final TimedLifeDynamicEntity sheepRightLeg1;
		final TimedLifeDynamicEntity sheepRightLeg2;

		if (!GameSettings.hippieMode) {
			sheepHead = new TimedLifeDynamicEntity(sheepX, sheepY, 5f, mScene.sheepHead);
			sheepTorso = new TimedLifeDynamicEntity(sheepX, sheepY, 5f, mScene.sheepTorso);
			sheepLeftLeg1 = new TimedLifeDynamicEntity(sheepX, sheepY, 5f, mScene.sheepLeftLeg);
			sheepLeftLeg2 = new TimedLifeDynamicEntity(sheepX, sheepY, 5f, mScene.sheepLeftLeg);
			sheepRightLeg1 = new TimedLifeDynamicEntity(sheepX, sheepY, 5f, mScene.sheepRightLeg);
			sheepRightLeg2 = new TimedLifeDynamicEntity(sheepX, sheepY, 5f, mScene.sheepRightLeg);
		} else {
			sheepHead = new TimedLifeDynamicEntity(sheepX, sheepY, 5f, mScene.flower0);
			sheepTorso = new TimedLifeDynamicEntity(sheepX, sheepY, 5f, mScene.flower1);
			sheepLeftLeg1 = new TimedLifeDynamicEntity(sheepX, sheepY, 5f, mScene.flower2);
			sheepLeftLeg2 = new TimedLifeDynamicEntity(sheepX, sheepY, 5f, mScene.flower3);
			sheepRightLeg1 = new TimedLifeDynamicEntity(sheepX, sheepY, 5f, mScene.flower4);
			sheepRightLeg2 = new TimedLifeDynamicEntity(sheepX, sheepY, 5f, mScene.flower5);
		}

		ArrayList<TimedLifeDynamicEntity> bodyPartsList = new ArrayList<TimedLifeDynamicEntity>(6);

		bodyPartsList.add(sheepHead);
		bodyPartsList.add(sheepTorso);
		bodyPartsList.add(sheepLeftLeg1);
		bodyPartsList.add(sheepLeftLeg2);
		bodyPartsList.add(sheepRightLeg1);
		bodyPartsList.add(sheepRightLeg2);

		for (TimedLifeDynamicEntity s : bodyPartsList) {
			s.applyForceToCenter(((GameSettings.random.nextFloat() * (GameSettings.random.nextInt(20)-10))) * 40f,
					((GameSettings.random.nextFloat() * (GameSettings.random.nextInt(20)-10))) * 40f);
			s.mBody.applyTorque(GameSettings.random.nextFloat() * 40f - 20);
		}

	}

	public void electrocute() {
		if (!mIsElectrocuted) {
			mIsElectrocuted = true;
			((AnimatedSprite) mSprite).animate(new long[] { 1, 400 }, 1, 2, false);
		}
	}

	@Override
	public void collidedWith(PhysicsEntity other) {
		if (other instanceof SheepEntity) if (!connectedSheep.contains(other)) connectTo((SheepEntity) other);
	}

	@Override
	public void doneColldingWith(PhysicsEntity other) {
		// do nothing
	}

	@Override
	public void think(float timeElapsed) {
		mScaredTime -= timeElapsed;
		if (mIsElectrocuted) {
			mTimeElectrocuted += timeElapsed;
			if (mTimeElectrocuted > 1.5f)
				mScene.toRemove.add(this);
			else if (!((AnimatedSprite) mSprite).isAnimationRunning()) {
				((AnimatedSprite) mSprite).animate(new long[] { 75, 75 }, 1, 2, true);
			}
		} else if (mScaredTime <= 0)
			((AnimatedSprite) mSprite).animate(new long[] { 1200, 300 }, 0, 1, true);
		else ((AnimatedSprite) mSprite).setCurrentTileIndex(3);
	}

}
