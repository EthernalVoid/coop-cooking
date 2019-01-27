/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dongbat.game.cook.logic;

/**
 *
 * @author tao
 */
public class GameInfo {
  
  public int numberOfPlayers;
  public int playerIndex;
  public int frame = 0;

  public GameInfo() {
  }

  public GameInfo(int numberOfPlayers, int playerIndex) {
    this.numberOfPlayers = numberOfPlayers;
    this.playerIndex = playerIndex;
  }
}
