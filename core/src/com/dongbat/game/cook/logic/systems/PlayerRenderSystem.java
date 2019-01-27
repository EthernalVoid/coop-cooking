/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dongbat.game.cook.logic.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.dongbat.game.cook.logic.component.Player;
import com.dongbat.game.cook.logic.component.Position;
import com.dongbat.game.cook.logic.component.Velocity;

/**
 *
 * @author tao
 */
public class PlayerRenderSystem extends IteratingSystem {

  private final Animation<TextureAtlas.AtlasRegion> actAnimation;

  private final TextureAtlas atlas;
  private final Animation<TextureAtlas.AtlasRegion> idleAnimation;
  private MapRenderSystem mapRenderSystem;

  private ComponentMapper<Position> positionComponentMapper;
  private ComponentMapper<Player> playerComponentMapper;
  private ComponentMapper<Velocity> velocityComponentMapper;

  private float stateTime = 0;
  private final Animation<TextureAtlas.AtlasRegion> walkDownAnimation;
  private final Animation<TextureAtlas.AtlasRegion> walkLeftAnimation;
  private final Animation<TextureAtlas.AtlasRegion> walkRightAnimation;
  private final Animation<TextureAtlas.AtlasRegion> walkUpAnimation;

  public PlayerRenderSystem() {
    super(Aspect.all(Position.class, Player.class));

    atlas = new TextureAtlas("coop-cooking.atlas");

    idleAnimation = new Animation<TextureAtlas.AtlasRegion>(0.3f, atlas.findRegions("player_idle"));
    actAnimation = new Animation<TextureAtlas.AtlasRegion>(0.3f, atlas.findRegions("player_acting_left"));

    walkLeftAnimation = new Animation<TextureAtlas.AtlasRegion>(0.3f, atlas.findRegions("player_walk_left"));
    walkRightAnimation = new Animation<TextureAtlas.AtlasRegion>(0.3f, atlas.findRegions("player_walk_right"));
    walkUpAnimation = new Animation<TextureAtlas.AtlasRegion>(0.3f, atlas.findRegions("player_walk_up"));
    walkDownAnimation = new Animation<TextureAtlas.AtlasRegion>(0.3f, atlas.findRegions("player_walk_down"));
  }

  @Override
  protected void process(int id) {
    stateTime += world.delta;
    Position pos = positionComponentMapper.get(id);
    Player player = playerComponentMapper.get(id);
    Velocity v = velocityComponentMapper.get(id);

    TextureAtlas.AtlasRegion keyFrame = idleAnimation.getKeyFrame(stateTime, true);
    
    if (v.x != 0 || v.y != 0) {
      if (Math.abs(v.x) > Math.abs(v.y)) {
        // horizontal
        if (v.x > 0) {
          keyFrame = walkRightAnimation.getKeyFrame(stateTime, true);
        } else {
          keyFrame = walkLeftAnimation.getKeyFrame(stateTime, true);
        }
      } else {
        if (v.y > 0) {
          keyFrame = walkUpAnimation.getKeyFrame(stateTime, true);
        } else {
          keyFrame = walkDownAnimation.getKeyFrame(stateTime, true);
        }
      }
    }

    if (player.toolId != -1) {
      keyFrame = actAnimation.getKeyFrame(stateTime, true);
    }

    mapRenderSystem.pending(keyFrame, pos.x - 20, pos.y - 20, 70, 70, false, false, 1, 0, Color.WHITE, MapRenderSystem.PendingDraw.Layer.Effect);
  }

  @Override
  protected void dispose() {
    atlas.dispose();
  }

}
