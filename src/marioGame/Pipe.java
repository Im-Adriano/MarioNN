package marioGame;

import processing.core.PApplet;
import processing.core.PImage;

public class Pipe extends Collider {
    private PApplet frame;
    private boolean tall;
    private PImage sprite;

    public Pipe(float xLocation, float yLocation, PApplet frame, PImage sprite, boolean tall){
        super(48, 32, xLocation, yLocation);
        this.frame = frame;
        this.sprite = sprite;
        this.tall = tall;
    }


    public void show(){
        if(!tall){
            frame.image(sprite, xLocation, yLocation);
        }else{

        }
    }

    public void reset(){
        this.xLocation = startX;
        this.yLocation = startY;
    }
}
