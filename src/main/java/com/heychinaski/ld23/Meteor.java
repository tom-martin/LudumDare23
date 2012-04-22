package com.heychinaski.ld23;

import static com.heychinaski.ld23.Util.randomInt;

public class Meteor extends SpaceJunk {

  public Meteor() {
    super(300, 200, 0);
  }

  @Override
  public void collided(Entity with, float tick, Game game) {
    if(with instanceof Bullet) {
      for(int i = 0; i < ((int)w / 50) + 5; i++) {
        Rock rock = new Rock();
        game.addRock(rock);
        game.removeMeteor(this);
      
        rock.nextX = this.x + (randomInt((int)w) - (w / 2));
        rock.nextY = this.y + (randomInt((int)h) - (h / 2));
//        rock.xMomentum += this.xMomentum;
//        rock.yMomentum += this.yMomentum;
      }
      
      game.addNewMeteor();
    }
    
    if(with instanceof Meteor) {
      xMomentum *= -1;
      yMomentum *= -1;
    }
  }

}
