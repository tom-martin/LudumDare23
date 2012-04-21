package com.heychinaski.ld23;

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
  
  public void addRock(Rock rock) {
    rocks.add(rock);
    rock.setPlanet(this);
    game.decrementFreeRockCount();
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
  }
}
