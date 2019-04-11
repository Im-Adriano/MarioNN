import AD_Neural_Network_Stuff.*;
import AD_Neural_Network_Stuff.AD_NEAT.*;
import AD_Neural_Network_Stuff.AD_NN.NeuralNetwork;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

import marioGame.*;

import java.text.DecimalFormat;
import java.util.*;

public class AI_Plays_Mario extends PApplet{
    public boolean dynamic = false;
    public boolean world = false;

    private int width = 640;
    private int height = 320;

    private float x = width/2f;

    private boolean once = true;

    private List<Pipe> pipes;
    private List<Ground> groundTiles;
    private List<Mario> marios;
    private Bullet bullet;

    private int scale = 32;
    private int NumOfGridSpaces = width/scale * height/scale;
    private List<Float> worldView = new ArrayList<>(Collections.nCopies(NumOfGridSpaces, -1f));
    private ArrayList<Float> inputs = new ArrayList<>(Collections.nCopies(5, -1f));
    private Random random;

    public int generation;
    public GA ga;
    private DecimalFormat df = new DecimalFormat("#.00");
    private HashMap<Integer, Point> nodePositions = new HashMap<>();

    private int pauseCounter = 0;

    public AI_Plays_Mario(){}

    public static void main(String[] args){
       PApplet.main(AI_Plays_Mario.class.getName());
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
        marios = new ArrayList<>();


        for(int i = 0; i < 10; i++) {
            pipes.add(new Pipe(i * 2 * scale + width, height-80, mainApplet, pipe));
        }
        for(int i = 0; i < width/16 + 2; i++){
            groundTiles.add(new Ground(i * 16, height-32, mainApplet, ground, ground2));
        }
        for(int i = 0; i < 100; i++){
            marios.add(new Mario( width/2f, height-60, mainApplet, marioAnimations, i));
        }

        bullet = new Bullet((width + (random.nextInt(width / scale) * scale)), height-115, mainApplet, smallBullet);

        if(dynamic && world) {
            Genome start = new Genome();
            int i;
            for (i = 0; i < NumOfGridSpaces; i++) {
                start.addNodeGene(new NodeGene(NodeGene.TYPE.SENSOR, i, 0));
            }

            start.addNodeGene(new NodeGene(NodeGene.TYPE.OUTPUT, i, 1));
            start.addNodeGene(new NodeGene(NodeGene.TYPE.OUTPUT, i + 1, 1));
            start.addNodeGene(new NodeGene(NodeGene.TYPE.OUTPUT, i + 2, 1));
            start.addNodeGene(new NodeGene(NodeGene.TYPE.OUTPUT, i + 3, 1));
            Innovations innovations = new Innovations();

            ga = new AD_Neural_Network_Stuff.AD_NEAT.GeneticAlgorithm(100, start, innovations, world);
            ga.remap();
        }else if(world){
            ga = new AD_Neural_Network_Stuff.AD_NN.GeneticAlgorithm(100, 200, 3, 8, 4, false);
            ga.remap();
        }else if(dynamic){
            Genome start = new Genome();
            int i;
            for (i = 0; i < 5; i++) {
                start.addNodeGene(new NodeGene(NodeGene.TYPE.SENSOR, i, 0));
            }

            start.addNodeGene(new NodeGene(NodeGene.TYPE.OUTPUT, i, 1));
            start.addNodeGene(new NodeGene(NodeGene.TYPE.OUTPUT, i + 1, 1));
            start.addNodeGene(new NodeGene(NodeGene.TYPE.OUTPUT, i + 2, 1));
            start.addNodeGene(new NodeGene(NodeGene.TYPE.OUTPUT, i + 3, 1));
            Innovations innovations = new Innovations();

            if(ga == null){
                ga = new AD_Neural_Network_Stuff.AD_NEAT.GeneticAlgorithm(100, start, innovations, world);
            }else{
                prepareToVisualize(ga.getFittest());
            }

            ga.remap();
        }else{
            if(ga == null){
                ga = new AD_Neural_Network_Stuff.AD_NN.GeneticAlgorithm(100, 5, 3, 8, 4, false);
            }else{
                prepareToVisualize(ga.getFittest());
            }
            ga.remap();
        }
        textFont(createFont("Arial",15,true),15);
    }

    public void draw()
    {
        if(ga.getGeneration() > generation){
            noLoop();
        }

        background(200);
        worldView.replaceAll(e -> e = -1f);

        fill(0);
        text("Generation: " + ga.getGeneration(),  450, 25);
        text("Highest Fitness: " + df.format(ga.getHighestFitness()), 450, 50);


        float maxSpeed = 1.47f;
        boolean allDead = true;

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

            int loc;
            if(p.getxLocation() > 0 && p.getxLocation() < width){
                loc = (int)(p.getxLocation()/scale);
                loc += (int)(p.getyLocation()/scale) * (width/scale);
                worldView.set(loc, 1f);
                worldView.set(loc + width/scale, 1f);
                if(p.getxLocation() + p.getWidth() < width) {
                    loc = (int) ((p.getxLocation() + p.getWidth()) / scale);
                    loc += (int)(p.getyLocation()/scale) * (width/scale);
                    worldView.set(loc, 1f);
                    worldView.set(loc + width / scale, 1f);
                }
                if(p.getxLocation() + p.getWidth()/2 < width) {
                    loc = (int) ((p.getxLocation() + p.getWidth()/2) / scale);
                    loc += (int)(p.getyLocation()/scale) * (width/scale);
                    worldView.set(loc, 1f);
                    worldView.set(loc + width / scale, 1f);
                }
            }
        }


        if(x + width/2f > bullet.getxLocation()) {
            bullet.show();
            bullet.setxLocation(bullet.getxLocation() - maxSpeed*1.5f);
            if(bullet.getxLocation() < 0 - bullet.getWidth()){
                bullet.setxLocation((width + (random.nextInt(width / scale) * scale)));
            }
        }


        int loc;
        if(bullet.getxLocation() > 0 && bullet.getxLocation() < width){
            loc = (int)((bullet.getxLocation()+bullet.getWidth()/2)/scale);
            loc += (int)((bullet.getyLocation()+bullet.getHeight()/2)/scale) * (width/scale);
            worldView.set(loc, 1f);
        }


        for(Ground g : groundTiles){
            g.show();
            g.setxLocation(g.getxLocation() - maxSpeed);
            if(g.getxLocation() + g.getWidth() <  0 ){
                g.setxLocation(g.getxLocation() + width + 16);
            }else if(g.getxLocation() > width ){
                g.setxLocation(g.getxLocation() - width - 16);
            }

            if(g.getxLocation() > 0 && g.getxLocation() < width){
                loc = (int)(g.getxLocation()/scale);
                loc += (int)(g.getyLocation()/scale * width/scale);
                worldView.set(loc, 1f);
            }
        }

        for(Mario m : marios){
            if(m.getxLocation() < 0 - m.getWidth()){
                m.setDead(true);
            }


            if(!m.isDead()) {
                loc = (int)(m.getxLocation()/scale) + (int)((m.getyLocation())/scale) * (width/scale);
                Float oldVal = worldView.get(loc);
                worldView.set(loc, 9999f);
                allDead = false;
                List<Float> out;



                float closest = 9999;
                float secondClosest = 9999;

                for(Pipe p : pipes){
                    float distance = p.getxLocation()-m.getxLocation();
                    if(distance > 0 && distance < closest){
                        secondClosest = closest;
                        closest = distance;
                    }else if(distance < secondClosest && distance > 0){
                        secondClosest = distance;
                    }
                }

                inputs.set(0, m.getyLocation());
//                inputs.set(1, m.getxLocation());
                inputs.set(1, m.getJump());
                inputs.set(2, closest-m.getWidth());
                inputs.set(3, secondClosest-closest-32);
                inputs.set(4, bullet.getxLocation() - m.getxLocation());

                if(world) {
                    out = ga.getGenome(m.getId()).compute(worldView);
                }else{
                    out = ga.getGenome(m.getId()).compute(inputs);
                }
                m.setKeydown(out.get(0) > .5);
                m.setKeyleft(out.get(1) > .5);
                m.setKeyright(out.get(2) > .5);
                m.setKeyup(out.get(3) > .5);
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
                        m.setG(height - 32 - m.getHeight());
                    }
                }


                int collision = m.checkCollision(bullet);
                if(collision == 1){
                    m.setDead(true);
                }


                worldView.set(loc, oldVal);
            }else{
                if(once) {
                    ga.getGenome(m.getId()).setFitness(m.getDistance());
                }
            }
        }


        visualizeBrain(ga.getFittest(), worldView);


        if(allDead){
            if(once){
                ga.evaluate();
                ga.remap();

                x = width/2f;
                prepareToVisualize(ga.getFittest());
            }
            if(pauseCounter > 180) {
                for (Mario m : marios) {
                    m.reset();
                }
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
        else{
            once = true;
            x += maxSpeed;
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

    private void visualizeBrain(Brain brain, List<Float> worldview ){
        if(world) {
            for (int i = 0; i < width / scale; i++) {
                for (int j = 0; j < height / scale; j++) {
                    strokeWeight(.05f);
                    if (worldview.get(i + j * width / scale) > 0) {
                        fill(0, 75f);
                        rect(i * 8, j * 8, 8, 8);
                    } else {
                        fill(0, 0f);
                        rect(i * 8, j * 8, 8, 8);
                    }
                }
            }
        }
        strokeWeight(.05f);
        fill(0, 75f);
        if(brain instanceof Genome) {
            Genome genome = (Genome)brain;

            for (NodeGene nodeGene : genome.getNodes().values()) {
                if (nodeGene.getType() != NodeGene.TYPE.SENSOR || !world) {
                    rect(nodePositions.get(nodeGene.getId()).getX(), nodePositions.get(nodeGene.getId()).getY(), 8, 8);
                }
            }

            for (ConnectionGene gene : genome.getConnections().values()) {
                if (!gene.isExpressed()) {
                    continue;
                }
                Point inNode = nodePositions.get(gene.getInNode());
                Point outNode = nodePositions.get(gene.getOutNode());
                strokeWeight(2f);
                if (gene.getWeight() > 0) {
                    stroke(0, 255, 0, Math.abs(gene.getWeight()*40+20));
                } else {
                    stroke(255, 0, 0, Math.abs(gene.getWeight()*40+20));
                }
                line(inNode.getX() + 8 / 2, inNode.getY() + 8 / 2, outNode.getX() + 8 / 2, outNode.getY() + 8 / 2);
                stroke(0);
            }
        }else if( brain instanceof NeuralNetwork){
            NeuralNetwork network = (NeuralNetwork) brain;
            float[][][] neuronWeights = network.getNeuronWeights();
            float[][] biasWeights = network.getBiasWeights();
            int currentNode = 0;
            if(!nodePositions.isEmpty()) {
                for (int i = 0; i < neuronWeights.length; i++) {
                    for (int j = 0; j < neuronWeights[i].length; j++) {
                        for (int k = 0; k < neuronWeights[i][j].length; k++) {
                            strokeWeight(1f);
                            Point inNode = nodePositions.get(currentNode + k);
                            Point outNode = nodePositions.get(currentNode + neuronWeights[i][j].length + j);
                            if (neuronWeights[i][j][k] > 0) {
                                stroke(0, 255, 0, Math.abs(neuronWeights[i][j][k]*40+20));
                            } else {
                                stroke(255, 0, 0,  Math.abs(neuronWeights[i][j][k]*40+20));
                            }
                            line(inNode.getX() + 8 / 2, inNode.getY() + 8 / 2, outNode.getX() + 8 / 2, outNode.getY() + 8 / 2);
                            stroke(0);
                        }
                        Point inNode = nodePositions.get(nodePositions.size()-1);
                        Point outNode = nodePositions.get(currentNode + neuronWeights[i][j].length + j);
                        if (biasWeights[i][j] > 0) {
                            stroke(0, 255, 0,  Math.abs(biasWeights[i][j]*40+20));
                        } else {
                            stroke(255, 0, 0,  Math.abs(biasWeights[i][j]*40+20));
                        }
                        line(inNode.getX() + 8 / 2, inNode.getY() + 8 / 2, outNode.getX() + 8 / 2, outNode.getY() + 8 / 2);
                    }
                    currentNode += neuronWeights[i][0].length;
                }
                for(Point p : nodePositions.values()){
                    strokeWeight(.05f);
                    rect(p.getX(), p.getY(), 8, 8);
                }
            }
        }
    }

    private void prepareToVisualize(Brain brain){
        nodePositions.clear();

        if(brain instanceof Genome) {
            Genome genome = (Genome) brain;
            Random r = new Random();

            int outNum = 0;
            for (NodeGene gene : genome.getNodes().values()) {
                if (gene.getType() == NodeGene.TYPE.SENSOR) {
                    int x,y;
                    if(world){
                        x = gene.getId() % (width / scale) * 8;
                        y = gene.getId() / (width / scale) * 8;
                    } else{
                        y = gene.getId() * (height / scale * 8) / 5;
                        x = (width / scale) * 8;
                    }
                    nodePositions.put(gene.getId(), new Point(x, y));
                } else if (gene.getType() == NodeGene.TYPE.HIDDEN) {
                    int x = (int) (gene.getLayer() * width / scale * 8 + (width / scale * 8));
                    int y = r.nextInt(height / scale * 8);
                    nodePositions.put(gene.getId(), new Point(x, y));
                } else if (gene.getType() == NodeGene.TYPE.OUTPUT) {
                    int y = outNum * (height / scale * 8) / 4;
                    outNum++;
                    int x = (int) (gene.getLayer() * width / scale * 8 + (width / scale * 8));
                    nodePositions.put(gene.getId(), new Point(x, y));
                }
            }
        }else if(brain instanceof NeuralNetwork){
            NeuralNetwork network = (NeuralNetwork) brain;
            float numHiddenLayers = network.getHiddenLayers();
            float inputNum = network.getInputNum();
            float hiddenLayerSize = network.getHiddenLayerSize();
            float outputNum = network.getOutputNum();

            for(int i = 0; i < inputNum; i++){
                int x,y;
                if(world){
                    x = i % (width / scale) * 8;
                    y = i / (width / scale) * 8;
                } else{
                    y = (int)(i * (height / scale * 8) / inputNum);
                    x = (width / scale) * 8;
                }
                nodePositions.put(i, new Point(x, y));
            }

            for(int i = 0; i < numHiddenLayers; i++){
                for(int j = 0; j < hiddenLayerSize; j++){
                    int x = (int) ((i+1)/numHiddenLayers * width / scale * 8 + (width / scale * 8));
                    int y = (int)((j) * (height / scale * 8) / hiddenLayerSize);
                    nodePositions.put((int)(inputNum + (hiddenLayerSize*i) + j), new Point(x, y));
                }
            }

            for(int i = 0; i < outputNum; i ++){
                int y = i * (height / scale * 8) / 4;
                int x = (int)(width / scale * 8 + (1 + (1/numHiddenLayers)) * width/scale*8);
                nodePositions.put((int)(inputNum + (numHiddenLayers*hiddenLayerSize) + i), new Point(x, y));
            }

            nodePositions.put((int)(inputNum + (numHiddenLayers*hiddenLayerSize) + outputNum), new Point((width/scale * 8), height/scale*8));
        }
    }
}


