package AD_Neural_Network_Stuff.AD_NEAT;

public class NodeGene {
    public enum TYPE {
        SENSOR,
        HIDDEN,
        OUTPUT
    }

    private TYPE type;
    private int id;
    private float layer;
    private float inputSum;

    public NodeGene(TYPE type, int id){
        super();
        this.type = type;
        this.id = id;
    }

    public NodeGene(TYPE type, int id, float layer){
        super();
        this.type = type;
        this.id = id;
        this.layer = layer;
    }

    public NodeGene(NodeGene gene) {
        this.type = gene.type;
        this.id = gene.id;
        this.layer = gene.layer;
    }

    public TYPE getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public NodeGene copy(){
        return new NodeGene(type, id, layer);
    }

    public float getLayer() {
        return layer;
    }

    public void setLayer(float layer) {
        this.layer = layer;
    }

    public float getInputSum() {
        return inputSum;
    }

    public void addToInputSum(float value) {
        this.inputSum += value;
    }

    public void setInputSum(float inputSum) {
        this.inputSum = inputSum;
    }

    public void reset(){
        this.inputSum = 0;
    }


}
