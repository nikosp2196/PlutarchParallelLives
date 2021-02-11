package daintiness.gui.details;

import daintiness.clustering.EntityGroup;
import daintiness.models.Entity;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class EntityGroupDetails extends ScrollPane {
    private Label entityGroupNameTitle;
    private VBox vBox;
    private String title;

    public EntityGroupDetails(EntityGroup group) {
        super();
        prefHeight(400);
        prefWidth(200);
        showComponents(group);
        setContent(vBox);
    }

    private void showComponents(EntityGroup group) {
        vBox = new VBox();
        Label groupInfo = new Label(String.format("Group id: %d\n" +
                "GroupLifeDetails:\n" +
                "->Birth: %d\n" +
                "->Death: %d\n" +
                "->Duration: %d\n" +
                "->Alive: %b\n\n" +
                "Components:\n",
                group.getEntityGroupId(),
                group.getLifeDetails().getBirthBeatId(),
                group.getLifeDetails().getDeathBeatId(),
                group.getLifeDetails().getDuration(),
                group.getLifeDetails().isAlive()));
        vBox.getChildren().add(groupInfo);
        for (Entity entity: group.getGroupComponents()) {
            vBox.getChildren().add(new Label(entity.toString()));
        }
    }
}
