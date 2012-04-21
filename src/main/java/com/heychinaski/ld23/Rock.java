package com.heychinaski.ld23;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class Rock extends Entity {
  
  int[] xPositions = new int[12];
  int[] yPositions = new int[12];
  
  boolean held = false;
  
  public boolean setHeld(boolean held) {
    if(planet != null) {
      held = false;
      return false;
    }
    
    this.held = held;
    return true;
  }

  float xMomentum, yMomentum;
  private Planet planet;
  Color outlineColor = new Color(200, 150, 50);
  Color fillColor = new Color(255, 200, 100);
  
  public Rock() {
    int size = Util.randomInt(50) + 30;
    w = size - 20;
    h = size - 20;
    
    xPositions[0] = -size / 2;
    yPositions[0] = -size / 2;
    
    xPositions[1] = -size / 3;
    yPositions[1] = -size / 2;
    
    xPositions[2] = size / 3;
    yPositions[2] = -size / 2;
    
    xPositions[3] = size / 2;
    yPositions[3] = -size / 2;
    
    xPositions[4] = size / 2;
    yPositions[4] = -size / 3;
    
    xPositions[5] = size / 2;
    yPositions[5] = size / 3;
    
    xPositions[6] = size / 2;
    yPositions[6] = size / 2;
    
    xPositions[7] = size / 3;
    yPositions[7] = size / 2;
    
    xPositions[8] = -size / 3;
    yPositions[8] = size / 2;
    
    xPositions[9] = -size / 2;
    yPositions[9] = size / 2;
    
    xPositions[10] = -size / 2;
    yPositions[10] = size / 3;
    
    xPositions[11] = -size / 2;
    yPositions[11] = -size / 3;
    
    
    for(int i = 0; i < xPositions.length; i++) {
      xPositions[i] += Util.randomInt(size / 3) - (size / 6);
      yPositions[i] += Util.randomInt(size / 3) - (size / 6);
    }
    
    xMomentum = Util.randomFloat(500) - 250;
    yMomentum = Util.randomFloat(500) - 250;
  }
  
  @Override
  public void update(float tick, Input input) {
    if(planet == null) {
      nextX = x + (xMomentum * tick);
      nextY = y + (yMomentum * tick);
    }
  }
  
  @Override
  public void collided(Entity entity, float tick) {
    if(planet != null || held) return;
    if(entity instanceof Player) return;
    
    if(entity instanceof Rock && ((Rock)entity).planet != null) {
      ((Rock)entity).planet.addRock(this);
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

  void draw(Graphics2D g) {
    fill(g);
    outline(g);
  }

  void outline(Graphics2D g) {
    g.setStroke(new BasicStroke(planet == null ? 5 : 10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    g.setColor(outlineColor);
    g.drawPolygon(xPositions, yPositions, 12);
  }
  
  void fill(Graphics2D g) {
    g.setColor(fillColor);
    g.fillPolygon(xPositions, yPositions, 12);
  }

  public void setPlanet(Planet planet) {
    this.planet = planet;
    held = false;
  }

}
