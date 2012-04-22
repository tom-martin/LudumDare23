package com.heychinaski.ld23;


import static com.heychinaski.ld23.Util.randomFloat;
import static com.heychinaski.ld23.Util.randomInt;
import static java.lang.Math.round;

import java.awt.Color;
import java.awt.Graphics2D;

public class Cloud extends Entity {
  
  private static final float ROT_SPEED = 0.5f;
  private static final float JIGGLE_SPEED = 20f;

  Planet planet;
  
  float rot;

  private float rotMomentum;
  
  float xPositions[] = new float[10];
  float yPositions[] = new float[10];

  public Cloud(Planet planet) {
    super();
    this.planet = planet;
    
    rotMomentum = randomFloat(ROT_SPEED) -(ROT_SPEED / 2);
    rot = randomFloat(360f);
    
    for(int i = 0; i < xPositions.length; i++) {
      xPositions[i] = randomInt(25) - 12;
      yPositions[i] = randomInt(100) - 50;
    }
  }

  @Override
  public void update(float tick, Game game) {
    rot += rotMomentum * tick;
    
    if(randomInt(200) == 0) {
      rotMomentum = randomFloat(ROT_SPEED) -(ROT_SPEED / 2);
    }
    
    for(int i = 0; i < xPositions.length; i++) {
      xPositions[i] += randomFloat(tick * JIGGLE_SPEED) - (tick * (JIGGLE_SPEED / 2));
      yPositions[i] += randomFloat(tick * JIGGLE_SPEED) - (tick * (JIGGLE_SPEED / 2));
    }
  }

  @Override
  public void render(Graphics2D g) {
    g = (Graphics2D) g.create();
    g.translate(planet.x, planet.y);
    g.rotate(rot);
    g.translate(-(planet.radius + 200), 0);
    
    g.setColor(Color.black);
    for(int i = 0; i < xPositions.length; i++) {
      g.drawOval(round(xPositions[i]), round(yPositions[i]), 40, 40);
    }
    
    g.setColor(Color.white);
    
    for(int i = 0; i < xPositions.length; i++) {
      g.fillOval(round(xPositions[i]), round(yPositions[i]), 40, 40);
    }
    
    g.dispose();
  }

  @Override
  public void collided(Entity with, float tick, Game game) {
    // TODO Auto-generated method stub

  }

}
