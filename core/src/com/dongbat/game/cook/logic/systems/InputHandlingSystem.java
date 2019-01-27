/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dongbat.game.cook.logic.systems;

import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.dongbat.game.cook.logic.component.Velocity;
import com.dongbat.game.cook.logic.data.MoveInput;
import com.dongbat.game.cook.logic.data.UseInput;

/**
 *
 * @author tao
 */
public class InputHandlingSystem extends BaseSystem {

  private ComponentMapper<Velocity> velocityComponentMapper;

  public PlayerSystem playerSystem;
  public ToolSystem toolSystem;
  public PlayerToolSystem playerToolSystem;

  // TODO: change this to use map and frame
  public final Array<MoveInput> moveInputs = new Array<MoveInput>();
  public final Array<UseInput> useInputs = new Array<UseInput>();

  private final Vector2 tmp = new Vector2();

  @Override
  protected void processSystem() {
    for (MoveInput moveInput : moveInputs) {
      int id = playerSystem.indexToId.get(moveInput.playerIndex, -1);
      if (id == -1) {
        continue;
      }
      tmp.set(moveInput.x, moveInput.y);
      tmp.nor().scl(300);
      velocityComponentMapper.get(id).set(tmp.x, tmp.y);
    }

    // TODO: this cannot run on a networked env
    for (MoveInput moveInput : moveInputs) {
      Pools.free(moveInput);
    }
    moveInputs.clear();

    for (UseInput useInput : useInputs) {
      int id = playerSystem.indexToId.get(useInput.playerIndex, -1);
      if (id == -1) {
        continue;
      }

      int toolId = toolSystem.indexToId.get(useInput.toolIndex, -1);

      if (toolSystem.isTool(toolId)) {
        playerToolSystem.useItem(id, toolId);
      } else if (toolSystem.isDispenser(toolId)) {
        playerToolSystem.pickFromDispenser(id, toolId);
      } else if (toolSystem.isMixer(toolId)) {
        playerToolSystem.useMixer(id, toolId);
      }
    }

    for (UseInput useInput : useInputs) {
      Pools.free(useInput);
    }

    useInputs.clear();
  }

}
