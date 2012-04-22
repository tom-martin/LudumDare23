package com.heychinaski.ld23;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class Rock extends SpaceJunk {
  
  private static final float MAX_MOMENTUM = 400;

  List<GrassBlob> grass = new ArrayList<GrassBlob>();
  
  boolean held = false;
  
  long brokenOffTime = 0;
  
  public boolean setHeld(boolean held) {
    if(planet != null) {
      held = false;
      return false;
    }
    
    this.held = held;
    return true;
  }

  protected Planet planet;

  private long lastHit;
  
  public Rock() {
    super(80, 30, 20);
  }
  
  @Override
  public void update(float tick, Game game) {
    if(planet == null) {
      super.update(tick, game);
    }
  }
  
  @Override
  public void collided(Entity entity, float tick, Game game) {
    if(entity instanceof Cloud) return;
    if(entity instanceof Meteor && planet != null && !planet.isFinished()) {
      planet.removeRock(this);
      this.xMomentum = ((Meteor)entity).xMomentum;
      this.yMomentum = ((Meteor)entity).yMomentum;
      
      brokenOffTime = System.currentTimeMillis(); 
      
      return;
    }
    
    if(planet != null || held) return;
    if(entity instanceof Player) return;
    
    if(entity instanceof Bullet) {
      game.removeBullet((Bullet)entity);
      game.removeRock(this);
    }
    
    if( entity instanceof Rock && 
        ((Rock)entity).planet != null &&
        !((Rock)entity).planet.maxSizeReached() &&
        System.currentTimeMillis() - brokenOffTime > 1000) {
      ((Rock)entity).planet.addRock(this);
      ((Rock)entity).grass.clear();
      return;
    }
 
    if(System.currentTimeMillis() - lastHit > 500) {
      lastHit = System.currentTimeMillis();
      xMomentum = Math.max(-MAX_MOMENTUM, Math.min(MAX_MOMENTUM, -xMomentum + entity.xMomentum));
      yMomentum = Math.max(-MAX_MOMENTUM, Math.min(MAX_MOMENTUM, -yMomentum + entity.yMomentum));
    }
  }

  @Override
  public void render(Graphics2D g) {
    if(!held && planet == null) {
      g = (Graphics2D) g.create();
      g.translate(x, y);
      draw(g);
      g.dispose();
    }
  }

  void outline(Graphics2D g) {
    g.setStroke(new BasicStroke(planet == null ? 5 : 10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    g.setColor(outlineColor);
    g.drawPolygon(xPositions, yPositions, 12);
  }
  
  public void setPlanet(Planet planet) {
    this.planet = planet;
    held = false;
  }

  public void addGrassBlob(GrassBlob grassBlob) {
    grass.add(grassBlob);
  }

}
