package com.dongbat.game.cook;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.dongbat.game.cook.logic.Simulation;

public class Core extends Game {

  private Simulation simulation;

  @Override
  public void create() {
    simulation = new Simulation();
  }

  @Override
  public void render() {
    Gdx.gl.glClearColor(0, 0, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    
    simulation.update(Gdx.graphics.getDeltaTime());
  }

  @Override
  public void resize(int width, int height) {
    simulation.resize(width, height);
  }

  @Override
  public void dispose() {
    simulation.dispose();
  }
}
