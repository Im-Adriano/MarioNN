package AD_Neural_Network_Stuff.AD_NN;

import AD_Neural_Network_Stuff.Brain;
import AD_Neural_Network_Stuff.GA;

import java.io.Serializable;
import java.util.*;

public class GeneticAlgorithm implements GA, Serializable {
    List<NeuralNetwork> neuralNetworks;
    Random random = new Random();
    private int GenerationSize;
    private int NumElite;
    private float PercentMutate = .05f;

    private float TopFit = 0;
    private float TopGenFit = 0;
    private List<Float> GenFit = new ArrayList<>();
    private List<Float> Fit = new ArrayList<>();

    private Map<Integer, Brain> IDtoGenome;
    private NeuralNetwork TopCarBrain;
    private Util.NNFitnessComparator comparator = new Util.NNFitnessComparator();
    private int inputNum, hiddenLayers, hiddenLayerSize, outputNum, Generation = 0;

    private boolean world;

    // Use this for initialization
    public GeneticAlgorithm(int GenerationSize, int inputNum, int hiddenLayers, int hiddenLayerSize, int outputNum, boolean world)
    {
        this.inputNum = inputNum;
        this.hiddenLayers = hiddenLayers;
        this.hiddenLayerSize = hiddenLayerSize;
        this.outputNum = outputNum;
        this.world = world;

        this.GenerationSize = GenerationSize;
        this.NumElite = (int)(GenerationSize * .05f);
        neuralNetworks = new ArrayList<>();
        for (int i = 0; i < GenerationSize; i++)
        {
            neuralNetworks.add(new NeuralNetwork(random, inputNum, hiddenLayers, hiddenLayerSize, outputNum));
        }
        IDtoGenome = new HashMap<>();

    }

    public Brain getGenome(int id){
        return IDtoGenome.get(id);
    }

    public void remap(){
        IDtoGenome.clear();
        int Counter = 0;
        for (NeuralNetwork neuralNetwork : this.neuralNetworks) {
            IDtoGenome.put(Counter, neuralNetwork);
            Counter++;
        }
    }

    public void evaluate()
    {
        List<NeuralNetwork> Elite = new ArrayList<>();
        float AvgFit = 0;
        neuralNetworks.sort(comparator);

        for(int q = 0; q < NumElite; q++)
        {
            Elite.add(neuralNetworks.get(GenerationSize - 1 - q));
        }

        if (Elite.get(0).fitness > TopFit)
        {
            TopFit = Elite.get(0).fitness;
            if(TopCarBrain == null){
                TopCarBrain = new NeuralNetwork(random, inputNum, hiddenLayers, hiddenLayerSize, outputNum);
            }
            TopCarBrain.Set(Elite.get(0));
        }

        for (NeuralNetwork neuralNetwork : neuralNetworks)
        {
            float fit = neuralNetwork.fitness;
            AvgFit += fit;
        }

        Fit.add(Elite.get(0).fitness);

        float currentGenFit = AvgFit / GenerationSize;
        if(currentGenFit > TopGenFit)
        {
            TopGenFit = currentGenFit;
        }
        GenFit.add(currentGenFit);


        int Count = 0;
        List<NeuralNetwork> NewGeneration = new ArrayList<>();

        for(NeuralNetwork elite : Elite)
        {
            NewGeneration.add(new NeuralNetwork(random, inputNum, hiddenLayers, hiddenLayerSize, outputNum));
            NewGeneration.get(Count).Set(elite);
            Count++;
        }

        for(NeuralNetwork elite : Elite)
        {
            NewGeneration.add(new NeuralNetwork(random, inputNum, hiddenLayers, hiddenLayerSize, outputNum));
            NewGeneration.get(Count).Combine(elite, elite, PercentMutate);
            Count++;
        }

        for (int i = Count; i < GenerationSize; i++)
        {
            NeuralNetwork Elite1 = Elite.get(random.nextInt(NumElite - 1));
            NeuralNetwork Elite2 = Elite.get(random.nextInt(NumElite - 1));

            NewGeneration.add(new NeuralNetwork(random, inputNum, hiddenLayers, hiddenLayerSize, outputNum));
            NewGeneration.get(Count).Combine(Elite1, Elite2, PercentMutate);
        }

        neuralNetworks.clear();
        neuralNetworks = NewGeneration;
        Generation++;
    }

    public float evaluateGenome(Brain genome) {
        return genome.getFitness();
    }

    public float getHighestFitness() {
        return TopFit;
    }

    public NeuralNetwork getFittest(){return TopCarBrain;}

    public int getGeneration() {
        return Generation;
    }

    public boolean usingWorldView() {
        return world;
    }
}
