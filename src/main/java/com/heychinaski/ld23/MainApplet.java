package com.heychinaski.ld23;

import java.applet.Applet;
import java.awt.BorderLayout;

public class MainApplet extends Applet {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Override
  public void start() {
    super.start();
    resize(1024, 768);
    setLayout(new BorderLayout());
    final Game game = new Game();
    add(game, BorderLayout.CENTER);
    
    new Thread() {
      public void run() {
        game.start();
      }
    }.start();
    
    game.requestFocus();
  }
}
