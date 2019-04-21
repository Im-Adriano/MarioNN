import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

import marioGame.*;

import java.text.DecimalFormat;
import java.util.*;

public class Play_Mario extends PApplet{

    private int width = 640;
    private int height = 320;

    private float x = width/2f;

    private boolean keyUp = false;
    private boolean keyRight = false;
    private boolean keyLeft = false;
    private boolean keyDown = false;

    private Mario mario;
    private boolean once = true;
    private List<Pipe> pipes;
    private List<Ground> groundTiles;
    private int scale = 32;
    private Random random;
    private int pauseCounter = 0;
    private int generation = 0;
    private float highScore = 0;
    private DecimalFormat df = new DecimalFormat("#.00");
    private Bullet bullet;

    public Play_Mario(){}

    public static void main(String[] args){
        PApplet.main(Play_Mario.class.getName());
    }

    public void settings(){
        size(width, height, P3D);
    }

    public void setup()
    {

        PApplet mainApplet = this;
        mainApplet.getSurface().setResizable(false);
        setDefaultClosePolicy(this, false);

        random = new Random();

        Map<Mario.Animations, PImage> marioAnimations = new HashMap<>();

        marioAnimations.put(Mario.Animations.left, loadImage("pics/mario/left.png"));
        marioAnimations.put(Mario.Animations.walkleft, loadImage("pics/mario/walkleft.png"));
        marioAnimations.put(Mario.Animations.walkleft2, loadImage("pics/mario/walkleft2.png"));
        marioAnimations.put(Mario.Animations.right, loadImage("pics/mario/right.png"));
        marioAnimations.put(Mario.Animations.walkright, loadImage("pics/mario/walkright.png"));
        marioAnimations.put(Mario.Animations. walkright2, loadImage("pics/mario/walkright2.png"));
        marioAnimations.put(Mario.Animations.LtoR, loadImage("pics/mario/LtoR.png"));
        marioAnimations.put(Mario.Animations.RtoL, loadImage("pics/mario/RtoL.png"));
        marioAnimations.put(Mario.Animations.duckleft, loadImage("pics/mario/duckleft.png"));
        marioAnimations.put(Mario.Animations.duckright, loadImage("pics/mario/duckright.png"));
        marioAnimations.put(Mario.Animations. leftjump, loadImage("pics/mario/leftjump.png"));
        marioAnimations.put(Mario.Animations.rightjump, loadImage("pics/mario/rightjump.png"));
        marioAnimations.put(Mario.Animations.leftfall, loadImage("pics/mario/leftfall.png"));
        marioAnimations.put(Mario.Animations. rightfall, loadImage("pics/mario/rightfall.png"));
        marioAnimations.put(Mario.Animations.rightup, loadImage("pics/mario/rightup.png"));
        marioAnimations.put(Mario.Animations.leftup, loadImage("pics/mario/leftup.png"));

        PImage ground = loadImage("pics/ground.png");
        PImage ground2 = loadImage("pics/ground2.png");
        PImage pipe = loadImage("pics/pipe.png");
        PImage smallBullet = loadImage("pics/smallBullet.png");

        frameRate(120);

        background(200);

        pipes = new ArrayList<>();
        groundTiles = new ArrayList<>();

        //Creating mario, pipes, ground and bullet
        mario = new Mario( width/2f, height-60, mainApplet, marioAnimations, 0);

        for(int i = 0; i < 10; i++) {
            pipes.add(new Pipe(i * 2 * scale + width, height-80, mainApplet, pipe));
        }
        for(int i = 0; i < width/16 + 2; i++){
            groundTiles.add(new Ground(i * 16, height-32, mainApplet, ground, ground2));
        }

        bullet = new Bullet((width + (random.nextInt(width / scale) * scale)), height-70, mainApplet, smallBullet);


        textFont(createFont("Arial",15,true),15);
    }

    public void draw()
    {
        background(200);
        float maxSpeed = 1.47f;

        fill(0);
        text("Generation: " + generation,  25, 100);
        text("Highest Fitness: " + df.format(highScore), 25, 125);

        //Loop through the pipes and move them, if they go out of bounds randomly place them to the right
        for (Pipe p : pipes) {
            if(x + width/2f > p.getxLocation()) {
                p.show();
                p.setxLocation(p.getxLocation() - maxSpeed);
                if(p.getxLocation() < 0 - p.getWidth()){
                    int xLoc = (width + (random.nextInt(width / scale) * scale));
                    for(int i = 0; i < 250; i++) {
                        boolean good = true;
                        for(Pipe q : pipes){
                            if(xLoc == q.getxLocation()){
                                good = false;
                                break;
                            }
                        }
                        if(good){
                            p.setxLocation(xLoc);
                            break;
                        }else{
                            xLoc = (width + (random.nextInt(width / scale) * scale));
                        }
                    }
                }
            }
        }

        //Move the bullet if it goes out of bounds randomly move it to the right.
        if(x + width/2f > bullet.getxLocation()) {
            bullet.show();
            bullet.setxLocation(bullet.getxLocation() - maxSpeed*1.5f);
            if(bullet.getxLocation() < 0 - bullet.getWidth()){
                bullet.setxLocation(width + (random.nextInt(width / scale) * scale));
            }
        }

        //Loop through the groundTiles and move them,
        // if they go out of bounds move them to the end of the right to create infinite ground :)
        for(Ground g : groundTiles){
            g.show();
            g.setxLocation(g.getxLocation() - maxSpeed);
            if(g.getxLocation() + g.getWidth() <  0 ){
                g.setxLocation(g.getxLocation() + width + 16);
            }else if(g.getxLocation() > width ){
                g.setxLocation(g.getxLocation() - width - 16);
            }
        }

        //If mario goes out of bounds he loses.
        if(mario.getxLocation() < 0 - mario.getWidth()){
            mario.setDead(true);
        }


        if(!mario.isDead()) {
            mario.setKeydown(keyDown);
            //if you want to add the ability to go left
//            mario.setKeyleft(keyLeft);
            mario.setKeyright(true);
            mario.setKeyup(keyUp);
            mario.show();



            boolean onTop = false;
            //move mario
            mario.setxLocation(mario.getxLocation() - maxSpeed + mario.getSpeed());

            //check collision between mario and pipes and act accordingly
            for (Pipe p : pipes) {
                int collision = mario.checkCollision(p);
                if (collision == 1) {
                    if (mario.getLR() == 1) {
                        mario.setxLocation(p.getxLocation() - mario.getWidth());
                    } else {
                        mario.setxLocation(p.getxLocation() + p.getWidth());
                    }
                } else if (collision == 2) {
                    mario.setG(p.getyLocation() - mario.getHeight());
                    onTop = true;
                } else if (collision == 0 && !onTop) {
                    mario.setG(height - 32 - mario.getHeight());
                }
            }

            //kill mario if he hits the bullet
            int collision = mario.checkCollision(bullet);
            if(collision == 1){
                mario.setDead(true);
            }

            once = true;
            x += maxSpeed;
        }else{
            //Reset everything if mario died
            if(once){
                x = width/2f;
                if(mario.getDistance() > highScore){
                    highScore = mario.getDistance();
                }
                generation++;
            }
            if(pauseCounter > 180) {
                mario.reset();
                for (Pipe p : pipes) {
                    p.reset();
                }
                bullet.reset();
                for (Ground g : groundTiles) {
                    g.reset();
                }
                pauseCounter = 0;
            }else{
                pauseCounter++;
                once = false;
            }
        }
    }

    private static void setDefaultClosePolicy(PApplet pa, boolean keepOpen) {
        final Object surf = pa.getSurface().getNative();
        final PGraphics canvas = pa.getGraphics();

        if (canvas.isGL()) {
            final com.jogamp.newt.Window w = (com.jogamp.newt.Window) surf;

            for (com.jogamp.newt.event.WindowListener wl : w.getWindowListeners())
                if (wl.toString().startsWith("processing.opengl.PSurfaceJOGL"))
                    w.removeWindowListener(wl);

            w.setDefaultCloseOperation(keepOpen?
                    com.jogamp.nativewindow.WindowClosingProtocol.WindowClosingMode
                            .DO_NOTHING_ON_CLOSE :
                    com.jogamp.nativewindow.WindowClosingProtocol.WindowClosingMode
                            .DISPOSE_ON_CLOSE);
        } else if (canvas instanceof processing.awt.PGraphicsJava2D) {
            final javax.swing.JFrame f = (javax.swing.JFrame)
                    ((processing.awt.PSurfaceAWT.SmoothCanvas) surf).getFrame();

            for (java.awt.event.WindowListener wl : f.getWindowListeners())
                if (wl.toString().startsWith("processing.awt.PSurfaceAWT"))
                    f.removeWindowListener(wl);

            f.setDefaultCloseOperation(keepOpen?
                    f.DO_NOTHING_ON_CLOSE : f.DISPOSE_ON_CLOSE);
        }
    }

    public void keyPressed() {
        if (key == CODED) {
            if (keyCode == UP) keyUp = true;
            if (keyCode == DOWN) keyDown = true;
            if (keyCode == LEFT) keyLeft = true;
            if (keyCode == RIGHT) keyRight = true;
        }
    }

    public void keyReleased() {
        if (key == CODED) {
            if (keyCode == UP) keyUp = false;
            if (keyCode == DOWN) keyDown = false;
            if (keyCode == LEFT) keyLeft = false;
            if (keyCode == RIGHT) keyRight = false;
        }
    }
}


