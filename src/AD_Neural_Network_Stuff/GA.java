package AD_Neural_Network_Stuff;

public interface GA {

    Brain getGenome(int id);

    void remap();

    //    public void print();

    float evaluateGenome(Brain genome);

    void evaluate();

    float getHighestFitness();

    Brain getFittest();
}
