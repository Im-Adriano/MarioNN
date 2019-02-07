import AD_NEAT.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GA extends GeneticAlgorithm {

    private Map<Integer, Genome> IDtoGenome;

    public GA(int populationSize, Genome startingGenome, Innovations connectionInnovation){
        super(populationSize, startingGenome, connectionInnovation);
        IDtoGenome = new HashMap<>();
    }

    public Genome getGenome(int id){
        return IDtoGenome.get(id);
    }

    public void remap(){
        IDtoGenome.clear();
        int Counter = 0;
        for (Genome genome : this.genomes) {
            IDtoGenome.put(Counter, genome);
            Counter++;
        }
    }

    public void print(){
        for (Genome genome : this.genomes){
//            System.out.print("[ ");
//            for(NodeGene node: genome.getNodes().values()) {
//                System.out.print(node.getId() + " ");
//            }
//            System.out.print("] ");
            for(ConnectionGene con : genome.getConnections().values()) {
                System.out.print(con.getInNode() + " -> " + con.getOutNode() + "  ");
            }
            System.out.println();
        }
    }

    @Override
    protected float evaluateGenome(Genome genome) {
        return genome.getFitness();
    }
}
