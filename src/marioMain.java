import AD_NEAT.Genome;
import AD_NEAT.Innovations;
import AD_NEAT.NodeGene;
import processing.core.PApplet;
import processing.core.PImage;
import marioGame.*;

import java.text.DecimalFormat;
import java.util.*;

public class marioMain extends PApplet{

    private PImage bg, bg2, coin, pipe, ground, ground2;

    private int width = 640;
    private int height = 360;

    private float x = width/2f;

    private static PApplet mainApplet;

    private boolean keyup = false;
    private boolean keyright = false;
    private boolean keyleft = false;
    private boolean keydown = false;
    private boolean once = true;
    private List<Pipe> pipes;
    private List<Ground> groundTiles;
    private List<Mario> marios;
    private int scale = 20;
    private int NumOfGridSpaces = width/scale * height/scale * 2/3;
    private List<Float> worldView = new ArrayList<>(Collections.nCopies(NumOfGridSpaces, 0f));
    private Random random;
    private int generation = 0;
    private GA ga;
    private DecimalFormat df = new DecimalFormat("#.00");

    private int pauseCounter = 0;

    public static void main(String[] args){
       PApplet.main("marioMain", args);
    }

    public void settings(){
        size(width, height);
    }

    public void setup()
    {
        mainApplet = this;
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

        bg = loadImage("pics/bg.png");
        ground = loadImage("pics/ground.png");
        ground2 = loadImage("pics/ground2.png");
        pipe = loadImage("pics/pipe.png");
        coin = loadImage("pics/coin.png");
        bg2 = loadImage("pics/bg2.jpg");

        frameRate(60);

        background(200);

        pipes = new ArrayList<>();
        groundTiles = new ArrayList<>();
        marios = new ArrayList<>();

        for(int i = 0; i < 10; i++) {
            pipes.add(new Pipe(i * 2 * scale + width, height-80, mainApplet, pipe, false));
        }
        for(int i = 0; i < width/16 + 2; i++){
            groundTiles.add(new Ground(i * 16, height-32, mainApplet, ground, ground2));
        }
        for(int i = 0; i < 100; i++){
            marios.add(new Mario( width/2f, height-60, mainApplet, marioAnimations, i));
        }

        Genome start = new Genome();
        int i;
        for(i = 0; i < NumOfGridSpaces; i++){
            start.addNodeGene(new NodeGene(NodeGene.TYPE.SENSOR, i));
        }

        start.addNodeGene(new NodeGene(NodeGene.TYPE.OUTPUT, i));
        start.addNodeGene(new NodeGene(NodeGene.TYPE.OUTPUT, i+1));
        start.addNodeGene(new NodeGene(NodeGene.TYPE.OUTPUT, i+2));
        start.addNodeGene(new NodeGene(NodeGene.TYPE.OUTPUT, i+3));
        Innovations innovations = new Innovations();

        ga = new GA(100, start, innovations);
        ga.remap();
    }

    public void draw()
    {
        background(200);
        worldView.replaceAll(e -> e = 0f);
        fill(0);
        textFont(createFont("Arial",15,true),15);
        text("Generation: " + generation, 25, 25);
        text("Highest Fitness: " + df.format(ga.getHighestFitness()), 25, 50);
        float maxSpeed = 0;
        boolean allDead = true;

        for (Pipe p : pipes) {
            int loc = 0;
            if(p.getxLocation() > 0 && p.getxLocation() < width){
                loc = (int)(p.getxLocation()/scale);
                worldView.set(loc, 1f);
                worldView.set(loc + width/scale, 1f);
            }
        }

        for(Mario m : marios){
            if(m.getxLocation() < 0 - m.getWidth()){
                m.setDead(true);
            }


            if(!m.isDead()) {
                int loc = (int)(m.getxLocation()/scale) + (int)((height-m.getyLocation())/scale*width/scale);
                worldView.set(loc, 9999f);
                allDead = false;
                List<Float> out =  ga.getGenome(m.getId()).compute(worldView);

                m.setKeydown(out.get(0) > .8);
                m.setKeyleft(out.get(1) > .8);
                m.setKeyright(out.get(2) > .8);
                m.setKeyup(out.get(3) > .8);
                m.show();

                maxSpeed = 1.47f;
                boolean onTop = false;

                m.setxLocation(m.getxLocation() - maxSpeed + m.getSpeed());

                for (Pipe p : pipes) {
                    int collision = m.checkCollision(p);
                    if (collision == 1) {
                        if (m.getLR() == 1) {
                            m.setxLocation(p.getxLocation() - m.getWidth());
                        } else {
                            m.setxLocation(p.getxLocation() + p.getWidth());
                        }
                    } else if (collision == 2) {
                        m.setG(p.getyLocation() - m.getHeight());
                        onTop = true;
                    } else if (collision == 0 && !onTop) {
                        m.setG(height-60);
                    }
                }
                worldView.set(loc, 0f);
            }else{
                ga.getGenome(m.getId()).setFitness(m.getDistance());
            }
        }

        for(Pipe p : pipes){
            if(x + width/2f > p.getxLocation()) {
                p.show();
                p.setxLocation(p.getxLocation() - maxSpeed);
                if(p.getxLocation() < 0 - p.getWidth()){
                    p.setxLocation((float)(width + (random.nextInt(width/scale) * scale)));
                }
            }
        }

        x += maxSpeed;

        for(Ground g : groundTiles){
            g.show();
            g.setxLocation(g.getxLocation() - maxSpeed);
            if(g.getxLocation() + g.getWidth() <  0 ){
                g.setxLocation(g.getxLocation() + width + 16);
            }else if(g.getxLocation() > width ){
                g.setxLocation(g.getxLocation() - width - 16);
            }
        }

        if(allDead){
            if(once){
                ga.evaluate();
                ga.remap();
                generation++;
                x = width/2f;
            }
            if(pauseCounter > 180) {
                for (Mario m : marios) {
                    m.reset();
                }
                for (Pipe p : pipes) {
                    p.reset();
                }
                for (Ground g : groundTiles) {
                    g.reset();
                }
                pauseCounter = 0;
            }else{
                pauseCounter++;
                once = false;
            }
        }
        else{
            once = true;
        }
    }


    public void keyPressed() {
        if (key == CODED) {
            if (keyCode == UP) keyup = true;
            if (keyCode == DOWN) keydown = true;
            if (keyCode == LEFT) keyleft = true;
            if (keyCode == RIGHT) keyright = true;
        }
    }

    public void keyReleased() {
        if (key == CODED) {
            if (keyCode == UP) keyup = false;
            if (keyCode == DOWN) keydown = false;
            if (keyCode == LEFT) keyleft = false;
            if (keyCode == RIGHT) keyright = false;
        }
    }
}
