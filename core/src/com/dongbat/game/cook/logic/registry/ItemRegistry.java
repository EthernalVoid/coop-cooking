/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dongbat.game.cook.logic.registry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;

/**
 *
 * @author tao
 */
public final class ItemRegistry {
  
  public static class ItemData {
    public final IntIntMap toolTransforms = new IntIntMap();
    public final IntIntMap itemTransforms = new IntIntMap();
  }
  
  public final IntMap<String> names = new IntMap<String>();
  public final ObjectMap<String, Integer> ids = new ObjectMap<String, Integer>();
  
  public IntMap<ItemData> datas = new IntMap<ItemData>();
  
  private int counter = 0;

  public ItemRegistry() {
    String fileData = Gdx.files.internal("items.txt").readString();
    String[] lines = fileData.split("\n");
    for (String line : lines) {
      line = line.trim();
      if (line.isEmpty()) {
        continue;
      }
      String[] pair = line.split(":");
      String name = pair[0].trim();
      
      register(counter++, name);
    }
    
    for (String line : lines) {
      line = line.trim();
      if (line.isEmpty()) {
        continue;
      }
      String[] pair = line.split(":");
      if (pair.length < 2) {
        continue;
      }
      String name = pair[0].trim();
      
      int id = getId(name);
      ItemData data = datas.get(id);
      
      String[] recipes = pair[1].split(";");
      for (String recipe : recipes) {
        String[] frags = recipe.split("=");
        if (frags.length < 2) continue;
        String req = frags[0].trim();
        String res = frags[1].trim();
        int resId = getId(res);
        
        if (req.startsWith("?")) {
          int tool = "?stove".equals(req)? 1: 0;
          data.toolTransforms.put(tool, resId);
        } else {
          int reqId = getId(req);
          data.itemTransforms.put(reqId, resId);
          
          datas.get(reqId).itemTransforms.put(id, resId);
        }
      }
    }
  }
  
  public final void register(int id, String name) {
    names.put(id, name);
    ids.put(name, id);
    
    datas.put(id, new ItemData());
  }
  
  public int getId(String name) {
    return ids.get(name);
  }
  
  public String getName(int id) {
    return names.get(id);
  }
}
