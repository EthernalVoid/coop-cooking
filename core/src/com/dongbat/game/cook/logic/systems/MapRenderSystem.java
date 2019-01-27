/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dongbat.game.cook.logic.systems;

import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import static com.badlogic.gdx.graphics.g2d.Batch.C1;
import static com.badlogic.gdx.graphics.g2d.Batch.C2;
import static com.badlogic.gdx.graphics.g2d.Batch.C3;
import static com.badlogic.gdx.graphics.g2d.Batch.C4;
import static com.badlogic.gdx.graphics.g2d.Batch.U1;
import static com.badlogic.gdx.graphics.g2d.Batch.U2;
import static com.badlogic.gdx.graphics.g2d.Batch.U3;
import static com.badlogic.gdx.graphics.g2d.Batch.U4;
import static com.badlogic.gdx.graphics.g2d.Batch.V1;
import static com.badlogic.gdx.graphics.g2d.Batch.V2;
import static com.badlogic.gdx.graphics.g2d.Batch.V3;
import static com.badlogic.gdx.graphics.g2d.Batch.V4;
import static com.badlogic.gdx.graphics.g2d.Batch.X1;
import static com.badlogic.gdx.graphics.g2d.Batch.X2;
import static com.badlogic.gdx.graphics.g2d.Batch.X3;
import static com.badlogic.gdx.graphics.g2d.Batch.X4;
import static com.badlogic.gdx.graphics.g2d.Batch.Y1;
import static com.badlogic.gdx.graphics.g2d.Batch.Y2;
import static com.badlogic.gdx.graphics.g2d.Batch.Y3;
import static com.badlogic.gdx.graphics.g2d.Batch.Y4;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ReflectionPool;
import com.badlogic.gdx.utils.viewport.FitViewport;
import java.util.Comparator;

/**
 *
 * @author tao
 */
public class MapRenderSystem extends BaseSystem {

  @Wire
  private TiledMap map;
  @Wire
  private FitViewport viewport;
  private final Rectangle viewBounds = new Rectangle();

  private final Batch batch = new SpriteBatch();

  @Override
  protected void processSystem() {
    batch.begin();
    batch.setProjectionMatrix(viewport.getCamera().combined);
    
    AnimatedTiledMapTile.updateAnimationBaseTime();
    for (MapLayer layer : map.getLayers()) {
      if (layer.isVisible()) {
        if (layer instanceof TiledMapTileLayer) {
          if ("Objects".equals(layer.getName())) {
            renderTileLayer((TiledMapTileLayer) layer, PendingDraw.Layer.Object);
          } else if ("GroundObjects".equals(layer.getName())) {
            renderTileLayer((TiledMapTileLayer) layer, PendingDraw.Layer.GroundObject);
          } else if ("Deco".equals(layer.getName())) {
            renderTileLayer((TiledMapTileLayer) layer, PendingDraw.Layer.Effect);
          } else if ("Misc".equals(layer.getName())) {
            renderTileLayer((TiledMapTileLayer) layer, PendingDraw.Layer.Overlay);
          } else {
            renderTileLayer((TiledMapTileLayer) layer, null);
          }
        }
      }
    }
    clearPendings();
    
    batch.end();
  }

  @Override
  protected void dispose() {
    batch.dispose();
  }

  private void renderTileLayer(TiledMapTileLayer layer, PendingDraw.Layer pendingLayer) {
    OrthographicCamera camera = (OrthographicCamera) viewport.getCamera();
    final Color batchColor = batch.getColor();
    final float color = Color.toFloatBits(batchColor.r, batchColor.g, batchColor.b, batchColor.a * layer.getOpacity());

    final int layerWidth = layer.getWidth();
    final int layerHeight = layer.getHeight();

    float unitScale = 1;
    final float layerTileWidth = layer.getTileWidth() * unitScale;
    final float layerTileHeight = layer.getTileHeight() * unitScale;
    float width = camera.viewportWidth * camera.zoom;
    float height = camera.viewportHeight * camera.zoom;
    float w = width * Math.abs(camera.up.y) + height * Math.abs(camera.up.x);
    float h = height * Math.abs(camera.up.y) + width * Math.abs(camera.up.x);
    viewBounds.set(camera.position.x - w / 2, camera.position.y - h / 2, w, h);

    final int col1 = Math.max(0, (int) (viewBounds.x / layerTileWidth));
    final int col2 = Math.min(layerWidth, (int) ((viewBounds.x + viewBounds.width + layerTileWidth) / layerTileWidth));

    final int row1 = Math.max(0, (int) (viewBounds.y / layerTileHeight));
    final int row2 = Math.min(layerHeight, (int) ((viewBounds.y + viewBounds.height + layerTileHeight) / layerTileHeight));

    float y = row2 * layerTileHeight;
    float xStart = col1 * layerTileWidth;
    int NUM_VERTICES = 20;

    float vertices[] = new float[NUM_VERTICES];

    for (int row = row2; row >= row1; row--) {
      if (pendingLayer != null) {
        boolean drawPendings = drawPendings(layerTileHeight, row, pendingLayer);
      }
      float x = xStart;
      for (int col = col1; col < col2; col++) {
        final TiledMapTileLayer.Cell cell = layer.getCell(col, row);
        if (cell == null) {
          x += layerTileWidth;
          continue;
        }
        final TiledMapTile tile = cell.getTile();

        if (tile != null) {
          final boolean flipX = cell.getFlipHorizontally();
          final boolean flipY = cell.getFlipVertically();
          final int rotations = cell.getRotation();

          TextureRegion region = tile.getTextureRegion();

          float x1 = x + tile.getOffsetX() * unitScale;
          float y1 = y + tile.getOffsetY() * unitScale;
          float x2 = x1 + region.getRegionWidth() * unitScale;
          float y2 = y1 + region.getRegionHeight() * unitScale;

          float u1 = region.getU();
          float v1 = region.getV2();
          float u2 = region.getU2();
          float v2 = region.getV();

          vertices[X1] = x1;
          vertices[Y1] = y1;
          vertices[C1] = color;
          vertices[U1] = u1;
          vertices[V1] = v1;

          vertices[X2] = x1;
          vertices[Y2] = y2;
          vertices[C2] = color;
          vertices[U2] = u1;
          vertices[V2] = v2;

          vertices[X3] = x2;
          vertices[Y3] = y2;
          vertices[C3] = color;
          vertices[U3] = u2;
          vertices[V3] = v2;

          vertices[X4] = x2;
          vertices[Y4] = y1;
          vertices[C4] = color;
          vertices[U4] = u2;
          vertices[V4] = v1;

          if (flipX) {
            float temp = vertices[U1];
            vertices[U1] = vertices[U3];
            vertices[U3] = temp;
            temp = vertices[U2];
            vertices[U2] = vertices[U4];
            vertices[U4] = temp;
          }
          if (flipY) {
            float temp = vertices[V1];
            vertices[V1] = vertices[V3];
            vertices[V3] = temp;
            temp = vertices[V2];
            vertices[V2] = vertices[V4];
            vertices[V4] = temp;
          }
          if (rotations != 0) {
            switch (rotations) {
              case TiledMapTileLayer.Cell.ROTATE_90: {
                float tempV = vertices[V1];
                vertices[V1] = vertices[V2];
                vertices[V2] = vertices[V3];
                vertices[V3] = vertices[V4];
                vertices[V4] = tempV;

                float tempU = vertices[U1];
                vertices[U1] = vertices[U2];
                vertices[U2] = vertices[U3];
                vertices[U3] = vertices[U4];
                vertices[U4] = tempU;
                break;
              }
              case TiledMapTileLayer.Cell.ROTATE_180: {
                float tempU = vertices[U1];
                vertices[U1] = vertices[U3];
                vertices[U3] = tempU;
                tempU = vertices[U2];
                vertices[U2] = vertices[U4];
                vertices[U4] = tempU;
                float tempV = vertices[V1];
                vertices[V1] = vertices[V3];
                vertices[V3] = tempV;
                tempV = vertices[V2];
                vertices[V2] = vertices[V4];
                vertices[V4] = tempV;
                break;
              }
              case TiledMapTileLayer.Cell.ROTATE_270: {
                float tempV = vertices[V1];
                vertices[V1] = vertices[V4];
                vertices[V4] = vertices[V3];
                vertices[V3] = vertices[V2];
                vertices[V2] = tempV;

                float tempU = vertices[U1];
                vertices[U1] = vertices[U4];
                vertices[U4] = vertices[U3];
                vertices[U3] = vertices[U2];
                vertices[U2] = tempU;
                break;
              }
            }
          }
          batch.draw(region.getTexture(), vertices, 0, NUM_VERTICES);
        }
        x += layerTileWidth;
      }
      y -= layerTileHeight;
    }
    if (pendingLayer != null) {
      drawPendings(layerTileHeight, row1 - 1, pendingLayer);
    }
  }

  private Pool<PendingDraw> pendingDrawPool = new ReflectionPool<PendingDraw>(PendingDraw.class);
  private DelayedRemovalArray<PendingDraw> pendingDraws = new DelayedRemovalArray<PendingDraw>();
  private Sprite sprite = new Sprite();

  public void pending(TextureRegion textureRegion, float x, float y, float width, float height, boolean flipX, boolean flipY, float alpha, float rotation) {
    pending(textureRegion, x, y, width, height, flipX, flipY, alpha, rotation, null);
  }

  public void pending(TextureRegion textureRegion, float x, float y, float width, float height, boolean flipX, boolean flipY, float alpha, float rotation, Color color) {
    pending(textureRegion, x, y, width, height, flipX, flipY, alpha, rotation, color, PendingDraw.Layer.Object);
  }

  public void pending(TextureRegion textureRegion, float x, float y, float width, float height, boolean flipX, boolean flipY, float alpha, float rotation, Color tint, PendingDraw.Layer layer) {
    PendingDraw pendingDraw = pendingDrawPool.obtain();
    pendingDraw.set(textureRegion, x, y, width, height, flipX, flipY, alpha, rotation, tint, layer);
    pendingDraws.add(pendingDraw);
  }
  
  public void pending(TextureRegion textureRegion, float x, float y, float width, float height, boolean flipX, boolean flipY, float alpha, float rotation, Color tint, PendingDraw.Layer layer, int zIndex) {
    PendingDraw pendingDraw = pendingDrawPool.obtain();
    pendingDraw.set(textureRegion, x, y, width, height, flipX, flipY, alpha, rotation, tint, layer);
    pendingDraw.zIndex = zIndex;
    pendingDraws.add(pendingDraw);
  }

  public void clearPendings() {
    pendingDrawPool.freeAll(pendingDraws);
    pendingDraws.clear();
  }

  private final Comparator<PendingDraw> pendingComparator = new Comparator<PendingDraw>() {
    @Override
    public int compare(PendingDraw o1, PendingDraw o2) {
      if (o1.zIndex > o2.zIndex) {
        return 1;
      }
      if (o1.y > o2.y) {
        return -1;
      } else if (o1.y < o2.y) {
        return 1;
      } else {
        return 0;
      }
    }
  };

  public boolean drawPendings(float tileHeight, int y, PendingDraw.Layer pendingLayer) {
    boolean drawed = false;
    pendingDraws.sort(pendingComparator);
    for (PendingDraw pendingDraw : pendingDraws) {
      int tileY = (int) (pendingDraw.y / tileHeight);
      if (tileY == y && pendingDraw.layer == pendingLayer) {
        drawed = true;
        sprite.setColor(Color.WHITE);
        if (pendingDraw.tint != null) {
          sprite.setColor(pendingDraw.tint);
        }
        sprite.setPosition(pendingDraw.x, pendingDraw.y);
        sprite.setSize(pendingDraw.width, pendingDraw.height);
        sprite.setRegion(pendingDraw.textureRegion);
        sprite.setOriginCenter();
        sprite.setFlip(pendingDraw.flipX, pendingDraw.flipY);
        sprite.setAlpha(pendingDraw.alpha);
        sprite.setRotation(pendingDraw.rotation);
        sprite.draw(batch);
      }
    }
    return drawed;
  }

  public static class PendingDraw implements Pool.Poolable {

    public enum Layer {
      Object, GroundObject, Effect, Overlay
    }

    public TextureRegion textureRegion;
    public float x, y, width, height;
    public boolean flipX, flipY;
    public float alpha;
    public float rotation;
    public Color tint;
    public Layer layer;
    
    public int zIndex = 0;

    public PendingDraw() {
      textureRegion = null;
      x = y = width = height = 0;
      flipX = flipY = false;
      alpha = 1;
      rotation = 0;
      tint = null;
      layer = Layer.Object;
    }

    public void set(TextureRegion textureRegion, float x, float y, float width, float height, boolean flipX, boolean flipY, float alpha, float rotation, Color tint, Layer layer) {
      this.textureRegion = textureRegion;
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
      this.flipX = flipX;
      this.flipY = flipY;
      this.alpha = alpha;
      this.rotation = rotation;
      this.tint = tint;
      this.layer = layer;
    }

    @Override
    public void reset() {
      textureRegion = null;
      x = y = width = height = 0;
      flipX = flipY = false;
      alpha = 1;
      rotation = 0;
      tint = null;
      layer = Layer.Object;
      zIndex = 0;
    }

  }

}
