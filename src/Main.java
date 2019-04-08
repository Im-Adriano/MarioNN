import AD_Neural_Network_Stuff.AD_NEAT.GeneticAlgorithm;
import AD_Neural_Network_Stuff.AD_NEAT.Genome;
import AD_Neural_Network_Stuff.AD_NEAT.Innovations;
import AD_Neural_Network_Stuff.AD_NEAT.NodeGene;
import AD_Neural_Network_Stuff.GA;
import processing.core.PApplet;

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

        ga = new GeneticAlgorithm(100, start, innovations);
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
        ga.evaluate();
        count++;
    }
}
