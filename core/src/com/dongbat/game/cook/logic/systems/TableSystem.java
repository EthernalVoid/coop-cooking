/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dongbat.game.cook.logic.systems;

import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.World;

/**
 *
 * @author tao
 */
public class TableSystem extends BaseSystem {

  @Wire
  private TiledMap tiledMap;
  @Wire
  private World physicsWorld;
  
  public final Array<Rectangle> tables = new Array<Rectangle>();

  @Override
  protected void initialize() {
    MapLayer tableLayer = tiledMap.getLayers().get("table");
    if (tableLayer == null) {
      return;
    }
    MapObjects objects = tableLayer.getObjects();
    for (MapObject object : objects) {
      if (object instanceof RectangleMapObject) {
        RectangleMapObject rectangleMapObject = (RectangleMapObject) object;
        Rectangle rectangle = rectangleMapObject.getRectangle();
        physicsWorld.add(new Item(), rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        tables.add(rectangle);
      }
    }
  }

  @Override
  protected void processSystem() {

  }

}
