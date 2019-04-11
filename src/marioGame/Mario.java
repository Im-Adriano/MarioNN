package marioGame;

import processing.core.PApplet;
import processing.core.PImage;

import java.util.Map;


public class Mario extends Collider{

    public enum Animations{
        left,
        walkleft,
        walkleft2,
        right,
        walkright,
        walkright2,
        LtoR,
        RtoL,
        duckleft,
        duckright,
        leftjump,
        rightjump,
        leftfall ,
        rightfall,
        rightup,
        leftup
    }

    private PApplet frame;
    private Map<Animations, PImage> marioAnimations;

    private PImage rightup, leftup, leftjump, rightjump, leftfall, rightfall;
    private PImage duckleft, duckright, left, walkleft;
    private PImage walkleft2, right, walkright, walkright2;
    private PImage LtoR,RtoL;

    private int id;

    private float speed = 0f;
    private float jump;
    private float g;
    private int LR =0;
    private int jumpon=0;
    private int jumptime=0;
    private int onejump=0;
    private float distance=0;
    private float origin = 0;

    private boolean dead = false;

    private boolean keyup = false;
    private boolean keyright = false;
    private boolean keyleft = false;
    private boolean keydown = false;

    public Mario(float xLocation, float yLocation, PApplet frame, Map<Animations, PImage> marioAnimations, int id){
        super(28, 15, xLocation, yLocation);
        this.frame = frame;
        this.marioAnimations = marioAnimations;

        this.right = marioAnimations.get(Animations.right);
        this.rightup = marioAnimations.get(Animations.rightup);
        this.walkright = marioAnimations.get(Animations.walkright);
        this.walkright2 = marioAnimations.get(Animations.walkright2);
        this.rightjump = marioAnimations.get(Animations.rightjump);
        this.duckright = marioAnimations.get(Animations.duckright);
        this.rightfall = marioAnimations.get(Animations.rightfall);


        this.left = marioAnimations.get(Animations.left);
        this.leftup = marioAnimations.get(Animations.leftup);
        this.walkleft = marioAnimations.get(Animations.walkleft);
        this.walkleft2 = marioAnimations.get(Animations.walkleft2);
        this.leftjump = marioAnimations.get(Animations.leftjump);
        this.duckleft = marioAnimations.get(Animations.duckleft);
        this.leftfall = marioAnimations.get(Animations.leftfall);

        this.LtoR = marioAnimations.get(Animations.LtoR);
        this.RtoL = marioAnimations.get(Animations.RtoL);
        this.id = id;
        this.g = yLocation;
        this.origin = xLocation + 1;
    }

    public void show(){
        if(!dead) {
//            xLocation=xLocation+speed;
            distance += Math.abs(1/(origin-xLocation));
            if(jump != 0){
                distance += .01;
            }
            yLocation = yLocation - jump;

            if (keyup) {
                jumpon = 1;
                jumptime++;
            } else {
                onejump = 0;
                jumptime = 0;
            }


            if (!keydown) {
                if (keyleft && !keyright) {
                    if (speed > -1.5) speed -= 0.1;

                    LR = 0;

                    if (speed > 0 && yLocation == g) {
                        frame.image(RtoL, xLocation, yLocation);
                    }
                } else if (keyright && !keyleft) {
                    if (speed < 1.5) speed += 0.1;

                    LR = 1;

                    if (speed < 0 && yLocation == g) {
                        frame.image(LtoR, xLocation, yLocation);
                    }
                }


                if (LR == 1 && speed > 0 && jump == 0) {
                    if (frame.frameCount % 32 < 8) {
                        frame.image(right, xLocation, yLocation);
                    } else if (frame.frameCount % 32 < 16) {
                        frame.image(walkright, xLocation, yLocation);
                    } else if (frame.frameCount % 32 < 24) {
                        frame.image(walkright2, xLocation, yLocation);
                    } else if (frame.frameCount % 32 < 32) {
                        frame.image(walkright, xLocation, yLocation);
                    }
                } else if (LR == 0 && speed < 0 && jump == 0) {
                    if (frame.frameCount % 32 < 8) {
                        frame.image(left, xLocation, yLocation);
                    } else if (frame.frameCount % 32 < 16) {
                        frame.image(walkleft, xLocation, yLocation);
                    } else if (frame.frameCount % 32 < 24) {
                        frame.image(walkleft2, xLocation, yLocation);
                    } else if (frame.frameCount % 32 < 32) {
                        frame.image(walkleft, xLocation, yLocation);
                    }
                } else if (speed < 0.1 && speed > -0.1 && LR == 0) {
                    frame.image(left, xLocation, yLocation);
                } else if (speed < 0.1 && speed > -0.1 && LR == 1) {
                    frame.image(right, xLocation, yLocation);
                }
                if (speed >= 0.01) speed -= 0.05;
                if (speed <= -0.01) speed += 0.05;
            } else {
                if (speed >= 0.01) speed -= 0.02;
                if (speed <= -0.01) speed += 0.02;
            }

            if (jumpon == 1) {
                if (jump < 0) jumpon = 0;

                if (yLocation == g && jumpon == 1) {
                    if (onejump == 1) {
                        jump = 4;
                    }
                }
                onejump = 0;


                if (jumptime > 10) jump -= 0.08;
                else if (jumptime > 0 && jumptime < 10) jump -= 0.12;
                else if (jumptime == 0) jump -= 0.3;


            }

            if (jump < 0 && jump > -3) jump -= 0.1;
            if (jump < 0 && yLocation > g - 5) {
                jump = 0;
                yLocation = g;
            }

            if (jump > 0 && !keydown) {
                if (LR == 1) {
                    frame.image(rightjump, xLocation, yLocation);
                } else if (LR == 0) {
                    frame.image(leftjump, xLocation, yLocation);
                }
            } else if (jump < 0 && !keydown) {
                if (LR == 1) {
                    frame.image(rightfall, xLocation, yLocation);
                } else if (LR == 0) {
                    frame.image(leftfall, xLocation, yLocation);
                }
            }

            if (keydown) {
                if (LR == 1) {
                    frame.image(duckright, xLocation, yLocation);
                }
                if (LR == 0) {
                    frame.image(duckleft, xLocation, yLocation);
                }
            }

            if (!keyup) {
                onejump = 1;
            }

            if (speed < .05 && LR == 1) speed = 0;
            if (speed > -.05 && LR == 0) speed = 0;
        }else{
            speed = 0;
        }

    }

    public void setKeyup(boolean keyup) {
        this.keyup = keyup;
    }

    public void setKeyright(boolean keyright) {
        this.keyright = keyright;
    }

    public void setKeyleft(boolean keyleft) {
        this.keyleft = keyleft;
    }

    public void setKeydown(boolean keydown) {
        if(keydown != this.keydown && keydown){
            height = 15;
            yLocation+=13;
        }else if(keydown != this.keydown){
            height = 28;
            yLocation-=13;
        }
        this.keydown = keydown;
    }

    public float getSpeed() {
        return speed;
    }

    public void setG(float g) {
        this.g = g;
        jumpon = 1;
    }

    public int getLR() {
        return LR;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public float getG() {
        return g;
    }

    public float getJump() {
        return jump;
    }

    public void reset(){
        xLocation = startX;
        yLocation = startY;
        dead = false;
        keyup = false;
        keyright = false;
        keyleft = false;
        keydown = false;
        speed = 0;
        distance = 0;
        height = 28;
    }

    public float getDistance() {
        return distance;
    }

    public int getId() {
        return id;
    }
}
