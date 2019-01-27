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
import com.dongbat.game.cook.logic.GameInfo;
import com.dongbat.game.cook.logic.component.Dispenser;
import com.dongbat.game.cook.logic.component.Mixer;
import com.dongbat.game.cook.logic.component.Player;
import com.dongbat.game.cook.logic.component.Tool;
import com.dongbat.game.cook.logic.registry.ItemRegistry;

/**
 *
 * @author tao
 */
public class PlayerToolSystem extends IteratingSystem {

  private ComponentMapper<Tool> toolComponentMapper;
  private ComponentMapper<Player> playerComponentMapper;
  private ComponentMapper<Dispenser> dispenserComponentMapper;
  private ComponentMapper<Mixer> mixerComponentMapper;

  @Wire
  private ItemRegistry itemRegistry;

  @Wire
  private GameInfo gameInfo;

  public PlayerToolSystem() {
    super(Aspect.all(Player.class));
  }

  public void useItem(int playerId, int toolId) {
    Player player = playerComponentMapper.get(playerId);
    Tool tool = toolComponentMapper.get(toolId);

    player.toolId = toolId;

    tool.setLastUse(gameInfo.frame);
  }

  public void pickFromDispenser(int playerId, int dispenserId) {
    if (!dispenserComponentMapper.has(dispenserId)) {
      return;
    }
    Player player = playerComponentMapper.get(playerId);
    if (player.itemType != -1) {
      return;
    }
    Dispenser dispenser = dispenserComponentMapper.get(dispenserId);

    player.itemType = dispenser.materialType;
  }

  public void pickFromMixer(int playerId, int mixerId) {
    if (!mixerComponentMapper.has(mixerId)) {
      return;
    }
    Player player = playerComponentMapper.get(playerId);
    if (player.itemType != -1) {
      return;
    }
    Mixer mixer = mixerComponentMapper.get(mixerId);
    player.itemType = mixer.materialType;
    mixer.materialType = -1;
  }

  public void putOnMixer(int playerId, int mixerId) {
    if (!mixerComponentMapper.has(mixerId)) {
      return;
    }
    Player player = playerComponentMapper.get(playerId);
    if (player.itemType == -1) {
      return;
    }
    Mixer mixer = mixerComponentMapper.get(mixerId);
    if (mixer.materialType == -1) {
      mixer.materialType = player.itemType;
    } else {
      ItemRegistry.ItemData data = itemRegistry.datas.get(mixer.materialType);
      int resultType = data.itemTransforms.get(player.itemType, -1);
      mixer.materialType = resultType;
    }
    player.itemType = -1;
  }

  @Override
  protected void process(int id) {
    Player player = playerComponentMapper.get(id);
    if (player.toolId == -1) {
      return;
    }
    int toolId = player.toolId;
    Tool tool = toolComponentMapper.get(toolId);
    if (tool.isAvailable(gameInfo.frame)) {
      player.toolId = -1;

      if (player.itemType != -1) {
        ItemRegistry.ItemData data = itemRegistry.datas.get(player.itemType);
        player.itemType = data.toolTransforms.get(tool.type, -1);
      }
    }
  }

  public void useMixer(int id, int toolId) {
    if (!mixerComponentMapper.has(toolId)) {
      return;
    }
    Player player = playerComponentMapper.get(id);
    if (player.itemType == -1) {
      pickFromMixer(id, toolId);
    } else {
      putOnMixer(id, toolId);
    }
    
  }

}
