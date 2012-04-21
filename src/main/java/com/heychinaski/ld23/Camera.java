package com.heychinaski.ld23;

import java.awt.Graphics2D;

public abstract class Camera extends Entity {
  public int width;
  public int height;

  public Camera(int width, int height) {
    this.width = width;
    this.height = height;
  }

  public void look(Graphics2D g) {
    g.translate((width / 2)-x, (height / 2)-y);
  }

}
