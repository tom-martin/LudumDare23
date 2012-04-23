package com.heychinaski.ld23;

import static com.heychinaski.ld23.Util.randomInt;

import java.awt.Color;

public class Meteor extends SpaceJunk {
  
  Pointer pointer;

  public Meteor() {
    super(300, 200, 0);
    
    outlineColor = new Color(150, 100, 10);
    fillColor = new Color(200, 150, 50);
  }

  @Override
  public void collided(Entity with, float tick, Game game) {
    if(with instanceof Bullet) {
      for(int i = 0; i < ((int)w / 50) + 5; i++) {
        game.playAsplodeSound();
        Rock rock = new Rock();
        game.addRock(rock);
        game.removeMeteor(this);
      
        rock.nextX = this.x + (randomInt((int)w) - (w / 2));
        rock.nextY = this.y + (randomInt((int)h) - (h / 2));
        rock.x = nextX;
        rock.x = nextY;
        rock.xMomentum += this.xMomentum;
        rock.yMomentum += this.yMomentum;
      }
    }
    
    if(with instanceof Meteor) {
      xMomentum *= -1;
      yMomentum *= -1;
    }
  }

}
