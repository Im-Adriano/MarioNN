package marioGame;

import processing.core.PApplet;
import processing.core.PImage;

public class Ground extends Collider {
    private PApplet frame;
    private PImage top;
    private PImage bottom;

    public Ground(float xLocation, float yLocation, PApplet frame, PImage top, PImage bottom){
        super(16,16, xLocation, yLocation);
        this.frame = frame;
        this.top = top;
        this.bottom = bottom;
        this.height = this.height * 2;
    }

    public void show(){
            frame.image(bottom, xLocation, yLocation  + 16);
            frame.image(top, xLocation, yLocation);
    }

    public void reset(){
        this.xLocation = startX;
        this.yLocation = startY;
    }
}
