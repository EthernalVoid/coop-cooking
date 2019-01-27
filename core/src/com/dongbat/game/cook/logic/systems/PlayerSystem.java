/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dongbat.game.cook.logic.systems;

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.IntMap;
import com.dongbat.game.cook.logic.GameInfo;
import com.dongbat.game.cook.logic.component.Player;
import com.dongbat.game.cook.logic.component.Position;
import com.dongbat.game.cook.logic.component.Velocity;
import com.dongbat.jbump.CollisionFilter;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.Rect;
import com.dongbat.jbump.World;

/**
 *
 * @author tao
 */
public class PlayerSystem extends IteratingSystem {

  private Archetype playerArchetype;
  private ComponentMapper<Position> positionComponentMapper;
  private ComponentMapper<Player> playerComponentMapper;
  private ComponentMapper<Velocity> velocityComponentMapper;
  
  @Wire
  private TiledMap map;
  @Wire
  private GameInfo gameInfo;
  @Wire
  private World physicsWorld;
  
  public final IntMap<Item> playerBodies = new IntMap<Item>();
  public final IntIntMap indexToId = new IntIntMap();

  public PlayerSystem() {
    super(Aspect.all(Player.class, Position.class, Velocity.class));
  }

  @Override
  protected void initialize() {
    playerArchetype = new ArchetypeBuilder().add(Player.class).add(Velocity.class).add(Position.class).build(world);
    
    MapLayer infoLayer = map.getLayers().get("info");
    if (infoLayer == null) return;
    MapObject spawn = infoLayer.getObjects().get("spawn");
    if (spawn != null && spawn instanceof RectangleMapObject) {
      Rectangle spawnRect = ((RectangleMapObject)spawn).getRectangle();
      for (int i = 0; i < gameInfo.numberOfPlayers; i++) {
        float x = MathUtils.random(spawnRect.getX(), spawnRect.getX() + spawnRect.getWidth());
        float y = MathUtils.random(spawnRect.getY(), spawnRect.getY() + spawnRect.getHeight());
        
        createPlayer(x, y, i);
      }
    }
  }
  
  public int createPlayer(float x, float y, int index) {
    int id = world.create(playerArchetype);
    positionComponentMapper.get(id).set(x, y);
    playerComponentMapper.get(id).index = index;
    
    Item item = physicsWorld.add(new Item(), x, y, 24, 24);
    playerBodies.put(index, item);
    
    return id;
  }
  
  private final Vector2 tmp = new Vector2();

  @Override
  protected void begin() {
    indexToId.clear();
    IntBag entityIds = getEntityIds();
    for (int i = 0; i < entityIds.size(); i++) {
      int id = entityIds.get(i);
      Player player = playerComponentMapper.get(id);
      indexToId.put(player.index, id);
    }
  }

  @Override
  protected void process(int id) {
    Player player = playerComponentMapper.get(id);
    Position position = positionComponentMapper.get(id);
    Velocity velocity = velocityComponentMapper.get(id);
    
    Item item = playerBodies.get(player.index);
    
    physicsWorld.update(item, position.x, position.y);
    
    if (player.toolId != -1) {
      velocity.set(0, 0);
    }
    
    tmp.set(velocity.x, velocity.y)
      .scl(world.delta)
      .add(position.x, position.y);
    
    physicsWorld.move(item, tmp.x, tmp.y, CollisionFilter.defaultFilter);
    Rect rect = physicsWorld.getRect(item);
    position.set(rect.x, rect.y);
  }
  
}
