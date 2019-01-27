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
public class UseInput implements Pool.Poolable {

  public int playerIndex;
  public int toolIndex;

  public UseInput() {
  }

  public void set(int playerIndex, int toolIndex) {
    this.playerIndex = playerIndex;
    this.toolIndex = toolIndex;
  }

  @Override
  public void reset() {
    playerIndex = 0;
    toolIndex = 0;
  }

}
