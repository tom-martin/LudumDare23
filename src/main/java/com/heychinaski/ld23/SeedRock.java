package com.heychinaski.ld23;

import java.awt.Color;

public class SeedRock extends Rock {
  public SeedRock() {
    super();
    fillColor = new Color(200, 100, 80);
    outlineColor = new Color(120, 100, 80);
  }
  
  @Override
  public void collided(Entity entity, float tick, Game game) {
    
  }
}
