package daintiness.gui;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import javax.swing.JTable;

import daintiness.clustering.ClusteringProfile;
import daintiness.clustering.EntityGroup;
import daintiness.clustering.measurements.ChartGroupPhaseMeasurement;
import daintiness.gui.details.EntityGroupDetails;
import daintiness.gui.details.PhaseDetails;
import daintiness.gui.tableview.PLDiagram;
import daintiness.maincontroller.IMainController;
import daintiness.maincontroller.MainControllerFactory;
import daintiness.models.measurement.IMeasurement;
import daintiness.utilities.Constants;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class Controller {
    @FXML
    public VBox maTypes;

    @FXML
    public VBox diagramsVBox;

    @FXML
    private BorderPane borderPane;

    @FXML
    private GridPane sortingOptions;

    @FXML
    private MenuItem saveMenuItem;

    @FXML
    private MenuItem saveAsMenuItem;

    @FXML
    private MenuItem clusterData;

    @FXML
    private MenuItem screenShotMenuItem;

    @FXML
    private MenuItem showPLDMenuItem;

    @FXML
    private MenuItem exportProjectMenuItem;


    @FXML
    private MenuItem closePLDMenuItem;

    @FXML
    private Button showPLDButton;

    @FXML
    private ToggleGroup sortingToggleGroup;


    private HBox pldButtonBar;

    private IMainController mainController;

    private final File initialDirectory = new File("src" + Constants.FS + "main" + Constants.FS + "resources" + Constants.FS + "data");

    private PLDiagram pld;

    private final ObjectProperty<Constants.SelectedCellType> selectedCellTypeProperty = new SimpleObjectProperty<>(Constants.SelectedCellType.DEFAULT);

    private final BooleanProperty selectionHasChanged = new SimpleBooleanProperty(false);
    private ChangeListener<Boolean> selectionHasChangedListener;

    private EntityGroupDetails entityGroupDetailsScrollPane;
    private PhaseDetails phaseDetailsScrollPane;

    private int maTypesSizeWithoutDetails;

    private Constants.MeasurementType measurementType;
    private Constants.AggregationType aggregationType;



    private enum GuiCondition {
        NO_DATA, DATA_NO_PLD, PLD
    }
    public void initialize() {
        MainControllerFactory factory = new MainControllerFactory();
        mainController = factory.getMainController("SIMPLE_MAIN_CONTROLLER");
        enableButtons(GuiCondition.NO_DATA);
        selectionHasChangedListener = (observable, oldValue, newValue) -> {
            if (maTypes.getChildren().size() > maTypesSizeWithoutDetails) {
                maTypes.getChildren().remove(maTypesSizeWithoutDetails);
            }
            switch (selectedCellTypeProperty.get()) {
                case PHASE:
                    phaseDetailsScrollPane = new PhaseDetails(pld.getSelectedPhase());
                    maTypes.getChildren().add(phaseDetailsScrollPane);
                    break;
                case ENTITY_GROUP:
                    entityGroupDetailsScrollPane = new EntityGroupDetails(pld.getSelectedGroup());
                    maTypes.getChildren().add(entityGroupDetailsScrollPane);
                    break;
                case MEASUREMENT:
                    System.out.println("MEASUREMENT");
                    break;
                default:
                    System.out.println("SelectedCellTypeProperty ChangeListener: Invalid SelectedCellType");
                    System.out.println(newValue);
            }
        };
        
        
        

        
        Button closePLDButton = new Button();
        closePLDButton.setText("X");
        closePLDButton.setStyle("-fx-font-weight: bold; -fx-border-color: black; -fx-background-color: #c62828;");
//        closePLDButton.setPadding(new Insets(5,5,5,5));
        closePLDButton.setOnAction(this::closePLD);

        Button screenShotButton = new Button();
        screenShotButton.setText("screenshot");
        screenShotButton.setStyle("-fx-font-weight: bold; -fx-border-color: black;");
//        screenShotButton.setPadding(new Insets(5,5,5,5));
        screenShotButton.setOnAction(this::takeScreenShot);


        pldButtonBar = new HBox();
        pldButtonBar.setAlignment(Pos.CENTER_RIGHT);
        pldButtonBar.getChildren().add(screenShotButton);
        pldButtonBar.getChildren().add(closePLDButton);
    }

    @FXML
    public void loadFromFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Load data from file...");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("SUPPORTED FILES", "*.tsv", "*.csv"),
                new FileChooser.ExtensionFilter("CSV", "*.csv"),
                new FileChooser.ExtensionFilter("TSV", "*.tsv"),
                new FileChooser.ExtensionFilter("ALL FILES", "*.*")
        );
        chooser.setInitialDirectory(initialDirectory);
        File selectedFile = chooser.showOpenDialog(borderPane.getScene().getWindow());
        if ((selectedFile != null) && selectedFile.exists()) {
            mainController.load(selectedFile);
            showClusteringDialog();
        }
    }

    @FXML
    public void loadFromFolder() {

        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Load data from directory (SCHEMA-EVO)");
        chooser.setInitialDirectory(initialDirectory);
        File selectedDirectory = chooser.showDialog(borderPane.getScene().getWindow());
        if ((selectedDirectory != null) && selectedDirectory.exists()) {
            mainController.load(selectedDirectory);
            showClusteringDialog();
        }
    }

    private void enableButtons(GuiCondition condition) {
        switch (condition) {
            case NO_DATA:
                saveMenuItem.setDisable(true);
                saveAsMenuItem.setDisable(true);
                clusterData.setDisable(true);
                screenShotMenuItem.setDisable(true);
                showPLDButton.setDisable(true);
                showPLDMenuItem.setDisable(true);
                exportProjectMenuItem.setDisable(true);
                closePLDMenuItem.setDisable(true);
                break;
            case DATA_NO_PLD:
                sortingOptions.setDisable(false);

                saveMenuItem.setDisable(false);
                saveAsMenuItem.setDisable(false);
                clusterData.setDisable(false);
                showPLDButton.setDisable(false);
                showPLDMenuItem.setDisable(false);
                exportProjectMenuItem.setDisable(false);
                break;
            case PLD:
                screenShotMenuItem.setDisable(false);
                showPLDButton.setDisable(true);
                showPLDMenuItem.setDisable(true);
                closePLDMenuItem.setDisable(false);
                break;
            default:
        }
    }

    
    private void createJTableMouseListener() {
    	pld.getJTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                if (e.getClickCount() == 1) {
                	final JTable target = (JTable)e.getSource();
                    final int row = target.getSelectedRow();
                    final int column = target.getSelectedColumn();
                    // Cast to ur Object type
                	final ChartGroupPhaseMeasurement cell = (ChartGroupPhaseMeasurement)target.getValueAt(row, column);

                    Platform.runLater(new Runnable(){
                    	@Override public void run() {
                    		if (maTypes.getChildren().size() > maTypesSizeWithoutDetails && column == 0) {
                                maTypes.getChildren().remove(maTypesSizeWithoutDetails);
                            }
                    		
                    		if(column == 0) {
                    			entityGroupDetailsScrollPane = new EntityGroupDetails(cell.getEntityGroup());
                                maTypes.getChildren().add(entityGroupDetailsScrollPane);
                    		}
                    		

                        }
                	});
                	
                }
            }
        });
    }
    
    private void generateChartData(ClusteringProfile profile) {



        // Clear selected sorting button from previous PLD
        if (sortingToggleGroup.getSelectedToggle() != null) {
            sortingToggleGroup.getSelectedToggle().setSelected(false);
        }


//        When the second pld will be added change this.
        // Remove PLD
        if (diagramsVBox.getChildren().size() > 2) {
            selectedCellTypeProperty.unbind();
            diagramsVBox.getChildren().remove(pld);
            diagramsVBox.getChildren().remove(pldButtonBar);

        }

        if (profile != null) {
            mainController.fitDataToGroupPhaseMeasurements(profile);
        }

        pld = new PLDiagram(
                mainController.getChartData(),
                mainController.getPhases());


        selectedCellTypeProperty.bind(pld.selectedCellTypeProperty());

        selectionHasChanged.bind(pld.selectionHasChangedProperty());
        selectionHasChanged.addListener(selectionHasChangedListener);
        initializeMATypes();

        
        createJTableMouseListener();
    	
        
        
        
        enableButtons(GuiCondition.DATA_NO_PLD);
    }


    @FXML
    public void save() {

        if (mainController.hasOutputPath()) {
            mainController.save();
        } else {
            saveAs();
        }
    }


    @FXML
    public void saveAs() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save as...");
        chooser.setInitialDirectory(initialDirectory);
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TSV", "*.tsv"));
        File selectedFile = chooser.showSaveDialog(borderPane.getScene().getWindow());

        if (selectedFile != null) {
            mainController.save(selectedFile);
        }
    }


    @FXML
    public void exit() {
        //Alert alert = new Alert();

        Platform.exit();
    }


    
    private void showPLDAfterSettingsChange() {
    	diagramsVBox.getChildren().remove(pld);
        diagramsVBox.getChildren().remove(pldButtonBar);
        if (maTypes.getChildren().size() > maTypesSizeWithoutDetails) {
            maTypes.getChildren().remove(maTypesSizeWithoutDetails);
        }
        pld = new PLDiagram(
                mainController.getChartData(),
                mainController.getPhases());
        showPLD();
        
        createJTableMouseListener();
        
    }
    
    private void initializeMATypes() {
        if (maTypes.getChildren().size() != 0) {
            maTypes.getChildren().clear();
        }

        Label measurementTypeLabel = new Label("Available measurement types: ");
        measurementTypeLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        measurementTypeLabel.setPadding(new Insets(10,5,10,5));

        maTypes.getChildren().add(measurementTypeLabel);
        ToggleGroup measurementTypeGroup = new ToggleGroup();
        for (Constants.MeasurementType tmpMeasurementType: mainController.getAvailableMeasurementTypesList()){
            RadioButton measurementTypeRadioButton = new RadioButton();
            measurementTypeRadioButton.setStyle("-fx-text-fill:  white");
            measurementTypeRadioButton.setText(String.valueOf(tmpMeasurementType));
            measurementTypeRadioButton.setToggleGroup(measurementTypeGroup);
            measurementTypeRadioButton.setOnAction(event -> measurementType = tmpMeasurementType);
            measurementTypeRadioButton.setPadding(new Insets(5,5,5,5));

            if (mainController.getMeasurementType() == tmpMeasurementType) {
                measurementTypeRadioButton.setSelected(true);
                measurementType = tmpMeasurementType;
            }

            maTypes.getChildren().add(measurementTypeRadioButton);
        }

        maTypes.getChildren().add(new Separator());
        Label aggregationTypeLabel = new Label("Available aggregation types: ");
        aggregationTypeLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white");
        aggregationTypeLabel.setPadding(new Insets(10,5,10,5));
        maTypes.getChildren().add(aggregationTypeLabel);

        ToggleGroup aggregationTypeGroup = new ToggleGroup();
        for (Constants.AggregationType tmpAggregationType: mainController.getAvailableAggregationTypesList()){
            RadioButton aggregationTypeRadioButton = new RadioButton();
            aggregationTypeRadioButton.setStyle("-fx-text-fill: white");
            aggregationTypeRadioButton.setText(String.valueOf(tmpAggregationType));
            aggregationTypeRadioButton.setToggleGroup(aggregationTypeGroup);
            aggregationTypeRadioButton.setOnAction(event -> aggregationType = tmpAggregationType);
            aggregationTypeRadioButton.setPadding(new Insets(5,5,5,5));

            if (mainController.getAggregationType() == tmpAggregationType) {
                aggregationTypeRadioButton.setSelected(true);
                aggregationType = tmpAggregationType;
            }

            maTypes.getChildren().add(aggregationTypeRadioButton);
        }
        Button confirmButton = new Button("confirm");
        confirmButton.setPadding(new Insets(10,100,10,100));
        confirmButton.setOnAction(event -> {
        	mainController.generateChartDataOfType(measurementType, aggregationType);
        	showPLDAfterSettingsChange();
        });
        maTypes.getChildren().add(confirmButton);
        maTypesSizeWithoutDetails = maTypes.getChildren().size();
        
    }

    @FXML
    public void sortByActivityD() {
        mainController.sortChartData(Constants.SortingType.ACTIVITY_DESCENDING);
        showPLDAfterSettingsChange();
    }


    @FXML
    public void sortByBirthDateD() {
        mainController.sortChartData(Constants.SortingType.BIRTH_DESCENDING);
        showPLDAfterSettingsChange();
    }


    @FXML
    public void sortByDurationD() {
        mainController.sortChartData(Constants.SortingType.LIFE_DURATION_DESCENDING);
        showPLDAfterSettingsChange();
    }


    @FXML
    public void sortByActivityA() {
        mainController.sortChartData(Constants.SortingType.ACTIVITY_ASCENDING);
        showPLDAfterSettingsChange();
    }


    @FXML
    public void sortByBirthDateA() {
        mainController.sortChartData(Constants.SortingType.BIRTH_ASCENDING);
        showPLDAfterSettingsChange();
    }


    @FXML
    public void sortByDurationA() {
        mainController.sortChartData(Constants.SortingType.LIFE_DURATION_ASCENDING);
        showPLDAfterSettingsChange();
    }


    @FXML
    public void showClusteringDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(borderPane.getScene().getWindow());
        dialog.setTitle("Set clustering profile");


        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxml/ClusteringProfileDialog.fxml"));
        fxmlLoader.setController(ClusteringProfileDialogController.getInstance());
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        ClusteringProfileDialogController.getInstance().setDefaultGroupNumbers(mainController.getNumberOfBeats(), mainController.getNumberOfEntities());

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            generateChartData(ClusteringProfileDialogController.getInstance().getClusteringProfile());

        } else {
            System.out.println("Cancel was pressed");
        }
    }

    @FXML
    public void takeScreenShot(ActionEvent actionEvent) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save image...");
        chooser.setInitialDirectory(initialDirectory);
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG", "*.png"));
        File selectedFile = chooser.showSaveDialog(borderPane.getScene().getWindow());

        if (selectedFile != null) {
            //WritableImage img = pld.getTableView().snapshot(null, null);
        	WritableImage img = pld.getJTableNode().snapshot(null, null);
            try {
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(img, null);
                ImageIO.write(bufferedImage, "png", selectedFile);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @FXML
    public void closePLD(ActionEvent actionEvent) {
        if (diagramsVBox.getChildren().contains(pld)) {
            diagramsVBox.getChildren().remove(pld);
            diagramsVBox.getChildren().remove(pldButtonBar);

            enableButtons(GuiCondition.DATA_NO_PLD);
        }
    }

    @FXML
    public void importProject(ActionEvent actionEvent) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose project folder");
        chooser.setInitialDirectory(initialDirectory);
        File selectedFile = chooser.showDialog(borderPane.getScene().getWindow());
        if ((selectedFile != null)) {
            mainController.importProject(selectedFile);
            enableButtons(GuiCondition.DATA_NO_PLD);
            generateChartData(null);
        }
    }

    @FXML
    public void exportProject() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose folder to save the PLD data");
        chooser.setInitialDirectory(initialDirectory);
        File selectedFile = chooser.showDialog(borderPane.getScene().getWindow());
        if ((selectedFile != null)) {
            mainController.exportProject(selectedFile);
        }
    }

    @FXML
    public void showPLD() {
        diagramsVBox.getChildren().add(pldButtonBar);
        diagramsVBox.getChildren().add(pld);

        enableButtons(GuiCondition.PLD);
    }
}
