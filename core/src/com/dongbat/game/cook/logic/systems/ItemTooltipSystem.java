/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dongbat.game.cook.logic.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ObjectMap;
import com.dongbat.game.cook.logic.component.Mixer;
import com.dongbat.game.cook.logic.component.Player;
import com.dongbat.game.cook.logic.component.Position;
import com.dongbat.game.cook.logic.registry.ItemRegistry;

/**
 *
 * @author tao
 */
public class ItemTooltipSystem extends IteratingSystem {

  private final TextureRegion bubble;

  private final ObjectMap<String, TextureRegion> materialTextures = new ObjectMap<String, TextureRegion>();

  private ComponentMapper<Player> playerComponentMapper;
  private ComponentMapper<Mixer> mixerComponentMapper;
  private ComponentMapper<Position> positionComponentMapper;

  private MapRenderSystem mapRenderSystem;
  
  @Wire
  private ItemRegistry itemRegistry;

  public ItemTooltipSystem() {
    super(Aspect.one(Player.class, Mixer.class));

    bubble = new TextureRegion(new Texture("bubble.png"));

    String[] materials = new String[]{"groundedBeef", "lettuce", "mashedPotato", "potato",
      "potatoChip", "salad", "searedBeef", "slicedPotato", "steak"};
    
    for (String material : materials) {
      materialTextures.put(material, new TextureRegion(new Texture("material/" + material + ".png")));
    }
  }

  @Override
  protected void process(int id) {
    Position pos = positionComponentMapper.get(id);

    int itemType = -1;
    float offsetX = 0, offsetY = 0;

    if (playerComponentMapper.has(id)) {
      Player player = playerComponentMapper.get(id);
      itemType = player.itemType;
      offsetX = 10;
      offsetY = 10;
    } else if (mixerComponentMapper.has(id)) {
      Mixer mixer = mixerComponentMapper.get(id);
      itemType = mixer.materialType;
    }

    if (itemType != -1) {
      mapRenderSystem.pending(bubble, pos.x + 5 + offsetX, pos.y + offsetY, 50, 50, false, false, 1, 0, Color.WHITE, MapRenderSystem.PendingDraw.Layer.Effect);
      TextureRegion region = materialTextures.get(itemRegistry.getName(itemType));
      if (region != null) {
        mapRenderSystem.pending(region, pos.x + 5 + offsetX + 12, pos.y + offsetY + 17, 26, 26, false, false, 1, 0, Color.WHITE, MapRenderSystem.PendingDraw.Layer.Overlay);
      }
    }
  }

  @Override
  protected void dispose() {
    bubble.getTexture().dispose();
    for (ObjectMap.Entry<String, TextureRegion> entry : materialTextures) {
      entry.value.getTexture().dispose();
    }
    materialTextures.clear();
  }

}
