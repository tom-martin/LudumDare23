package com.heychinaski.ld23;

import static java.lang.Math.round;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;


public class Player extends Entity {

  private Image leftFlyImage;
  private Image rightFlyImage;
  
  private Image leftArmImage;
  private Image rightArmImage;
  
  private Rock heldRock = null;
  
  float xMomentum = 0;
  float yMomentum = 0;
  
  int xDir = -1;
  
  int aimX, aimY;
  private long lastHeld;
  
  private long lastFire = 0;
  
  static final float MAX_MOMENTUM = 1000;
  static final float ACC = 1000;
  static final float DAMP = 200;
  static final float THROW_SPEED = 200;
  static final float BULLET_SPEED = 600;
  static final float FIRE_PAUSE = 200;

  public Player(Image flyingImage, Image armImage, GraphicsConfiguration gc) {
    w = flyingImage.getWidth(null);
    h = flyingImage.getHeight(null);
    
    this.leftFlyImage = gc.createCompatibleImage((int)w, (int)h, Transparency.TRANSLUCENT);
    this.rightFlyImage = gc.createCompatibleImage((int)w, (int)h, Transparency.TRANSLUCENT);
    
    Graphics rightG = rightFlyImage.getGraphics();
    rightG.drawImage(flyingImage, 0, 0, null);
    Graphics2D leftG = (Graphics2D) leftFlyImage.getGraphics();
    leftG.drawImage(flyingImage, 0, 0, (int)w, (int)h, (int)w, 0, 0, (int)h, null);
    rightG.dispose();
    leftG.dispose();
    
    int armW = armImage.getWidth(null);
    int armH = armImage.getHeight(null);
    this.rightArmImage = gc.createCompatibleImage(armW, armH, Transparency.TRANSLUCENT);
    this.leftArmImage = gc.createCompatibleImage(armW, armH, Transparency.TRANSLUCENT);
    rightG = rightArmImage.getGraphics();
    rightG.drawImage(armImage, 0, 0, null);
    leftG = (Graphics2D) leftArmImage.getGraphics();
    leftG.drawImage(armImage, 0, 0, armW, armH, armW, 0, 0, armH, null);
    
    rightG.dispose();
    leftG.dispose();
  }
  
  @Override
  public void update(float tick, Game game) {
    if(game.input.isKeyDown(KeyEvent.VK_W)) yMomentum = Math.max(-MAX_MOMENTUM, yMomentum - (tick * ACC));
    else if(game.input.isKeyDown(KeyEvent.VK_S)) yMomentum = Math.min(MAX_MOMENTUM, yMomentum + (tick * ACC));
    else if(yMomentum > 0) yMomentum = Math.max(0, yMomentum - (tick * DAMP));
    else if(yMomentum < 0) yMomentum = Math.min(0, yMomentum + (tick * DAMP));
    
    if(game.input.isKeyDown(KeyEvent.VK_A)) {
      xMomentum = Math.max(-MAX_MOMENTUM, xMomentum - (tick * ACC));
    } else if(game.input.isKeyDown(KeyEvent.VK_D)) {
      xMomentum = Math.min(MAX_MOMENTUM, xMomentum + (tick * ACC));
    } else if(xMomentum > 0) xMomentum = Math.max(0, xMomentum - (tick * DAMP));
    else if(xMomentum < 0) xMomentum = Math.min(0, xMomentum + (tick * DAMP));
    
    nextX = x + (xMomentum * tick);
    nextY = y + (yMomentum * tick);
      
    aimX = game.input.getWorldMouseX();
    aimY = game.input.getWorldMouseY();
    
    xDir = x > aimX ? -1 : 1;
    
    if(game.input.isMouseDown(MouseEvent.BUTTON1))
    if(heldRock != null) {
      heldRock.setHeld(false);
      Point2D.Float v = new Point2D.Float(game.input.getWorldMouseX() - x, game.input.getWorldMouseY() - y);
      Util.normalise(v);
      
      heldRock.xMomentum = xMomentum + (v.x * THROW_SPEED);
      heldRock.yMomentum = yMomentum + (v.y * THROW_SPEED);
      
      
      heldRock = null;
      
      lastFire = System.currentTimeMillis();
      lastHeld = System.currentTimeMillis();
    } else if(System.currentTimeMillis() - lastFire > FIRE_PAUSE){
      Point2D.Float v = new Point2D.Float(game.input.getWorldMouseX() - x, game.input.getWorldMouseY() - y);
      Util.normalise(v);
      
      Bullet bullet = new Bullet(x + (v.x * 3), y + (v.y * 3), xMomentum + (v.x * BULLET_SPEED), yMomentum + (v.y * BULLET_SPEED));
      
      game.addBullet(bullet);
      lastFire = System.currentTimeMillis();
    }
  }
  
  @Override
  public void collided(Entity with, float tick, Game game) {
    if(heldRock == null && with instanceof Rock && (System.currentTimeMillis() - lastHeld) > 2000) {
      if(((Rock)with).setHeld(true)) {
        heldRock = (Rock)with;
      }
    }
    
    if(with instanceof Meteor) {
      xMomentum = Math.max(-MAX_MOMENTUM, Math.min(MAX_MOMENTUM, -xMomentum + ((Meteor)with).xMomentum));
      yMomentum = Math.max(-MAX_MOMENTUM, Math.min(MAX_MOMENTUM, -yMomentum + ((Meteor)with).yMomentum));
    }
  }
  
  @Override
  public void applyNext() {
    x = nextX;
    y = nextY;
    
    if(heldRock != null) {
      heldRock.x = x;
      heldRock.y = y;
      
      heldRock.nextX = x;
      heldRock.nextY = y;
    }
  }
  
  @Override
  public void render(Graphics2D g) {
    Image image = xDir < 0 ? leftFlyImage : rightFlyImage;
    int drawX = round(x) - (image.getWidth(null) / 2);
    int drawY = round(y) - (image.getHeight(null) / 2);
    g.drawImage(image, drawX, drawY, null);
    
    drawArm(g);
  }

  private void drawArm(Graphics2D g) {
    Image armImage = xDir < 0 ? leftArmImage : rightArmImage;
    
    int armYOriginCorrection = 10;
    int armXOriginCorrection = xDir < 0 ? -10 : 10;
    
    int armYCorrection = -7;
    int armXCorrection = xDir < 0 ? -34 : -7;
    
    g = (Graphics2D) g.create();
    float rot = (float) Math.atan2(((y-armYOriginCorrection)-aimY),((x-armXOriginCorrection)-aimX));
    if(xDir > 0) rot += 135;
    g.translate(x - armXOriginCorrection, y - armYOriginCorrection);
    
    g.rotate(rot, 0, 0);
    
    if(heldRock != null) {
      Graphics2D rockG = (Graphics2D) g.create();
      rockG.translate((20 * xDir), 0);
      heldRock.draw(rockG);
      rockG.dispose();
    }
    
    g.fillOval(-5, -5, 10, 10);
    g.drawImage(armImage, armXCorrection, armYCorrection, null);
    
    g.dispose();
  }
}
