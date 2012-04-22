package com.heychinaski.ld23;

import java.awt.Color;
import java.awt.Graphics2D;

public class Bullet extends Entity {
  private static final int BULLET_LIFETIME = 2000;

  float xMomentum, yMomentum;
  
  long start = System.currentTimeMillis();
  
  public Bullet(float x, float y, float xMomentum, float yMomentum) {
    super();
    this.x = x;
    this.y = y;
    this.xMomentum = xMomentum;
    this.yMomentum = yMomentum;
    
    this.w = 10;
    this.h = 10;
  }

  @Override
  public void update(float tick, Game game) {
    nextX = x + (xMomentum * tick);
    nextY = y + (yMomentum * tick);
    
    if(System.currentTimeMillis() - start > BULLET_LIFETIME) {
      game.removeBullet(this);
    }
 }
  
  @Override
  public void applyNext() {
    super.applyNext();
  }

  @Override
  public void render(Graphics2D g) {
    g.setColor(Color.WHITE);
    g.fillOval(Math.round(x) - 5, Math.round(y) - 5, 10, 10);
    
  }

  @Override
  public void collided(Entity with, float tick, Game game) {
    if(!(with instanceof Player)) game.removeBullet(this);
  }

}
