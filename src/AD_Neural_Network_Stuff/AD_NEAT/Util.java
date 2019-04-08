package AD_Neural_Network_Stuff.AD_NEAT;

import java.util.Comparator;

public class Util {
    public static class GenomeFitnessComparator implements Comparator<Genome> {
        @Override
        public int compare(Genome one, Genome two) {
            if (one.getFitness() > two.getFitness()) {
                return 1;
            } else if (one.getFitness() < two.getFitness()) {
                return -1;
            }
            return 0;
        }
    }

    public static class NodeLayerCompare implements Comparator<NodeGene> {
        @Override
        public int compare(NodeGene one, NodeGene two) {
            if (one.getLayer() > two.getLayer()) {
                return 1;
            } else if (one.getLayer() < two.getLayer()) {
                return -1;
            }
            return 0;
        }
    }

}
