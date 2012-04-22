package com.heychinaski.ld23;

import static java.lang.Math.round;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;

public class Game extends Canvas {
  private static final long serialVersionUID = 1L;
  
  private BackgroundTile bgTile;
  
  Input input = new Input();
  private ImageManager imageManager;
  
  public final static int SCREEN_WIDTH = 800;
  public final static int SCREEN_HEIGHT = 600;
  
  List<Entity> entities;

  private CollisionManager collisionManager;
  
  int worldSize = 4096;

  private EntityTrackingCamera camera;

  private List<Rock> rocks;
  
  private List<Meteor> meteors;
  
  private List<Bullet> bullets;

  private Planet planet;
  
  public Game() {
    setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
    setIgnoreRepaint(true);
    
    addKeyListener(new KeyListener() {
      
      @Override
      public void keyTyped(KeyEvent e) {}
      
      @Override
      public void keyReleased(KeyEvent e) {input.keyUp(e.getKeyCode());}
      
      @Override
      public void keyPressed(KeyEvent e) { input.keyDown(e.getKeyCode());}
    });
    
    addMouseListener(new MouseListener() {

      @Override
      public void mouseClicked(MouseEvent arg0) {
        
      }

      @Override
      public void mouseEntered(MouseEvent arg0) {
        
      }

      @Override
      public void mouseExited(MouseEvent arg0) {
        
      }

      @Override
      public void mousePressed(MouseEvent e) {
        input.setMouseDown(e.getButton());
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        input.setMouseUp(e.getButton());
      }
      
    });
  }
  
  public void start() {
    createBufferStrategy(2);
    BufferStrategy strategy = getBufferStrategy();
    
    entities = new ArrayList<Entity>();
    collisionManager = new CollisionManager(this);
    
    imageManager = new ImageManager(this, "man_flying.png", "man_arm.png");
    
    Graphics2D g = (Graphics2D)strategy.getDrawGraphics();
    Player player = new Player(imageManager.get("man_flying.png"), imageManager.get("man_arm.png"), g.getDeviceConfiguration());
    camera = new EntityTrackingCamera(player, SCREEN_WIDTH, SCREEN_HEIGHT);
    bgTile = new BackgroundTile(1024, g.getDeviceConfiguration());
    
    entities.add(player);
    
    rocks = new ArrayList<Rock>();
    meteors = new ArrayList<Meteor>();
    bullets = new ArrayList<Bullet>();
    
    planet = new Planet(this);
    Rock seedRock = new Rock();
    seedRock.fillColor = new Color(200, 100, 80);
    seedRock.outlineColor = new Color(120, 100, 80);
    rocks.add(seedRock);
    seedRock.x = 100;
    seedRock.y = 100;
    seedRock.nextX = 100;
    seedRock.nextY = 100;
    entities.add(seedRock);
    planet.addRock(seedRock);
    
//    for(int i = 1; i < 200; i++) {
//      addNewRock();
//    }
    
    for(int i = 1; i < 10; i++) {
      addNewMeteor();
    }
    
    long last = System.currentTimeMillis();
    while (true) {
      long now = System.currentTimeMillis();
      float tick = (float)(now - last) / 1000;
      if(Util.randomInt(200) == 0) System.out.println("Fps: " + 1f / tick);
      last = now;
      
      if(input.isKeyDown(KeyEvent.VK_ESCAPE)) System.exit(0);
      
//      if(freeRockCount < 200) addNewRock();
      
      for(int i = 0; i < entities.size(); i++) {
        entities.get(i).update(tick, this);
      }
      collisionManager.update(tick);
      for(int i = 0; i < entities.size(); i++) {
        entities.get(i).applyNext();
      }
      camera.update(tick, this);
      Point mousePosition = getMousePosition();
      if(mousePosition != null) input.update(camera, mousePosition.x, mousePosition.y);
      
      g = (Graphics2D)strategy.getDrawGraphics();
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      
      bgTile.render(round(-camera.x), round(-camera.y), g);
      camera.look(g);
      
      
      renderWithTrans(g, player, 0, 0);
      if(camera.x < -worldSize + SCREEN_WIDTH) {
        renderWithTrans(g, player, -worldSize * 2, 0);
      }
      if(camera.y < -worldSize + SCREEN_HEIGHT) {
        renderWithTrans(g, player, 0, -worldSize * 2);
      }
      if(camera.x < -worldSize + SCREEN_WIDTH && camera.y < -worldSize + SCREEN_HEIGHT) {
        renderWithTrans(g, player, -worldSize * 2, -worldSize * 2);
      }
      if(camera.x > worldSize - SCREEN_WIDTH) {
        renderWithTrans(g, player, worldSize * 2, 0);
      }
      if(camera.y > worldSize - SCREEN_HEIGHT) {
        renderWithTrans(g, player, 0, worldSize * 2);
      }
      if(camera.x > worldSize - SCREEN_WIDTH && camera.y > worldSize - SCREEN_HEIGHT) {
        renderWithTrans(g, player, worldSize * 2, worldSize * 2);
      }
      
//      g.setColor(Color.white);
//      g.drawRect(-worldSize, -worldSize, worldSize * 2, worldSize * 2);
      
    
      g.dispose();
      strategy.show();
      
    
      // finally pause for a bit. Note: this should run us at about
      // 100 fps but on windows this might vary each loop due to
      // a bad implementation of timer
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }    
  }
  
  void addNewMeteor() {
    Meteor meteor = new Meteor();
    meteor.x = Util.randomInt(worldSize * 2) - worldSize;
    meteor.y = Util.randomInt(worldSize * 2) - worldSize;
    meteor.nextX = meteor.x;
    meteor.nextY = meteor.y;
    entities.add(meteor);
    meteors.add(meteor);
  }

  public void addRock(Rock rock) {
    entities.add(rock);
    rocks.add(rock);
  }

  private void renderWithTrans(Graphics2D g, Player player,
      int transX, int transY) {
    Graphics2D extraG = (Graphics2D) g.create();
    extraG.translate(transX, transY);
    
    float cameraL = camera.x - (SCREEN_WIDTH / 2);
    float cameraR= camera.x + (SCREEN_WIDTH / 2);
    
    float cameraT = camera.y - (SCREEN_WIDTH / 2);
    float cameraB= camera.y + (SCREEN_WIDTH / 2);
    
    for(int i = 0; i < meteors.size(); i++) {
      Meteor meteor = meteors.get(i);
      if((meteor.x + meteor.w) + transX > cameraL && (meteor.x - meteor.w) + transX < cameraR &&
         (meteor.y + meteor.h) + transY > cameraT && (meteor.y  - meteor.h) + transY < cameraB) {
        meteor.render(extraG);
      }
    }
    
    for(int i = 0; i < rocks.size(); i++) {
      Rock rock = rocks.get(i);
      if((rock.x + rock.w) + transX > cameraL && (rock.x - rock.w) + transX < cameraR &&
         (rock.y + rock.h) + transY > cameraT && (rock.y  - rock.h) + transY < cameraB) {
        rock.render(extraG);
      }
    }
    
    
    planet.render(extraG);
    player.render(extraG);
    
    for(int i = 0; i < bullets.size(); i++) {
      Bullet bullet = bullets.get(i);
      if((bullet.x + bullet.w) + transX > cameraL && (bullet.x - bullet.w) + transX < cameraR &&
         (bullet.y + bullet.h) + transY > cameraT && (bullet.y  - bullet.h) + transY < cameraB) {
        bullet.render(extraG);
      }
    }
    
    extraG.dispose();
  }

  public void addBullet(Bullet bullet) {
    bullets.add(bullet);
    entities.add(bullet);
  }
  
  public void removeBullet(Bullet bullet) {
    bullets.remove(bullet);
    entities.remove(bullet);
  }

  public void removeMeteor(Meteor meteor) {
    meteors.remove(meteor);
    entities.remove(meteor);
  }
}
