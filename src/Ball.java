import AD_NEAT.Genome;

public class Ball {
    private Genome genome;
    private int ID;

    public Ball(int ID){
        this.ID = ID;
    }

    public void updateGenome(GA ga){
        genome = ga.getGenome(ID);
    }

    public void step(){

    }

}
