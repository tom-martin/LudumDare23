package com.heychinaski.ld23;

import java.awt.Graphics2D;

public abstract class Entity {
  public float x;
  public float y;
  
  public float w;
  public float h;
  
  public float nextX = x;
  public float nextY = y;
  
  public abstract void update(float tick, Game game);
  
  public abstract void render(Graphics2D g);
  
  public abstract void collided(Entity with, float tick, Game game);

  public void applyNext() {
    x = nextX;
    y = nextY;
  }
}
