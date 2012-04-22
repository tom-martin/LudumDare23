package com.heychinaski.ld23;

import java.applet.Applet;
import java.awt.Dimension;

import javax.swing.JPanel;

public class MainApplet extends Applet {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Override
  public void start() {
    super.start();
    
    JPanel panel = new JPanel();
    
    panel.setPreferredSize(new Dimension(800,600));
    panel.setLayout(null);
    
    final Game game = new Game();
    panel.add(game);
    add(panel);
    
    new Thread() {
      public void run() {
        game.start();
      }
    }.start();
  }
}
