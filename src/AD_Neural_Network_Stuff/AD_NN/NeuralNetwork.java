package AD_Neural_Network_Stuff.AD_NN;

import AD_Neural_Network_Stuff.Brain;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NeuralNetwork implements Brain {
    private float[][] layers;
    private float[][][] NeuronWeights;
    private float[][] BiasWeights;
    private float BiasNeuron = 1f;

    private int hiddenLayers;
    private int inputNum;
    private int hiddenLayerSize;
    private int outputNum;
    float fitness = 0f;
    private Random rand;

    public float getFitness() {
        return fitness;
    }

    public void setFitness(float fitness) {
        this.fitness = fitness;
    }

    // Use this for initialization
    public NeuralNetwork(Random random, int inputNum, int hiddenLayers, int hiddenLayerSize, int outputNum)
    {
        this.inputNum = inputNum;
        this.hiddenLayers = hiddenLayers;
        this.hiddenLayerSize = hiddenLayerSize;
        this.outputNum = outputNum;

        rand = random;

        layers = new float[hiddenLayers + 2][];
        layers[0] = new float[inputNum];


        for (int i = 1; i <= hiddenLayers; i++)
        {
            layers[i] = new float[hiddenLayerSize];
        }

        layers[layers.length - 1] = new float[outputNum];

        initWeights();
        initBiases();

    }

    public float[][][] getNeuronWeights() {
        return NeuronWeights;
    }

    public float[][] getBiasWeights() {
        return BiasWeights;
    }

    public int getHiddenLayers() {
        return hiddenLayers;
    }

    public int getInputNum() {
        return inputNum;
    }

    public int getHiddenLayerSize() {
        return hiddenLayerSize;
    }

    public int getOutputNum() {
        return outputNum;
    }

    private void initWeights()
    {
        NeuronWeights = new float[hiddenLayers + 1][][];
        for (int i = 0; i <= hiddenLayers; i++)
        {
            NeuronWeights[i] = new float[layers[i + 1].length][];
            for (int j = 0; j < layers[i + 1].length; j++)
            {
                NeuronWeights[i][j] = new float[layers[i].length];
                for (int k = 0; k < layers[i].length; k++)
                {
                    NeuronWeights[i][j][k] = (float)(rand.nextDouble() -.5f);
                }
            }
        }
    }

    private void initBiases()
    {
        BiasWeights = new float[hiddenLayers + 1][];
        for (int i = 0; i <= hiddenLayers; i++)
        {
            BiasWeights[i] = new float[layers[i + 1].length];
            for (int j = 0; j < layers[i + 1].length; j++)
            {
                BiasWeights[i][j] = (float)(rand.nextDouble() - .5f);
            }
        }
    }

    public List<Float> compute(List<Float> inputs)
    {
        for (int i = 0; i < layers[0].length; i++)
        {
            layers[0][i] = inputs.get(i);
        }

        for (int i = 0; i < NeuronWeights.length; i++)
        {
            for (int j = 0; j < NeuronWeights[i].length; j++)
            {
                float sum = 0;
                for (int k = 0; k < NeuronWeights[i][j].length; k++)
                {
                    sum += layers[i][k] * NeuronWeights[i][j][k];
                }
                sum += BiasWeights[i][j] * BiasNeuron;
                layers[i + 1][j] = 1 / (float)(1 + Math.pow(Math.E, -sum));  //0 to 1
//                layers[i + 1][j] = sum / (float) Math.sqrt(1 + Math.pow(sum, 2)); // -1 to 1
            }
        }
        ArrayList<Float> out = new ArrayList<>();
        for(float f : layers[layers.length - 1]){
            out.add(f);
        }
        return out;
    }

    public float[] Normalize(float[] data)
    {

        float mean = 0;
        float variance = 0;

        for(int i = 0; i < data.length; i++)
        {
            mean += data[i];
            variance += (float) Math.pow(data[i],2);
        }
        mean = mean / data.length;
        variance = (variance / data.length) - (float) Math.pow(mean, 2);
        float[] ret = new float[data.length];
        for(int i = 0; i < data.length; i++)
        {
            ret[i] = (data[i] - mean) / (float)Math.sqrt(variance);
        }

        return ret;
    }



    public void Combine(NeuralNetwork other, NeuralNetwork other2, float mutate)
    {

        for (int i = 0; i <= hiddenLayers; i++)
        {
            for (int j = 0; j < layers[i + 1].length; j++)
            {
                for (int k = 0; k < layers[i].length; k++)
                {
                    if (rand.nextDouble() < mutate)
                    {
                        double avg = (other.NeuronWeights[i][j][k] + other2.NeuronWeights[i][j][k]) / 2;
                        NeuronWeights[i][j][k] = (float)(avg * rand.nextDouble() - .5f);
                    }

                    else
                    {
                        if (rand.nextDouble() > .5)
                        {
                            NeuronWeights[i][j][k] = other.NeuronWeights[i][j][k];
                        }
                        else
                        {
                            NeuronWeights[i][j][k] = other2.NeuronWeights[i][j][k];
                        }
                    }
                }
            }
        }
        for (int i = 0; i <= hiddenLayers; i++)
        {
            for (int j = 0; j < layers[i + 1].length; j++)
            {
                if (rand.nextDouble() < mutate)
                {
                    double avg = (other.BiasWeights[i][j] + other2.BiasWeights[i][j]) / 2;
                    BiasWeights[i][j] = (float)(avg * rand.nextDouble() - .5f);
                }
                else
                {
                    if (rand.nextDouble() > .5)
                    {
                        BiasWeights[i][j] = other.BiasWeights[i][j];
                    }
                    else
                    {
                        BiasWeights[i][j] = other2.BiasWeights[i][j];
                    }
                }

            }
        }
    }

    public void Set(NeuralNetwork other)
    {
        for (int i = 0; i <= hiddenLayers; i++)
        {
            for (int j = 0; j < layers[i + 1].length; j++)
            {
                for(int k = 0; k < layers[i].length; k++)
                {
                    NeuronWeights[i][j][k] = other.NeuronWeights[i][j][k];
                }
            }
        }
        for (int i = 0; i <= hiddenLayers; i++)
        {
            for(int j = 0; j < layers[i + 1].length; j++)
            {
                BiasWeights[i][j] = other.BiasWeights[i][j];
            }
        }
    }
}
