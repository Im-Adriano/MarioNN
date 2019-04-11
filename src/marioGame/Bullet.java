package marioGame;

import processing.core.PApplet;
import processing.core.PImage;

public class Bullet extends Collider {
    private PApplet frame;
    private PImage sprite;

    public Bullet(float xLocation, float yLocation, PApplet frame, PImage sprite){
        super(16, 14, xLocation, yLocation);
        this.frame = frame;
        this.sprite = sprite;
    }

    public void show(){
        frame.image(sprite, xLocation, yLocation);
    }

    public void reset(){
        this.xLocation = startX;
        this.yLocation = startY;
    }
}
