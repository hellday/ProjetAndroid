package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.Controller;
import com.mygdx.game.GameTest;
import com.mygdx.game.Items.Item;
import com.mygdx.game.Items.ItemDef;
import com.mygdx.game.Items.Mushroom;
import com.mygdx.game.scenes.EndLevel;
import com.mygdx.game.scenes.Hud;
import com.mygdx.game.sprites.CollisionWall.Area;
import com.mygdx.game.sprites.Enemies.Enemy;
import com.mygdx.game.sprites.Player.Mario;
import com.mygdx.game.tools.B2WorldCreator;
import com.mygdx.game.tools.WorldContactListener;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Terry on 09/11/2016.
 */

public class PlayScreen implements Screen{

    private GameTest game;
    private TextureAtlas atlas;

    private OrthographicCamera gamecam;
    public Viewport gamePort;
    private Hud hud;
    private EndLevel endLevel;

    //WorldContactListener
    private WorldContactListener wcl;

    //Variables de la map
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private int levelWidth;
    private int levelHeight;

    //Variables Box2d
    public World world;
    private Box2DDebugRenderer b2dr;
    private B2WorldCreator creator;

    //Sprites
    private static Mario player;


    private Music music;

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


    /** Constructeur de l'écran */
    public PlayScreen(GameTest game, String user, int lvl){

        atlas = new TextureAtlas("Mario_and_Enemies.pack");

        this.game = game;
        this.usernameSession = user;
        this.level = lvl;


        //Création d'une caméra qui va suivre notre personnage dans notre monde
        gamecam = new OrthographicCamera();
        gamePort = new FitViewport(GameTest.V_WIDTH / GameTest.PPM, GameTest.V_HEIGHT / GameTest.PPM, gamecam);
        gamePort.setCamera(gamecam);

        //Création du HUD (scores,timers...)
        hud = new Hud(game.batch);

        //Création de l'écran de fin de niveau
        endLevel = new EndLevel(game.batch);

        //Chargement de la map
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("level" + level + ".tmx");
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
        player = new Mario(this, usernameSession);

        wcl = new WorldContactListener();
        world.setContactListener(wcl);

        //Musique utilisation
        music = GameTest.manager.get("audio/music/mario_music.ogg", Music.class);
        music.setLooping(true);
        //music.play();

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

        //Si mario n'est pas mort, sinon on n'active pas les touches
        if(canPlay) {
            if (player.currentState != Mario.State.DEAD) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && player.b2body.getLinearVelocity().y == 0 && wcl.isPlayerIsOnGround()) { //SAUT
                    player.b2body.applyLinearImpulse(new Vector2(0, 3f), player.b2body.getWorldCenter(), true);
                    GameTest.manager.get("audio/sounds/jump_small.wav", Sound.class).play();

                    //Si le joueur est BLEU il peut sauter 2 fois
                    if(player.getBuff()== Mario.Color.BLUE) {
                        canDoubleJump = true;
                    }
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

                //Controller
                if (controller.isRightPressed())
                    player.b2body.setLinearVelocity(new Vector2(1, player.b2body.getLinearVelocity().y));

                else if (controller.isLeftPressed())
                    player.b2body.setLinearVelocity(new Vector2(-1, player.b2body.getLinearVelocity().y));

                else
                    if (player.b2body.getLinearVelocity().x>2)
                    {
                        player.b2body.setLinearVelocity(new Vector2(player.b2body.getLinearVelocity().x-0.1f, player.b2body.getLinearVelocity().y));
                    }
                    else if (player.b2body.getLinearVelocity().x<-2)
                    {
                        player.b2body.setLinearVelocity(new Vector2(player.b2body.getLinearVelocity().x+0.1f, player.b2body.getLinearVelocity().y));
                    }
                    else
                    {
                        player.b2body.setLinearVelocity(new Vector2(0, player.b2body.getLinearVelocity().y));
                    }

                if (controller.isUpPressed() && player.b2body.getLinearVelocity().y == 0 && wcl.isPlayerIsOnGround()) {
                    player.b2body.applyLinearImpulse(new Vector2(0, 3f), player.b2body.getWorldCenter(), true);
                    GameTest.manager.get("audio/sounds/jump_small.wav", Sound.class).play();


                }

                if (controller.isDownPressed()) {
                    if (canFire) {
                        player.attack();
                        player.setAttack(true);
                        canFire = false;
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

        for(Enemy enemy : creator.getEnemies()) { //Ennemies
            enemy.update(dt);
            if (enemy.getX() < player.getX() + 224 / GameTest.PPM)
                enemy.b2body.setActive(true);
        }

        for(Item item : items){
            item.update(dt);
        }

        hud.update(dt);
        endLevel.update(dt);

        //Controle de la caméra
        float startX = gamecam.viewportWidth / 2;
        float startY = gamecam.viewportHeight / 2;

        if(player.currentState != Mario.State.DEAD) {
            gamecam.position.x = player.b2body.getPosition().x;
            gamecam.position.y = player.b2body.getPosition().y;
            boundary(gamecam, startX, startY, levelWidth * GameTest.PPM - startX * 2, levelHeight * GameTest.PPM - startY * 2);
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

            GameTest.manager.get("audio/music/mario_music.ogg", Music.class).stop();
            GameTest.manager.get("audio/music/stage_clear.ogg", Music.class).play();

            float delay = 4; // seconds

            Timer.schedule(new Timer.Task(){
                @Override
                public void run() {
                   //game.setScreen(new GameOverScreen(game, usernameSession));
//                    game.batch.setProjectionMatrix(endLevel.stage.getCamera().combined);
//                    endLevel.stage.draw();
                    String score = hud.getScore().toString();
                    game.setScreen(new EndLevelScreen(game, usernameSession, level));
                }
            }, delay);

            movePlayerEnd = false;
        }

    }

    @Override
    /** Affiche/dessine les informations à l'écran */
    public void render(float delta) {
        update(delta);

        //Nettoie l'écran de jeu avec du Noir
        Gdx.gl.glClearColor(0, 0 , 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();

        //Affichage des DEBUG (Bodies et collision..)
        b2dr.render(world, gamecam.combined);

        game.batch.setProjectionMatrix(gamecam.combined);
        game.batch.begin();

        player.draw(game.batch); //Dessine le joueur

        for (Enemy enemy : creator.getEnemies()) //Dessine les ennemies
            enemy.draw(game.batch);

        for(Item item : items){ //Dessine les objets
            item.draw(game.batch);
        }

        game.batch.end();

        //HUD
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        //Controller
        controller.draw();

        //Si il y a GameOver, on affiche l'écran de fin
        if(gameOver()){
            game.setScreen(new GameOverScreen(game, usernameSession, level));
            dispose();
        }

    }

    public boolean gameOver(){
        //Si il est mort depuis 3 secondes
        if(player.currentState == Mario.State.DEAD && player.getStateTimer() >3){
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

    public static Mario getPlayer(){
        return player;
    }

    public static void setEndLevel() {
        canPlay = false;
        movePlayerEnd = true;
    }






}
