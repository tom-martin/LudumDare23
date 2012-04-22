package com.heychinaski.ld23;

import static java.lang.Math.round;

import java.awt.Canvas;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;

public class Game extends Canvas {
  private static final int METEOR_COUNT = 8;

  private static final long serialVersionUID = 1L;

  private static final int ENEMY_COUNT = 100;
  
  private BackgroundTile bgTile;
  
  Input input = new Input();
  ImageManager imageManager;
  
  List<Entity> entities;

  private CollisionManager collisionManager;
  
  int worldSize = 8192;

  EntityTrackingCamera camera;

  private List<Rock> rocks;
  
  private List<Meteor> meteors;
  
  private List<Bullet> bullets;

  private List<Pointer> pointers;
  
  private List<IceBlock> iceblocks;
  
  private List<Cloud> clouds;
  
  private List<Enemy> enemies;
  
  private Planet planet;

  Player player;
  
  private Image heartImage;

  private Image alienImage;

  public Game() {
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
    setSize(1024, 768);
    createBufferStrategy(2);
    BufferStrategy strategy = getBufferStrategy();
    
    entities = new ArrayList<Entity>();
    collisionManager = new CollisionManager(this);
    
    imageManager = new ImageManager(this, "man_flying.png", "man_arm.png", "grass1.png", "grass2.png", "grass3.png", "heart.png", "alien.png");
    
    heartImage = imageManager.get("heart.png");
    alienImage = imageManager.get("alien.png");
    
    Graphics2D g = (Graphics2D)strategy.getDrawGraphics();
    player = new Player(imageManager.get("man_flying.png"), imageManager.get("man_arm.png"), g.getDeviceConfiguration());
    camera = new EntityTrackingCamera(player, this);
    bgTile = new BackgroundTile(1024, g.getDeviceConfiguration());
    
    entities.add(player);
    
    rocks = new ArrayList<Rock>();
    meteors = new ArrayList<Meteor>();
    bullets = new ArrayList<Bullet>();
    pointers = new ArrayList<Pointer>();
    iceblocks = new ArrayList<IceBlock>();
    clouds = new ArrayList<Cloud>();
    enemies = new ArrayList<Enemy>();
    
    planet = new Planet(this);
    Rock seedRock = new SeedRock();
    rocks.add(seedRock);
    seedRock.x = 100;
    seedRock.y = 100;
    seedRock.nextX = 100;
    seedRock.nextY = 100;
    entities.add(seedRock);
    planet.addRock(seedRock);
    
    Pointer pointer = new Pointer(player, seedRock);
    pointers.add(pointer);
    entities.add(pointer);
    
    for(int i = 0; i < METEOR_COUNT; i++) {
      addNewMeteor();
    }
    
    for(int i = 0; i < ENEMY_COUNT; i++) {
      addNewEnemy();
    }
    
    long last = System.currentTimeMillis();
    while (true) {
      long now = System.currentTimeMillis();
      float tick = (float)(now - last) / 1000;
      if(Util.randomInt(200) == 0) System.out.println("Fps: " + 1f / tick);
      last = now;
      
      if(input.isKeyDown(KeyEvent.VK_ESCAPE)) gameOver();
      
      if(player.health <= 0) gameOver();
      
      if(Util.randomInt(1000) == 0 && iceblocks.size() < 3 && planet.isFinished()) {
        addNewIceBlock();
      }
      
      if(Util.randomInt(2000) == 0 && enemies.size() < ENEMY_COUNT) {
        addNewEnemy();
      }
      
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
      Graphics2D orig = g;
      g = (Graphics2D) g.create();
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      
      bgTile.render(round(-camera.x), round(-camera.y), g);
      camera.look(g);
      
      
      renderWithTrans(g, player, 0, 0);
      if(camera.x < -worldSize + getWidth()) {
        renderWithTrans(g, player, -worldSize * 2, 0);
      }
      if(camera.y < -worldSize + getHeight()) {
        renderWithTrans(g, player, 0, -worldSize * 2);
      }
      if(camera.x < -worldSize + getWidth() && camera.y < -worldSize + getHeight()) {
        renderWithTrans(g, player, -worldSize * 2, -worldSize * 2);
      }
      if(camera.x > worldSize - getWidth()) {
        renderWithTrans(g, player, worldSize * 2, 0);
      }
      if(camera.y > worldSize - getHeight()) {
        renderWithTrans(g, player, 0, worldSize * 2);
      }
      if(camera.x > worldSize - getWidth() && camera.y > worldSize - getHeight()) {
        renderWithTrans(g, player, worldSize * 2, worldSize * 2);
      }
      
//      g.setColor(Color.white);
//      g.drawRect(-worldSize, -worldSize, worldSize * 2, worldSize * 2);
      renderHUD(orig);
    
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

  void addNewEnemy() {
    Enemy enemy = new Enemy(alienImage);
    enemy.x = Util.randomInt(worldSize * 2) - worldSize;
    enemy.y = Util.randomInt(worldSize * 2) - worldSize;
    enemy.nextX = enemy.x;
    enemy.nextY = enemy.y;
    enemies.add(enemy);
    entities.add(enemy);
  }

  private void gameOver() {
    System.exit(0);
  }

  private void renderHUD(Graphics2D g) {
    g.setClip(new Rectangle(0, getHeight() - 50, Math.round(380 * ((float)player.health / 100)), 50));
    for(int i = 0; i < 10; i++) {
      g.drawImage(heartImage, 10 + (i * 37), getHeight() - 50, null);
    }
  }

  void addCloud(Cloud cloud) {
    clouds.add(cloud);
    entities.add(cloud);
  }
  
  void addNewMeteor() {
    Meteor meteor = new Meteor();
    
    Pointer pointer = new Pointer(player, meteor);
    pointer.fillColor = meteor.fillColor;
    pointer.outlineColor = meteor.outlineColor;
    pointers.add(pointer);
    entities.add(pointer);
    meteor.pointer = pointer;
    
    meteor.x = Util.randomInt(worldSize * 2) - worldSize;
    meteor.y = Util.randomInt(worldSize * 2) - worldSize;
    meteor.nextX = meteor.x;
    meteor.nextY = meteor.y;
    entities.add(meteor);
    meteors.add(meteor);
  }
  
  void addNewIceBlock() {
    IceBlock iceBlock = new IceBlock();
    
    Pointer pointer = new Pointer(player, iceBlock);
    pointer.fillColor = iceBlock.fillColor;
    pointer.outlineColor = iceBlock.outlineColor;
    pointers.add(pointer);
    entities.add(pointer);
    iceBlock.pointer = pointer;
    
    iceBlock.x = Util.randomInt(worldSize * 2) - worldSize;
    iceBlock.y = Util.randomInt(worldSize * 2) - worldSize;
    iceBlock.nextX = iceBlock.x;
    iceBlock.nextY = iceBlock.y;
    entities.add(iceBlock);
    iceblocks.add(iceBlock);
  }

  public void addRock(Rock rock) {
    entities.add(rock);
    rocks.add(rock);
  }

  private void renderWithTrans(Graphics2D g, Player player,
      int transX, int transY) {
    Graphics2D extraG = (Graphics2D) g.create();
    extraG.translate(transX, transY);
    
    float cameraL = camera.x - (getWidth() / 2);
    float cameraR= camera.x + (getWidth() / 2);
    
    float cameraT = camera.y - (getWidth() / 2);
    float cameraB= camera.y + (getWidth() / 2);
    
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
    
    for(int i = 0; i < iceblocks.size(); i++) {
      IceBlock iceblock = iceblocks.get(i);
      if((iceblock.x + iceblock.w) + transX > cameraL && (iceblock.x - iceblock.w) + transX < cameraR &&
         (iceblock.y + iceblock.h) + transY > cameraT && (iceblock.y  - iceblock.h) + transY < cameraB) {
        iceblock.render(extraG);
      }
    }
    
    for(int i = 0; i < bullets.size(); i++) {
      Bullet bullet = bullets.get(i);
      if((bullet.x + bullet.w) + transX > cameraL && (bullet.x - bullet.w) + transX < cameraR &&
         (bullet.y + bullet.h) + transY > cameraT && (bullet.y  - bullet.h) + transY < cameraB) {
        bullet.render(extraG);
      }
    }
    
    for(int i = 0; i < enemies.size(); i++) {
      Enemy enemy = enemies.get(i);
      if((enemy.x + enemy.w) + transX > cameraL && (enemy.x - enemy.w) + transX < cameraR &&
         (enemy.y + enemy.h) + transY > cameraT && (enemy.y  - enemy.h) + transY < cameraB) {
        enemy.render(extraG);
      }
    }
    
    for(int i = 0; i < clouds.size(); i++) {
      Cloud cloud = clouds.get(i);
      if((cloud.x + cloud.w) + transX > cameraL && (cloud.x - cloud.w) + transX < cameraR &&
         (cloud.y + cloud.h) + transY > cameraT && (cloud.y  - cloud.h) + transY < cameraB) {
        cloud.render(extraG);
      }
    }
    
    planet.render(extraG);
    
    player.render(extraG);
    
    for(int i = 0; i < pointers.size(); i++) {
      Pointer pointer = pointers.get(i);
      pointer.render(extraG);      
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
    
    pointers.remove(meteor.pointer);
    entities.remove(meteor.pointer);
  }

  public void removeIceBlock(IceBlock iceBlock) {
    iceblocks.remove(iceBlock);
    entities.remove(iceBlock);
    
    pointers.remove(iceBlock.pointer);
    entities.remove(iceBlock.pointer);
  }

  public void removeRock(Rock rock) {
    rocks.remove(rock);
    entities.remove(rock);
  }

  public void removeEnemy(Enemy enemy) {
    enemies.remove(enemy);
    entities.remove(enemy);
  }
}
