package com.heychinaski.ld23;

import java.awt.Component;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ImageManager {

  private Map<String, Image> images = new HashMap<String, Image>();
      
  public ImageManager(Component component, String...requiredImages) {
    MediaTracker mediaTracker = new MediaTracker(component);
    
    int nextId = 0;
    for(String imageLoc : requiredImages) {
      Image image = loadImage(imageLoc);
      mediaTracker.addImage(image, nextId);
      images.put(imageLoc, image);
      
      nextId ++;
    }
    
    for(int i = 0; i < nextId; i++) {
      try {
        mediaTracker.waitForID(i);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
  
  private Image loadImage(String imageName) {
    ClassLoader loader = getClass().getClassLoader();
    URL url = loader.getResource(imageName);
    return Toolkit.getDefaultToolkit().getImage(url);
  }
  
  public Image get(String loc) {
    return images.get(loc);
  }
}
