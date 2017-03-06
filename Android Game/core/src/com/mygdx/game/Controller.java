package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by Terry
 */
public class Controller {
    Viewport viewport;
    public Stage stage;
    boolean upPressed, downPressed, leftPressed, rightPressed, pausePressed;
    OrthographicCamera cam;

    public Controller(){
        cam = new OrthographicCamera();
        viewport = new FitViewport(800, 480, cam);
        stage = new Stage(viewport, GameTest.batch);

        stage.addListener(new InputListener(){

            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                switch(keycode){
                    case Input.Keys.UP:
                        upPressed = true;
                        break;
                    case Input.Keys.DOWN:
                        downPressed = true;
                        break;
                    case Input.Keys.LEFT:
                        leftPressed = true;
                        break;
                    case Input.Keys.RIGHT:
                        rightPressed = true;
                        break;
//                    case Input.Keys.P:
//                        pausePressed = true;
//                        break;
                }
                return true;
            }

            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                switch(keycode){
                    case Input.Keys.UP:
                        upPressed = false;
                        break;
                    case Input.Keys.DOWN:
                        downPressed = false;
                        break;
                    case Input.Keys.LEFT:
                        leftPressed = false;
                        break;
                    case Input.Keys.RIGHT:
                        rightPressed = false;
                        break;
//                    case Input.Keys.P:
//                        pausePressed = false;
//                        break;
                }
                return true;
            }
        });

        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.left().bottom();

        Image upImg = new Image(new Texture("controller/a_controller.png"));
        upImg.setSize(75, 75);
        upImg.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                upPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                upPressed = false;
            }
        });

        Image downImg = new Image(new Texture("controller/b_controller.png"));
        downImg.setSize(75, 75);
        downImg.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                downPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                downPressed = false;
            }
        });

        Image pauseImg = new Image(new Texture("controller/start_controller.png"));
        pauseImg.setSize(80, 40);
        pauseImg.setPosition(360, 10);

        pauseImg.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                pausePressed = true;
            }
        });


        Image rightImg = new Image(new Texture("controller/right_controller.png"));
        rightImg.setSize(75, 75);
        rightImg.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                rightPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                rightPressed = false;
            }
        });

        Image leftImg = new Image(new Texture("controller/left_controller.png"));
        leftImg.setSize(75, 75);
        leftImg.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                leftPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                leftPressed = false;
            }
        });


        table.add();
        table.row().pad(5, 5, 5, 5);
        table.add(leftImg).size(leftImg.getWidth(), leftImg.getHeight());
        table.add();
        table.add(rightImg).size(rightImg.getWidth(), rightImg.getHeight());
        table.add();table.add();table.add();table.add();table.add();table.add();
        table.add();table.add();table.add();table.add();table.add();table.add();
        table.add();table.add();table.add();table.add();table.add();table.add();
        //table.add(pauseImg).size(pauseImg.getWidth(), pauseImg.getHeight());
        table.add();table.add();table.add();table.add();table.add();table.add();
        table.add();table.add();table.add();table.add();table.add();table.add();
        table.add();table.add();table.add();table.add();table.add();table.add();
        table.add();table.add();table.add();table.add();table.add();table.add();table.add();table.add();
        table.add(downImg).size(downImg.getWidth(), downImg.getHeight());
        table.add();
        table.add(upImg).size(upImg.getWidth(), upImg.getHeight());
        table.add();
        table.row().padBottom(15);
        table.add();

        stage.addActor(table);
        stage.addActor(pauseImg);
    }

    public void draw(){
        stage.draw();
    }

    public boolean isUpPressed() {
        return upPressed;
    }

    public boolean isDownPressed() {
        return downPressed;
    }

    public boolean isLeftPressed() {
        return leftPressed;
    }

    public boolean isRightPressed() {
        return rightPressed;
    }

    public boolean isPausePressed() {
        return pausePressed;
    }

    public void resize(int width, int height){
        viewport.update(width, height);
    }

    public void dispose () {
        stage.dispose();
    }

    public void setPausePressed(boolean pausePressed) {
        this.pausePressed = pausePressed;
    }

    public void setUpPressed(boolean upPressed) {
        this.upPressed = upPressed;
    }
}