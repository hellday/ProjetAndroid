package com.mygdx.game.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.GameTest;

/**
 * Created by Terry on 10/11/2016.
 */

public class Hud  implements Disposable{
    public Stage stage;
    public Viewport viewport;

    private Integer worldTimer;
    private float timeCount;
    private static Integer score;
    public static int heartcount;

    private Label countdownLabel;
    private static Label scoreLabel;
    private Label timeLabel;
    private Label levelLabel;
    private Label worldLabel;
    private Label marioLabel;
    private static Image[] hearts;


    private TextureAtlas atlas;
    private Skin skin;

    /** Informations au top de l'écran (score etc...), fondu avec le Playscreen **/
    public Hud(SpriteBatch sb){
        worldTimer = 300;
        timeCount = 0;
        score = 0;

        atlas = new TextureAtlas("skin/uiskin.atlas");
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"), atlas);

        viewport = new FitViewport(GameTest.V_WIDTH, GameTest.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);

        Table table = new Table();
        table.top();
        table.setFillParent(true);

        countdownLabel = new Label(String.format("%03d", worldTimer), skin);
        scoreLabel = new Label(String.format("%06d", score), skin);
        //timeLabel = new Label("TIME", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        timeLabel = new Label("TIME", skin);
        levelLabel = new Label("1-1", skin);
        worldLabel = new Label("WORLD", skin);
        marioLabel = new Label("SCORE", skin);

//        Image upImg = new Image(new Texture("controller/a_controller.png"));
//        //upImg.setSize(75, 75);
//        upImg.setPosition(200,200);
//        upImg.addListener(new ClickListener(){
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                System.out.println("Bouton Pause");
//            }
//        });

        table.add().expandX().padTop(10);
        table.add(marioLabel).expandX().padTop(10);
        table.add(timeLabel).expandX().padTop(10);
        table.row();
        table.add().expandX();
        table.add(scoreLabel).expandX();
        table.add(countdownLabel).expandX();

        stage.addActor(table);
        hearts = new Image[3];
        heartcount = 3;
        for (int i=0; i<3;i++)
        {
            hearts[i] = new Image(new Texture("heart.png"));
            hearts[i].setHeight(20);
            hearts[i].setWidth(20);
            hearts[i].setPosition(20+i*20,175);
            stage.addActor(hearts[i]);
        }

    }

    //Constructeur par défaut
    public Hud(){

    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public void update(float dt){

        //Timer
        timeCount += dt;

        if(timeCount >= 1){
            worldTimer--;
            countdownLabel.setText(String.format("%03d", worldTimer));
            timeCount = 0;
        }
    }

    public static void addScore(int value){
        score += value;
        scoreLabel.setText(String.format("%06d", score));
    }

    public static void damage(int value){
        heartcount -=value;
        System.out.print(" dans damage");
        for (int i = 2;i>=heartcount;i--)
        {
            hearts[i].setVisible(false);
        }
    }

    public static  void heal(int value){
        heartcount +=value;
        for (int i = 0;i<heartcount;i++)
        {
            hearts[i].setVisible(true);
        }
    }

    public Integer getScore() {
        return score;
    }
}
