/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dongbat.game.cook.logic;

import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.dongbat.game.cook.logic.registry.ItemRegistry;
import com.dongbat.game.cook.logic.systems.BgRenderSystem;
import com.dongbat.game.cook.logic.systems.InputHandlingSystem;
import com.dongbat.game.cook.logic.systems.InputSamplingSystem;
import com.dongbat.game.cook.logic.systems.ItemTooltipSystem;
import com.dongbat.game.cook.logic.systems.MapRenderSystem;
import com.dongbat.game.cook.logic.systems.PlayerRenderSystem;
import com.dongbat.game.cook.logic.systems.PlayerSystem;
import com.dongbat.game.cook.logic.systems.PlayerToolSystem;
import com.dongbat.game.cook.logic.systems.TableSystem;
import com.dongbat.game.cook.logic.systems.ToolSystem;
import com.dongbat.game.cook.logic.systems.ToolTipSystem;

/**
 *
 * @author tao
 */
public class Simulation implements Disposable {

  private final FitViewport fitViewport;
  private final GameInfo gameInfo;

  private final World world;

  public Simulation() {
    fitViewport = new FitViewport(600, 300);
    resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

    gameInfo = new GameInfo(2, 0);

    WorldConfiguration wc = new WorldConfiguration();
    TiledMap map = new TmxMapLoader().load("kitchen01.tmx");
    wc.register(map);
    wc.register(gameInfo);
    wc.register(new com.dongbat.jbump.World<Object>());
    wc.register(fitViewport);
    wc.register(new ItemRegistry());

    wc.setSystem(new PlayerSystem())
      .setSystem(new TableSystem())
      .setSystem(new InputHandlingSystem())
      .setSystem(new InputSamplingSystem())
      .setSystem(new ToolSystem())
      .setSystem(new PlayerToolSystem())
      .setSystem(new BgRenderSystem())
//      .setSystem(new DebugRenderSystem())
      .setSystem(new PlayerRenderSystem())
      .setSystem(new ItemTooltipSystem())
      .setSystem(new MapRenderSystem())
      .setSystem(new ToolTipSystem());
    world = new World(wc);
  }

  public void update(float delta) {
    world.setDelta(delta);
    world.process();
    gameInfo.frame++;
  }

  public final void resize(int w, int h) {
    fitViewport.update(w, h, true);
  }

  @Override
  public void dispose() {
    world.dispose();
  }

}
