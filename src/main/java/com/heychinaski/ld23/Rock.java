package com.heychinaski.ld23;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class Rock extends Entity {
  
  int[] xPositions = new int[12];
  int[] yPositions = new int[12];
  
  float xMomentum, yMomentum; 
  
  public Rock() {
    xPositions[0] = -20;
    yPositions[0] = -20;
    
    xPositions[1] = -12;
    yPositions[1] = -20;
    
    xPositions[2] = 12;
    yPositions[2] = -20;
    
    xPositions[3] = 20;
    yPositions[3] = -20;
    
    xPositions[4] = 20;
    yPositions[4] = -12;
    
    xPositions[5] = 20;
    yPositions[5] = 12;
    
    xPositions[6] = 20;
    yPositions[6] = 20;
    
    xPositions[7] = 12;
    yPositions[7] = 20;
    
    xPositions[8] = -12;
    yPositions[8] = 20;
    
    xPositions[9] = -20;
    yPositions[9] = 20;
    
    xPositions[10] = -20;
    yPositions[10] = 12;
    
    xPositions[11] = -20;
    yPositions[11] = -12;
    
    w = 40;
    h = 40;
    
    for(int i = 0; i < xPositions.length; i++) {
      xPositions[i] += Util.randomInt(12) - 6;
      yPositions[i] += Util.randomInt(12) - 6;
    }
    
    xMomentum = Util.randomFloat(200) - 100;
    yMomentum = Util.randomFloat(200) - 100;
  }
  
  @Override
  public void update(float tick, Input input) {
    nextX = x + (xMomentum * tick);
    nextY = y + (yMomentum * tick);
  }
  
  @Override
  public void collided(Entity entity, float tick) {
   xMomentum *= -1;
   yMomentum *= -1;
   
   nextX = x + (xMomentum * tick);
   nextY = y + (yMomentum * tick);
  }

  @Override
  public void render(Graphics2D g) {
    g = (Graphics2D) g.create();
    g.translate(x, y);
    g.setColor(new Color(255, 200, 100));
    g.fillPolygon(xPositions, yPositions, 12);
    g.setColor(new Color(200, 150, 50));
    g.setStroke(new BasicStroke(5));
    g.drawPolygon(xPositions, yPositions, 12);
    g.dispose();
  }

}
