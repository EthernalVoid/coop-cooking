/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dongbat.game.cook.logic.systems;

import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.utils.Pools;
import com.dongbat.game.cook.logic.GameInfo;
import com.dongbat.game.cook.logic.data.MoveInput;

/**
 *
 * @author tao
 */
public class InputSamplingSystem extends BaseSystem {

  private InputHandlingSystem inputHandlingSystem;
  @Wire
  private GameInfo gameInfo;

  @Override
  protected void processSystem() {
    float x = 0;
    float y = 0;
    if (Gdx.input.isKeyPressed(Keys.W)) {
      y = 1;
    } else if (Gdx.input.isKeyPressed(Keys.S)) {
      y = -1;
    }
    if (Gdx.input.isKeyPressed(Keys.A)) {
      x = -1;
    } else if (Gdx.input.isKeyPressed(Keys.D)) {
      x = 1;
    }
    
    MoveInput moveInput = Pools.obtain(MoveInput.class);
    moveInput.set(0, x, y);
    inputHandlingSystem.moveInputs.add(moveInput);
    x = 0;
    y = 0;
    
    if (Gdx.input.isKeyPressed(Keys.UP)) {
      y = 1;
    } else if (Gdx.input.isKeyPressed(Keys.DOWN)) {
      y = -1;
    }
    if (Gdx.input.isKeyPressed(Keys.LEFT)) {
      x = -1;
    } else if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
      x = 1;
    }
    
    moveInput = Pools.obtain(MoveInput.class);
    moveInput.set(1, x, y);
    inputHandlingSystem.moveInputs.add(moveInput);
  }

}
