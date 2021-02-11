package daintiness.gui.tableview;

import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

import java.util.List;

import daintiness.clustering.EntityGroup;
import daintiness.clustering.Phase;
import daintiness.clustering.measurements.ChartGroupPhaseMeasurement;
import daintiness.models.measurement.EmptyIMeasurement;
import daintiness.models.measurement.IMeasurement;
import daintiness.utilities.Constants;

public class PLDiagram extends ScrollPane {
    private final Group group;
    private final TableView<ChartGroupPhaseMeasurement> tableView;

    private final int cellWidth = 20;
    private final int firstColumnWidth = 110;

    private final double minScale = 0.2;
    private final double maxScale = 2.0;


    private ObjectProperty<Constants.SelectedCellType> selectedCellType = new SimpleObjectProperty<>(Constants.SelectedCellType.DEFAULT);
    private BooleanProperty selectionHasChanged = new SimpleBooleanProperty(false);
    private EntityGroup selectedGroup;
    private IMeasurement selectedMeasurement;
    private Phase selectedPhase;

    public PLDiagram(ObservableList<ChartGroupPhaseMeasurement> observableList,
                     List<Phase> phases) {
        super();
        group = new Group();
        tableView = new TableView<>();

        initializeComponents(observableList, phases);
    }

    private void initializeComponents(ObservableList<ChartGroupPhaseMeasurement> observableList, List<Phase> phases) {
        int numCols = phases.size();
        int numRows = observableList.size() + 1;

        setTableSize(numCols, numRows);

        initializeEntityGroupsColumn();
        initializePhaseColumns(phases);

        initializeCellSelectionHandler();

        enableZoom();

        tableView.setItems(observableList);
        group.getChildren().add(tableView);
        setContent(group);
//        setPrefHeight(400.0);

        selectedCellType.setValue(Constants.SelectedCellType.ENTITY_GROUP);
    }

    private void initializeCellSelectionHandler() {
        tableView.getFocusModel().focusedCellProperty().addListener((observable, oldValue, newValue) -> {
            int row = newValue.getRow();
            int column = newValue.getColumn();
            if (column == 0) {
                selectedCellType.set(Constants.SelectedCellType.ENTITY_GROUP);
                selectedGroup = tableView.getItems().get(row).getEntityGroup();
                System.out.println(selectedGroup.getGroupComponents().get(0));
            } else if (column > 0) {
                selectedCellType.set(Constants.SelectedCellType.MEASUREMENT);
                if (tableView.getItems().get(row).containsMeasurementInPhase(column)) {
                    selectedMeasurement = tableView.getItems().get(row).getMeasurement(column);
                }
            } else {
                System.out.println("Out of border selection");
            }
            selectionHasChanged.setValue(!selectionHasChanged.getValue());
        });
    }

    private void enableZoom() {
        group.setOnScroll(scrollEvent -> {

            double translationFactor = 0.02;
            double zoomFactor = 1 + translationFactor;
            double deltaY = scrollEvent.getDeltaY();


            if (deltaY < 0) {
                zoomFactor = 2 - zoomFactor;
                translationFactor = -0.02;
            }

            Translate center = new Translate(group.getTranslateX(), group.getTranslateY());
            center.xProperty().bind(tableView.widthProperty().multiply(translationFactor));
            center.yProperty().bind(tableView.heightProperty().multiply(-translationFactor));


            Scale scale = new Scale();
            scale.xProperty().setValue(zoomFactor);
            scale.yProperty().setValue(zoomFactor);


            if ((group.getScaleX() > minScale && zoomFactor < 1) ||
                    (group.getScaleX() < maxScale && zoomFactor > 1)) {

                tableView.getTransforms().addAll(scale, center);
            }

            scrollEvent.consume();
        });
    }

    private void initializeEntityGroupsColumn() {
        TableColumn<ChartGroupPhaseMeasurement, EntityGroup> entitiesCol = new TableColumn<>("Groups");

        entitiesCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getEntityGroup()));

        entitiesCol.setCellFactory(e -> new TableCell<>() {
            @Override
            protected void updateItem(EntityGroup item, boolean empty) {
                super.updateItem(item, empty);

                if (item != null) {
                    if (item.getGroupComponents().size() == 1) {
                        setText(item.getGroupComponents().get(0).getEntityName());
                    } else {
                        setText(String.valueOf(item.getEntityGroupId()));
                    }
                    StringBuilder tooltipTextBuilder = new StringBuilder();
                    tooltipTextBuilder.append(String.format("GroupID: %d  Size: %d\n", item.getEntityGroupId(), item.getGroupComponentsNames().size()));
                    tooltipTextBuilder.append(String.format("Birth: %d  Death: %d\n", item.getLifeDetails().getBirthBeatId(), item.getLifeDetails().getDeathBeatId()));



                    if (item.getLifeDetails().isAlive()) {
                        setStyle("-fx-background-color: #47c4c4; -fx-border-color: black;");
                    } else {
                        setStyle("-fx-background-color: #c26847; -fx-border-color: black;");
                    }
                    setTextFill(Color.WHITE);
                    setTooltip(new Tooltip(tooltipTextBuilder.toString()));


                } else {
                    setText(null);
                }
            }
        });


        entitiesCol.setSortable(false);
        entitiesCol.setEditable(false);

        entitiesCol.setMaxWidth(firstColumnWidth);
        tableView.getColumns().add(entitiesCol);
    }

    private void initializePhaseColumns(List<Phase> phases) {
        for (Phase phase : phases) {

            int phaseId = phase.getPhaseId();
            TableColumn<ChartGroupPhaseMeasurement, IMeasurement> phaseColumn = new TableColumn<>(String.valueOf(phaseId));
            ContextMenu contextMenu = new ContextMenu();
            MenuItem details = new MenuItem("Show phase details");
            details.setOnAction(event -> {
                selectedCellType.set(Constants.SelectedCellType.PHASE);
                selectedPhase = phase;
                selectionHasChanged.setValue(!selectionHasChanged.getValue());
            });

            contextMenu.getItems().add(details);
            phaseColumn.setContextMenu(contextMenu);
            phaseColumn.setCellValueFactory(param -> {
                Constants.GPMType type =  param.getValue().getEntityGroup().getGPMType(phase.getFirstPhaseBeat().getBeatId(), phase.getLastPhaseBeat().getBeatId());
                IMeasurement measurement;
                if (type.equals(Constants.GPMType.ACTIVE) && param.getValue().containsMeasurementInPhase(phaseId)) {
                    measurement = param.getValue().getMeasurement(phaseId);
                } else {
                    measurement = new EmptyIMeasurement(type);
                }
                return new ReadOnlyObjectWrapper<>(measurement);
            });

            phaseColumn.setCellFactory(e-> new TableCell<>() {
                @Override
                protected void updateItem(IMeasurement item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null) {
                        Tooltip tooltip = new Tooltip(String.format("Measurement type: %s \n " +
                                "Aggregation type: %s\n" +
                                "Value: %.4f", item.getMeasurementType(), item.getAggregationType(), item.getValue()));
                        setTooltip(tooltip);
                        setStyle("-fx-background-color: " + item.getColor() + "; -fx-border-color: " + item.getColor() + ";");
                    } else {
                        setText(null);
                    }
                }
            });

            phaseColumn.setSortable(false);
            phaseColumn.setEditable(false);
            phaseColumn.setMaxWidth(cellWidth * 1.5);
            phaseColumn.setPrefWidth(cellWidth);
            tableView.getColumns().add(phaseColumn);
        }
    }

    private void setTableSize(int numCols, int numRows) {
        double tableWidth = (numCols * cellWidth) + firstColumnWidth;
        double tableHeight = numRows * cellWidth;

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setPrefSize(tableWidth * 1.5, tableHeight);
    }

    public Group getGroup() {
        return group;
    }

    public TableView<ChartGroupPhaseMeasurement> getTableView() {
        return tableView;
    }

    public EntityGroup getSelectedGroup() {
        return selectedGroup;
    }

    public IMeasurement getSelectedMeasurement() {
        return selectedMeasurement;
    }

    public Phase getSelectedPhase() {
        return selectedPhase;
    }

    public Property<Constants.SelectedCellType> selectedCellTypeProperty() {
        return selectedCellType;
    }

    public BooleanProperty selectionHasChangedProperty() {
        return selectionHasChanged;
    }
}
