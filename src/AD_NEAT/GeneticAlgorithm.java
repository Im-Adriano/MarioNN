package AD_NEAT;

import java.util.*;
import AD_NEAT.Util;

public abstract class GeneticAlgorithm {

    private Util.GenomeFitnessComparator genomeFitnessComparator = new Util.GenomeFitnessComparator();


    private Innovations connectionInnovation;

    private Random random = new Random();

    private float C1 = 1.0f;
    private float C2 = 1.0f;
    private float C3 = 0.4f;
    private float DT = 10.0f;
    private float MUTATION_RATE = 0.5f;
    private float ADD_CONNECTION_RATE = 0.1f;
    private float ADD_NODE_RATE = 0.1f;

    private int populationSize;

    public List<Genome> genomes;
    private List<Genome> nextGenerationGenomes;

    private List<Species> species;

    private Map<Genome, Species> mappedGenomesToSpecies;
    private Map<Genome, Float> mappedScoreToGenomes;
    private float highestScore;
    private Genome fittestGenome;

    public GeneticAlgorithm(int populationSize, Genome startingGenome, Innovations connectionInnovation) {
        this.populationSize = populationSize;
        this.connectionInnovation = connectionInnovation;
        genomes = new ArrayList<>(populationSize);
        for (int i = 0; i < populationSize; i++) {
            genomes.add(new Genome(startingGenome));
        }
        nextGenerationGenomes = new ArrayList<>(populationSize);
        mappedGenomesToSpecies = new HashMap<>();
        mappedScoreToGenomes = new HashMap<>();
        species = new ArrayList<>();
    }

    /**
     * Runs one generation
     */
    public void evaluate() {
        for (Species s : species) {
            s.reset(random);
        }
        mappedScoreToGenomes.clear();
        mappedGenomesToSpecies.clear();
        nextGenerationGenomes.clear();
        highestScore = Float.MIN_VALUE;
        fittestGenome = null;

        // Place genomes into species
        for (Genome genome : genomes) {
            boolean foundSpecies = false;
            for (Species species : species) {
                if (Genome.compatibilityDistance(genome, species.mascot, C1, C2, C3) < DT) {
                    species.members.add(genome);
                    mappedGenomesToSpecies.put(genome, species);
                    foundSpecies = true;
                    break;
                }
            }
            if (!foundSpecies) {
                Species newSpecies = new Species(genome);
                species.add(newSpecies);
                mappedGenomesToSpecies.put(genome, newSpecies);
            }
        }

        for (Iterator<Species> it = species.iterator(); it.hasNext(); ) {
            Species species = it.next();
            if(species.members.isEmpty()) {
                it.remove();
            }
        }

        for (Genome genome : genomes) {
            Species species = mappedGenomesToSpecies.get(genome);

            float score = evaluateGenome(genome);
            float adjustedScore = score / mappedGenomesToSpecies.get(genome).members.size();

            species.addAdjustedFitness(adjustedScore);
            genome.setFitness(adjustedScore);
            mappedScoreToGenomes.put(genome, adjustedScore);
            if (score > highestScore) {
                highestScore = score;
                fittestGenome = genome;
            }
        }

        for (Species species : species) {
            Collections.sort(species.members, genomeFitnessComparator);
            Collections.reverse(species.members);
            Genome fittestInSpecies = species.members.get(0);
            nextGenerationGenomes.add(fittestInSpecies);
        }

        while (nextGenerationGenomes.size() < populationSize) { // replace removed genomes by randomly breeding
            Species species = getRandomSpeciesBiasedAdjustedFitness(random);

            Genome parent1 = getRandomGenomeBiasedAdjustedFitness(species, random);
            Genome parent2 = getRandomGenomeBiasedAdjustedFitness(species, random);

            Genome child;
            if (mappedScoreToGenomes.get(parent1) >= mappedScoreToGenomes.get(parent2)) {
                child = Genome.crossover(parent1, parent2, random);
            } else {
                child = Genome.crossover(parent2, parent1, random);
            }
            if (random.nextFloat() < MUTATION_RATE) {
                child.mutation(random);
            }
            if (random.nextFloat() < ADD_CONNECTION_RATE) {
                child.addConnectionMutation(random, connectionInnovation, 10);
            }
            if (random.nextFloat() < ADD_NODE_RATE) {
                child.addNodeMutation(random, connectionInnovation);
            }
            nextGenerationGenomes.add(child);
        }

        genomes = nextGenerationGenomes;
        nextGenerationGenomes = new ArrayList<>();
    }

    private Species getRandomSpeciesBiasedAdjustedFitness(Random random) {
        double completeWeight = 0.0;	// sum of probabilities of selecting each species - selection is more probable for species with higher fitness
        for (Species s : species) {
            completeWeight += s.totalAdjustedFitness;
        }
        double r = random.nextDouble() * completeWeight;
        double countWeight = 0.0;
        for (Species s : species) {
            countWeight += s.totalAdjustedFitness;
            if (countWeight >= r) {
                return s;
            }
        }
        throw new RuntimeException("Couldn't find a species... Number is species in total is "+species.size()+", and the total adjusted fitness is "+completeWeight);
    }

    private Genome getRandomGenomeBiasedAdjustedFitness(Species selectFrom, Random random) {
        double completeWeight = 0.0;	// sum of probabilities of selecting each genome - selection is more probable for genomes with higher fitness
        for (Genome fg : selectFrom.members) {
            completeWeight += fg.getFitness();
        }
        double r = random.nextDouble() * completeWeight;
        double countWeight = 0.0;
        for (Genome fg : selectFrom.members) {
            countWeight += fg.getFitness();
            if (countWeight >= r) {
                return fg;
            }
        }
        throw new RuntimeException("Couldn't find a genome... Number is genomes in selæected species is "+selectFrom.members.size()+", and the total adjusted fitness is "+completeWeight);
    }

    public int getSpeciesAmount() {
        return species.size();
    }

    public float getHighestFitness() {
        return highestScore;
    }

    public Genome getFittestGenome() {
        return fittestGenome;
    }

    protected abstract float evaluateGenome(Genome genome);



}
