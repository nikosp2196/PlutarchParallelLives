package daintiness.gui.tableview;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import daintiness.clustering.BeatClusteringProfile;
import daintiness.clustering.ClusteringProfile;
import daintiness.clustering.EntityClusteringProfile;
import daintiness.clustering.Phase;
import daintiness.clustering.measurements.ChartGroupPhaseMeasurement;
import daintiness.data.IDataHandler;
import daintiness.io.input.ILoader;
import daintiness.io.input.SimpleLoader;
import daintiness.maincontroller.MainController;
import daintiness.models.Beat;
import daintiness.models.Entity;
import daintiness.models.TimeEntityMeasurements;
import daintiness.utilities.Constants;
import javafx.collections.ObservableList;

public class TableviewTest {
	MainController mainController = new MainController();
	File originalFile = new File(
			"src" + Constants.FS + "test" + Constants.FS + "resources" + Constants.FS + "RefactoringTestData");
	String Path = "src" + Constants.FS + "test" + Constants.FS + "resources" + Constants.FS + "RefactoringTestData"
			+ Constants.FS + "figures" + Constants.FS + "PPL";
	
	ILoader loader;
	IDataHandler dataHandler;

	
	List<Entity> expectedEntities = new ArrayList<Entity>();

	@Test
	@DisplayName("Check if new JTable is filled correctly test")
	public void newJTableTest() {
		// Manage TSV
		String filePath = Path + "/RefactoringTestData_detailedPLD.tsv";

		File tsvFile = new File(filePath);
		
		loader = new SimpleLoader(tsvFile, "\t");
		dataHandler = loader.load();
		
		// Manage data in JTable Data
		mainController.load(originalFile);
		mainController.fitDataToGroupPhaseMeasurements(
				new ClusteringProfile(new BeatClusteringProfile(mainController.getNumberOfBeats()),
						new EntityClusteringProfile(mainController.getNumberOfEntities())));


		ObservableList<ChartGroupPhaseMeasurement> observableList = mainController.getChartData();
		List<Phase> phases = mainController.getPhases();

		for (ChartGroupPhaseMeasurement tab : observableList) {
			
			expectedEntities = tab.getEntityGroup().getGroupComponents();
			String entityName = tab.getEntityGroup().getGroupComponents().get(0).getEntityName();
			List<Double> expectedMeasurements = new ArrayList<Double>();
			
			for(Phase phase: phases) {
				if(tab.getMeasurement(phase.getPhaseId()) != null) {
					expectedMeasurements.add(tab.getMeasurement(phase.getPhaseId()).getValue());
				}
				else {
					expectedMeasurements.add(null);
				}
				
			}
			
			testMeasurements(entityName,expectedMeasurements);
		}
		
		List<Beat> beatList = new  ArrayList<Beat>(); 
		for(Phase ps: phases) {
			beatList.add(ps.getPhaseComponents().get(0));
			
		}
		
		
		
		

		//TimeLineTest
		List<Beat> actualTimeLine = dataHandler.getTimeline();
		Assumptions.assumeTrue(beatList.size() == actualTimeLine.size());

        for(int i = 0; i < actualTimeLine.size(); i++){
            Assertions.assertEquals(beatList.get(i).getDate().toString(), actualTimeLine.get(i).getDate().toString());
        }
		
        
        //EntitiesTest
        List<Entity> actualEntities = dataHandler.getPopulation();
        Assumptions.assumeTrue(expectedEntities.size() == actualEntities.size());

        for(int i = 0; i < actualEntities.size(); i++){
            Assertions.assertEquals(expectedEntities.get(i).getEntityName(), actualEntities.get(i).getEntityName());
        }
        

	}
	
    //MeasurementsTest
	private void testMeasurements(String entityName, List<Double> expectedMeasurements) {
    	Map<Integer, TimeEntityMeasurements> actualMeasurements = dataHandler.getEntityNameToTEMMap().get(entityName);
    	if(actualMeasurements != null) {
			for (Integer i : actualMeasurements.keySet()) {
				Assertions.assertEquals(expectedMeasurements.get(i), actualMeasurements.get(i).getMeasurementOfType(Constants.MeasurementType.RAW_VALUE,
						Constants.AggregationType.NO_AGGREGATION));
			}
			 
    	}
    }

}
