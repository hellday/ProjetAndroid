package com.mygdx.game.sprites.Enemies;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.GameTest;
import com.mygdx.game.scenes.HealthBarBoss;
import com.mygdx.game.scenes.Hud;
import com.mygdx.game.screens.PlayScreen;
import com.mygdx.game.sprites.Player.Player;
import com.mygdx.game.tools.B2WorldCreator;

/**
 * Created by Terry on 09/03/2017.
 */

public class Boss extends Enemy {
    private TextureAtlas atlas;
    private float stateTime;
    private Animation walkAnimation, hitAnimation, deadAnimation;
    private Array<TextureRegion> frames;
    public boolean setToDestroy;
    private boolean destroyed;
    private boolean canBeRemoved;

    private enum State {IDLE, WALKING, DEAD, HIT}
    private State currentState;
    private State previousState;

    private TextureRegion idle, dead;

    private int health;


    public Boss(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        velocity = new Vector2(-1, -2); //Vitesse du Boss
        frames = new Array<TextureRegion>();

        atlas = new TextureAtlas("sprites/ghost.pack");

        // Walk Animation
        for(int i = 1; i < 4; i++){
            frames.add(new TextureRegion(atlas.findRegion("ghost"), i * 96, 0, 96, 128));
        }
        walkAnimation = new Animation(0.5f, frames);
        frames.clear();

        //Hit Animation
        for(int i = 0; i < 2; i++){
            frames.add(new TextureRegion(atlas.findRegion("ghost"), i * 96, 0, 96, 128));
        }
        hitAnimation = new Animation(0.1f, frames);
        frames.clear();

        // Death Animation
        for(int i = 0; i < 2; i++){
            frames.add(new TextureRegion(atlas.findRegion("ghost"), i * 96, 0, 96, 128));
        }
        dead = new TextureRegion(atlas.findRegion("ghost"), 96, 0, 96, 128);
        dead.flip(true, false);
        frames.add(dead);
        deadAnimation = new Animation(0.1f, frames);
        frames.clear();

        // Idle Animation
        idle = new TextureRegion(atlas.findRegion("ghost"), 0, 0, 96, 128);

        setBounds(getX(), getY(), 48 / GameTest.PPM, 48 / GameTest.PPM);
        setToDestroy = false;
        destroyed = false;
        canBeRemoved = false;

        currentState = previousState = State.IDLE;

        health = 5;

    }

    public TextureRegion getFrame(float dt){
        TextureRegion region;

        switch (currentState){
            case WALKING:
                region = walkAnimation.getKeyFrame(stateTime, true);
                break;
            case IDLE:
            default:
                region = idle;
                break;
            case HIT:
                region = hitAnimation.getKeyFrame(stateTime, true);
                break;
            case DEAD:
                region = deadAnimation.getKeyFrame(stateTime, true);
                break;
        }

        if(velocity.x > 0 && !region.isFlipX()){
            region.flip(true, false);
        }

        if(velocity.x < 0 && region.isFlipX()){
            region.flip(true, false);
        }

        //if the current state is the same as the previous state increase the state timer.
        //otherwise the state has changed and we need to reset timer.
        stateTime = currentState == previousState ? stateTime + dt : 0;
        //update previous state
        previousState = currentState;
        //return our final adjusted frame
        return region;
    }

    public void update(float dt){
        setRegion(getFrame(dt));

        if(setToDestroy && !destroyed){
            world.destroyBody(b2body);
            destroyed = true;
            callRemove();

        }else if(!destroyed) {
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
            //setRegion(walkAnimation.getKeyFrame(stateTime, true));
        }

        //Si touché par une Tortue ou une Fireball
//        if(currentState == State.DEAD){
//            deadRotationDegrees =+ 3;
//            rotate(deadRotationDegrees);
//        }else
            b2body.setLinearVelocity(velocity);

        if(canBeRemoved){
            B2WorldCreator.removeBoss(this);
            System.out.println("Boss détruit !");
        }

        if(currentState == State.HIT){
            Timer.schedule(new Timer.Task(){
                @Override
                public void run() {
                    currentState = State.IDLE;
                }
            }, 0.4f);
        }

        if(currentState == State.DEAD){
            velocity.x = 0;
            Timer.schedule(new Timer.Task(){
                @Override
                public void run() {

                    currentState = State.IDLE;
                }
            }, 2f);
        }
    }

    @Override
    protected void defineEnemy() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(15 / GameTest.PPM, 20 / GameTest.PPM);
        fdef.filter.categoryBits = GameTest.ENEMY_BIT;
        fdef.filter.maskBits = GameTest.GROUND_BIT |
                GameTest.COIN_BIT |
                GameTest.BRICK_BIT |
                GameTest.ENEMY_BIT |
                GameTest.OBJECT_BIT |
                GameTest.MARIO_BIT |
                GameTest.FIREBALL_BIT |
                GameTest.ATTACK_BIT|
                GameTest.ENEMY_WALL_BIT; //Avec quoi il y a collision

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

    }

    public void draw(Batch batch){ //Disparition du Goomba
        if(!destroyed || stateTime < 1){ //Temps avant de disparaitre
            super.draw(batch);
        }
    }

    @Override
    public void hitOnHead(Player player) {
        setToDestroy = true;
        GameTest.manager.get("audio/sounds/stomp.wav", Sound.class).play();
    }

    public void onEnemyHit(Enemy enemy){

    }

    public void onBladeHit(){
        health--;
        System.out.println("Vie restante : " + health);
        GameTest.manager.get("audio/sounds/dead_spectre.wav", Sound.class).play(0.2f);

        //On retire de la vie sur la HealthBar
        HealthBarBoss.setHealth();


        if(health == 0) {
            setToDestroy = true;
            currentState = State.DEAD;
            GameTest.manager.get("audio/sounds/dead_spectre.wav", Sound.class).play(0.5f);
            Hud.addScore(1000);
            HealthBarBoss.delete();
        }else currentState = State.HIT;
    }



    public void killed(){
        currentState = State.DEAD;
        Filter filter = new Filter();
        filter.maskBits = GameTest.NOTHING_BIT;

        for(Fixture fixture : b2body.getFixtureList()){
            fixture.setFilterData(filter);
        }

        b2body.applyLinearImpulse(new Vector2(0, 5f), b2body.getWorldCenter(), true);
    }

    public void callRemove(){
        float delay = 1; // seconds
        Timer.schedule(new Timer.Task(){
            @Override
            public void run() {
                canBeRemoved = true;
            }
        }, delay);

    }

    public boolean isSetToDestroy() {
        return setToDestroy;
    }

    @Override
    public void setToDestroy(boolean value) {
        setToDestroy = value;
    }

    public boolean isDead(){
        if(currentState == State.DEAD){
            return true;
        }else {
            return false;
        }
    }

    public int getHealth(){
        return health;
    }



}
