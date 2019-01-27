/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dongbat.game.cook.logic.component;

import com.artemis.PooledComponent;
import com.badlogic.gdx.utils.IntArray;

/**
 *
 * @author tao
 */
public class Goal extends PooledComponent {

  public final IntArray disks = new IntArray();

  @Override
  protected void reset() {
    disks.clear();
  }

}
