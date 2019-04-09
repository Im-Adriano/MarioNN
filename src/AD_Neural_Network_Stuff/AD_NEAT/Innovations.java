package AD_Neural_Network_Stuff.AD_NEAT;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Innovations implements Serializable {
    private int currentInnovation = 0;

    private Map<ConnectionGene, Integer> ConnectionInovations;

    public Innovations(){
        ConnectionInovations = new HashMap<>();
    }

    public int getInnovation(int in, int out){
        ConnectionGene temp = new ConnectionGene(in, out, 0f, false, 0);
        if(ConnectionInovations.containsKey(temp)){
            return ConnectionInovations.get(temp);
        }else {
            return currentInnovation++;
        }
    }
}
