package com.heychinaski.ld23;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Point2D;

public class Enemy extends Entity {
  
  private static final int ATTACK = 0;
  private static final int RETREAT = 1;
  private static final int MAX_MOMENTUM = 400;
  int strategy = 0;
  private long lastHit;
  private Image image;
  
  public Enemy(Image image) {
    super();
    xMomentum = Util.randomFloat(800) - 400;
    yMomentum = Util.randomFloat(800) - 400;
    
    this.image = image;
    
    w = image.getWidth(null);
    h = image.getHeight(null);
  }

  @Override
  public void update(float tick, Game game) {
    if(Util.randomInt(2000) == 0) strategy = Util.randomInt(1);
    
    if(System.currentTimeMillis() - lastHit > 500) {
      float xDiff = game.player.x - x;
      float yDiff = game.player.y - y;
      float dist = (float) Math.sqrt((xDiff * xDiff) + (yDiff * yDiff));
      if(dist < 400) {
        Point2D.Float v = new Point2D.Float(xDiff, yDiff);
        Util.normalise(v);
        xMomentum = v.x * 400;
        yMomentum = v.y * 400;
        if(strategy == RETREAT) {
          xMomentum *= -1;
          yMomentum *= -1;
        }
      }
    }
    
    nextX = x + (xMomentum * tick);
    nextY = y + (yMomentum * tick);
  }

  @Override
  public void render(Graphics2D g) {
    g.drawImage(image, (int)x - 32, (int)y - 32, null);
  }

  @Override
  public void collided(Entity with, float tick, Game game) {
    if(with instanceof Bullet) {
      game.removeBullet((Bullet) with);
      game.removeEnemy(this);
    }
    if(with instanceof Enemy) {
      strategy = RETREAT;
    }
    if(System.currentTimeMillis() - lastHit > 500) {
      lastHit = System.currentTimeMillis();
      xMomentum = Math.max(-MAX_MOMENTUM, Math.min(MAX_MOMENTUM, -xMomentum + with.xMomentum));
      yMomentum = Math.max(-MAX_MOMENTUM, Math.min(MAX_MOMENTUM, -yMomentum + with.yMomentum));
    }
  }

}
