package com.mygdx.game.scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.GameTest;

/**
 * Created by Terry on 21/03/2017.
 */
public class HealthBarBoss implements Disposable {

    private static ShapeRenderer shapeRenderer;
    public Stage stage;
    public Viewport viewport;
    public static int health;
    public SpriteBatch batch;

    public HealthBarBoss(SpriteBatch sb){
        batch = sb;
        shapeRenderer = new ShapeRenderer();
        viewport = new FitViewport(GameTest.V_WIDTH, GameTest.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);
        health = 125;


    }

    @Override
    public void dispose() {
        stage.dispose();
        shapeRenderer.dispose();
    }

    public void draw(){

        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        stage.draw();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(130, 30, 135, 20);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(135, 35, health, 10);
        shapeRenderer.end();
    }

    public static void setHealth(){
        health = health - 25;
        System.out.println("Vie : " + health);
    }

    public static void delete(){
        shapeRenderer.dispose();
    }
}
