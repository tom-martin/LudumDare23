package com.heychinaski.ld23;

import java.awt.Graphics2D;
import java.awt.Image;

public class GrassBlob extends Entity {
  
  private Image image;
  private float rot;

  public GrassBlob(Rock rock, Image image) {
    this.image = image;
    x = Util.randomFloat(rock.w);
    y = Util.randomFloat(rock.h);
    rock.addGrassBlob(this);
    rot = Util.randomFloat(360);
  }

  @Override
  public void update(float tick, Game game) {
    
  }

  @Override
  public void render(Graphics2D g) {
    g = (Graphics2D) g.create();
    g.rotate(rot);
    g.translate(x - image.getWidth(null) / 2, y - image.getHeight(null) / 2);
    g.drawImage(image, 0, 0, null);
    g.dispose();
  }

  @Override
  public void collided(Entity with, float tick, Game game) {

  }

}
