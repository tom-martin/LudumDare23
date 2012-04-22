package com.heychinaski.ld23;


import static com.heychinaski.ld23.Util.randomFloat;
import static com.heychinaski.ld23.Util.randomInt;
import static java.lang.Math.round;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

public class Cloud extends Entity {
  
  private static final BasicStroke RAIN_STROKE = new BasicStroke(2);
  private static final Stroke OUTLINE_STROKE = new BasicStroke(5);
  private static final Stroke OUTLINE_STROKE2 = new BasicStroke(10);
  private static final float ROT_SPEED = 0.5f;
  private static final float JIGGLE_SPEED = 20f;
  private static final float RAIN_SPEED = 200f;

  Planet planet;
  
  float rot;

  private float rotMomentum;
  
  float xPositions[] = new float[10];
  float yPositions[] = new float[10];
  
  float rainXPositions[] = new float[10];
  float rainYPositions[] = new float[10];
  
  boolean raining = false;
  
  Rock nearestRock = null;

  public Cloud(Planet planet) {
    super();
    this.planet = planet;
    
    rotMomentum = randomFloat(ROT_SPEED) -(ROT_SPEED / 2);
    rot = randomFloat(360f);
    
    for(int i = 0; i < xPositions.length; i++) {
      xPositions[i] = randomInt(25) - 12;
      yPositions[i] = randomInt(100) - 50;
    }
    
    for(int i = 0; i < xPositions.length; i++) {
      rainXPositions[i] = randomInt(170);
      rainYPositions[i] = randomInt(100) - 50;
    }
  }

  @Override
  public void update(float tick, Game game) {
    rot += rotMomentum * tick;
    
    if(randomInt(200) == 0) {
      rotMomentum = randomFloat(ROT_SPEED) -(ROT_SPEED / 2);
    }
    
    if(randomInt(200) == 0) {
      raining = !raining;
    }
    
    if(raining && randomInt(10) == 0) {
      nextX = (float) ((-(planet.radius + 200)) * Math.cos(rot)) + planet.x;
      nextY = (float) ((-(planet.radius + 200)) * Math.sin(rot)) + planet.y;
      nearestRock = planet.getNearestRock(nextX, nextY);
      if(nearestRock != null && nearestRock.grass.size() < 10) {
        nearestRock.addGrassBlob(new GrassBlob(nearestRock, game.imageManager.get("grass1.png")));
      }
    }
    
    w = 200;
    h = 200;
    
    for(int i = 0; i < xPositions.length; i++) {
      xPositions[i] += randomFloat(tick * JIGGLE_SPEED) - (tick * (JIGGLE_SPEED / 2));
      yPositions[i] += randomFloat(tick * JIGGLE_SPEED) - (tick * (JIGGLE_SPEED / 2));
    }
    
    for(int i = 0; i < rainXPositions.length; i++) {
      rainXPositions[i] = (rainXPositions[i] + (tick * RAIN_SPEED)) % (170 + planet.radius);
      rainYPositions[i] += randomFloat(tick * JIGGLE_SPEED) - (tick * (JIGGLE_SPEED / 2));
    }
  }

  @Override
  public void render(Graphics2D g) {
    g = (Graphics2D) g.create();
//    Graphics2D debugG = (Graphics2D) g.create(); 
    g.translate(planet.x, planet.y);
    g.rotate(rot);
    g.translate(-(planet.radius + 200), 0);
    
    g.setColor(Color.white);
    g.setStroke(OUTLINE_STROKE2);
    for(int i = 0; i < xPositions.length; i++) {
      g.drawOval(round(xPositions[i]), round(yPositions[i]), 40, 40);
    }
    
    g.setColor(Color.black);
    g.setStroke(OUTLINE_STROKE);
    for(int i = 0; i < xPositions.length; i++) {
      g.drawOval(round(xPositions[i]), round(yPositions[i]), 40, 40);
    }
    
    
    g.setColor(Color.white);
    
    for(int i = 0; i < xPositions.length; i++) {
      g.fillOval(round(xPositions[i]), round(yPositions[i]), 40, 40);
    }
    
    if(raining) {
      g.setColor(Color.blue);
      g.setStroke(RAIN_STROKE);
      
      for(int i = 0; i < rainXPositions.length; i++) {
        g.drawLine(round(rainXPositions[i]) + 30, round(rainYPositions[i]), round(rainXPositions[i]) + 50, round(rainYPositions[i]));
      }
    }
    
    g.dispose();

//    debugG.setColor(Color.GREEN);
//    debugG.fillOval((int)x - 5, (int)y - 5, 10, 10);
//    if(nearestRock != null) {
//      debugG.drawLine((int)x, (int)y, (int)nearestRock.x, (int)nearestRock.y);
//    }
//    
//    debugG.dispose();
  }

  @Override
  public void collided(Entity with, float tick, Game game) {

  }

}
