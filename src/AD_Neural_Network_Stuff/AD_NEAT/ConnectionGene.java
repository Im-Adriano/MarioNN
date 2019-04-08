package AD_Neural_Network_Stuff.AD_NEAT;

public class ConnectionGene {
    private int inNode;
    private int outNode;
    private float weight;
    private boolean expressed;
    private int innovation;

    public ConnectionGene(int inNode, int outNode, float weight, boolean expressed, int innovation){
        super();
        this.inNode = inNode;
        this.outNode = outNode;
        this.weight = weight;
        this.expressed = expressed;
        this.innovation = innovation;
    }

    public ConnectionGene(ConnectionGene con) {
        this.inNode = con.inNode;
        this.outNode = con.outNode;
        this.weight = con.weight;
        this.expressed = con.expressed;
        this.innovation = con.innovation;
    }

    public int getInNode() {
        return inNode;
    }

    public int getOutNode() {
        return outNode;
    }

    public float getWeight() {
        return weight;
    }

    public boolean isExpressed() {
        return expressed;
    }

    public int getInnovation() {
        return innovation;
    }

    public void disable(){
        this.expressed = false;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public ConnectionGene copy(){
        return new ConnectionGene(inNode, outNode, weight, expressed, innovation);
    }

    @Override
    public boolean equals(Object obj) {
        ConnectionGene other = (ConnectionGene)obj;
        if(other.outNode == outNode && other.inNode == inNode){
            return true;
        }else{
            return false;
        }
    }
}
