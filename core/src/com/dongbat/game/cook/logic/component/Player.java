/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dongbat.game.cook.logic.component;

import com.artemis.PooledComponent;
import com.artemis.annotations.EntityId;

/**
 *
 * @author tao
 */
public class Player extends PooledComponent {

  public int index = 0;
  public int itemType = -1;

  @EntityId
  public int toolId = -1;

  @Override
  protected void reset() {
    itemType = -1;
    toolId = -1;
    index = 0;
  }

}
