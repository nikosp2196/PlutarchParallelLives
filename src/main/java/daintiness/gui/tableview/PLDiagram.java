package daintiness.gui.tableview;

import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

import javax.swing.BorderFactory;

import javax.swing.JScrollPane;
import javax.swing.JTable;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import daintiness.clustering.EntityGroup;
import daintiness.clustering.Phase;
import daintiness.clustering.measurements.ChartGroupPhaseMeasurement;
import daintiness.models.measurement.EmptyIMeasurement;
import daintiness.models.measurement.IMeasurement;
import daintiness.utilities.Constants;

public class PLDiagram extends ScrollPane {
	private /* final */ Group group;
	private /* final */ TableView<ChartGroupPhaseMeasurement> tableView;
	

    private final int cellWidth = 25;
    private final int firstColumnWidth = 110;

    private final double minScale = 0.2;
    private final double maxScale = 2.0;


    private ObjectProperty<Constants.SelectedCellType> selectedCellType = new SimpleObjectProperty<>(Constants.SelectedCellType.DEFAULT);
    private BooleanProperty selectionHasChanged = new SimpleBooleanProperty(false);
    private EntityGroup selectedGroup;
    private IMeasurement selectedMeasurement;
    private Phase selectedPhase;
    
    private List<Phase> phasesJTable;
    private JScrollPane jScrollPane;
    private JTable table;
    private SwingNode swingNode;
    private DefaultTableModel tableModel;
    

    public PLDiagram(ObservableList<ChartGroupPhaseMeasurement> observableList,
                     List<Phase> phases) {
        super();
		
		group = new Group(); 
		//tableView = new TableView<>();
		//initializeComponents(observableList, phases);
        createJTable(observableList, phases);
		
    }
    
    private void createJTable(ObservableList<ChartGroupPhaseMeasurement> observableList, List<Phase> phases) {
    	List<String> columnsJTable = new ArrayList<String>();
    	
    	phasesJTable = new ArrayList<Phase>(phases);

    	//Initialize Columns
    	for(Phase i: phases) {
    		int phaseId = i.getPhaseId();
    		if(phaseId == 0) {
    			columnsJTable.add("Groups");
    		}
    		columnsJTable.add(Integer.toString(phaseId));	
    	}
    
    	String[] col = columnsJTable.toArray(new String[0]);
     	tableModel = new DefaultTableModel(col, 0);

     	//Initialize JTable
     	table = new JTable(tableModel) {
     		private static final long serialVersionUID = 1L;
     		public boolean isCellEditable(int row, int column){  
     	          return false;  
     	      };
     	};
     	table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);	
     	table.getColumnModel().getColumn(0).setPreferredWidth(firstColumnWidth);

     	for(int i = 1; i < table.getColumnCount(); i++) {
     		table.getColumnModel().getColumn(i).setPreferredWidth(cellWidth);
     	}
     	
     	
     	//Initialize data in JTable based on a ArrayList with ChartGroupPhaseMeasurement Objects
		for(ChartGroupPhaseMeasurement tab : observableList) {
			List<ChartGroupPhaseMeasurement> entityGroupsJTable = new ArrayList<ChartGroupPhaseMeasurement>();

			entityGroupsJTable.add(tab);
			for (Phase phase : phases) {
				entityGroupsJTable.add(tab);
			}
			Object[] objs = entityGroupsJTable.toArray(new Object[phases.size()]);
			tableModel.addRow(objs);
			
		}

		table.setDefaultRenderer(Object.class, new PaintTableCellRenderer());
     	

		jScrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		//Table size
		int tableWidth = (phases.size() * cellWidth) + firstColumnWidth;
        int tableHeight = observableList.size() * cellWidth;
        Dimension tableDimensions = new Dimension();
        tableDimensions.setSize(tableWidth * 1.1, tableHeight);
        jScrollPane.setPreferredSize(tableDimensions);
		
		
        
        swingNode = new SwingNode();
        swingNode.setContent(jScrollPane);
        group.getChildren().add(swingNode);
        group.prefHeight(900);
        group.prefWidth(900);
        setContent(group);
        
        enableZoomJTable(swingNode,Double.valueOf(tableWidth),Double.valueOf(tableHeight));
        
    }
    
    

    public class PaintTableCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        	
        	super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        	
        	
        	ChartGroupPhaseMeasurement val = (ChartGroupPhaseMeasurement)value;
        	
        	if(column == 0) {
        		EntityGroup entityGroup = val.getEntityGroup();
        		String entityGroupName = null;

        		if (entityGroup != null) {
    				if (entityGroup.getGroupComponents().size() == 1) {
    					entityGroupName = entityGroup.getGroupComponents().get(0).getEntityName();
    				} else {
    					entityGroupName = String.valueOf(entityGroup.getEntityGroupId());
    				}
    			}
        		

        		if(entityGroup.getLifeDetails().isAlive()) {
        			setForeground(java.awt.Color.white);
        			setBackground(java.awt.Color.decode("#47c4c4"));
        			setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, java.awt.Color.BLACK));
        			
        			setValue(entityGroupName);
        		}
        		else {
        			setForeground(java.awt.Color.white);
            		setBackground(java.awt.Color.decode("#c26847"));
            		setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, java.awt.Color.BLACK));
            		setValue(entityGroupName);
            		
            	}
        	}
        	else {
        		if(val != null) {
    				Phase phase = phasesJTable.get(column-1);
    				int phaseId = phase.getPhaseId();
    				if(phaseId == column - 1) {
    					Constants.GPMType type = val.getEntityGroup().getGPMType(phase.getFirstPhaseBeat().getBeatId(),
        						phase.getLastPhaseBeat().getBeatId());
        				IMeasurement measurement = new EmptyIMeasurement(type);
        				
        				
        				if (type.equals(Constants.GPMType.ACTIVE) && val.containsMeasurementInPhase(phaseId)) {
        					String color = val.getMeasurement(phaseId).getColor();
        					if(color != null) {
        						setBackground(java.awt.Color.decode(val.getMeasurement(phaseId).getColor()));
            					setValue("");
        					}
        				} else {
        					String color = measurement.getColor();
        					if(color != null) {
        						setBackground(java.awt.Color.decode(measurement.getColor()));
            					setValue("");
        					}
        				}
    				}
        		}
        	}
        	
        	return this;
        }
    }

    
    private void enableZoomJTable(SwingNode s,double tableWidth, double tableHeight) {
        group.setOnScroll(scrollEvent -> {

            double translationFactor = 0.02;
            double zoomFactor = 1 + translationFactor;
            double deltaY = scrollEvent.getDeltaY();

            
            if (deltaY < 0) {
                zoomFactor = 2 - zoomFactor;
                translationFactor = -0.02;
            }
            
            DoubleProperty widthProperty = new SimpleDoubleProperty();
            DoubleProperty heightProperty = new SimpleDoubleProperty();
            
            widthProperty.set(tableWidth);
            heightProperty.set(tableHeight);
            
            
            
            Translate center = new Translate(group.getTranslateX(), group.getTranslateY());
            center.xProperty().bind(widthProperty.multiply(translationFactor));
            center.yProperty().bind(heightProperty.multiply(-translationFactor));


            Scale scale = new Scale();
            scale.xProperty().setValue(zoomFactor);
            scale.yProperty().setValue(zoomFactor);


            if ((group.getScaleX() > minScale && zoomFactor < 1) ||
                    (group.getScaleX() < maxScale && zoomFactor > 1)) {
            	
                s.getTransforms().addAll(scale, center);
            }

            scrollEvent.consume();
        });
    }

    
    
    //Methods below are for the previous JavaFX TableView.
    
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
    
    public SwingNode getJTableNode() {
        return swingNode;
    }
    
    public TableModel getTableModel() {
        return tableModel;
    }
    
    public JTable getJTable() {
        return table;
    }
}
