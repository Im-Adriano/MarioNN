package AD_Neural_Network_Stuff.AD_NN;

import java.io.Serializable;
import java.util.Comparator;

public class Util implements Serializable {
    public static class NNFitnessComparator implements Comparator<NeuralNetwork>, Serializable {
        @Override
        public int compare(NeuralNetwork one, NeuralNetwork two) {
            if (one.getFitness() > two.getFitness()) {
                return 1;
            } else if (one.getFitness() < two.getFitness()) {
                return -1;
            }
            return 0;
        }
    }

}
