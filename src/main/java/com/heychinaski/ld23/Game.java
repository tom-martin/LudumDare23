package com.heychinaski.ld23;

import static java.lang.Math.round;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;

public class Game extends Canvas {
  private static final long serialVersionUID = 1L;
  
  private BackgroundTile bgTile;
  
  private Input input = new Input();
  private ImageManager imageManager;
  
  public final static int SCREEN_WIDTH = 800;
  public final static int SCREEN_HEIGHT = 600;
  
  List<Entity> entities;

  private CollisionManager collisionManager;
  
  int worldSize = 4096;

  private EntityTrackingCamera camera;
  
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
    
    Rock[] rocks = new Rock[100];
    for(int i = 0; i < rocks.length; i++) {
      rocks[i] = new Rock();
      entities.add(rocks[i]);
      rocks[i].x = Util.randomInt(worldSize * 2) - worldSize;
      rocks[i].y = Util.randomInt(worldSize * 2) - worldSize;
    }
    
    
    long last = System.currentTimeMillis();
    while (true) {
      long now = System.currentTimeMillis();
      float tick = (float)(now - last) / 1000;
      if(Util.randomInt(200) == 0) System.out.println("Fps: " + 1f / tick);
      last = now;
      
      if(input.isKeyDown(KeyEvent.VK_ESCAPE)) System.exit(0);
      
      for(int i = 0; i < entities.size(); i++) {
        entities.get(i).update(tick, input);
      }
      collisionManager.update(tick);
      for(int i = 0; i < entities.size(); i++) {
        entities.get(i).applyNext();
      }
      camera.update(tick, input);
      Point mousePosition = getMousePosition();
      if(mousePosition != null) input.update(camera, mousePosition.x, mousePosition.y);
      
      g = (Graphics2D)strategy.getDrawGraphics();
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      
      bgTile.render(round(-camera.x), round(-camera.y), g);
      camera.look(g);
      
      
      renderWithTrans(g, player, rocks, 0, 0);
      if(camera.x < -worldSize + SCREEN_WIDTH) {
        renderWithTrans(g, player, rocks, -worldSize * 2, 0);
      }
      if(camera.y < -worldSize + SCREEN_HEIGHT) {
        renderWithTrans(g, player, rocks, 0, -worldSize * 2);
      }
      if(camera.x < -worldSize + SCREEN_WIDTH && camera.y < -worldSize + SCREEN_HEIGHT) {
        renderWithTrans(g, player, rocks, -worldSize * 2, -worldSize * 2);
      }
      if(camera.x > worldSize - SCREEN_WIDTH) {
        renderWithTrans(g, player, rocks, worldSize * 2, 0);
      }
      if(camera.y > worldSize - SCREEN_HEIGHT) {
        renderWithTrans(g, player, rocks, 0, worldSize * 2);
      }
      if(camera.x > worldSize - SCREEN_WIDTH && camera.y > worldSize - SCREEN_HEIGHT) {
        renderWithTrans(g, player, rocks, worldSize * 2, worldSize * 2);
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

  private void renderWithTrans(Graphics2D g, Player player, Rock[] rocks,
      int transX, int transY) {
    Graphics2D extraG = (Graphics2D) g.create();
    extraG.translate(transX, transY);
    
    float cameraL = camera.x - (SCREEN_WIDTH / 2);
    float cameraR= camera.x + (SCREEN_WIDTH / 2);
    
    float cameraT = camera.y - (SCREEN_WIDTH / 2);
    float cameraB= camera.y + (SCREEN_WIDTH / 2);
    
    for(int i = 0; i < rocks.length; i++) {
      Rock rock = rocks[i];
      if((rock.x + rock.w) + transX > cameraL && (rock.x - rock.w) + transX < cameraR &&
         (rock.y + rock.h) + transY > cameraT && (rock.y  - rock.h) + transY < cameraB) {
        rock.render(extraG);
      }
    }
    player.render(extraG);
    extraG.dispose();
  }
}
