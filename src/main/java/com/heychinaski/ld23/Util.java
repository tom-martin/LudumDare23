package com.heychinaski.ld23;

import static java.lang.Math.abs;
import static java.lang.Math.random;
import static java.lang.Math.sqrt;

import java.awt.geom.Point2D;

public class Util {

  public static int randomInt(int max) { return (int)(random() * max);}

  public static float randomFloat(float max) { return (float)(random() * max);}
  
  public static void normalise(Point2D.Float v) {
    float mag = (float) abs(sqrt((v.x * v.x) + (v.y * v.y)));
    v.x /= mag;
    v.y /= mag;
  }
}
