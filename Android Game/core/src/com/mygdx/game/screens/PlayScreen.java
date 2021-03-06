package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.Controller;
import com.mygdx.game.Database.DataBaseTest;
import com.mygdx.game.GameTest;
import com.mygdx.game.Items.Item;
import com.mygdx.game.Items.ItemDef;
import com.mygdx.game.Items.Mushroom;
import com.mygdx.game.scenes.HealthBarBoss;
import com.mygdx.game.scenes.Hud;
import com.mygdx.game.scenes.Pause;
import com.mygdx.game.sprites.CollisionWall.Area;
import com.mygdx.game.sprites.Enemies.Boss;
import com.mygdx.game.sprites.Enemies.Enemy;
import com.mygdx.game.sprites.Other.FireBoss;
import com.mygdx.game.sprites.Player.Player;
import com.mygdx.game.tools.B2WorldCreator;
import com.mygdx.game.tools.WorldContactListener;

import java.util.concurrent.LinkedBlockingQueue;


/**
 * Created by Terry on 09/11/2016.
 */

public class PlayScreen implements Screen{

    private GameTest game;
    private TextureAtlas atlas, atlasStage;

    private OrthographicCamera gamecam;
    public Viewport gamePort;
    private static Hud hud;
    private HealthBarBoss hbb;
    private Pause pause;

    //WorldContactListener
    private WorldContactListener wcl;

    //Variables de la map
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private int levelWidth;
    private int levelHeight;

    //Variables Box2d
    public World world;
    private Box2DDebugRenderer b2dr;
    private B2WorldCreator creator;

    //Sprites
    private static Player player;

    //Musique
    private Music music;
    private float MUSIC_FADE_STEP = 0.02f;
    private float fadeRate = 0.1f;

    private Array<Item> items;
    private LinkedBlockingQueue<ItemDef> itemsToSpawn;

    //Controller
    private static Controller controller;


    //Fireball
    long startTime = 0;
    public boolean canFire;

    private final float radius = 6.8f / GameTest.PPM;
    private static boolean canPlay;
    private static boolean movePlayerEnd;

    private String usernameSession;
    private int level;

    //Blue Knight : Double jump
    private boolean canDoubleJump;

    private boolean paused;

    private Stage stage;
    private Skin skin;

    private ParticleEffect pe, peBoss;
    private ParticleEffectPool pool, poolBoss;
    private Array<ParticleEffectPool.PooledEffect> effects, effectsBoss;

    private Array<FireBoss> fireboss;
    private static boolean canFireBoss, canPlayBoss;

    //Camera
    private static boolean cameraChange, startEffect, moveCamera, cameraEndBoss, drawHealthBarBoss;
    private float startX, startY;

    private static Fixture fixtureStartBoss, fixtureEndBoss;
    private Area endBossWall;

    //Database
    DataBaseTest db;



    /** Constructeur de l'écran */
    public PlayScreen(GameTest game, String user, int lvl){

        atlas = new TextureAtlas("Mario_and_Enemies.pack");

        this.game = game;
        this.usernameSession = user;
        this.level = lvl;

        //Database
        db = new DataBaseTest();
        db.createDatabase();

        //Création d'une caméra qui va suivre notre personnage dans notre monde
        gamecam = new OrthographicCamera();
        gamePort = new FitViewport(GameTest.V_WIDTH / GameTest.PPM, GameTest.V_HEIGHT / GameTest.PPM, gamecam);
        gamePort.setCamera(gamecam);


        //test
        stage = new Stage(gamePort, game.batch);
        atlasStage = new TextureAtlas("skin/uiskin.atlas");
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"), atlasStage);
        Label endLabel = new Label("Fin du niveau ", skin);
        endLabel.setColor(Color.WHITE);
        stage.addActor(endLabel);

        //Création du HUD (scores,timers...)
        hud = new Hud(game.batch);

        //Création healthbar du boss
        hbb = new HealthBarBoss(game.batch);

        //Création de l'écran de Pause
        pause = new Pause();

        //Chargement de la map
        map = new TmxMapLoader().load("level" + level + ".tmx");
        MapProperties props = map.getProperties();
        levelWidth = props.get("width", Integer.class);
        levelHeight = props.get("height", Integer.class);

        renderer = new OrthogonalTiledMapRenderer(map, 1 / GameTest.PPM);


        //Initialise la caméra au début de notre niveau
        gamecam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        world = new World(new Vector2(0, -10), true);
        b2dr = new Box2DDebugRenderer();

        //Création du monde
        creator = new B2WorldCreator(this);

        //Création de Mario dans notre monde
        player = new Player(this, usernameSession);

        wcl = new WorldContactListener();
        world.setContactListener(wcl);

        //Musique utilisation
        music = GameTest.manager.get("audio/music/music.mp3", Music.class);
        music.setVolume(0.75f);
        music.setLooping(true);

        if(db.returnData("settings", "music", "idUser", db.getIdFromNameUser(usernameSession)).equalsIgnoreCase("on")) { //Si la musique est activé dans les paramètres
            music.play();
        }

        //Items
        items = new Array<Item>();
        itemsToSpawn = new LinkedBlockingQueue<ItemDef>();

        //Controller
        controller = new Controller();

        //Fireball
        startTime = TimeUtils.nanoTime();
        canFire = true;

        canPlay = true;
        movePlayerEnd = false;

        //Double Jump
        canDoubleJump = false;

        //Menu de pause
        paused = false;

        //Effects
        pe = new ParticleEffect();
        pe.load(Gdx.files.internal("effects/spectre_dead.p"), Gdx.files.internal("effects"));
        pe.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() /2);
        pe.scaleEffect(0.003f);
        pe.start();

        peBoss = new ParticleEffect();
        peBoss.load(Gdx.files.internal("effects/boss_dead.p"), Gdx.files.internal("effects"));
        peBoss.scaleEffect(0.003f);
        peBoss.start();

        pool = new ParticleEffectPool(pe, 0 , 10000);
        effects = new Array<ParticleEffectPool.PooledEffect>();
        poolBoss = new ParticleEffectPool(peBoss, 0 , 10000);
        effectsBoss = new Array<ParticleEffectPool.PooledEffect>();

        //Fireball
        fireboss = new Array<FireBoss>();
        canFireBoss = true;
        canPlayBoss = false;

        //Camera
        cameraChange = false;
        startEffect = false;
        moveCamera = true;
        cameraEndBoss = false;

        //End Boss Wall
        endBossWall = new Area(this, new RectangleMapObject());
        drawHealthBarBoss = false;

    }

    public void spawnItem(ItemDef idef){
        itemsToSpawn.add(idef);
    }

    public void handleSpawningItems(){
        if(!itemsToSpawn.isEmpty()){
            ItemDef idef = itemsToSpawn.poll();
            if(idef.type == Mushroom.class){
                items.add(new Mushroom(this, idef.position.x, idef.position.y));
            }

        }
    }

    @Override
    public void show() {

    }

    public TextureAtlas getAtlas(){
        return atlas;
    }

    /** Gère les contrôles  */
    public void handleInput(float dt){
//        if(Gdx.input.isTouched()){
//            gamecam.position.x += 100 * dt;
//        }

        if(!paused) {
            //Si mario n'est pas mort, sinon on n'active pas les touches
            if (canPlay) {
                if (player.currentState != Player.State.DEAD) {
                    if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && player.b2body.getLinearVelocity().y == 0 && wcl.isPlayerIsOnGround()) { //SAUT
                        player.b2body.applyLinearImpulse(new Vector2(0, 3f), player.b2body.getWorldCenter(), true);
                        GameTest.manager.get("audio/sounds/jump_small.wav", Sound.class).play();

                        //Si le joueur est BLEU il peut sauter 2 fois
                        if (player.getBuff() == Player.Color.BLUE) {
                            canDoubleJump = true;
                        }
                        controller.setUpPressed(false);
                    }
                    if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && canDoubleJump && !wcl.isPlayerIsOnGround()) { //SAUT 2
                        player.b2body.applyLinearImpulse(new Vector2(0, 3f), player.b2body.getWorldCenter(), true);
                        GameTest.manager.get("audio/sounds/jump_small.wav", Sound.class).play();
                        canDoubleJump = false;
                    }

                    if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2body.getLinearVelocity().x <= 2) {
                        player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
                    }

                    if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2body.getLinearVelocity().x >= -2) {
                        player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);
                    }

                    if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                        player.attack();
                        player.setAttack(true);
                    }

                    if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                        if (player.getBuff() == Player.Color.GREY) {
                            player.setBuff("red");
                        } else if (player.getBuff() == Player.Color.RED) {
                            player.setBuff("blue");
                        } else if (player.getBuff() == Player.Color.BLUE) {
                            player.setBuff("grey");
                        }
                    }

                    if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
                        paused = true;
                    }

                    //Controller
//                    if (controller.isUpPressed() && controller.isDownPressed()) {
//                        if (player.getBuff() == Mario.Color.GREY) {
//                            player.setBuff("red");
//                        } else if (player.getBuff() == Mario.Color.RED) {
//                            player.setBuff("blue");
//                        } else if (player.getBuff() == Mario.Color.BLUE) {
//                            player.setBuff("grey");
//                        }
//                    }

                    if (controller.isRightPressed())
                        player.b2body.setLinearVelocity(new Vector2(1, player.b2body.getLinearVelocity().y));

                    else if (controller.isLeftPressed())
                        player.b2body.setLinearVelocity(new Vector2(-1, player.b2body.getLinearVelocity().y));

                    else if (player.b2body.getLinearVelocity().x > 2) {
                        player.b2body.setLinearVelocity(new Vector2(player.b2body.getLinearVelocity().x - 0.1f, player.b2body.getLinearVelocity().y));
                    } else if (player.b2body.getLinearVelocity().x < -2) {
                        player.b2body.setLinearVelocity(new Vector2(player.b2body.getLinearVelocity().x + 0.1f, player.b2body.getLinearVelocity().y));
                    } else {
                        player.b2body.setLinearVelocity(new Vector2(0, player.b2body.getLinearVelocity().y));
                    }

                    if (controller.isUpPressed() && player.b2body.getLinearVelocity().y == 0 && wcl.isPlayerIsOnGround()) {
                        player.b2body.applyLinearImpulse(new Vector2(0, 3f), player.b2body.getWorldCenter(), true);
                        GameTest.manager.get("audio/sounds/jump_small.wav", Sound.class).play();

                        //Si le joueur est BLEU il peut sauter 2 fois
                        if (player.getBuff() == Player.Color.BLUE) {
                            canDoubleJump = true;
                        }
                        controller.setUpPressed(false);
                    }

                    if (controller.isUpPressed() && canDoubleJump && !wcl.isPlayerIsOnGround()) { //SAUT 2
                        player.b2body.applyLinearImpulse(new Vector2(0, 3f), player.b2body.getWorldCenter(), true);
                        GameTest.manager.get("audio/sounds/jump_small.wav", Sound.class).play();
                        canDoubleJump = false;
                    }

                    if (controller.isDownPressed()) {
                        if (canFire) {
                            GameTest.manager.get("audio/sounds/attack.mp3", Sound.class).play();
                            player.attack();
                            player.setAttack(true);
                            canFire = false;
                        }
                    }

                    if (controller.isPausePressed()) {
                        GameTest.manager.get("audio/sounds/pause.wav", Sound.class).play();
                        music.pause();
                        paused = true;
                        controller.setPausePressed(false);
                    }
                }
            }

        }
    }

    /** Actualise les informations à l'écran */
    public void update(float dt){
        handleInput(dt);
        handleSpawningItems();

        world.step(1/60f, 6, 2);

        player.update(dt);

        //Projectiles du Boss
        for(FireBoss  ball : fireboss) {
            ball.update(dt);
            if(ball.isDestroyed())
                fireboss.removeValue(ball, true);
        }

        //Distance d'apparition des ennemies
        for(Enemy enemy : creator.getEnemies()) {
            enemy.update(dt);
            if (enemy.getX() < player.getX() + 260 / GameTest.PPM) {
                enemy.b2body.setActive(true);
            }

            //IA déplacements du Boss
            if(enemy instanceof Boss){
                if(canPlayBoss) {
                    if ((enemy.getX() - player.getX()) < 0.1f && (enemy.getX() - player.getX()) > 0) {
                        enemy.velocity.x = 0;

                    } else if (player.getX() > enemy.getX() && !((Boss) enemy).isDead()) {
                        enemy.velocity.x = 0.6f;
                        fireBoss(enemy, true);
                    } else if (player.getX() < enemy.getX() && !((Boss) enemy).isDead()) {
                        enemy.velocity.x = -0.6f;
                        fireBoss(enemy, false);
                    }
                }else enemy.velocity.x = 0;

                if(((Boss) enemy).isDead()){
                    canPlay = false;
                    Timer.schedule(new Timer.Task(){
                        @Override
                        public void run() {
                            cameraEndBoss = true;
                            cameraChange = false;
                            canPlay = true;
                        }
                    }, 4);



                }
            }
        }



        for(Item item : items){
            item.update(dt);
        }

        hud.update(dt);


        startX = gamecam.viewportWidth / 2;
        startY = gamecam.viewportHeight / 2;

        //Controle de la caméra
        if(player.currentState != Player.State.DEAD) {

            //Si la caméra n'a pas été changé pour un évènement
            if(!cameraChange) {
                gamecam.position.x = player.b2body.getPosition().x;
                gamecam.position.y = player.b2body.getPosition().y;
                boundary(gamecam, startX, startY, levelWidth / 6.25f - startX * 2, levelHeight / 6.25f - startY * 2);
            }else {
                if (moveCamera) { //On bouge la caméra en transition jusqu'au joueur
                    gamecam.position.x += 0.018;
                }else if(!cameraEndBoss) {
                    gamecam.position.x = player.b2body.getPosition().x;
                    gamecam.position.y = player.b2body.getPosition().y;
                    boundary(gamecam, startX + 16.15f, startY, levelWidth / 6.25f - startX * 2 - 25.5f, levelHeight / 6.25f - startY * 2);
                }else {
                    gamecam.position.x = player.b2body.getPosition().x;
                    gamecam.position.y = player.b2body.getPosition().y;
                    boundary(gamecam, startX + 16.15f, startY, levelWidth / 6.25f - startX * 2, levelHeight / 6.25f - startY * 2);
                }

            }
        }



        gamecam.update();
        renderer.setView(gamecam);

        //Timer Fireball
        if (TimeUtils.timeSinceNanos(startTime) > 1000000000) {
        // if time passed since the time you set startTime at is more than 1 second
            canFire = true;
        //also you can set the new startTime
        //so this block will execute every one second
            startTime = TimeUtils.nanoTime();
        }

        //Quand le joueur atteint la fin du niveau
        if(movePlayerEnd){
            //player.b2body.setLinearVelocity(1, 0);
            controller.dispose();
            hud.dispose();

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    if (music.getVolume() >= MUSIC_FADE_STEP)
                        music.setVolume(music.getVolume()-MUSIC_FADE_STEP);
                    else {
                        music.stop();
                        this.cancel();
                    }
                }
            }, 0f, fadeRate);

            Music music = GameTest.manager.get("audio/music/end_level.wav", Music.class);
            music.setVolume(0.75f);
            music.play();

            float delay = 4; // seconds

            Timer.schedule(new Timer.Task(){
                @Override
                public void run() {
                    String score = hud.getScore().toString();
                    game.setScreen(new EndLevelScreen(game, usernameSession, level));
                }
            }, delay);

            movePlayerEnd = false;
        }

    }

    public void updatePause(float dt){
        Gdx.input.setInputProcessor(pause.stage);

        if(pause.isResume()){
            if(db.returnData("settings", "music", "idUser", db.getIdFromNameUser(usernameSession)).equalsIgnoreCase("on")) { //Si la musique est activé dans les paramètres
                music.play();
            }
            Gdx.input.setInputProcessor(controller.stage);
            player.b2body.setLinearVelocity(new Vector2(0, player.b2body.getLinearVelocity().y));
            paused = false;
            pause.setResume(false);
        }

        if(pause.isQuit()){
            music.stop();
            game.setScreen(new LevelSelectScreen(game, usernameSession));
            pause.setQuit(false);
        }


    }

    @Override
    /** Affiche/dessine les informations à l'écran */
    public void render(float delta) {

        if(paused){
//            Gdx.gl.glClearColor(0, 0, 0, 1);
//            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            updatePause(delta);
            pause.draw();
        }

        if(!paused) {
            //Nettoie l'écran de jeu avec du Noir
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            pe.update(Gdx.graphics.getDeltaTime());
            update(delta);

            renderer.render();

            //Affichage des DEBUG (Bodies et collision..)
            //b2dr.render(world, gamecam.combined);

            game.batch.setProjectionMatrix(gamecam.combined);
            game.batch.begin();

            //Effects
            for(ParticleEffectPool.PooledEffect effect : effects){
                effect.draw(game.batch, delta);
                if(effect.isComplete()){
                    effects.removeValue(effect, true);
                    effect.free();
                }
            }

            for(ParticleEffectPool.PooledEffect effect : effectsBoss){
                effect.draw(game.batch, delta);
                if(effect.isComplete()){
                    effectsBoss.removeValue(effect, true);
                    effect.free();
                }
            }


            //Fireboss
            for(FireBoss ball : fireboss) {
                ball.draw(game.batch);
            }

            //Joueur
            player.draw(game.batch); //Dessine le joueur

            for (Enemy enemy : creator.getEnemies()) {//Dessine les ennemies
                enemy.draw(game.batch);
                if(enemy.isSetToDestroy() && enemy instanceof Boss){
                    System.out.println("Boss mort");
                    ParticleEffectPool.PooledEffect effect = poolBoss.obtain();
                    effect.setPosition(enemy.b2body.getPosition().x, enemy.b2body.getPosition().y - 0.25f);
                    effectsBoss.add(effect);

                    enemy.setToDestroy(false);

                    //Actions à la mort du boss
                    endBossWall.setCategoryFilterFixture(GameTest.DESTROYED_BIT, fixtureEndBoss);
                    endBossWall.drawEndBossWall();

                    //On autorise la suppression de la barre de vie
                    drawHealthBarBoss = false;

                }else if(enemy.isSetToDestroy()){
                    System.out.println("setToDestroy Effect");
                    ParticleEffectPool.PooledEffect effect = pool.obtain();
                    effect.setPosition(enemy.b2body.getPosition().x, enemy.b2body.getPosition().y - 0.1f);
                    effects.add(effect);
                    enemy.setToDestroy(false);
                }

            }


            for (Item item : items) { //Dessine les objets
                item.draw(game.batch);
            }

            if(cameraChange){
                if(startEffect) {
                    ParticleEffectPool.PooledEffect effect = pool.obtain();
                    effect.setPosition(player.b2body.getPosition().x -0.4f, player.b2body.getPosition().y - 0.1f);
                    effects.add(effect);
                }
                startEffect = false;
            }

            game.batch.end();

            //HUD
            game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
            hud.stage.draw();

            //HealthBarBoss
            if(drawHealthBarBoss) {
                //game.batch.setProjectionMatrix(gamecam.combined);
                hbb.draw();
            }

            //Controller
            controller.draw();

            //Si il y a GameOver, on affiche l'écran de fin
            if (gameOver()) {
                game.setScreen(new GameOverScreen(game, usernameSession, level));
                dispose();
            }


        }

    }

    public boolean gameOver(){
        //Si il est mort depuis 3 secondes
        if(player.currentState == Player.State.DEAD && player.getStateTimer() >3){
            return true;
        }else return false;
    }




    @Override
    public void resize(int width, int height) {

        gamePort.update(width,height);
        controller.resize(width, height);
    }

    public TiledMap getMap(){
        return map;
    }

    public World getWorld(){
        return world;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();

    }

    //Positionne la caméra à l'intérieur de la map
    public static void boundary(Camera gamecam, float startX, float startY, float width, float height){
        Vector3 position = gamecam.position;

        if(position.x < startX){
            position.x = startX;
        }
        if(position.y < startY){
            position.y = startY;
        }

        if(position.x > startX + width){
            position.x = startX + width;
        }

        if(position.y > startY + height){
            position.y = startY + height;
        }

        gamecam.position.set(position);
        gamecam.update();
    }

    public static Player getPlayer(){
        return player;
    }

    public static void setEndLevel() {
        canPlay = false;
        movePlayerEnd = true;
    }

    //Boss
    public void fireBoss(Enemy enemy, boolean right){
        if(canFireBoss) {
            fireboss.add(new FireBoss(this, enemy.b2body.getPosition().x, enemy.b2body.getPosition().y, right));
            GameTest.manager.get("audio/sounds/fireball.ogg", Sound.class).play();
            canFireBoss = false;

                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        canFireBoss = true;
                    }
                }, 3);

        }
    }

    public static void cameraChangeBoss(boolean bool){
        cameraChange = bool;
        startEffect = bool;
        canPlay = false;

        Timer.schedule(new Timer.Task(){
                @Override
                public void run() {
                    moveCamera = false;
                    drawHealthBarBoss = true;
                }
            }, 1.55f);

        Timer.schedule(new Timer.Task(){
            @Override
            public void run() {
                canPlayBoss = true;
                canPlay = true;
            }
        }, 2f);

    }

    //Récupère les Fixture d'une aire de collision dans Area
    public static void setFixtureStartBoss(Fixture fix){
        fixtureStartBoss = fix;
    }

    public static Fixture getFixtureStartBoss(){
        return fixtureStartBoss;
    }

    public static void setFixtureEndBoss(Fixture fix){
        fixtureEndBoss = fix;
    }

    public static Fixture getFixtureEndBoss(){
        return fixtureEndBoss;
    }

    public static void setColorKnight(String color){
        player.setBuff(color);
        hud.addScore(300);
        GameTest.manager.get("audio/sounds/power_knight.ogg", Sound.class).play();
    }


}
