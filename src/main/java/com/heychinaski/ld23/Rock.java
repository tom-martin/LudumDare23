package com.heychinaski.ld23;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class Rock extends SpaceJunk {
  
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
    if(entity instanceof Meteor && planet != null) {
      planet.removeRock(this);
      this.xMomentum = ((Meteor)entity).xMomentum;
      this.yMomentum = ((Meteor)entity).yMomentum;
      
      brokenOffTime = System.currentTimeMillis(); 
      
      return;
    }
    
    if(planet != null || held) return;
    if(entity instanceof Player) return;
    
    if(entity instanceof Rock && ((Rock)entity).planet != null && System.currentTimeMillis() - brokenOffTime > 1000) {
      ((Rock)entity).planet.addRock(this);
      ((Rock)entity).grass.clear();
      return;
    }
    
    xMomentum *= -1;
    yMomentum *= -1;
   
    if(planet != null) {
      nextX = x + (xMomentum * tick);
      nextY = y + (yMomentum * tick);
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
