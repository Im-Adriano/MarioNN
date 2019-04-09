package AD_Neural_Network_Stuff.AD_NEAT;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Species implements Serializable {

    public Genome mascot;
    public List<Genome> members;
    public float totalAdjustedFitness = 0f;

    public Species(Genome mascot) {
        this.mascot = mascot;
        this.members = new LinkedList<>();
        this.members.add(mascot);
    }

    public void addAdjustedFitness(float adjustedFitness) {
        this.totalAdjustedFitness += adjustedFitness;
    }

    public void reset(Random r) {
        int newMascotIndex = r.nextInt(members.size());
        this.mascot = members.get(newMascotIndex);
        members.clear();
        totalAdjustedFitness = 0f;
    }
}