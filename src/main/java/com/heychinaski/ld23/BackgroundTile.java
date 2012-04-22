package com.heychinaski.ld23;

import static com.heychinaski.ld23.Util.randomInt;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.RenderingHints;

public class BackgroundTile {
  Image image;
  private int size;

  public BackgroundTile(int size, GraphicsConfiguration gc) {
    this.size = size;
    
    image = gc.createCompatibleImage(size, size);
    
    Graphics2D imageG = (Graphics2D) image.getGraphics();
    imageG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
    imageG.setColor(new Color(30, 30, 30));
    imageG.fillRect(0, 0, size, size);
    
    for(int i = 0; i < 100; i ++) {
      renderStar(imageG);
    }
    
    imageG.dispose();
  }
  
  public void render(int x, int y, Graphics2D g) {
    int normX = x % size;
    int normY = y % size;
    
    if(normX < 0) normX = normX + size;
    if(normY < 0) normY = normY + size;
    
    g.drawImage(image, normX - size, normY - size, null);
    g.drawImage(image, normX - size, normY, null);
    g.drawImage(image, normX, normY - size, null);
    g.drawImage(image, normX, normY, null);
    g.drawImage(image, normX + size, normY - size, null);
    g.drawImage(image, normX - size, normY + size, null);
    g.drawImage(image, normX + size, normY + size, null);
    g.drawImage(image, normX + size, normY, null);
    g.drawImage(image, normX, normY + size, null);
  }
  
  private void renderStar(Graphics2D g) {
    int starSize = randomInt(10);
    int x = randomInt(size);
    int y = randomInt(size);

    g.setColor(Color.white);
    g.fillOval(x, y, starSize, starSize);
  }
}
