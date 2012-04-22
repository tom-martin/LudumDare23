package com.heychinaski.ld23;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main {

  /**
   * @param args
   */
  public static void main(String[] args) {
    JFrame mainWindow = new JFrame("Working title");
    JPanel panel = (JPanel) mainWindow.getContentPane();
    
    panel.setPreferredSize(new Dimension(800,600));
    panel.setLayout(null);
    
    final Game game = new Game();
    panel.add(game);
    
    mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mainWindow.pack();
    mainWindow.setResizable(false);
    
    new Thread() {
      public void run() {
        game.start();
      }
    }.start();
    game.requestFocus();
    mainWindow.setVisible(true);
  }

}
