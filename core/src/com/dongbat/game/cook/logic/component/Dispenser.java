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
public class Dispenser extends PooledComponent {

  public int materialType;
  public int id;

  public Dispenser() {
  }

  @Override
  protected void reset() {
    materialType = 0;
    id = 0;
  }

}
