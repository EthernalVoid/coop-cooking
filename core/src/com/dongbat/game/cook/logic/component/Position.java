/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dongbat.game.cook.logic.component;

import com.artemis.PooledComponent;

/**
 *
 * @author tao
 */
public class Position extends PooledComponent {
  
  public float x, y;

  public Position() {
  }

  public Position(float x, float y) {
    this.x = x;
    this.y = y;
  }
  
  public Position set(float x, float y) {
    this.x = x;
    this.y = y;
    return this;
  }

  @Override
  protected void reset() {
    x = 0;
    y = 0;
  }
  
}
