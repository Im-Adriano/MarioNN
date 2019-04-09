import AD_Neural_Network_Stuff.*;
import AD_Neural_Network_Stuff.AD_NEAT.*;

import AD_Neural_Network_Stuff.AD_NN.NeuralNetwork;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import marioGame.*;

import java.text.DecimalFormat;
import java.util.*;

public class marioMain extends PApplet{

    private PImage pipe, ground, ground2;

    public static boolean dynamic = false;
    public static boolean world = false;

    private int width = 640;
    private int height = 320;

    private float x = width/2f;

    public PApplet mainApplet;

    private boolean keyup = false;
    private boolean keyright = false;
    private boolean keyleft = false;
    private boolean keydown = false;

    private boolean once = true;
    private List<Pipe> pipes;
    private List<Ground> groundTiles;
    private List<Mario> marios;
    private int scale = 32;
    private int NumOfGridSpaces = width/scale * height/scale;
    private List<Float> worldView = new ArrayList<>(Collections.nCopies(NumOfGridSpaces, -1f));
    private ArrayList<Float> inputs = new ArrayList<>(Collections.nCopies(4, -1f));
    private Random random;
    public int generation;
    public GA ga;
    private DecimalFormat df = new DecimalFormat("#.00");
    private HashMap<Integer, Point> nodePositions = new HashMap<>();
    private int pauseCounter = 0;

    public static void main(String[] args){
       PApplet.main(marioMain.class.getName());
    }

    public void settings(){
        size(width, height, P3D);
    }

    public void setup()
    {

        mainApplet = this;
        mainApplet.frame.setResizable(false);
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

        ground = loadImage("pics/ground.png");
        ground2 = loadImage("pics/ground2.png");
        pipe = loadImage("pics/pipe.png");

        frameRate(240);

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
            ga = new AD_Neural_Network_Stuff.AD_NN.GeneticAlgorithm(100, 200, 2, 8, 5, world);
            ga.remap();
        }else if(dynamic){
            Genome start = new Genome();
            int i;
            for (i = 0; i < 4; i++) {
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
                ga = new AD_Neural_Network_Stuff.AD_NN.GeneticAlgorithm(100, 4, 2, 8, 5, world);
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
            mainApplet.noLoop();
        }

        background(200);
        worldView.replaceAll(e -> e = -1f);


        fill(0);
        text("Generation: " + ga.getGeneration(),  25, 100);
        text("Highest Fitness: " + df.format(ga.getHighestFitness()), 25, 125);


        float maxSpeed = 1.47f;
        boolean allDead = true;

        for (Pipe p : pipes) {
            if(x + width/2f > p.getxLocation()) {
                p.show();
                p.setxLocation(p.getxLocation() - maxSpeed);
                if(p.getxLocation() < 0 - p.getWidth()){
                    p.setxLocation((float)(width + (random.nextInt(width/scale) * scale)));
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

        for(Ground g : groundTiles){
            g.show();
            g.setxLocation(g.getxLocation() - maxSpeed);
            if(g.getxLocation() + g.getWidth() <  0 ){
                g.setxLocation(g.getxLocation() + width + 16);
            }else if(g.getxLocation() > width ){
                g.setxLocation(g.getxLocation() - width - 16);
            }

            int loc = 0;
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
                int loc = (int)(m.getxLocation()/scale) + (int)((m.getyLocation())/scale) * (width/scale);
                Float oldVal = worldView.get(loc);
                worldView.set(loc, 9999f);
                allDead = false;
                List<Float> out;

                inputs.set(0, m.getyLocation());
//                inputs.add(m.getxLocation());
                inputs.set(1, m.getJump());

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
                inputs.set(2, closest);
                inputs.set(3, secondClosest-closest);

                if(world) {
                    out = ga.getGenome(m.getId()).compute(worldView);
                }else{
                    out = ga.getGenome(m.getId()).compute(inputs);
                }
                m.setKeydown(out.get(0) > .9);
                m.setKeyleft(out.get(1) > .9);
                m.setKeyright(out.get(2) > .9);
                m.setKeyup(out.get(3) > .9);
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

    public static final void setDefaultClosePolicy(PApplet pa, boolean keepOpen) {
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

    public void visualizeBrain(Brain brain, List<Float> worldview ){
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
                    stroke(0, 255, 0, 75f);
                } else {
                    stroke(255, 0, 0, 75f);
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
                                stroke(0, 255, 0, 50f);
                            } else {
                                stroke(255, 0, 0, 50f);
                            }
                            line(inNode.getX() + 8 / 2, inNode.getY() + 8 / 2, outNode.getX() + 8 / 2, outNode.getY() + 8 / 2);
                            stroke(0);
                        }
                        Point inNode = nodePositions.get(nodePositions.size()-1);
                        Point outNode = nodePositions.get(currentNode + neuronWeights[i][j].length + j);
                        if (biasWeights[i][j] > 0) {
                            stroke(0, 255, 0, 50f);
                        } else {
                            stroke(255, 0, 0, 50f);
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

            int outNum = 1;
            int outSpacing = 8;
            for (NodeGene gene : genome.getNodes().values()) {
                if (gene.getType() == NodeGene.TYPE.SENSOR) {
                    int x,y;
                    if(world){
                        x = gene.getId() % (width / scale) * 8;
                        y = gene.getId() / (width / scale) * 8;
                    } else{
                        y = gene.getId() % (width / scale) * 8 + (gene.getId()+1) * 8 + 8;
                        x = (width / scale) * 8;
                    }
                    nodePositions.put(gene.getId(), new Point(x, y));
                } else if (gene.getType() == NodeGene.TYPE.HIDDEN) {
                    int x = (int) (gene.getLayer() * width / scale * 8 + (width / scale * 8));
                    int y = r.nextInt(height / scale * 8);
                    nodePositions.put(gene.getId(), new Point(x, y));
                } else if (gene.getType() == NodeGene.TYPE.OUTPUT) {
                    int y = 8 * outNum + outNum * outSpacing;
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
                    y = i % (width / scale) * 8 + (i+1) * 8 + 8;
                    x = (width / scale) * 8;
                }
                nodePositions.put(i, new Point(x, y));
            }

            for(int i = 0; i < numHiddenLayers; i++){
                for(int j = 0; j < hiddenLayerSize; j++){
                    int x = (int) ((i+1)/numHiddenLayers * width / scale * 8 + (width / scale * 8));
                    int y = (int)(8 * (j/hiddenLayerSize) * 8);
                    nodePositions.put((int)(inputNum + (hiddenLayerSize*i) + j), new Point(x, y));
                }
            }

            for(int i = 0; i < outputNum; i ++){
                int y = 8 * i + i * 8;
                int x = (int)(width / scale * 8 + (1 + (1/numHiddenLayers)) * width/scale*8);
                nodePositions.put((int)(inputNum + (numHiddenLayers*hiddenLayerSize) + i), new Point(x, y));
            }

            nodePositions.put((int)(inputNum + (numHiddenLayers*hiddenLayerSize) + outputNum), new Point((width/scale * 8), width/scale*8/2));
        }
    }
}


