/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dongbat.game.cook.logic.systems;

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntIntMap;
import com.dongbat.game.cook.logic.component.Dispenser;
import com.dongbat.game.cook.logic.component.Mixer;
import com.dongbat.game.cook.logic.component.Position;
import com.dongbat.game.cook.logic.component.Tool;
import com.dongbat.game.cook.logic.registry.ItemRegistry;

/**
 *
 * @author tao
 */
public class ToolSystem extends BaseEntitySystem {

  private Archetype dispenserArchetype;
  private Archetype mixerArchetype;

  private Archetype toolArchetype;
  private ComponentMapper<Position> positionComponentMapper;
  private ComponentMapper<Dispenser> dispenserComponentMapper;
  private ComponentMapper<Mixer> mixerComponentMapper;
  private ComponentMapper<Tool> toolComponentMapper;
  
  public boolean isTool(int id) {
    return toolComponentMapper.has(id);
  }
  
  public boolean isDispenser(int id) {
    return dispenserComponentMapper.has(id);
  }
  
  public boolean isMixer(int id) {
    return mixerComponentMapper.has(id);
  }

  @Wire
  private TiledMap map;
  
  @Wire
  private ItemRegistry itemRegistry;
  
  public final IntIntMap indexToId = new IntIntMap();

  public ToolSystem() {
    super(Aspect.one(Dispenser.class, Mixer.class, Tool.class));
  }

  @Override
  protected void initialize() {
    toolArchetype = new ArchetypeBuilder()
      .add(Position.class, Tool.class)
      .build(world);

    dispenserArchetype = new ArchetypeBuilder().add(Position.class, Dispenser.class).build(world);
    mixerArchetype = new ArchetypeBuilder().add(Mixer.class, Position.class).build(world);

    MapLayer kitchenLayer = map.getLayers().get("kitchenware");
    if (kitchenLayer == null) {
      return;
    }

    MapObjects objects = kitchenLayer.getObjects();
    Vector2 tmp = new Vector2();
    for (MapObject object : objects) {
      if (!(object instanceof RectangleMapObject)) {
        continue;
      }
      RectangleMapObject rmo = (RectangleMapObject) object;
      Rectangle rectangle = rmo.getRectangle();
      rectangle.getCenter(tmp);

      String type = rmo.getProperties().get("type", null, String.class);
      if ("chop".equals(type) || "stove".equals(type)) {
        int toolId = world.create(toolArchetype);
        Tool tool = toolComponentMapper.get(toolId);

        tool.id = rmo.getProperties().get("id", Integer.class);
        int typeId = "chop".equals(type)? 0: 1;
        tool.type = typeId;

        tool.duration = 60;

        Position position = positionComponentMapper.get(toolId);
        position.set(tmp.x, tmp.y);
      } else if ("dispenser".equals(type)) {
        String material = rmo.getProperties().get("material", null, String.class);
        if (material != null) {
          int id = world.create(dispenserArchetype);

          Dispenser dispenser = dispenserComponentMapper.get(id);
          dispenser.materialType = itemRegistry.getId(material);
          dispenser.id = rmo.getProperties().get("id", Integer.class);

          Position position = positionComponentMapper.get(id);
          position.set(tmp.x, tmp.y);
        }
      } else if ("mixer".equals(type)) {
        int id = world.create(mixerArchetype);
        
        Mixer mixer = mixerComponentMapper.get(id);
        mixer.id = rmo.getProperties().get("id", Integer.class);
        
        Position position = positionComponentMapper.get(id);
        position.set(tmp.x, tmp.y);
      }
    }
  }

  @Override
  protected void processSystem() {
    IntBag ids = getEntityIds();
    for (int i = 0; i < ids.size(); i++) {
      int id = ids.get(i);
      if (toolComponentMapper.has(id)) {
        Tool tool = toolComponentMapper.get(id);
        indexToId.put(tool.id, id);
      } else if(dispenserComponentMapper.has(id)) {
        Dispenser dispenser = dispenserComponentMapper.get(id);
        indexToId.put(dispenser.id, id);
      } else {
        Mixer mixer = mixerComponentMapper.get(id);
        indexToId.put(mixer.id, id);
      }
    }
  }

}
