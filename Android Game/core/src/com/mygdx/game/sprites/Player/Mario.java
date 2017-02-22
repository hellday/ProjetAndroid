package com.mygdx.game.sprites.Player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.GameTest;
import com.mygdx.game.screens.PlayScreen;
import com.mygdx.game.sprites.Enemies.Enemy;
import com.mygdx.game.sprites.Enemies.Turtle;
import com.mygdx.game.sprites.Other.FireBall;

import static com.mygdx.game.scenes.Hud.damage;
import static com.mygdx.game.scenes.Hud.heartcount;

/**
 * Created by Terry on 10/11/2016.
 */

public class Mario extends Sprite {
    public enum State { FALLING, JUMPING, STANDING, RUNNING, GROWING, ATTACKING, DEAD};
    public State currentState;
    public State previousState;
    public World world;
    public Body b2body;
    public BodyDef bdef;

    private Animation marioStand;
    private Animation marioRun;
    private TextureRegion marioJump;
    private TextureRegion marioDead;

    private Animation marioAttack;

    //Big Mario
    private TextureRegion bigMarioStand;
    private TextureRegion bigMarioJump;
    private Animation bigMarioRun;
    private Animation growMario;

    private float stateTimer;
    private boolean runningRight;
    private boolean marioIsBig;
    private boolean runGrowAnimation;
    private boolean timeToDefineBigMario;
    private boolean timeToRedefineMario;
    private boolean marioIsDead;

    private boolean marioIsAttacking;
    private boolean marioCanAttack;

    private TextureAtlas atlas, atlasAttack;

    private PlayScreen screen;

    private final float radius = 6.8f / GameTest.PPM;

    private Array<FireBall> fireballs;

    public Mario(PlayScreen screen){
        this.screen = screen;
        this.world = screen.getWorld();
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;
        marioIsAttacking = false;
        marioCanAttack = true;

        Array<TextureRegion> frames = new Array<TextureRegion>();

        atlas = new TextureAtlas("sprites/knight_grey.pack");
        atlasAttack = new TextureAtlas("sprites/knight_grey_attack.pack");

        //Animation : COURIR

        for(int i = 0; i < 2; i++){
            frames.add(new TextureRegion(atlas.findRegion("knight_walk"), i * 234, 0, 234, 255));
        }

        marioRun = new Animation(0.08f, frames);
        frames.clear();

        //Animation ATTACK

        for(int i = 1; i < 4; i++){
            frames.add(new TextureRegion(atlasAttack.findRegion("knight_attack"), i * 156, 0, 156, 170));
        }
        for(int i = 0; i < 3; i++){
            frames.add(new TextureRegion(atlasAttack.findRegion("knight_attack"), i * 156, 170, 156, 170));
        }
        marioAttack = new Animation(0.08f, frames);
        frames.clear();

        //Animation BIG : COURIR
        for(int i = 1; i < 4; i++){
            frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), i * 16, 0, 16, 32));
        }
        bigMarioRun = new Animation(0.1f, frames);
        frames.clear();

        //Animation Mario qui grandi
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        growMario = new Animation(0.2f, frames);
        frames.clear();

        //Animation : SAUT (1 seule animation donc : TextureRegion et pas Animation)
        marioJump = new TextureRegion(atlas.findRegion("knight_walk"), 234, 255, 234, 255);
        bigMarioJump = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 80, 0, 16, 32);

        //Création de l'animation pour Mario immobile
        frames.add(new TextureRegion(atlas.findRegion("knight_walk"), 0, 0, 234, 255));
        frames.add(new TextureRegion(atlas.findRegion("knight_walk"), 0, 255, 234, 255));
        marioStand = new Animation(0.8f, frames);
        frames.clear();


        bigMarioStand = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32);

        //Création de la texture Mario mort
        marioDead = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 96, 0, 16, 16);

        defineMario();
        setBounds(0, 0, 48 / GameTest.PPM, 48 / GameTest.PPM);
        //setRegion(marioStand);

        //Fireball
        fireballs = new Array<FireBall>();
    }

    public void update(float dt){
        //Attache le Sprite au body
        if(marioIsBig)
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2 - 6 / GameTest.PPM);
        else {
            if(runningRight) {
                setPosition(b2body.getPosition().x + 0.06f - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
            }else setPosition(b2body.getPosition().x - 0.06f - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        }

        setRegion(getFrame(dt));

        if(timeToDefineBigMario){
            defineBigMario();
        }

        if(timeToRedefineMario){
            redefineMario();
        }

        if(marioIsAttacking)
        {
            marioIsAttacking = false;
            Timer.schedule(new Timer.Task(){
                @Override
                public void run() {
                    marioCanAttack = true;
                    System.out.println("Stop");
                }
            }, 0.56f);
        }

        //Fireball
        for(FireBall  ball : fireballs) {
            ball.update(dt);
            if(ball.isDestroyed())
                fireballs.removeValue(ball, true);
        }
    }

    //Mario grandi
    public void grow(){
        runGrowAnimation = true;
        marioIsBig = true;
        timeToDefineBigMario = true;
        setBounds(getX(), getY(), getWidth(), getHeight() * 2);
        GameTest.manager.get("audio/sounds/powerup.wav", Sound.class).play();
    }

    public boolean isBig(){
        return marioIsBig;
    }



    public void defineMario(){
        bdef = new BodyDef();
        bdef.position.set(128 / GameTest.PPM, 32 / GameTest.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        //CircleShape shape = new CircleShape();
        //shape.setRadius(14 / GameTest.PPM); //Taille du personnage
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(9 / GameTest.PPM, 12 / GameTest.PPM);

        fdef.filter.categoryBits = GameTest.MARIO_BIT;
        fdef.filter.maskBits = GameTest.GROUND_BIT |
                GameTest.COIN_BIT |
                GameTest.BRICK_BIT |
                GameTest.ENEMY_BIT |
                GameTest.OBJECT_BIT |
                GameTest.ENEMY_HEAD_BIT |
                GameTest.ITEM_BIT |
                GameTest.DEAD_ZONE_BIT |
                GameTest.AREA_BIT;

        fdef.shape = shape;
        fdef.friction = 0;
        b2body.createFixture(fdef).setUserData(this);

        // Mario's head
        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / GameTest.PPM, 6 / GameTest.PPM), new Vector2(2 / GameTest.PPM, 6 / GameTest.PPM));
        fdef.filter.categoryBits = GameTest.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.friction = 0;
        fdef.isSensor = true;
        b2body.createFixture(fdef).setUserData(this);

        // Mario's feet
        head.set(new Vector2(-(6 / GameTest.PPM), -(12 / GameTest.PPM)), new Vector2(6 / GameTest.PPM, -(12 / GameTest.PPM)));
        fdef.filter.categoryBits = GameTest.MARIO_FOOT_BIT;
        fdef.shape = head;
        fdef.isSensor = true;
        b2body.createFixture(fdef).setUserData(this);

        shape.dispose();
        head.dispose();

    }

    public void defineBigMario(){
        Vector2 currentPosition = b2body.getPosition(); //Récupère le body du petit Mario
        world.destroyBody(b2body); //et le détruit

        bdef = new BodyDef();
        bdef.position.set(currentPosition.add(0, 10 / GameTest.PPM));
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / GameTest.PPM); //Taille du personnage
        fdef.filter.categoryBits = GameTest.MARIO_BIT;
        fdef.filter.maskBits = GameTest.GROUND_BIT |
                GameTest.COIN_BIT |
                GameTest.BRICK_BIT |
                GameTest.ENEMY_BIT |
                GameTest.OBJECT_BIT |
                GameTest.ENEMY_HEAD_BIT |
                GameTest.ITEM_BIT |
                GameTest.DEAD_ZONE_BIT |
                GameTest.AREA_BIT;

        fdef.shape = shape;
        fdef.friction = 0;
        b2body.createFixture(fdef).setUserData(this);

        shape.setPosition(new Vector2(0, -14 / GameTest.PPM));
        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);


        // Mario's head
        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / GameTest.PPM, 6 / GameTest.PPM), new Vector2(2 / GameTest.PPM, 6 / GameTest.PPM));
        fdef.filter.categoryBits = GameTest.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.friction = 0;
        fdef.isSensor = true;
        b2body.createFixture(fdef).setUserData(this);

        // Mario's feet

        head.set(new Vector2(-(6 / GameTest.PPM), -(20 / GameTest.PPM)), new Vector2(6 / GameTest.PPM, -(20 / GameTest.PPM)));
        fdef.filter.categoryBits = GameTest.MARIO_FOOT_BIT;
        fdef.shape = head;
        fdef.isSensor = true;
        b2body.createFixture(fdef).setUserData(this);

        //Mario's blade sides
       /* EdgeShape blade = new EdgeShape();
        head.set(new Vector2(-20 / GameTest.PPM, -10 / GameTest.PPM), new Vector2(-20 / GameTest.PPM, 10 / GameTest.PPM));
        fdef.filter.categoryBits = GameTest.NOTHING_BIT;
        fdef.shape = head;
        fdef.friction = 0;
        fdef.isSensor = true;
        b2body.createFixture(fdef).setUserData(this); */

        timeToDefineBigMario = false;

        shape.dispose();
        head.dispose();

    }

    public void redefineMario(){
        Vector2 position = b2body.getPosition();
        world.destroyBody(b2body);


        bdef = new BodyDef();
        bdef.position.set(position);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / GameTest.PPM); //Taille du personnage
        fdef.filter.categoryBits = GameTest.MARIO_BIT;
        fdef.filter.maskBits = GameTest.GROUND_BIT |
                GameTest.COIN_BIT |
                GameTest.BRICK_BIT |
                GameTest.ENEMY_BIT |
                GameTest.OBJECT_BIT |
                GameTest.ENEMY_HEAD_BIT |
                GameTest.ITEM_BIT |
                GameTest.DEAD_ZONE_BIT |
                GameTest.AREA_BIT;

        fdef.shape = shape;
        fdef.friction = 0;
        b2body.createFixture(fdef).setUserData(this);

        //Mario's head
        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / GameTest.PPM, 6 / GameTest.PPM), new Vector2(2 / GameTest.PPM, 6 / GameTest.PPM));
        fdef.filter.categoryBits = GameTest.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.friction = 0;
        fdef.isSensor = true;
        b2body.createFixture(fdef).setUserData(this);

        // Mario's feet
        head.set(new Vector2(-(6 / GameTest.PPM), -(6 / GameTest.PPM)), new Vector2(6 / GameTest.PPM, -(6 / GameTest.PPM)));
        fdef.filter.categoryBits = GameTest.MARIO_FOOT_BIT;
        fdef.shape = head;
        fdef.isSensor = true;
        b2body.createFixture(fdef).setUserData(this);

        timeToRedefineMario = false;

        shape.dispose();
        head.dispose();
    }



    public TextureRegion getFrame(float dt){
        currentState = getState();

        TextureRegion region;
        switch(currentState){
            case DEAD:
                region = marioDead;
                break;
            case GROWING:
                region = growMario.getKeyFrame(stateTimer);
                if(growMario.isAnimationFinished(stateTimer))
                    runGrowAnimation = false;
                break;
            case JUMPING:
            case FALLING:
                region = marioIsBig ? bigMarioJump : marioJump;
                break;
            case RUNNING:
                region = marioIsBig ? bigMarioRun.getKeyFrame(stateTimer, true) : marioRun.getKeyFrame(stateTimer, true);
                break;
            case STANDING:
            default:
                region = marioIsBig ? bigMarioStand : marioStand.getKeyFrame(stateTimer, true);
                break;
            case ATTACKING:
                region = marioAttack.getKeyFrame(stateTimer, true);
                break;
        }

        //if mario is running left and the texture isnt facing left... flip it.
        if((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()){
            region.flip(true, false);
            runningRight = false;
        }

        //if mario is running right and the texture isnt facing right... flip it.
        else if((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()){
            region.flip(true, false);
            runningRight = true;
        }

        //if the current state is the same as the previous state increase the state timer.
        //otherwise the state has changed and we need to reset timer.
        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        //update previous state
        previousState = currentState;
        //return our final adjusted frame
        return region;

    }

    public State getState(){ //La position de Mario
        //Si il meurt
        if(marioIsDead){
            return State.DEAD;
        }else if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && marioCanAttack) {
            marioIsAttacking = true;
            marioCanAttack = false;
            System.out.println("Attack");
           return State.ATTACKING;
        }

        //Si il grandi
        else if(runGrowAnimation){
            return State.GROWING;
        }

        else if(b2body.getLinearVelocity().y > 0){
            return State.JUMPING;
        }
        else if(b2body.getLinearVelocity().y > 0){
            return State.FALLING;
        }else if(b2body.getLinearVelocity().x != 0 && marioCanAttack){
            return State.RUNNING;
        }else if(!marioCanAttack)
            return State.ATTACKING;
        else return State.STANDING;
    }

    public void hit(Enemy enemy){ //Si Mario se fait toucher par un ennemi

        if(enemy instanceof Turtle && ((Turtle) enemy).getCurrentState() == Turtle.State.STANDING_SHELL){
            ((Turtle) enemy).kick(this.getX() <= enemy.getX() ? Turtle.KICK_RIGHT_SPEED : Turtle.KICK_LEFT_SPEED);
        }else {

            if (marioIsBig) {
                marioIsBig = false;
                timeToRedefineMario = true;
                setBounds(getX(), getY(), getWidth(), getHeight() / 2);
                GameTest.manager.get("audio/sounds/powerdown.wav", Sound.class).play();

            }
            else
            {
                damage(1);

                if (heartcount>0){
                    /// on fait un bruit de dégat
                }
                else {
                    //On stop la musique
                    GameTest.manager.get("audio/music/mario_music.ogg", Music.class).stop();
                    //On lance le son de la mort de Mario
                    GameTest.manager.get("audio/sounds/mariodie.wav", Sound.class).play();
                    marioIsDead = true;

                    //On applique un filtre pour enlever les collisions
                    Filter filter = new Filter();
                    filter.maskBits = GameTest.NOTHING_BIT;
                    for (Fixture fixture : b2body.getFixtureList()) {
                        fixture.setFilterData(filter);
                    }
                    //Saut de la mort
                    b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
                }
        }
    }
    }

    public void die(){ //Si Mario se fait toucher par un ennemi

                //On stop la musique
                GameTest.manager.get("audio/music/mario_music.ogg", Music.class).stop();
                //On lance le son de la mort de Mario
                GameTest.manager.get("audio/sounds/mariodie.wav", Sound.class).play();
                marioIsDead = true;

                //On applique un filtre pour enlever les collisions
                Filter filter = new Filter();
                filter.maskBits = GameTest.NOTHING_BIT;
                for (Fixture fixture : b2body.getFixtureList()) {
                    fixture.setFilterData(filter);
                }
                //Saut de la mort
                b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);

        if (marioIsBig) {
            marioIsBig = false;
            setBounds(getX(), getY(), getWidth(), getHeight() / 2);
        }
    }

    public void onGround() {
        System.out.println("Test");
    }

    public boolean isDead(){
        return marioIsDead;
    }

    public float getStateTimer() {
        return stateTimer;
    }

    public void fire(){
        fireballs.add(new FireBall(screen, b2body.getPosition().x, b2body.getPosition().y, runningRight));
    }

    public void draw(Batch batch){
        super.draw(batch);
        for(FireBall ball : fireballs)
            ball.draw(batch);
    }



}
