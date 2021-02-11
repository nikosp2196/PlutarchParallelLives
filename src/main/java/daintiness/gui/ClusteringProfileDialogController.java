package daintiness.gui;

import daintiness.clustering.BeatClusteringProfile;
import daintiness.clustering.ClusteringProfile;
import daintiness.clustering.EntityClusteringProfile;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

public class ClusteringProfileDialogController {

    private static ClusteringProfileDialogController  instance = null;

//    TIME CLUSTERING GUI COMPONENTS

    @FXML
    private CheckBox timeClusteringCheckBox;

    @FXML
    private TextField numberOfPhasesTextField;
    private int desiredNumberOfPhases;

    @FXML
    private Label changesWeightLabel;

    @FXML
    private Label timeWeightLabel;

    @FXML
    private Slider weightSlider;

    @FXML
    private CheckBox entityClusteringCheckBox;

    @FXML
    private TextField numberOfEntityGroupsTextField;
    private int getDesiredNumberOfEntityGroups;

    private int maxNumberOfEntityGroups;
    private int maxNumberOfPhases;

    public static ClusteringProfileDialogController getInstance() {
        if (instance == null) {
            instance = new ClusteringProfileDialogController();
        }

        return instance;
    }

    public void initialize() {
        initializeDefaultTimeClusteringValues();

    }

    private void initializeDefaultTimeClusteringValues() {
        timeClusteringCheckBox.selectedProperty().addListener(
                (observable, oldValue, newValue) -> setDisabledTimeComponents(!newValue));
        entityClusteringCheckBox.selectedProperty().addListener(((observableValue, oldValue, newValue) -> setDisabledEntityComponents(!newValue)));

        numberOfPhasesTextField.setText(String.valueOf(5));
        numberOfPhasesTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            int value = 0;
            try {
                value = Integer.parseInt(newValue);
                if (value > 0 && value <= maxNumberOfPhases) {
                    desiredNumberOfPhases = value;
                } else {
                    numberOfPhasesTextField.setText(oldValue);
                }
            } catch (NumberFormatException e) {
                numberOfPhasesTextField.setText(newValue.replaceAll("\\D",""));
            }
        });

        numberOfEntityGroupsTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            int value = 0;
            try {
                value = Integer.parseInt(newValue);
                if (value > 0 && value <= maxNumberOfEntityGroups) {
                    getDesiredNumberOfEntityGroups = value;
                } else {
                    numberOfEntityGroupsTextField.setText(oldValue);
                }
            } catch (NumberFormatException e) {
                numberOfEntityGroupsTextField.setText(newValue.replaceAll("\\D",""));
            }
        });

        initWeightSlider();
        timeClusteringCheckBox.setSelected(true);
        entityClusteringCheckBox.setSelected(true);
    }

    private void initWeightSlider() {
        weightSlider.setMin(0);
        weightSlider.setMax(1);
        weightSlider.setValue(0.5);
        changesWeightLabel.textProperty().bind(Bindings.format("%.2f", weightSlider.valueProperty()));
        timeWeightLabel.textProperty().bind(Bindings.format("%.2f", weightSlider.valueProperty().negate().add(1)));
    }

    private void setDisabledTimeComponents(boolean disabled){
        numberOfPhasesTextField.setDisable(disabled);
        weightSlider.setDisable(disabled);
    }

    private void setDisabledEntityComponents(boolean disabled) {
        numberOfEntityGroupsTextField.setDisable(disabled);
    }

    private BeatClusteringProfile constructBeatClusteringProfile() {
        if (timeClusteringCheckBox.isSelected()) {
            return new BeatClusteringProfile(desiredNumberOfPhases, weightSlider.valueProperty().getValue(), false);
        } else {
            return new BeatClusteringProfile(desiredNumberOfPhases);
        }
    }

    private EntityClusteringProfile constructEntityClusteringProfile() {
        // TODO: Fill this
        if (entityClusteringCheckBox.isSelected()) {
            return new EntityClusteringProfile(getDesiredNumberOfEntityGroups);
        }
        return null;
    }

    public ClusteringProfile getClusteringProfile() {
        return new ClusteringProfile(constructBeatClusteringProfile(), constructEntityClusteringProfile());
    }

    public void setDefaultGroupNumbers(int numberOfPhases, int numberOfEntityGroups) {
        maxNumberOfEntityGroups = numberOfEntityGroups;
        maxNumberOfPhases = numberOfPhases;
        numberOfPhasesTextField.setText(String.valueOf(numberOfPhases));
        numberOfEntityGroupsTextField.setText(String.valueOf(numberOfEntityGroups));
    }
}
