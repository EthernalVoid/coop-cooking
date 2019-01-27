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
public class Mixer extends PooledComponent {

  public int materialType = -1;
  public int id;

  public Mixer() {
  }
  
  @Override
  protected void reset() {
    materialType = -1;
    id = 0;
  }

}
