package AD_Neural_Network_Stuff.AD_NEAT;

import AD_Neural_Network_Stuff.Brain;

import java.io.Serializable;
import java.util.*;

public class Genome implements Brain, Serializable {
    private Map<Integer, ConnectionGene> connections;
    private Map<Integer, NodeGene> nodes;
    private final float PROBABILITY_PERTURBING = 0.9f;
    private float fitness = 0f;
    private Util.NodeLayerCompare nodeLayerCompare = new Util.NodeLayerCompare();

    public Genome(){
        nodes = new HashMap<>();
        connections = new HashMap<>();
    }

    public Genome(Genome toBeCopied) {
        nodes = new HashMap<>();
        connections = new HashMap<>();

        for (Integer index : toBeCopied.getNodes().keySet()) {
            nodes.put(index, new NodeGene(toBeCopied.getNodes().get(index)));
        }

        for (Integer index : toBeCopied.getConnections().keySet()) {
            connections.put(index, new ConnectionGene(toBeCopied.getConnections().get(index)));
        }
    }

    public Map<Integer, ConnectionGene> getConnections() {
        return connections;
    }

    public Map<Integer, NodeGene> getNodes() {
        return nodes;
    }

    public float getFitness() {
        return fitness;
    }

    public void setFitness(float fitness) {
        this.fitness = fitness;
    }

    public void addToFitness(float value){
        this.fitness += value;
    }

    public void addNodeGene(NodeGene nodeGene){
        nodes.put(nodeGene.getId(), nodeGene);
    }

    public void addConnectionGene(ConnectionGene connectionGene){
        connections.put(connectionGene.getInnovation(), connectionGene);
    }

    public void mutation(Random r) {
        for(ConnectionGene con : connections.values()) {
            if (r.nextFloat() < PROBABILITY_PERTURBING) { 			// uniformly perturbing weights
                con.setWeight(con.getWeight()*(r.nextFloat()*4f-2f));
            } else { 												// assigning new weight
                con.setWeight(r.nextFloat()*4f-2f);
            }
        }
    }

    public void addConnectionMutation(Random random, Innovations conInnovations, int numOfTries){
        for(int i = 0; i < numOfTries; i++) {
            NodeGene node1 = nodes.get(random.nextInt(nodes.size()));
            NodeGene node2 = nodes.get(random.nextInt(nodes.size()));
            float weight = random.nextFloat() * 2f - 1f;

            boolean backwards = false;
            if (node1.getType() == NodeGene.TYPE.HIDDEN && node2.getType() == NodeGene.TYPE.SENSOR) {
                backwards = true;
            } else if (node1.getType() == NodeGene.TYPE.OUTPUT && node2.getType() == NodeGene.TYPE.HIDDEN) {
                backwards = true;
            } else if (node1.getType() == NodeGene.TYPE.OUTPUT && node2.getType() == NodeGene.TYPE.SENSOR) {
                backwards = true;
            }

            boolean connectionExists = false;
            for (ConnectionGene con : connections.values()) {
                if (con.getInNode() == node1.getId() && con.getOutNode() == node2.getId()) {
                    connectionExists = true;
                    break;
                } else if (con.getInNode() == node2.getId() && con.getOutNode() == node1.getId()) {
                    connectionExists = true;
                    break;
                }
            }

            boolean connectionImpossible = false;
            if (node1.getType() == NodeGene.TYPE.SENSOR && node2.getType() == NodeGene.TYPE.SENSOR) {
                connectionImpossible = true;
            } else if (node1.getType() == NodeGene.TYPE.OUTPUT && node2.getType() == NodeGene.TYPE.OUTPUT) {
                connectionImpossible = true;
            }

            if (connectionExists || connectionImpossible) {
                continue;
            }

            ConnectionGene newConnection = new ConnectionGene(backwards ? node2.getId() : node1.getId(),
                    backwards ? node1.getId() : node2.getId(),
                    weight,
                    true,
                    conInnovations.getInnovation(backwards ? node2.getId() : node1.getId(), backwards ? node1.getId() : node2.getId() ));
            addConnectionGene(newConnection);
            break;
        }
    }

    public void addNodeMutation(Random random, Innovations conInnovations){
        if(connections.size() != 0) {
            ConnectionGene con = (ConnectionGene) connections.values().toArray()[random.nextInt(connections.size())];

            NodeGene inNode = nodes.get(con.getInNode());
            NodeGene outNode = nodes.get(con.getOutNode());

            con.disable();

            NodeGene newNode = new NodeGene(NodeGene.TYPE.HIDDEN, nodes.size());
            ConnectionGene inToNew = new ConnectionGene(inNode.getId(), newNode.getId(), 1f, true, conInnovations.getInnovation(inNode.getId(), newNode.getId()));
            ConnectionGene newToOut = new ConnectionGene(newNode.getId(), outNode.getId(), 1f, true, conInnovations.getInnovation(inNode.getId(), newNode.getId()));
            newNode.setLayer((inNode.getLayer() + outNode.getLayer()) / 2);

            addNodeGene(newNode);
            addConnectionGene(inToNew);
            addConnectionGene(newToOut);
        }
    }

    public static Genome crossover(Genome parent1, Genome parent2, Random random){
        Genome child = new Genome();

        for (NodeGene parent1Node: parent1.getNodes().values()) {
            child.addNodeGene(parent1Node.copy());
        }

        for (ConnectionGene parent1Con: parent1.getConnections().values()) {
            if(parent2.getConnections().containsKey(parent1Con.getInnovation())){
                ConnectionGene childConGene = random.nextBoolean() ? parent1Con.copy() : parent2.getConnections().get(parent1Con.getInnovation()).copy();
                child.addConnectionGene(childConGene);
            }else{
                ConnectionGene childConGene = parent1Con.copy();
                child.addConnectionGene(childConGene);
            }
        }

        return child;
    }

    public static float compatibilityDistance(Genome genome1, Genome genome2, float c1, float c2, float c3){
        int excessGenes = 0;
        int disjointGenes = 0;
        int matchingGenes = 0;
        float avgWeightDiff = 0f;
        int numberOfGenes = 1;


        List<Integer> genomeArray1 = new ArrayList<>(genome1.nodes.keySet());
        List<Integer> genomeArray2 = new ArrayList<>(genome2.nodes.keySet());
        Collections.sort(genomeArray1);
        Collections.sort(genomeArray2);


        int highestInnovation1 = genomeArray1.get(genomeArray1.size()-1);
        int highestInnovation2 = genomeArray2.get(genomeArray2.size()-1);

        int indices = Math.max(highestInnovation1, highestInnovation2);

        numberOfGenes = indices;

        for(int i = 0; i <= indices; i++){
            NodeGene nodeGene1 = genome1.getNodes().get(i);
            NodeGene nodeGene2 = genome2.getNodes().get(i);

            if(nodeGene1 == null && highestInnovation1 > i && nodeGene2 != null){
                disjointGenes++;
            } else if(nodeGene2 == null && highestInnovation2 > i && nodeGene1 != null){
                disjointGenes++;
            } else if(i > highestInnovation1 && nodeGene1 != null && nodeGene2 == null){
                excessGenes++;
            } else if(i > highestInnovation2 && nodeGene2 != null && nodeGene1 == null){
                excessGenes++;
            }
        }



        genomeArray1.clear();
        genomeArray2.clear();
        genomeArray1.addAll(genome1.connections.keySet());
        genomeArray2.addAll(genome2.connections.keySet());
        Collections.sort(genomeArray1);
        Collections.sort(genomeArray2);

        try {
            highestInnovation1 = genomeArray1.get(genomeArray1.size() - 1);
            highestInnovation2 = genomeArray2.get(genomeArray2.size() - 1);

            indices = Math.max(highestInnovation1, highestInnovation2);

            if (numberOfGenes + indices > 20) {
                numberOfGenes += indices;
            } else {
                numberOfGenes = 1;
            }

            for (int i = 0; i <= indices; i++) {
                ConnectionGene connectionGene1 = genome1.getConnections().get(i);
                ConnectionGene connectionGene2 = genome2.getConnections().get(i);

                if (connectionGene1 != null && connectionGene2 != null) {
                    matchingGenes++;
                    avgWeightDiff += Math.abs(connectionGene1.getWeight() + connectionGene2.getWeight());
                } else if (connectionGene1 != null && highestInnovation1 > i && connectionGene2 == null) {
                    disjointGenes++;
                } else if (connectionGene2 != null && highestInnovation2 > i && connectionGene1 == null) {
                    disjointGenes++;
                } else if (i > highestInnovation1 && connectionGene1 != null) {
                    excessGenes++;
                } else if (i > highestInnovation2 && connectionGene2 != null) {
                    excessGenes++;
                }
            }
        }catch (IndexOutOfBoundsException e){
            matchingGenes = 1;
        }
        avgWeightDiff /= matchingGenes;

        return ((c1 * excessGenes) / numberOfGenes) + ((c2 * disjointGenes) / numberOfGenes) + (c3 * avgWeightDiff);

    }

    public List<Float> compute(List<Float> sensors){
        List<Float> ret = new ArrayList<>();

        for(NodeGene nodeGene: nodes.values()){
            nodeGene.reset();
        }

        for(int i = 0; i < sensors.size(); i++){
            nodes.get(i).addToInputSum(sensors.get(i));
        }

        List<NodeGene> orderedGenes = new ArrayList<>(nodes.values());

        orderedGenes.sort(nodeLayerCompare);

        for(NodeGene node : orderedGenes){
            if(node.getType() != NodeGene.TYPE.SENSOR) {
                node.setInputSum((float)(1 / (1 + Math.pow(Math.E, -node.getInputSum())))  ) ;
            }
            for(ConnectionGene con : connections.values()){
                if(con.getInNode() == node.getId() && con.isExpressed()){
                    nodes.get(con.getOutNode()).addToInputSum(node.getInputSum() * con.getWeight());
                }
            }
        }

        for(NodeGene node : nodes.values()){
            if(node.getType() == NodeGene.TYPE.OUTPUT){
                ret.add(node.getInputSum());
            }
        }

        return ret;
    }


}
