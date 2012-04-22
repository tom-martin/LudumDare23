package com.heychinaski.ld23;

import static java.lang.Math.abs;
import static java.lang.Math.max;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class Planet {

  Game game;
  public Planet(Game game) {
    super();
    this.game = game;
  }

  private List<Rock> rocks = new ArrayList<Rock>();
  private List<Cloud> clouds = new ArrayList<Cloud>();
  float x;
  float y;
  
  float radius = 10;
  
  public void addCloud(Cloud cloud) {
    clouds.add(cloud);
  }
  
  public void addRock(Rock rock) {
    rocks.add(rock);
    rock.setPlanet(this);
    
    if(rocks.size() == 1) {
      x = rock.x;
      y = rock.y;
    }
    
    radius = max(radius, max(abs(y - rock.y), abs(x - rock.x)));
  }
  
  public void removeRock(Rock rock) {
    rocks.remove(rock);
    rock.setPlanet(null);
  }
  
  public void render(Graphics2D g) {
    for(int i = 0; i < rocks.size(); i ++) {
      Rock rock = rocks.get(i);
      g.translate(rock.x, rock.y);
      rock.outline(g);
      g.translate(-rock.x, -rock.y);
    }
    
    for(int i = 0; i < rocks.size(); i ++) {
      Rock rock = rocks.get(i);
      g.translate(rock.x, rock.y);
      rock.fill(g);
      g.translate(-rock.x, -rock.y);
    }
    
    for(int i = 0; i < rocks.size(); i ++) {
      Rock rock = rocks.get(i);
      g.translate(rock.x, rock.y);
      for(int j = 0; j < rock.grass.size(); j++) {
        rock.grass.get(j).render(g);
      }
      g.translate(-rock.x, -rock.y);
    }
  }

  public Rock getNearestRock(float x2, float y2) {
    float minDistanceSq = -1f;
    Rock nearestRock = rocks.get(0);
    for(int i = 0; i < rocks.size(); i ++) {
      Rock rock = rocks.get(i);
      float xDiff = rock.x - x2;
      float yDiff = rock.y - y2;
      float dist = (xDiff * xDiff) + (yDiff * yDiff);
      
      if(dist < minDistanceSq || minDistanceSq == -1) {
        nearestRock = rock;
        minDistanceSq = dist;
      }
    }
    
    return nearestRock;
  }
}
