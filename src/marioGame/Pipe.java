package marioGame;

import processing.core.PApplet;
import processing.core.PImage;

public class Pipe extends Collider {
    private PApplet frame;
    private PImage sprite;

    public Pipe(float xLocation, float yLocation, PApplet frame, PImage sprite){
        super(48, 32, xLocation, yLocation);
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
