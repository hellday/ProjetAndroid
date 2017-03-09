package com.mygdx.game.sprites.Enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.GameTest;
import com.mygdx.game.scenes.Hud;
import com.mygdx.game.screens.PlayScreen;
import com.mygdx.game.sprites.Other.FireBall;
import com.mygdx.game.sprites.Player.Mario;
import com.mygdx.game.tools.B2WorldCreator;

/**
 * Created by Terry on 10/11/2016.
 */

public class Goomba extends com.mygdx.game.sprites.Enemies.Enemy {

    private TextureAtlas atlas;
    private float stateTime;
    private Animation walkAnimation;
    private Array<TextureRegion> frames;
    public boolean setToDestroy;
    private boolean destroyed;
    private boolean canBeRemoved;

    public enum State {WALKING, DEAD}
    public State currentState;
    public State previousState;
    private float deadRotationDegrees;


    public Goomba(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        frames = new Array<TextureRegion>();

        atlas = new TextureAtlas("sprites/ghost.pack");

        for(int i = 1; i < 4; i++){
            frames.add(new TextureRegion(atlas.findRegion("ghost"), i * 96, 0, 96, 128));
        }

        walkAnimation = new Animation(0.5f, frames);
        setBounds(getX(), getY(), 24 / GameTest.PPM, 24 / GameTest.PPM);
        setToDestroy = false;
        destroyed = false;
        canBeRemoved = false;

        currentState = previousState = State.WALKING;
        deadRotationDegrees = 0;

    }

    public TextureRegion getFrame(float dt){
        TextureRegion region;

        switch (currentState){
            case WALKING:
            default:
                region = walkAnimation.getKeyFrame(stateTime, true);
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

            setRegion(new TextureRegion(screen.getAtlas().findRegion("goomba"), 32, 0, 16, 16));
            //stateTime = 0;
        }else if(!destroyed) {
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
            setRegion(walkAnimation.getKeyFrame(stateTime, true));
        }

        //Si touché par une Tortue ou une Fireball
        if(currentState == State.DEAD){
            deadRotationDegrees =+ 3;
            rotate(deadRotationDegrees);


        }else
            b2body.setLinearVelocity(velocity);

        if(canBeRemoved){
            B2WorldCreator.removeGoomba(this);
            System.out.println("Goomba détruit !");
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
        shape.setAsBox(9 / GameTest.PPM, 11 / GameTest.PPM);
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

        //Création de la tête:
        PolygonShape head = new PolygonShape();
        Vector2[] vertice = new Vector2[4];
        vertice[0] = new Vector2(-4, 9).scl(1 / GameTest.PPM);
        vertice[1] = new Vector2(4, 9).scl(1 / GameTest.PPM);
        vertice[2] = new Vector2(-4, 5).scl(1 / GameTest.PPM);
        vertice[3] = new Vector2(4, 4).scl(1 / GameTest.PPM);
        head.set(vertice);

        fdef.shape = head;
        fdef.restitution = 0.5f; //Hauteur de saut du joueur après avoir toucher la tête
        fdef.filter.categoryBits = GameTest.ENEMY_HEAD_BIT;
        b2body.createFixture(fdef).setUserData(this);

    }

    public void draw(Batch batch){ //Disparition du Goomba
        if(!destroyed || stateTime < 1){ //Temps avant de disparaitre
            super.draw(batch);
        }
    }

    @Override
    public void hitOnHead(Mario mario) {
        setToDestroy = true;
        GameTest.manager.get("audio/sounds/stomp.wav", Sound.class).play();
    }

    public void onEnemyHit(Enemy enemy){
        if(enemy instanceof Turtle && ((Turtle) enemy).currentState == Turtle.State.MOVING_SHELL){
            killed();
        }else {
            reverseVelocity(true, false);
        }
    }

    public void onBladeHit(){
        setToDestroy = true;
        GameTest.manager.get("audio/sounds/dead_spectre.wav", Sound.class).play(0.2f);
        Hud.addScore(200);
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
}
