package com.heychinaski.ld23;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;


public abstract class SpaceJunk extends Entity {
  int[] xPositions = new int[12];
  int[] yPositions = new int[12];
  
  Color outlineColor = new Color(200, 150, 50);
  Color fillColor = new Color(255, 200, 100);
  
  public SpaceJunk(int maxSize, int minSize, int collisionFudge) {
    int size = Util.randomInt(maxSize - minSize) + minSize;
    w = size - collisionFudge;
    h = size - collisionFudge;
    
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
    
    xMomentum = Util.randomFloat(800) - 400;
    yMomentum = Util.randomFloat(800) - 400;
  }
  
  @Override
  public void update(float tick, Game game) {
    nextX = x + (xMomentum * tick);
    nextY = y + (yMomentum * tick);
  }
  
  @Override
  public void render(Graphics2D g) {
      g = (Graphics2D) g.create();
      g.translate(x, y);
      draw(g);
      g.dispose();
  }

  void draw(Graphics2D g) {
    fill(g);
    outline(g);
  }

  void outline(Graphics2D g) {
    g.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    g.setColor(outlineColor);
    g.drawPolygon(xPositions, yPositions, 12);
  }
  
  void fill(Graphics2D g) {
    g.setColor(fillColor);
    g.fillPolygon(xPositions, yPositions, 12);
  }

}
