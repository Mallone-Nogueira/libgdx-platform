package com.mygdx.game.entities;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.Constants;

public class Monster {
	private Body body;
	private Texture img;
	private Sprite sprite;
	private EntityStatus status;

	private Batch batch;
	private World world;
//	private float stateTime = 0;
//	private Animation<Texture> animation;

	public Monster(World world, Batch batch) {
		super();
		this.world = world;
		this.batch = batch;

		img = new Texture("carinha2.jpg");
		sprite = new Sprite(img);

//		animation = new Animation<>(0.1f,img,  new Texture("walk1.png"), img, new Texture("walk2.png"));
//		animation.setPlayMode(PlayMode.LOOP);

		body = createBodyPlayer(1600, 6670, Constants.PPM * 1.5f, Constants.PPM * 2.5f);
	}

	public Body createBodyPlayer(int x, int y, float width, float height) {
		Body pBody;
		BodyDef def = new BodyDef();

		def.type = BodyDef.BodyType.DynamicBody;

		def.position.set(x / Constants.PPM, y / Constants.PPM);
		def.fixedRotation = true;
		def.gravityScale = 5.0f;
		pBody = world.createBody(def);

		PolygonShape shape1 = new PolygonShape();
		shape1.setAsBox(width / 2f / Constants.PPM, height / 2f / Constants.PPM, new Vector2(0, 1.5f), 0);
		CircleShape shape2 = new CircleShape();
		shape2.setRadius(0.3f);
		shape2.setPosition(new Vector2(-0.5f, -0.1f));
		CircleShape shape3 = new CircleShape();
		shape3.setRadius(0.3f);
		shape3.setPosition(new Vector2(0.5f, -0.1f));

		setFixtureThis(pBody.createFixture(shape1, 0.5f));
		setFixtureThis(pBody.createFixture(shape2, 0.5f));
		setFixtureThis(pBody.createFixture(shape3, 0.5f));
		shape1.dispose();
		shape2.dispose();
		shape3.dispose();
		pBody.setUserData(this);
		return pBody;
	}

	public void setFixtureThis(Fixture fix) {
		fix.setUserData(this);
	}

	public void update(float delta, Vector2 positionSeguir) {
		if (positionSeguir.x > getPosition().x) {
			body.setLinearVelocity(10, body.getLinearVelocity().y);
		} else {
			body.setLinearVelocity(-10, body.getLinearVelocity().y);
		}

//		stateTime += delta;
		status = updateStatus();
	}

	private EntityStatus updateStatus() {
		if (body.getLinearVelocity().y > 0) {
			return EntityStatus.JUMP;
		}

		if (body.getLinearVelocity().y < -15) {
			return EntityStatus.FALL;
		}

		if (body.getLinearVelocity().x != 0) {
			return EntityStatus.WALK;
		}

		return EntityStatus.IDLE;
	}

	public void render(OrthographicCamera camera) {
		sprite.setTexture(getStatusTexture());

		sprite.setPosition((body.getPosition().x * Constants.PPM) + (camera.viewportWidth / 2) - camera.position.x - 6,
				(body.getPosition().y * Constants.PPM) + (camera.viewportHeight / 2) - camera.position.y - 3);
		batch.draw(sprite, sprite.getX(), sprite.getY());
	}

	private Texture getStatusTexture() {
		return img;
	}

	public Vector2 getPosition() {
		return body.getPosition();
	}

	public void dispose() {
		img.dispose();
		world.destroyBody(body);
	}
}
