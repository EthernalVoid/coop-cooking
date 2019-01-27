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
public class Tool extends PooledComponent {

  public int type;
  public int id = -1;
  public int duration = 0;
  public int lastUse = -1;
  
  public Tool setId(int id) {
    this.id = id;
    return this;
  }

  public boolean isAvailable(int frame) {
    return lastUse == -1 || frame >= lastUse + duration;
  }

  public Tool setType(int type) {
    this.type = type;
    return this;
  }

  public Tool setDuration(int duration) {
    this.duration = duration;
    return this;
  }

  public Tool setLastUse(int frame) {
    this.lastUse = frame;
    return this;
  }

  @Override
  protected void reset() {
    type = -1;
    id = -1;
    duration = 0;
    lastUse = -1;
  }
}
