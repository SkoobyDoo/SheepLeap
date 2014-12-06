package com.game.sheepleap.entities;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.game.sheepleap.entities.base.PhysicsEntity;
import com.game.sheepleap.scenes.GameScene;

public class GoalEntity extends PhysicsEntity {

	private static final FixtureDef GOAL_FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0, 0, true);
	GoalType mType;
	private static int goalWidth;
	private static int goalHeight;
	
	public enum GoalType {CLOUD, ASTEROID, PILLOW};
	
	public static GoalEntity createGoal(float x, float y, GoalType type, GameScene scene) { //assuming only one size of goal per level
		GoalEntity g=null;
		switch(type) {
		case CLOUD:
			goalWidth = 155;
			goalHeight = 106;
			g = new GoalEntity(x, y, scene.goalCloud_region);
			break;
		case ASTEROID:
			goalWidth = 200;
			goalHeight = 156;
			g = new GoalEntity(x, y, scene.goalAsteroid_region);
			break;
		case PILLOW:
			goalWidth = 400;
			goalHeight = 213;
			g = new GoalEntity(x, y, scene.goalPillow_region);
			break;
		}
		g.mType = type;
		return g;
	}
	
	private GoalEntity(float x, float y, ITiledTextureRegion tex) {
		super(x, y, tex);
	}

	@Override
	protected Body createBody(float x, float y) {
		Body bod;
		bod = PhysicsFactory.createBoxBody(mPhysWorld, mSprite, BodyType.StaticBody, GOAL_FIXTURE_DEF);
		bod.setUserData(this);

		mPhysWorld.registerPhysicsConnector(new PhysicsConnector(mSprite,bod, false, false));

		return bod;
	}

	@Override
	protected Sprite createSprite(float x, float y, ITiledTextureRegion pTiledTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		Debug.e("goalWidth: " + GoalEntity.goalWidth + "\ngoalHeight: " + goalHeight);
		AnimatedSprite s = new AnimatedSprite(x - goalWidth/2, y - goalHeight/2, goalWidth, goalHeight, pTiledTextureRegion, pVertexBufferObjectManager);
		return s;
	}

	@Override
	public void collidedWith(PhysicsEntity other) {
		if(other instanceof SheepEntity)
			mScene.getGoalSheep().add((SheepEntity) other);
	}

	@Override
	public void doneColldingWith(PhysicsEntity other) {
		if(other instanceof SheepEntity)
			mScene.getGoalSheep().remove(other);
	}

}
