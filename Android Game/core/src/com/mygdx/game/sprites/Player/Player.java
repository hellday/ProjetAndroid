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
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.Controller;
import com.mygdx.game.Database.DataBaseTest;
import com.mygdx.game.GameTest;
import com.mygdx.game.scenes.Hud;
import com.mygdx.game.screens.PlayScreen;
import com.mygdx.game.sprites.Enemies.Enemy;
import com.mygdx.game.sprites.Enemies.Turtle;
import com.mygdx.game.sprites.Other.FireBall;
import com.mygdx.game.sprites.Other.FireBoss;

import static com.mygdx.game.scenes.Hud.damage;
import static com.mygdx.game.scenes.Hud.heartcount;

/**
 * Created by Terry on 10/11/2016.
 */

public class Player extends Sprite {
    public enum State { FALLING, JUMPING, STANDING, RUNNING, GROWING, ATTACKING, DEAD}
    public enum Color {GREY, RED, BLUE}
    public Color buff;
    public State currentState;
    public State previousState;
    public World world;
    public Body b2body;
    public BodyDef bdef;
    public Fixture leftBlade;
    public Fixture rightBlade;
    public Fixture mainFixture;

    private Animation marioStand;
    private Animation marioRun;
    private TextureRegion marioJump;
    private TextureRegion marioDead;

    private Animation marioAttack;


    private float stateTimer;
    private boolean runningRight;
    private boolean marioIsDead;

    private boolean marioIsAttacking;
    private boolean marioCanAttack;
    private long bladeTime;
    private long invincibleTime;
    private boolean attack;

    private TextureAtlas atlas_grey, atlas_red, atlas_blue ,atlasAttack_grey, atlasAttack_red, atlasAttack_blue;

    private PlayScreen screen;
    private DataBaseTest db;
    private String usernameSession;

    private Array<FireBall> fireballs;

    private boolean isDead;

    public Player(PlayScreen screen, String user){
        this.screen = screen;
        this.world = screen.getWorld();
        this.usernameSession = user;

        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        buff = Color.GREY;
        runningRight = true;
        marioIsAttacking = false;
        marioCanAttack = true;


        //Texture Grey Knight
        atlas_grey = new TextureAtlas("sprites/knight_grey.pack");
        atlasAttack_grey = new TextureAtlas("sprites/knight_grey_attack.pack");
        //Texture Red Knight
        atlas_red = new TextureAtlas("sprites/knight_red.pack");
        atlasAttack_red = new TextureAtlas("sprites/knight_red_attack.pack");
        //Texture Blue Knight
        atlas_blue = new TextureAtlas("sprites/knight_blue.pack");
        atlasAttack_blue = new TextureAtlas("sprites/knight_blue_attack.pack");

        applyBuff();
        defineMario();
        setBounds(0, 0, 48 / GameTest.PPM, 48 / GameTest.PPM);

        //Fireball
        fireballs = new Array<FireBall>();
        attack = false;

        //Database
        db = new DataBaseTest();
        db.createDatabase();

        isDead = false;
    }

    public void update(float dt){
        //Attache le Sprite au body
        if(runningRight) {
            setPosition(b2body.getPosition().x + 0.06f - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        }else setPosition(b2body.getPosition().x - 0.06f - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);

        setRegion(getFrame(dt));


        if(marioIsAttacking)
        {
            marioIsAttacking = false;
            Timer.schedule(new Timer.Task(){
                @Override
                public void run() {
                    marioCanAttack = true;
                }
            }, 0.56f);
        }

        //Fireball
        for(FireBall  ball : fireballs) {
            ball.update(dt);
            if(ball.isDestroyed())
                fireballs.removeValue(ball, true);
        }

        if(TimeUtils.timeSinceNanos(bladeTime) > 250000000)
        {
            bladeOn(false);

        }

        if(!marioIsDead)
        {
            if(TimeUtils.timeSinceNanos(invincibleTime) > 1250000000)
            {
                invincible(false);
            }
        }


        if(Hud.getWorldTimer() == 0 && !isDead){
            die();
            isDead = true;
        }


    }

    public void bladeOn(Boolean On)
    {
        if(On){
            Filter filter = new Filter();
            filter.categoryBits = GameTest.ATTACK_BIT;
            if(runningRight)
            {
                rightBlade.setFilterData(filter);
            }
            else
            {
                leftBlade.setFilterData(filter);
            }


            bladeTime = TimeUtils.nanoTime();
        }
        else {
            Filter filter = new Filter();
            filter.categoryBits = GameTest.NOTHING_BIT;
            rightBlade.setFilterData(filter);
            leftBlade.setFilterData(filter);
        }
    }


    public void defineMario(){
        bdef = new BodyDef();
        bdef.position.set(100 / GameTest.PPM, 280 / GameTest.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(6 / GameTest.PPM, 12 / GameTest.PPM); //Taille du personnage

        fdef.filter.categoryBits = GameTest.MARIO_BIT;
        fdef.filter.maskBits = GameTest.GROUND_BIT |
                GameTest.COIN_BIT |
                GameTest.BRICK_BIT |
                GameTest.ENEMY_BIT |
                GameTest.OBJECT_BIT |
                GameTest.ITEM_BIT |
                GameTest.DEAD_ZONE_BIT |
                GameTest.AREA_BIT |
                GameTest.FIREBOSS_BIT;

        fdef.shape = shape;
        fdef.friction = 0;

        mainFixture = b2body.createFixture(fdef);
        mainFixture.setUserData(this);
        //b2body.createFixture(fdef).setUserData(this);

        // Mario's head
        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / GameTest.PPM, 6 / GameTest.PPM), new Vector2(2 / GameTest.PPM, 6 / GameTest.PPM));
        fdef.filter.categoryBits = GameTest.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.friction = 0;
        fdef.isSensor = true;
        b2body.createFixture(fdef).setUserData(this);

        // Mario's feet
        head.set(new Vector2(-(3 / GameTest.PPM), -(12 / GameTest.PPM)), new Vector2(3 / GameTest.PPM, -(12 / GameTest.PPM)));
        fdef.filter.categoryBits = GameTest.MARIO_FOOT_BIT;
        fdef.shape = head;
        fdef.isSensor = true;
        b2body.createFixture(fdef).setUserData(this);

        //left blade
        head.set(new Vector2(-20 / GameTest.PPM, -10 / GameTest.PPM), new Vector2(-20 / GameTest.PPM, 10 / GameTest.PPM));
        PolygonShape blade = new PolygonShape();
        fdef.filter.categoryBits = GameTest.NOTHING_BIT;
        Vector2[] vertice = new Vector2[5];
        vertice[0] = new Vector2(-10, -12).scl(1 / GameTest.PPM);
        vertice[1] = new Vector2(-30, -12).scl(1 / GameTest.PPM);
        vertice[2] = new Vector2(-20, 20).scl(1 / GameTest.PPM);
        vertice[3] = new Vector2(-10, 20).scl(1 / GameTest.PPM);
        vertice[4] = new Vector2(-30, 0).scl(1 / GameTest.PPM);
        blade.set(vertice);
        fdef.shape = blade;
        fdef.friction = 0;
        fdef.isSensor = true;
        leftBlade = b2body.createFixture(fdef);
        leftBlade.setUserData(this);


        // right blade
        fdef.filter.categoryBits = GameTest.NOTHING_BIT;
        vertice[0] = new Vector2(10, -12).scl(1 / GameTest.PPM);
        vertice[1] = new Vector2(30, -12).scl(1 / GameTest.PPM);
        vertice[2] = new Vector2(20, 20).scl(1 / GameTest.PPM);
        vertice[3] = new Vector2(10, 20).scl(1 / GameTest.PPM);
        vertice[4] = new Vector2(30, 0).scl(1 / GameTest.PPM);
        blade.set(vertice);
        fdef.shape = blade;
        fdef.friction = 0;
        fdef.isSensor = true;
        rightBlade = b2body.createFixture(fdef);
        rightBlade.setUserData(this);


        blade.dispose();
        shape.dispose();
        head.dispose();

    }

    public void applyBuff(){
        Array<TextureRegion> frames = new Array<TextureRegion>();
        switch (buff)
        {
            case RED:
                //Animation : COURIR
                for(int i = 0; i < 2; i++){
                    frames.add(new TextureRegion(atlas_red.findRegion("knight_walk"), i * 234, 0, 234, 255));
                }

                marioRun = new Animation(0.08f, frames);
                frames.clear();

                //Animation ATTACK
                for(int i = 1; i < 4; i++){
                    frames.add(new TextureRegion(atlasAttack_red.findRegion("knight_attack"), i * 156, 0, 156, 170));
                }
                for(int i = 0; i < 3; i++){
                    frames.add(new TextureRegion(atlasAttack_red.findRegion("knight_attack"), i * 156, 170, 156, 170));
                }
                marioAttack = new Animation(0.08f, frames);
                frames.clear();

                marioJump = new TextureRegion(atlas_red.findRegion("knight_walk"), 234, 255, 234, 255);

                //Création de l'animation pour Mario immobile
                frames.add(new TextureRegion(atlas_red.findRegion("knight_walk"), 0, 0, 234, 255));
                frames.add(new TextureRegion(atlas_red.findRegion("knight_walk"), 0, 255, 234, 255));
                marioStand = new Animation(0.8f, frames);
                frames.clear();

                //Création de la texture Mario mort
                marioDead = new TextureRegion(atlas_red.findRegion("knight_walk"), 0, 0, 234, 255);
                break;

            case BLUE:
                //Animation : COURIR
                for(int i = 0; i < 2; i++){
                    frames.add(new TextureRegion(atlas_blue.findRegion("knight_walk"), i * 234, 0, 234, 255));
                }

                marioRun = new Animation(0.08f, frames);
                frames.clear();

                //Animation ATTACK
                for(int i = 1; i < 4; i++){
                    frames.add(new TextureRegion(atlasAttack_blue.findRegion("knight_attack"), i * 156, 0, 156, 170));
                }
                for(int i = 0; i < 3; i++){
                    frames.add(new TextureRegion(atlasAttack_blue.findRegion("knight_attack"), i * 156, 170, 156, 170));
                }
                marioAttack = new Animation(0.08f, frames);
                frames.clear();

                marioJump = new TextureRegion(atlas_blue.findRegion("knight_walk"), 234, 255, 234, 255);

                //Création de l'animation pour Mario immobile
                frames.add(new TextureRegion(atlas_blue.findRegion("knight_walk"), 0, 0, 234, 255));
                frames.add(new TextureRegion(atlas_blue.findRegion("knight_walk"), 0, 255, 234, 255));
                marioStand = new Animation(0.8f, frames);
                frames.clear();

                //Création de la texture Mario mort
                marioDead = new TextureRegion(atlas_blue.findRegion("knight_walk"), 0, 0, 234, 255);
                break;


            case GREY:
            default:
                //Animation : COURIR
                for(int i = 0; i < 2; i++){
                    frames.add(new TextureRegion(atlas_grey.findRegion("knight_walk"), i * 234, 0, 234, 255));
                }

                marioRun = new Animation(0.08f, frames);
                frames.clear();

                //Animation ATTACK
                for(int i = 1; i < 4; i++){
                    frames.add(new TextureRegion(atlasAttack_grey.findRegion("knight_attack"), i * 156, 0, 156, 170));
                }
                for(int i = 0; i < 3; i++){
                    frames.add(new TextureRegion(atlasAttack_grey.findRegion("knight_attack"), i * 156, 170, 156, 170));
                }
                marioAttack = new Animation(0.08f, frames);
                frames.clear();

                marioJump = new TextureRegion(atlas_grey.findRegion("knight_walk"), 234, 255, 234, 255);

                //Création de l'animation pour Mario immobile
                frames.add(new TextureRegion(atlas_grey.findRegion("knight_walk"), 0, 0, 234, 255));
                frames.add(new TextureRegion(atlas_grey.findRegion("knight_walk"), 0, 255, 234, 255));
                marioStand = new Animation(0.8f, frames);
                frames.clear();

                //Création de la texture Mario mort
                marioDead = new TextureRegion(atlas_grey.findRegion("knight_walk"), 0, 0, 234, 255);
                break;
        }
    }



    public TextureRegion getFrame(float dt){
        currentState = getState();

        TextureRegion region;
        switch(currentState){
            case DEAD:
                region = marioDead;
                break;
            case JUMPING:
            case FALLING:
                region = marioJump;
                break;
            case RUNNING:
                region = marioRun.getKeyFrame(stateTimer, true);
                break;
            case STANDING:
            default:
                region = marioStand.getKeyFrame(stateTimer, true);
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

    //La position de Mario
    public State getState(){

        //Si il meurt
        if(marioIsDead){
            return State.DEAD;
        }else if (attack && marioCanAttack) {
            marioIsAttacking = true;
            marioCanAttack = false;
            attack = false;
            return State.ATTACKING;

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

    public void knockBack (Enemy enemy)
    {
        if(enemy.velocity.x >0)
        {
            b2body.applyLinearImpulse(new Vector2(3f, 3f), b2body.getWorldCenter(), true);
        }
        else
        {
            b2body.applyLinearImpulse(new Vector2(-3f, 3f), b2body.getWorldCenter(), true);
        }
    }

    public void knockBackFireBoss (FireBoss fireball)
    {
        if(fireball.b2body.getLinearVelocity().x>0)
        {
            b2body.applyLinearImpulse(new Vector2(3f, 3f), b2body.getWorldCenter(), true);
        }
        else
        {
            b2body.applyLinearImpulse(new Vector2(-3f, 3f), b2body.getWorldCenter(), true);
        }
    }

    public void invincible(boolean invincible)
    {

        if(invincible)
        {
            invincibleTime = TimeUtils.nanoTime();
            setCategoryFilter(GameTest.DESTROYED_BIT);

        }
        else
        {
            setCategoryFilter(GameTest.MARIO_BIT);

        }


    }

    public void setCategoryFilter(short filterBit){
        Filter filter = new Filter();
        filter.maskBits = GameTest.GROUND_BIT |
                GameTest.COIN_BIT |
                GameTest.BRICK_BIT |
                GameTest.ENEMY_BIT |
                GameTest.OBJECT_BIT |
                GameTest.ITEM_BIT |
                GameTest.DEAD_ZONE_BIT |
                GameTest.AREA_BIT|
                GameTest.FIREBOSS_BIT;

        filter.categoryBits = filterBit;
        mainFixture.setFilterData(filter);

    }

    public void hit(Enemy enemy){ //Si Mario se fait toucher par un ennemi


        damage(1);

        if(db.returnData("settings", "vibreur", "idUser", db.getIdFromNameUser(usernameSession)).equalsIgnoreCase("on")) { //Si le vibreur est activé dans les paramètres
            Gdx.input.vibrate(200);
        }

        if (heartcount>0){
            /// on fait un bruit de dégat
            knockBack(enemy);
            invincible(true);
        }
        else {
            die();
        }
    }

    public void hitBoss(FireBoss fireboss){
        damage(1);

        if(db.returnData("settings", "vibreur", "idUser", db.getIdFromNameUser(usernameSession)).equalsIgnoreCase("on")) { //Si le vibreur est activé dans les paramètres
            Gdx.input.vibrate(200);
        }
        if (heartcount>0){
            /// on fait un bruit de dégat
            knockBackFireBoss (fireboss);
            invincible(true);
        }
        else {
            die();
        }
    }



    //Si Mario se fait toucher par un ennemi
    public void die(){

        //On stop la musique
        GameTest.manager.get("audio/music/music.mp3", Music.class).stop();
        //On lance le son de la mort du Hero
        GameTest.manager.get("audio/sounds/dead_hero.wav", Sound.class).play(0.2f);
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
        GameTest.manager.get("audio/sounds/fireball.ogg", Sound.class).play();
        screen.canFire = false;

    }

    public void attack(){
        bladeOn(true);
        if(buff == Color.RED && screen.canFire == true)
        {
            fire();
        }

    }


    public void draw(Batch batch){
        super.draw(batch);
        for(FireBall ball : fireballs)
            ball.draw(batch);
    }

    public void setAttack(boolean att){
        attack = att;
    }

    public Color getBuff(){
        return buff;
    }

    public void setBuff(String color){
        if(color.equalsIgnoreCase("blue")){
            buff = Color.BLUE;
        }
        if(color.equalsIgnoreCase("grey")){
            buff = Color.GREY;
        }
        if(color.equalsIgnoreCase("red")){
            buff = Color.RED;
        }
        applyBuff();
    }



}
