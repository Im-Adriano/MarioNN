import AD_NEAT.ConnectionGene;
import AD_NEAT.Genome;
import AD_NEAT.Innovations;
import AD_NEAT.NodeGene;
import processing.core.PApplet;
import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;

public class Main extends PApplet{

    static GA ga;

    public static void main(String[] args){
        Genome start = new Genome();
        start.addNodeGene(new NodeGene(NodeGene.TYPE.SENSOR, 0));
        start.addNodeGene(new NodeGene(NodeGene.TYPE.SENSOR, 1));
        start.addNodeGene(new NodeGene(NodeGene.TYPE.SENSOR, 2));
        start.addNodeGene(new NodeGene(NodeGene.TYPE.SENSOR, 3));
        start.addNodeGene(new NodeGene(NodeGene.TYPE.OUTPUT, 4));
        Innovations innovations = new Innovations();

        ga = new GA(100, start, innovations);
        PApplet.main("Main", args);

    }

    public void settings(){
        size(700,700);
    }

    public void setup(){

    }
    int count = 0;
    public void draw(){
        System.out.println("Generation: " + count);
        ga.print();
        ga.evaluate();
        count++;
    }
}
