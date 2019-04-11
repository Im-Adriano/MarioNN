import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import processing.core.PApplet;

import java.io.*;
import java.util.ArrayList;

public class Controller {
    @FXML Button run;
    @FXML Button saveState;
    @FXML Button loadState;
    @FXML CheckBox worldView;
    @FXML CheckBox NEAT;
    @FXML Button clearGA;
    @FXML Slider numOfGenSlider;
    @FXML ChoiceBox saveSelect;
    @FXML Label numGenLabel;
    @FXML Label saveLabel;

    private ArrayList<AI_Plays_Mario> windows = new ArrayList<AI_Plays_Mario>();
    private AI_Plays_Mario current = new AI_Plays_Mario();
    private Play_Mario user;

    public void saveState() {
        try {
            FileChooser chooser= new FileChooser();
            chooser.setTitle("Save GA State");
            String path = chooser.showSaveDialog(new Stage()).getAbsoluteFile().toString();
            FileOutputStream fileOut =
                    new FileOutputStream(path);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(((AI_Plays_Mario)saveSelect.getValue()).ga);
            out.close();
            fileOut.close();
        } catch (Exception i) {
            i.printStackTrace();
        }
    }

    public void loadState() {
        try {
            FileChooser chooser= new FileChooser();
            chooser.setTitle("Open GA State");
            String path = chooser.showOpenDialog(new Stage()).getAbsoluteFile().toString();
            FileInputStream fileIn = new FileInputStream(path);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            current.ga = (AD_Neural_Network_Stuff.GA) in.readObject();

            if(current.ga instanceof AD_Neural_Network_Stuff.AD_NEAT.GeneticAlgorithm){
                NEAT.setSelected(true);
            }else{
                NEAT.setSelected(false);
            }
            if(current.ga.usingWorldView()){
                worldView.setSelected(true);
                current.world = true;
            }else{
                worldView.setSelected(false);
                current.world = false;
            }

            clearGA.setDisable(false);
            NEAT.setDisable(true);
            worldView.setDisable(true);
            in.close();
            fileIn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startSim() {
        saveSelect.setDisable(false);
        saveState.setDisable(false);
        clearGA.setDisable(true);
        numOfGenSlider.setDisable(true);
        NEAT.setDisable(false);
        worldView.setDisable(false);
        saveLabel.setDisable(false);

        current.generation = (int)numOfGenSlider.getValue();
        windows.add(current);
        saveSelect.getItems().add(current);
        PApplet.runSketch(new String[]{""}, current);
        current = new AI_Plays_Mario();
        numOfGenSlider.setDisable(false);

    }

    public void useWorldView(ActionEvent actionEvent) {
        CheckBox temp = (CheckBox)(actionEvent.getSource());
        current.world = temp.isSelected();
    }

    public void useNEAT(ActionEvent actionEvent) {
        CheckBox temp = (CheckBox)(actionEvent.getSource());
        current.dynamic = temp.isSelected();
    }

    public void clearGA() {
        run.setDisable(false);
        worldView.setDisable(false);
        NEAT.setDisable(false);
        loadState.setDisable(false);
        clearGA.setDisable(true);

        current.ga = null;
    }


    public void runAll(ActionEvent actionEvent) {
        saveSelect.setDisable(false);
        saveState.setDisable(false);
        clearGA.setDisable(true);
        numOfGenSlider.setDisable(true);
        NEAT.setDisable(false);
        worldView.setDisable(false);
        saveLabel.setDisable(false);

        ArrayList<AI_Plays_Mario> temp = new ArrayList<>();

        current = new AI_Plays_Mario();
        current.world = true;
        current.dynamic = true;
        current.generation = (int)numOfGenSlider.getValue();
        temp.add(current);
        current = new AI_Plays_Mario();
        current.world = false;
        current.dynamic = true;
        current.generation = (int)numOfGenSlider.getValue();
        temp.add(current);
        current = new AI_Plays_Mario();
        current.world = true;
        current.dynamic = false;
        current.generation = (int)numOfGenSlider.getValue();
        temp.add(current);
        current = new AI_Plays_Mario();
        current.world = false;
        current.dynamic = false;
        current.generation = (int)numOfGenSlider.getValue();
        temp.add(current);

        numOfGenSlider.setDisable(false);

        for(AI_Plays_Mario m : temp) {
            saveSelect.getItems().add(m);
            PApplet.runSketch(new String[]{""}, m);
            windows.add(m);
        }
    }

    public void userPlay(ActionEvent actionEvent) {
        user = new Play_Mario();
        PApplet.runSketch(new String[]{""}, user);
    }
}
