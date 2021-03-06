package com.mygdx.game.world;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.Constants;

public class GameMapBodyCreate {

	private PolygonShape shape;
	private BodyDef def;
	private World world;

	public GameMapBodyCreate (World world) {
		this.world = world;
		shape = new PolygonShape();
		shape.setAsBox(Constants.PPM / 2f / Constants.PPM, Constants.PPM/ 2f / Constants.PPM);

		def = new BodyDef();
		def.type = BodyDef.BodyType.StaticBody;
		def.fixedRotation = true;
	}

	public Body create(int x, int y) {
		def.position.set(x, y);
		Body pBody = this.world.createBody(def);
		pBody.createFixture(shape, 0);
		pBody.setAwake(false);
		return pBody;
	}

}
