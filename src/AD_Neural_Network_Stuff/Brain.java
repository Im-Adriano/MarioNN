package AD_Neural_Network_Stuff;

import java.util.List;

public interface Brain {
    float getFitness();

    void setFitness(float fitness);

    List<Float> compute(List<Float> inputs);
}
