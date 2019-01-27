/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dongbat.game.cook.logic.systems;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.dongbat.game.cook.logic.GameInfo;
import com.dongbat.game.cook.logic.component.Dispenser;
import com.dongbat.game.cook.logic.component.Mixer;
import com.dongbat.game.cook.logic.component.Player;
import com.dongbat.game.cook.logic.component.Position;
import com.dongbat.game.cook.logic.component.Tool;
import com.dongbat.game.cook.logic.data.UseInput;
import com.dongbat.game.cook.logic.registry.ItemRegistry;

/**
 *
 * @author tao
 */
public class ToolTipSystem extends BaseEntitySystem {

  private ImageButton button;
  private BitmapFont font;

  @Wire
  private ItemRegistry itemRegistry;

  @Wire
  private GameInfo gameInfo;

  @Wire
  private FitViewport fitViewport;
  private Label label;
  private ImageButton p2Btn;

  private PlayerSystem playerSystem;
  private InputHandlingSystem inputHandlingSystem;
  private ToolSystem toolSystem;

  private ComponentMapper<Position> positionComponentMapper;
  private ComponentMapper<Player> playerComponentMapper;

  private ComponentMapper<Mixer> mixerComponentMapper;
  private ComponentMapper<Dispenser> dispenserComponentMapper;
  private ComponentMapper<Tool> toolComponentMapper;

  private final Stage stage = new Stage();
  private final Texture use;

  public ToolTipSystem() {
    super(Aspect.one(Dispenser.class, Mixer.class, Tool.class));
    use = new Texture("quest-mark.png");
  }

  @Override
  protected void initialize() {
    stage.setViewport(fitViewport);

    button = new ImageButton(new TextureRegionDrawable(use));
    button.setSize(50, 50);
    
    Sprite sprite = new Sprite(use);
    sprite.setColor(Color.RED);
    p2Btn = new ImageButton(new SpriteDrawable(sprite));
    p2Btn.setSize(50, 50);
    
    font = new BitmapFont();

    label = new Label("", new Label.LabelStyle(font, Color.WHITE));

    label.setPosition(10, 10);

    stage.addActor(button);
    stage.addActor(label);
    stage.addActor(p2Btn);
  }

  @Override
  protected void processSystem() {
    int playerId = playerSystem.indexToId.get(gameInfo.playerIndex, 0);
    Position playerPos = positionComponentMapper.get(playerId);

    int activeTool = -1;
    float minDistance = Float.MAX_VALUE;

    float buttonX = 0, buttonY = 0;

    IntBag ids = getEntityIds();
    for (int i = 0; i < ids.size(); i++) {
      int id = ids.get(i);
      Position pos = positionComponentMapper.get(id);

      float dst2 = Vector2.dst2(playerPos.x, playerPos.y, pos.x, pos.y);
      if (dst2 < 4000 && minDistance > dst2) {
        minDistance = dst2;
        activeTool = id;
        buttonX = pos.x - 25;
        buttonY = pos.y - 13;
      }
    }

    boolean useItem = Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Keys.SPACE);

    if (useItem && activeTool != -1) {
      if (toolComponentMapper.has(activeTool)) {
        Tool tool = toolComponentMapper.get(activeTool);
        UseInput useInput = Pools.obtain(UseInput.class);
        useInput.set(gameInfo.playerIndex, tool.id);
        inputHandlingSystem.useInputs.add(useInput);
      } else if (dispenserComponentMapper.has(activeTool)) {
        Dispenser tool = dispenserComponentMapper.get(activeTool);
        UseInput useInput = Pools.obtain(UseInput.class);
        useInput.set(gameInfo.playerIndex, tool.id);
        inputHandlingSystem.useInputs.add(useInput);
      } else if (mixerComponentMapper.has(activeTool)) {
        Mixer tool = mixerComponentMapper.get(activeTool);
        UseInput useInput = Pools.obtain(UseInput.class);
        useInput.set(gameInfo.playerIndex, tool.id);
        inputHandlingSystem.useInputs.add(useInput);
      }
    }

    Player player = playerComponentMapper.get(playerId);
    if (player.itemType != -1) {
      label.setText("You are holding " + itemRegistry.getName(player.itemType));
    } else {
      label.setText("Your hands are empty!");
    }

    if (activeTool == -1) {
      button.setVisible(false);
    } else {
      button.setVisible(true);
    }

    button.setPosition(buttonX, buttonY);

    playerId = playerSystem.indexToId.get(1, 0);
    playerPos = positionComponentMapper.get(playerId);
    activeTool = -1;
    minDistance = Float.MAX_VALUE;
    buttonX = 0;
    buttonY = 0;

    ids = getEntityIds();
    for (int i = 0; i < ids.size(); i++) {
      int id = ids.get(i);
      Position pos = positionComponentMapper.get(id);

      float dst2 = Vector2.dst2(playerPos.x, playerPos.y, pos.x, pos.y);
      if (dst2 < 4000 && minDistance > dst2) {
        minDistance = dst2;
        activeTool = id;
        buttonX = pos.x - 25;
        buttonY = pos.y - 13;
      }
    }
    
    if (activeTool == -1) {
      p2Btn.setVisible(false);
    } else {
      p2Btn.setVisible(true);
    }
    
    p2Btn.setPosition(buttonX, buttonY);

    boolean player2UseItem = Gdx.input.isKeyJustPressed(Keys.SHIFT_RIGHT);

    if (player2UseItem) {
      if (activeTool != -1) {
        if (toolComponentMapper.has(activeTool)) {
          Tool tool = toolComponentMapper.get(activeTool);
          UseInput useInput = Pools.obtain(UseInput.class);
          useInput.set(1, tool.id);
          inputHandlingSystem.useInputs.add(useInput);
        } else if (dispenserComponentMapper.has(activeTool)) {
          Dispenser tool = dispenserComponentMapper.get(activeTool);
          UseInput useInput = Pools.obtain(UseInput.class);
          useInput.set(1, tool.id);
          inputHandlingSystem.useInputs.add(useInput);
        } else if (mixerComponentMapper.has(activeTool)) {
          Mixer tool = mixerComponentMapper.get(activeTool);
          UseInput useInput = Pools.obtain(UseInput.class);
          useInput.set(1, tool.id);
          inputHandlingSystem.useInputs.add(useInput);
        }
      }
    }
    
    stage.act(world.delta);
    stage.draw();
  }

  @Override
  protected void dispose() {
    stage.dispose();
    use.dispose();
    font.dispose();
  }

}
