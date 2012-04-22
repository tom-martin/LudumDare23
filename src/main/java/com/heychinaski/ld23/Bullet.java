package com.heychinaski.ld23;

import static java.lang.Math.abs;

import java.awt.Color;
import java.awt.Graphics2D;

public class Bullet extends Entity {
  float xMomentum, yMomentum;
  
  float startX, startY;
  
  public Bullet(float x, float y, float xMomentum, float yMomentum) {
    super();
    this.x = x;
    this.y = y;
    this.xMomentum = xMomentum;
    this.yMomentum = yMomentum;
    
    startX = x;
    startY = y;
    
    this.w = 10;
    this.h = 10;
  }

  @Override
  public void update(float tick, Game game) {
    nextX = x + (xMomentum * tick);
    nextY = y + (yMomentum * tick);
    
    if(abs(nextX) - abs(startX) > (game.getWidth() / 2) - 20 || abs(nextY) - abs(startY) > (game.getHeight() / 2)  - 20) {
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
