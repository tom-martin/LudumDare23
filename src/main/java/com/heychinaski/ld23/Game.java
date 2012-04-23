package com.heychinaski.ld23;

import static java.lang.Math.round;

import java.awt.Canvas;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Game extends Canvas {
  private static final int METEOR_COUNT = 12;

  private static final long serialVersionUID = 1L;

  private static final int ENEMY_COUNT = 300;
  
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
  
  private Planet currentPlanet;
  
  private List<Planet> planets;

  Player player;
  
  private Image heartImage;

  private Image alienImage;

  private Pointer planetPointer;

  private boolean running = true;

  private boolean showTitle = true;
  
  private boolean paused = false;

  private Image title;

  private long score; 
  
  private Image[] nums = new Image[10];

  boolean scoreDirty = false;

  private boolean showGameover;

  private Image gameOverImage;

  private Image pausedImage;

  private long pauseTime;

  private long lastRocketSoundTime;

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
    
    addFocusListener(new FocusListener() {
      
      @Override
      public void focusLost(FocusEvent arg0) {
        if(!showGameover && !showTitle) paused = true;
      }
      
      @Override
      public void focusGained(FocusEvent arg0) {
        
      }
    });
    
    addMouseListener(new MouseListener() {

      @Override
      public void mouseClicked(MouseEvent arg0) {
        if(showGameover) {
          reset();
          showTitle = true;
          showGameover = false;
        } else if(showTitle) {
          showTitle = false;
        }
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
    
    Graphics2D g;
    reset();
    
    long last = System.currentTimeMillis();
    while (running ) {
      long now = System.currentTimeMillis();
      float tick = (float)(now - last) / 1000;
//      if(!showTitle && !showGameover && !paused && Util.randomInt(200) == 0) System.out.println("Fps: " + 1f / tick);
      last = now;
      
      if(input.isKeyDown(KeyEvent.VK_ESCAPE)) {
        System.exit(0);
      }
      if(input.isKeyDown(KeyEvent.VK_P) && System.currentTimeMillis() - pauseTime > 500) {
        pauseTime = System.currentTimeMillis();
        paused = !paused;
      }
      
      if(showTitle || showGameover  || paused) {
        Image img = showGameover ? gameOverImage : title;
        if(paused) img = pausedImage;
        g = (Graphics2D)strategy.getDrawGraphics();
        bgTile.render(round(-camera.x), round(-camera.y), g);
        int imgX = (getWidth() - img.getWidth(null)) / 2;
        int imgY = (getHeight() - img.getHeight(null)) / 2;
        g.drawImage(img, imgX, imgY, null);
        
        if(showGameover) {
          renderScore(g, imgX + 110, imgY + 45);
        }
        
        g.dispose();
        strategy.show();
        
        continue;
      }
      
      if(player.health <= 0) {
        gameOver();
        continue;
      }
      
      if(scoreDirty) {
        updateScore();
        scoreDirty = false;
      }
      
      if(Util.randomInt(1000) == 0 && iceblocks.size() < 3 && currentPlanet.isFinished()) {
        addNewIceBlock();
      }
      
      if(Util.randomInt(1000) == 0 && meteors.size() < METEOR_COUNT && !currentPlanet.isFinished()) {
        addNewMeteor();
      }
      
      if(Util.randomInt(200 + enemies.size()) == 0 && enemies.size() < ENEMY_COUNT) {
        addNewEnemy();
      }
      
      if(currentPlanet.isFinished() && currentPlanet.clouds.size() >= 5) {
        createNewPlanet();
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

  private void reset() {
    BufferStrategy strategy = getBufferStrategy();
    entities = new ArrayList<Entity>();
    collisionManager = new CollisionManager(this);
    
    this.imageManager = new ImageManager(this, "title.png",
                                                "gameover.png",
                                                "paused.png",
                                                "man_flying.png", 
                                                "man_arm.png", 
                                                "grass1.png", 
                                                "grass2.png", 
                                                "grass3.png", 
                                                "heart.png", 
                                                "alien.png", 
                                                "0.png",
                                                "1.png",
                                                "2.png",
                                                "3.png",
                                                "4.png",
                                                "5.png",
                                                "6.png",
                                                "7.png",
                                                "8.png",
                                                "9.png");
    
    for(int i = 0; i < 10; i++) {
      nums[i] = imageManager.get(i+".png");
    }
    
    heartImage = imageManager.get("heart.png");
    alienImage = imageManager.get("alien.png");
    
    Graphics2D g = (Graphics2D)strategy.getDrawGraphics();
    player = new Player(imageManager.get("man_flying.png"), imageManager.get("man_arm.png"), g.getDeviceConfiguration());
    camera = new EntityTrackingCamera(player, this);
    bgTile = new BackgroundTile(1024, g.getDeviceConfiguration());
    title = imageManager.get("title.png");
    gameOverImage = imageManager.get("gameover.png");
    pausedImage = imageManager.get("paused.png");
    
    entities.add(player);
    
    rocks = new ArrayList<Rock>();
    meteors = new ArrayList<Meteor>();
    bullets = new ArrayList<Bullet>();
    pointers = new ArrayList<Pointer>();
    iceblocks = new ArrayList<IceBlock>();
    clouds = new ArrayList<Cloud>();
    enemies = new ArrayList<Enemy>();
    planets = new ArrayList<Planet>();
    
    planetPointer = null;
    
    score = 0;
    
    createNewPlanet();
    addNewMeteor();
  }

  private void createNewPlanet() {
    currentPlanet = new Planet(this);
    planets.add(currentPlanet);
    Rock seedRock = new SeedRock();
    rocks.add(seedRock);
    seedRock.x = Util.randomInt(worldSize * 2) - worldSize;
    seedRock.y = Util.randomInt(worldSize * 2) - worldSize;
    seedRock.nextX = seedRock.x;
    seedRock.nextY = seedRock.y;
    entities.add(seedRock);
    currentPlanet.addRock(seedRock);
    
    if(planetPointer == null) {
      planetPointer = new Pointer(player, seedRock);
      pointers.add(planetPointer);
      entities.add(planetPointer);
    } else {
      planetPointer.tracking = seedRock;
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
    showGameover = true;
    showTitle = false;
  }

  private void renderHUD(Graphics2D g) {
    renderScore(g, 380, getHeight() - 50);
    g.setClip(new Rectangle(0, getHeight() - 50, Math.round(380 * ((float)player.health / 100)), 50));
    for(int i = 0; i < 10; i++) {
      g.drawImage(heartImage, 10 + (i * 37), getHeight() - 50, null);
    }
  }

  private void renderScore(Graphics2D g, int x, int y) {
    String scoreStr = "" + score;
    int nextX = x;
    for(int i= 0; i < scoreStr.length(); i++) {
      Character c = scoreStr.charAt(i);
      Image img = nums[c.charValue() - 48];
      g.drawImage(img, nextX, y, null);
      nextX += img.getWidth(null) + 5;
    }
  }

  private void updateScore() {
    score = 0;
    
    for(int i = 0; i < planets.size(); i++) {
      Planet planet = planets.get(i);
      score += planet.score();
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
    
    for(int i = 0; i < planets.size(); i++) {
      Planet planet = planets.get(i);
      if((planet.x + (planet.radius * 2)) + transX > cameraL && (planet.x - (planet.radius * 2)) + transX < cameraR &&
         (planet.y + (planet.radius * 2)) + transY > cameraT && (planet.y  - (planet.radius * 2)) + transY < cameraB) {
        planet.render(extraG);
      }
    }
    
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
  
  public synchronized void playHurtSound() {
    playSound("/hurt" + Util.randomInt(2) + ".wav");
  }
  
  public synchronized void playAsplodeSound() {
    playSound("/asplode" + Util.randomInt(2) + ".wav");
  }
  
  public synchronized void playHitPlanet() {
    playSound("/hitplanet" + Util.randomInt(2) + ".wav");
  }
  
  public synchronized void playShootSound() {
    playSound("/shoot" + Util.randomInt(2) + ".wav");
  }
  
  public synchronized void playHitSound() {
    playSound("/hit" + Util.randomInt(2) + ".wav");
  }
  
  public synchronized void playRocketSound() {
    if(System.currentTimeMillis() - lastRocketSoundTime > 2000) {
      playSound("/rocket" + Util.randomInt(2) + ".wav");
      lastRocketSoundTime = System.currentTimeMillis();
    }
  }

  private void playSound(String soundLoc) {
    try {
      Clip clip = AudioSystem.getClip();
      AudioInputStream inputStream = AudioSystem.getAudioInputStream(Game.this.getClass().getResourceAsStream(soundLoc));
      clip.open(inputStream);
      clip.start(); 
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
  }
}
