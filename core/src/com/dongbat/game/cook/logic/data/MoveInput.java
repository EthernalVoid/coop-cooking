/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dongbat.game.cook.logic.data;

import com.badlogic.gdx.utils.Pool;

/**
 *
 * @author tao
 */
public class MoveInput implements Pool.Poolable {

  public int playerIndex;
  public float x, y;

  public MoveInput() {
  }

  public void set(int playerIndex, float x, float y) {
    this.playerIndex = playerIndex;
    this.x = x;
    this.y = y;
  }

  @Override
  public void reset() {
    playerIndex = 0;
    x = 0;
    y = 0;
  }

}
