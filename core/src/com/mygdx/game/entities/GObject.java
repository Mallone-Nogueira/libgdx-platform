package com.mygdx.game.entities;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import component.GAnimation;
import enemies.Enemy;
import enemies.Turtle;
import mariobros.MarioBros;
import sprites.enums.ObjectState;
import sprites.utils.GSprite;
import sprites.utils.GTexture;
import utils.GScreen;
import utils.SaveFileObject;

public class GObject extends Sprite {

    public enum Type {
        PLAYER, ENEMY, NPC, ITEM;
    }

    private static final int WIDHT_HEIGHT = 64;

    private ObjectState currentState;
    private ObjectState previousState;
    private ObjectState defaultState;

    private World world;
    private Body b2body;

    private Map<ObjectState, GAnimation<TextureRegion>> animations;

    private float stateTimer;
    private boolean runningRight;
    private boolean marioIsDead;

    private boolean hit;
    private float hitAtualTimer;
    private float hitTimer;

    private boolean attack;
    private float attackAtualTimer;
    private float attackTimer;

    private boolean roll;
    private float rollAtualTimer;
    private float rollTimer;

    public boolean action;

    private String name;

    public Type type;

    private float alfa;

    private float widht, height;
    private float jumpForce;

    float speedWalk;
    float speedRun;

    private GAnimation<TextureRegion> itemAnimation;
    private GObject item;

    public boolean invisibleWhenOtherFrames;

    private Consumer<GObject> consumer;

    public GObject(World world) {
        init(world);
    }

    public GObject(World world, String name) {
        this(world);

        setName(name);
    }

    public GObject(World word, String name, float x, float y) {
        this(word, name);

        setPosition(x, y);
    }

    public GObject(World word, String name, float x, float y, float width, float height) {
        this(word, name, x, y);

        setBounds(0, 0, width / MarioBros.PPM, height / MarioBros.PPM);
    }

    private void init(World world) {
        this.world = world;
        currentState = ObjectState.STAND;
        previousState = ObjectState.STAND;
        stateTimer = 0;
        runningRight = true;

        hitAtualTimer = 0;
        hitTimer = 0.1f;

        attackAtualTimer = 0;
        attackTimer = 50f;

        rollAtualTimer = 0;
        rollTimer = 50f;

        animations = new EnumMap<>(ObjectState.class);

        // define player in Box2d
        defineObject();

        jumpForce = 6.0f;
        widht = WIDHT_HEIGHT;
        height = WIDHT_HEIGHT;
        setBounds(0, 0, widht / MarioBros.PPM, height / MarioBros.PPM);
    }

    private GAnimation<TextureRegion> buildAnimation(String name, float frameDuration,
            Object... textureRegions) {
        if (textureRegions != null) {
            Array<TextureRegion> textures = new Array<>();
            // Arrays.asList(textureRegions).forEach(object -> textures.add((TextureRegion)
            // object));
            for (Object object : textureRegions) {
                TextureRegion textureRegion = new TextureRegion((TextureRegion) object);
                textures.add(textureRegion);
            }

            return new GAnimation<>(frameDuration, textures, name);
        }

        return null;
    }

    private GAnimation<TextureRegion> buildAnimation(String name, Object... textureRegions) {
        return buildAnimation(name, 0.1f, textureRegions);
    }

    public void setAnimation(ObjectState state, Object... textureRegions) {
        setAnimation(state.name(), state, textureRegions);
    }

    public void setAnimation(String name, ObjectState state, Object... textureRegions) {
        if (animations.get(state) != null) {
            animations.replace(state, buildAnimation(name, textureRegions));
        } else {
            animations.put(state, buildAnimation(name, textureRegions));
        }
    }

    public void setAnimation(ObjectState state, float frameDuration, Object... textureRegions) {
        setAnimation(state.name(), state, frameDuration, textureRegions);
    }

    public void setAnimation(String name, ObjectState state, float frameDuration,
            Object... textureRegions) {
        if (animations.get(state) != null) {
            animations.replace(state, buildAnimation(name, frameDuration, textureRegions));
        } else {
            animations.put(state, buildAnimation(name, frameDuration, textureRegions));
        }
    }

    public void setAnimation(String name, ObjectState state, List<GSprite> list) {
        setAnimation(name, state, list.toArray());
    }

    public void setAnimation(ObjectState state, List<GSprite> list) {
        setAnimation(state.name(), state, list.toArray());
    }

    public void setAnimation(String name, ObjectState state, float frameDuration,
            List<GSprite> list) {
        setAnimation(name, state, frameDuration, true, list);
    }

    public void setAnimation(ObjectState state, float frameDuration, List<GSprite> list) {
        setAnimation(state.name(), state, frameDuration, list);
    }

    public void setAnimation(String name, ObjectState state, float frameDuration, boolean loop,
            List<GSprite> list) {
        setAnimation(name, state, frameDuration, list.toArray());

        getAnimation(state).setLooping(loop);
    }

    public void setAnimation(ObjectState state, float frameDuration, boolean loop,
            List<GSprite> list) {
        setAnimation(state.name(), state, frameDuration, loop, list);
    }

    public GAnimation<TextureRegion> getAnimation(ObjectState state) {
        return animations.get(state);
    }

    public void removeAnimation(ObjectState state) {
        animations.remove(state);
    }

    @Deprecated
    public boolean collided(Vector2 vec) {
        return (vec.x >= getX() && vec.x <= (getX() + (getWidth() / MarioBros.PPM)))
                && (vec.y >= getY() && vec.y <= (getY() + (getHeight() / MarioBros.PPM)));
    }

    public boolean collided(GObject object) {
        return object.getXWidht() >= getX() && getXWidht() >= object.getX()
                && object.getYHeight() >= getY() && getYHeight() >= object.getY();
    }

    private float getXWidht() {
        return getX() + getWidth();
    }

    private float getYHeight() {
        return getY() + getHeight();
    }

    public void update(float dt) {
        setSpritePosition(b2body.getPosition().x - getWidth() / 2,
                b2body.getPosition().y - getHeight() / 2);

        TextureRegion frame = getFrame(dt);
        if (frame != null) {
            if (alfa == 0) {
                setAlpha(1);
            }

            setRegion(frame);
            // setBounds(frame);

            // setBounds(getX() - GScreen.divPPM(frame.getRegionWidth()) / 2, getY() -
            // GScreen.divPPM(frame.getRegionHeight())/ 2, frame.getRegionWidth() / MarioBros.PPM,
            // frame.getRegionHeight()/ MarioBros.PPM);
        } else {
            if (defaultState != null) {

            }

            if (invisibleWhenOtherFrames) {
                Texture texture2 = getTexture();
                if (texture2 != null) {
                    setAlpha(0);
                }
            }
        }

        if (hit) {
            hitAtualTimer++;

            if (hitAtualTimer > hitTimer) {
                hitAtualTimer = 0;
                hit = false;
            }
        }

        if (attack) {
            attackAtualTimer++;

            if (attackAtualTimer > attackTimer) {
                attackAtualTimer = 0;
                attack = false;

                if (itemAnimation != null) {
                    itemAnimation.getKeyFrame(dt);
                }
                if (item != null) {
                    item.dispose();
                    item = null;
                }
            }
        }

        if (roll) {
            rollAtualTimer++;

            if (rollAtualTimer > rollTimer) {
                rollAtualTimer = 0;
                roll = false;
            }
        }
        
        if (action) {
            
        }

        if (consumer != null) {
            consumer.accept(this);
        }
    }

    @Override
    public void setAlpha(float alfa) {
        this.alfa = alfa;
        super.setAlpha(alfa);
    }

    public TextureRegion getTextureAnimation(ObjectState state) {
        GAnimation<TextureRegion> animations2 = getAnimation(state);

        if (animations2 != null) {
            return animations2.getKeyFrame(stateTimer, animations2.isLooping());
        }

        return null;
    }

    public TextureRegion getFrame(float dt) {
        currentState = getState();

        TextureRegion region = getTextureRegion();

        if (region != null) {
            if ((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()) {
                region.flip(true, false);
                runningRight = false;
            }

            else if ((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()) {
                region.flip(true, false);
                runningRight = true;
            }
        }

        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        // update previous state
        previousState = currentState;
        // return our final adjusted frame
        return region;

    }

    private TextureRegion getTextureRegion() {
        return currentState != null ? getTextureAnimation(currentState)
                : getTextureAnimation(ObjectState.STAND);
    }

    public ObjectState getState() {
        if (marioIsDead)
            return ObjectState.DEAD;
        if (hit) {
            return ObjectState.HIT;
        } else if (attack) {
            return ObjectState.ATTACK;
        } else if ((b2body.getLinearVelocity().y != 0 && currentState == ObjectState.CLIMB)) {
            return ObjectState.CLIMB;
        } else if (roll) {
            return ObjectState.ROLL;
        } else if ((b2body.getLinearVelocity().y > 0
                && currentState == ObjectState.JUMP))
            return ObjectState.JUMP;
        else if (b2body.getLinearVelocity().y < -(getHeight()))
            return ObjectState.FALL;
        else if (b2body.getLinearVelocity().x != 0)
            return ObjectState.RUN;
        else if (action) {
            return ObjectState.ACTION_1;
        } else
            return ObjectState.STAND;
    }

    public void die() {
        if (!isDead()) {
            marioIsDead = true;
            Filter filter = new Filter();
            filter.maskBits = MarioBros.NOTHING_BIT;

            for (Fixture fixture : b2body.getFixtureList()) {
                fixture.setFilterData(filter);
            }

            b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
        }
    }

    public void simpleDie() {
        if (!isDead()) {
            marioIsDead = true;
            b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
        }
    }

    public void hit() {
        if (!hit) {
            hit = true;
            // b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
        }
    }

    public void attack() {
        if (!attack) {
            attack = true;

            if (this instanceof Player) {
                GTexture gTexture = new GTexture("prehistoric/items/1.png");
                item = new GObject(getWorld(), "aa", getX(), getY());
                item.setRegion(gTexture);
            }
        }
    }

    public void roll() {
        if (!roll) {
            roll = true;

            float force = 5.0f;

            TextureRegion region;
            region = getTextureRegion();
            if (region != null) {
                boolean flipX = region.isFlipX();

                if (flipX) {
                    force = force * -1;
                }
            }

            // b2body.applyLinearImpulse(new Vector2(force, 0), b2body.getWorldCenter(),
            // true);
        }
    }

    public void action1() {
        if (!action) {
            action = true;
        }
    }

    @Override
    public float getX() {
        if (b2body != null) {
            return b2body.getPosition().x;
        }
        return super.getX();
    }

    @Override
    public float getY() {
        if (b2body != null) {
            return b2body.getPosition().y;
        }
        return super.getY();
    }

    public boolean gameOver() {
        return currentState == ObjectState.DEAD && getStateTimer() > 3;
    }

    public boolean isDead() {
        return marioIsDead;
    }

    public float getStateTimer() {
        return stateTimer;
    }

    public void jump() {
        if (currentState != ObjectState.JUMP && currentState != ObjectState.FALL) {
            b2body.applyLinearImpulse(new Vector2(0, jumpForce), b2body.getWorldCenter(), true);
            currentState = ObjectState.JUMP;
        }
    }

    public void hit(Enemy enemy) {
        if (enemy instanceof Turtle && ((Turtle) enemy).currentState == Turtle.State.STANDING_SHELL)
            ((Turtle) enemy)
                    .kick(enemy.b2body.getPosition().x > b2body.getPosition().x ? Turtle.KICK_RIGHT
                            : Turtle.KICK_LEFT);
        else {
            die();
        }
    }

    public void defineObject() {
        BodyDef bdef = new BodyDef();
        // bdef.position.set((WIDHT_HEIGHT * 2) / MarioBros.PPM, (WIDHT_HEIGHT * 2) /
        // MarioBros.PPM);
        bdef.position.set(getX(), getY());
        bdef.type = BodyType.DynamicBody;
        // bdef.type = BodyDef.BodyType.StaticBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();

        fdef.filter.categoryBits = MarioBros.MARIO_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT | MarioBros.COIN_BIT | MarioBros.BRICK_BIT
                | MarioBros.ENEMY_BIT | MarioBros.OBJECT_BIT | MarioBros.ENEMY_HEAD_BIT
                | MarioBros.ITEM_BIT;

        // PolygonShape shape = new PolygonShape();
        // shape.setAsBox(getHeight() / 2, getWidth() / 2);
        CircleShape shape = new CircleShape();
        shape.setRadius((WIDHT_HEIGHT / 2f) / MarioBros.PPM);

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
        fdef.friction = 0;

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM),
                new Vector2(2 / MarioBros.PPM, 6 / MarioBros.PPM));
        fdef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);
    }

    public static float getWidhtHeight() {
        return WIDHT_HEIGHT;
    }

    @Override
    public void draw(Batch batch) {
        if (getTexture() != null) {
            super.draw(batch);
        }

        if (item != null) {
            item.draw(batch);
        }
    }

    public Map<ObjectState, GAnimation<TextureRegion>> getAnimations() {
        return animations;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Body getB2body() {
        return b2body;
    }

    public void save() {
        SaveFileObject.saveObject(SaveFileObject.PLAYER, this);
    }

    public ObjectState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(ObjectState currentState) {
        this.currentState = currentState;
    }

    public ObjectState getPreviousState() {
        return previousState;
    }

    public void setPreviousState(ObjectState previousState) {
        this.previousState = previousState;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public boolean isRunningRight() {
        return runningRight;
    }

    public void setRunningRight(boolean runningRight) {
        this.runningRight = runningRight;
    }

    public void setB2body(Body b2body) {
        this.b2body = b2body;
    }

    public void setStateTimer(float stateTimer) {
        this.stateTimer = stateTimer;
    }

    public float getJumpForce() {
        return jumpForce;
    }

    public void setJumpForce(float jumpForce) {
        this.jumpForce = jumpForce;
    }

    public void setWidht(int widht) {
        this.widht = widht;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public void setX(float x) {
        super.setX(x);
    }

    public void setPosition(Vector2 vec) {
        if (vec != null) {
            setPosition(vec.x, vec.y);
        }
    }

    @Override
    public void setPosition(float x, float y) {
        getB2body().setTransform(x, y, getB2body().getAngle());
        // super.setPosition(x, y);
    }

    public void setSpritePosition(float x, float y) {
        super.setPosition(x, y);
    }

    public void dispose() {
        world.destroyBody(b2body);
        b2body.setUserData(null);
        b2body = null;
    }

    public void addIA(Consumer<GObject> consumer) {
        this.consumer = consumer;
    }

    public void setBounds(Sprite sprite) {
        setBounds(sprite.getWidth(), sprite.getHeight());
    }

    public void setBounds(TextureRegion region) {
        setBounds(region.getRegionWidth(), region.getRegionHeight());
    }

    public void setBounds(Texture texture) {
        setBounds(texture.getWidth(), texture.getHeight());
    }

    public void setBounds(float width, float height) {
        setBounds(0, 0, width / MarioBros.PPM, height / MarioBros.PPM);
    }
}