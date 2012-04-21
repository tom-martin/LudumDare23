package com.heychinaski.ld23;

import java.awt.Graphics2D;

public abstract class Entity {
  public int x;
  public int y;
  
  public abstract void update(float tick, Input input);
  
  public abstract void render(Graphics2D g);
}
