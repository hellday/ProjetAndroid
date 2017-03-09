package com.mygdx.game.Effects;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Terry on 08/03/2017.
 */

public class Particles implements Screen {
    private SpriteBatch batch;
    private ParticleEffect pe;


    @Override
    public void render (float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        pe.update(Gdx.graphics.getDeltaTime());
        batch.begin();
        pe.draw(batch);
        batch.end();
        if (pe.isComplete())
            pe.reset();
    }

    @Override
    public void resize(int width, int height) {

    }

    public void dispose(){

    }

    public void hide(){

    }

    public void show(){
        batch = new SpriteBatch();

        pe = new ParticleEffect();
        pe.load(Gdx.files.internal("effects/spectre_dead.p"), Gdx.files.internal("effects"));
        pe.setPosition(Gdx.graphics.getWidth()/2,Gdx.graphics.getHeight()/2);
        pe.start();
    }

    public void pause(){

    }

    public void resume(){

    }
}
