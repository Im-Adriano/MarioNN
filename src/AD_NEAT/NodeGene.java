package AD_NEAT;

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

    public NodeGene(NodeGene gene) {
        this.type = gene.type;
        this.id = gene.id;
    }

    public TYPE getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public NodeGene copy(){
        return new NodeGene(type, id);
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
