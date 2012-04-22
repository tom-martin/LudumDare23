package com.heychinaski.ld23;

import java.awt.geom.Rectangle2D;

public class CollisionManager {
  
  private Game game;
  
  public CollisionManager(Game game) {
    super();
    this.game = game;
  }

  public void update(float tick) {
    Rectangle2D.Float boundsA = new Rectangle2D.Float();
    Rectangle2D.Float boundsB = new Rectangle2D.Float();
    
    for(int i = 0; i < game.entities.size(); i++) {
      Entity a = game.entities.get(i);
      
      if(a.nextX < -game.worldSize) {
        a.nextX = (game.worldSize * 2) - a.nextX;
      } else if(a.nextY < -game.worldSize) {
        a.nextY = (game.worldSize * 2) - a.nextY;
      } else if(a.nextX > game.worldSize) {
        a.nextX = (-game.worldSize * 2) + a.nextX;
      } else if(a.nextY > game.worldSize) {
        a.nextY = (-game.worldSize * 2) + a.nextY;
      }
    }
    
    for(int i = 0; i < game.entities.size(); i++) {
      Entity a = game.entities.get(i);
      
      boundsA.x = a.nextX - (a.w / 2);
      boundsA.y = a.nextY - (a.h / 2);
      boundsA.width = a.w;
      boundsA.height = a.h;
      
      for(int j = i; j < game.entities.size(); j++) {
        Entity b = game.entities.get(j);
        
        if(a != b) {
          boundsB.x = b.x - (b.w / 2);
          boundsB.y = b.y - (b.h / 2);
          boundsB.width = b.w;
          boundsB.height = b.h;
          
          if(boundsA.intersects(boundsB)) {
            a.collided(b, tick, game);
            b.collided(a, tick, game);
          }
        }
      }
    }
  }
}
