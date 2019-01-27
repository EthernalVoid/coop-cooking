package com.dongbat.game.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class ShapeUtil {
  
  public static Texture createWhite() {
    Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
    pixmap.setColor(Color.WHITE);
    pixmap.fill();
    Texture texture = new Texture(pixmap);
    pixmap.dispose();
    return texture;
  }

  public static void drawLine(Batch batch, float _x1, float _y1, float _x2, float _y2, float thickness, Texture tex) {
    drawLine(batch, _x1, _y1, _x2, _y2, thickness, null, tex);
  }

  public static void drawLine(Batch batch, float _x1, float _y1, float _x2, float _y2, float thickness, Color color, Texture tex) {
    float length = Vector2.dst(_x1, _y1, _x2, _y2);
    float dx = _x1;
    float dy = _y1;
    dx = dx - _x2;
    dy = dy - _y2;
    float angle = MathUtils.radiansToDegrees * MathUtils.atan2(dy, dx);
    angle = angle - 180;
    Color originalColor = batch.getColor();
    if (color != null) {
      batch.setColor(color);
    }
    batch.draw(tex, _x1, _y1, 0f, thickness * 0.5f, length, thickness, 1f, 1f, angle, 0, 0, tex.getWidth(), tex.getHeight(), false, false);
    batch.setColor(originalColor);
  }

  private static final Sprite sprite = new Sprite();

  public static void drawRect(Batch batch, float x, float y, float w, float h, Color color, float a, Texture texture) {
    sprite.setTexture(texture);
    sprite.setColor(color);
    sprite.setAlpha(a);
    sprite.setPosition(x, y);
    sprite.setSize(w, h);
    sprite.draw(batch);
  }

  public static void drawRect(Batch batch, float x, float y, float w, float h, Color color, Texture texture) {
    drawRect(batch, x, y, w, h, color, 1, texture);
  }

}
