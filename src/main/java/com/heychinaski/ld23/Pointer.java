package com.heychinaski.ld23;

import static java.lang.Math.max;
import static java.lang.Math.sqrt;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class Pointer extends Entity {

  private Player player;
  private Entity tracking;
  private float rot;
  
  int[] xPositions = new int[3];
  int[] yPositions = new int[3];
  private int distance;
  
  Color fillColor = new Color(200, 100, 80);
  Color outlineColor = new Color(120, 100, 80);
  
  boolean visible = false;
  
  BasicStroke basicStroke = new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
  private float prox;
  
  public Pointer(Player player, Entity tracking) {
    this.player = player;
    this.tracking = tracking;
    
    xPositions[0] = -10;
    yPositions[0] = 0;
    
    xPositions[1] = 10;
    yPositions[1] = -20;
    
    xPositions[2] = 10;
    yPositions[2] = 20;
  }

  @Override
  public void update(float tick, Game game) {
    this.nextX = player.x;
    this.nextY = player.y + 100;
    this.rot = (float) Math.atan2((player.y-tracking.y),(player.x-tracking.x));
    
    this.prox = (float) sqrt(((player.x - tracking.x) * (player.x - tracking.x)) + ((player.y - tracking.y) * (player.y - tracking.y)));  
    
    visible = tracking.x < (game.camera.x - game.getWidth() / 2) ||
              tracking.x > (game.camera.x + game.getWidth() / 2) ||
              tracking.y < (game.camera.y - game.getHeight() / 2) ||
              tracking.y > (game.camera.y + game.getHeight() / 2);
  
    distance = (game.getHeight() / 2) - 40;
  }

  @Override
  public void render(Graphics2D g) {
    if(!visible) return;
//    g.drawLine((int)player.x, (int)player.y, (int)tracking.x, (int)tracking.y);
    g = (Graphics2D) g.create();
    g.translate(player.x, player.y);
    g.rotate(rot);
    g.translate(-distance, 0);
    float proxScale = (float) max(0.5, 5 / (prox / 200));
    g.scale(proxScale, proxScale);
    
    g.setStroke(basicStroke);
    
    g.setColor(fillColor);
    g.fillPolygon(xPositions, yPositions, 3);
    
    g.setColor(outlineColor);
    g.drawPolygon(xPositions, yPositions, 3);
    
    g.dispose();
  }

  @Override
  public void collided(Entity with, float tick, Game game) {
    
  }
}
