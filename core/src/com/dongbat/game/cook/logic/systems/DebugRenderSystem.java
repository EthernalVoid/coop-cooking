/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dongbat.game.cook.logic.systems;

import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.dongbat.game.util.ShapeUtil;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.Rect;
import com.dongbat.jbump.World;

/**
 *
 * @author tao
 */
public class DebugRenderSystem extends BaseSystem {

  private PlayerSystem playerSystem;
  private TableSystem tableSystem;
  
  @Wire
  private World physicsWorld;
  @Wire
  private FitViewport viewport;

  private final Batch batch = new SpriteBatch();
  private final Texture white;

  public DebugRenderSystem() {
    white = ShapeUtil.createWhite();
  }

  @Override
  protected void processSystem() {
    batch.begin();
    batch.setProjectionMatrix(viewport.getCamera().combined);
    IntMap<Item> playerBodies = playerSystem.playerBodies;

    for (IntMap.Entry<Item> playerBody : playerBodies) {
      Rect rect = physicsWorld.getRect(playerBody.value);
      ShapeUtil.drawRect(batch, rect.x, rect.y, rect.w, rect.h, Color.RED, white);
    }

//    for (Rectangle rect : tableSystem.tables) {
//      ShapeUtil.drawRect(batch, rect.x, rect.y, rect.width, rect.height, Color.WHITE, white);
//    }
    batch.end();
  }

  @Override
  protected void dispose() {
    batch.dispose();
    white.dispose();
  }

}
