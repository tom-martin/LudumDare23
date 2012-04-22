package com.heychinaski.ld23;

import java.awt.Graphics2D;

public abstract class Camera extends Entity {

  Game game;

  public Camera(Game game) {
    this.game = game;
  }

  public void look(Graphics2D g) {
    g.translate((game.getWidth() / 2)-x, (game.getHeight() / 2)-y);
  }
  
}
