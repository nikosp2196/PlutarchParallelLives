package daintiness.gui.details;

import daintiness.clustering.Phase;
import daintiness.models.Beat;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class PhaseDetails extends ScrollPane {
    private Label phaseTitle;
    private VBox vBox;

    public PhaseDetails(Phase phase) {
        super();
        prefHeight(400);
        prefWidth(200);
        showComponents(phase);
        setContent(vBox);
    }

    private void showComponents(Phase phase) {
        vBox = new VBox();
        for (Beat beat: phase.getPhaseComponents()) {
            vBox.getChildren().add(new Label(beat.toString()));
        }
    }

}
