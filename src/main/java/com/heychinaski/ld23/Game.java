package com.heychinaski.ld23;

import static java.lang.Math.round;

import java.awt.Canvas;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;

public class Game extends Canvas {
  private static final long serialVersionUID = 1L;
  
  private BackgroundTile bgTile;
  
  private Input input = new Input();
  private ImageManager imageManager;
  
  public Game() {
    setSize(800, 600);
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
    
    imageManager = new ImageManager(this, "man_flying.png", "man_arm.png");
    
    Graphics2D g = (Graphics2D)strategy.getDrawGraphics();
    Player player = new Player(imageManager.get("man_flying.png"), imageManager.get("man_arm.png"), g.getDeviceConfiguration());
    Camera camera = new EntityTrackingCamera(player, 800, 600);
    bgTile = new BackgroundTile(1024, g.getDeviceConfiguration());
    
    long last = System.currentTimeMillis();
    while (true) {
      long now = System.currentTimeMillis();
      float tick = (float)(now - last) / 1000;
      if(now % 1000 == 0) System.out.println("Fps: " + 1f / tick);
      last = now;
      
      if(input.isKeyDown(KeyEvent.VK_ESCAPE)) System.exit(0);
      
      player.update(tick, input);
      camera.update(tick, input);
      Point mousePosition = getMousePosition();
      if(mousePosition != null) input.update(camera, mousePosition.x, mousePosition.y);
      
      g = (Graphics2D)strategy.getDrawGraphics();
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      
      bgTile.render(round(-camera.x), round(-camera.y), g);
      camera.look(g);
      player.render(g);
      g.fillOval(input.getWorldMouseX() - 5, input.getWorldMouseY() - 5, 10, 10);
    
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
}
