package com.heychinaski.ld23;

public class Input {

  public boolean[] pressedKeys = new boolean[1024];
  
  public int getMouseX() {
    return mouseX;
  }

  public void setMouseX(int mouseX) {
    this.mouseX = mouseX;
  }

  public int getMouseY() {
    return mouseY;
  }

  public void setMouseY(int mouseY) {
    this.mouseY = mouseY;
  }

  public int getWorldMouseX() {
    return worldMouseX;
  }

  public void setWorldMouseX(int worldMouseX) {
    this.worldMouseX = worldMouseX;
  }

  public int getWorldMouseY() {
    return worldMouseY;
  }

  public void setWorldMouseY(int worldMouseY) {
    this.worldMouseY = worldMouseY;
  }

  int mouseX;
  int mouseY;
  
  int worldMouseX;
  int worldMouseY;
  
  public void keyDown(int code) {
    if(code < pressedKeys.length) pressedKeys[code] = true;
  }
  
  public void keyUp(int code) {
    if(code < pressedKeys.length) pressedKeys[code] = false;
  }
  
  public boolean isKeyDown(int code) {
    return code < pressedKeys.length ? pressedKeys[code] : false; 
  }
  
  public void update(Camera camera, int mouseX, int mouseY) {
    this.mouseX = mouseX;
    this.mouseY = mouseY;
    
    this.worldMouseX = mouseX + ((int)camera.x - (camera.width / 2));
    this.worldMouseY = mouseY + ((int)camera.y - (camera.height / 2));
  }
  
}