package com.heychinaski.ld23;

import java.awt.Graphics2D;

public class EntityTrackingCamera extends Camera {
  
  private Entity toTrack;

  public EntityTrackingCamera(Entity toTrack, int width, int height) {
    super(width, height);
    this.toTrack = toTrack;
  }

  @Override
  public void update(float tick, Game game) {
    this.x = toTrack.x;
    this.y = toTrack.y;
  }

  @Override
  public void render(Graphics2D g) {}

  @Override
  public void collided(Entity with, float tick, Game game) {
  }

}
