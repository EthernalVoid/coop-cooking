/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dongbat.game.cook.logic.systems;

import com.artemis.BaseSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 *
 * @author tao
 */
public class BgRenderSystem extends BaseSystem {

  private final TextureRegion bg;

  private MapRenderSystem mapRenderSystem;

  public BgRenderSystem() {
    Texture texture = new Texture("bg.png");
    bg = new TextureRegion(texture);
  }

  @Override
  protected void processSystem() {
    mapRenderSystem.pending(bg, 0, 0, 600, 300, false, false, 1, 0, Color.WHITE, MapRenderSystem.PendingDraw.Layer.Object);
  }

  @Override
  protected void dispose() {
    bg.getTexture().dispose();
  }

}
