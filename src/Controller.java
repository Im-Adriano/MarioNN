import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
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

    ArrayList<marioMain> windows = new ArrayList<>();
    marioMain current = new marioMain();

    public void saveState(ActionEvent actionEvent) {
        try {
            FileChooser chooser= new FileChooser();
            chooser.setTitle("Save GA State");
            String path = chooser.showSaveDialog(new Stage()).getAbsoluteFile().toString();
            FileOutputStream fileOut =
                    new FileOutputStream(path);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(marioMain.ga);
            out.close();
            fileOut.close();
        } catch (Exception i) {
            i.printStackTrace();
        }
    }

    public void loadState(ActionEvent actionEvent) {
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
                marioMain.world = true;
            }else{
                worldView.setSelected(false);
                marioMain.world = false;
            }

            NEAT.setDisable(true);
            worldView.setDisable(true);
            in.close();
            fileIn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startSim(ActionEvent actionEvent) {
        run.setDisable(true);
        worldView.setDisable(true);
        NEAT.setDisable(true);
        loadState.setDisable(true);
        saveState.setDisable(false);
        clearGA.setDisable(true);
        numOfGenSlider.setDisable(true);
        current.generation = (int)numOfGenSlider.getValue();
        windows.add(current);
        PApplet.runSketch(new String[]{""}, current);
        current = new marioMain();
    }

    public void useWorldView(ActionEvent actionEvent) {
        CheckBox temp = (CheckBox)(actionEvent.getSource());
        marioMain.world = temp.isSelected();
    }

    public void useNEAT(ActionEvent actionEvent) {
        CheckBox temp = (CheckBox)(actionEvent.getSource());
        marioMain.dynamic = temp.isSelected();
    }

    public void clearGA(ActionEvent actionEvent) {
        run.setDisable(false);
        worldView.setDisable(false);
        NEAT.setDisable(false);
        loadState.setDisable(false);
        saveState.setDisable(true);

        current.ga = null;
    }


}
