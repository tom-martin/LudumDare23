package com.heychinaski.ld23;

import java.awt.Color;

public class IceBlock extends Rock {
  Pointer pointer;
  
  public IceBlock() {
    super();
    fillColor = new Color(180, 200, 255);
    outlineColor = new Color(120, 150, 200);
  }
  
  @Override
  public void collided(Entity entity, float tick, Game game) {
    if(entity instanceof Rock && ((Rock)entity).planet != null) {
      planet = ((Rock)entity).planet;
      Cloud cloud = new Cloud(planet);
      planet.addCloud(cloud);
      game.addCloud(cloud);
      
      game.removeIceBlock(this);
    }
  }
}
