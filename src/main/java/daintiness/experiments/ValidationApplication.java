package daintiness.experiments;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import daintiness.maincontroller.IMainController;
import daintiness.maincontroller.MainController;

public class ValidationApplication {
	private static int TIMES_PER_DATASET = 10;
	
	
//    private static double minPopulationSize = Integer.MAX_VALUE;
//    private static double maxPopulationSize = Integer.MIN_VALUE;
//
//    private static double minTimelineSize = Integer.MAX_VALUE;
//    private static double maxTimelineSize = Integer.MIN_VALUE;
//
//    private static double minNumberOfCells = Integer.MAX_VALUE;
//    private static double maxNumberOfCells = Integer.MIN_VALUE;
//
//    private static double minNumberOfNonEmptyCells = Integer.MAX_VALUE;
//    private static double maxNumberOfNonEmptyCells = Integer.MIN_VALUE;
//
//    private static double minNumberOfEmptyCells = Integer.MAX_VALUE;
//    private static double maxNumberOfEmptyCells = Integer.MIN_VALUE;
//
//    private static double minConversionTime = Double.MAX_VALUE;
//    private static double maxConversionTime = Double.MIN_VALUE;

    public static void main(String[] args){
//        if (args.length != 1) {
//            System.out.println("Argument failure: Enter just the folder path with the projects.");
//            System.exit(-1);
//        }

        File folderFile = new File("D:\\Documents\\DOCS\\UOI\\ts-visualizer\\Datasets\\schema_evo");
//        File folderFile = new File(args[0]);
        if (!folderFile.isDirectory()) {
            System.out.println("Argument failure: Enter just the folder path with the projects.");
            System.exit(-1);
        }

        IMainController mainController = new MainController();
        List<DatasetValidationInfo> results = new ArrayList<>();

        File[] datasets = folderFile.listFiles();

        for (File dataset: datasets) {
        	
            if (!dataset.isDirectory()) {
                System.out.println("The given folder should contain only schema evo datasets as folders.");
                System.exit(-1);
            }
            String datasetName = dataset.getName();
            
            
            double time_sum = 0;
            for (int i = 0; i < TIMES_PER_DATASET + 1; i++) {
            	double startTime = System.nanoTime();
                mainController.load(dataset);
                double time = System.nanoTime() - startTime;
                
                if (i != 0) {
                	time_sum += time;
                }
                
            }
            
            
            DatasetValidationInfo datasetInfo = new DatasetValidationInfo(
                    datasetName,
                    mainController.getNumberOfEntities(),
                    mainController.getNumberOfBeats(),
                    mainController.getNumberOfTEMs());

            datasetInfo.setConversionTimeNano(time_sum / TIMES_PER_DATASET);
//            updateMinMaxValues(datasetInfo);
            results.add(datasetInfo);
        }

//        for (DatasetValidationInfo dataset: results) {
//            dataset.setPopulationSize((dataset.getPopulationSize() - minPopulationSize) / (maxPopulationSize - minPopulationSize));
//            dataset.setTimelineSize((dataset.getTimelineSize() - minTimelineSize) / (maxTimelineSize - minTimelineSize));
//            dataset.setNumberOfCells((dataset.getNumberOfCells() - minNumberOfCells) / (maxNumberOfCells - minNumberOfCells));
//            dataset.setNumberOfNonEmptyCells((dataset.getNumberOfNonEmptyCells() - minNumberOfNonEmptyCells) / (maxNumberOfNonEmptyCells - minNumberOfNonEmptyCells));
//            dataset.setNumberOfEmptyCells((dataset.getNumberOfEmptyCells() - minNumberOfEmptyCells) / (maxNumberOfEmptyCells - minNumberOfEmptyCells));
//            dataset.setConversionTimeNano((dataset.getConversionTimeNano() - minConversionTime) / (maxConversionTime - minConversionTime));
//        }


        File parentFolder = folderFile.getParentFile();
        File validationResultsFile = new File(parentFolder.getPath() + File.separator + "validationResults.tsv");
        try (FileWriter fw = new FileWriter(validationResultsFile)) {
            fw.write("Dataset\t#Entities\t#TimeBeats\t#Cells\t#NonEmptyCells\t#EmptyCells\tConversionTime(nanoSecs)\n");
            for (DatasetValidationInfo dataset: results) {
                System.out.println(dataset);
                fw.write(dataset.toString() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private static void updateMinMaxValues(DatasetValidationInfo newInfo) {
//        if (newInfo.getPopulationSize() > maxPopulationSize) {
//            maxPopulationSize = newInfo.getPopulationSize();
//        }
//
//        if (newInfo.getPopulationSize() < minPopulationSize) {
//            minPopulationSize = newInfo.getPopulationSize();
//        }
//
//
//        if (newInfo.getTimelineSize() > maxTimelineSize) {
//            maxTimelineSize = newInfo.getTimelineSize();
//        }
//
//        if (newInfo.getTimelineSize() < minTimelineSize) {
//            minTimelineSize = newInfo.getTimelineSize();
//        }
//
//
//        if (newInfo.getNumberOfCells() > maxNumberOfCells) {
//            maxNumberOfCells = newInfo.getNumberOfCells();
//        }
//
//        if (newInfo.getNumberOfCells() < minNumberOfCells) {
//            minNumberOfCells = newInfo.getNumberOfCells();
//        }
//
//
//        if (newInfo.getNumberOfNonEmptyCells() > maxNumberOfNonEmptyCells) {
//            maxNumberOfNonEmptyCells = newInfo.getNumberOfNonEmptyCells();
//        }
//
//        if (newInfo.getNumberOfNonEmptyCells() < minNumberOfNonEmptyCells) {
//            minNumberOfNonEmptyCells = newInfo.getNumberOfNonEmptyCells();
//        }
//
//
//        if (newInfo.getNumberOfEmptyCells() > maxNumberOfEmptyCells) {
//            maxNumberOfEmptyCells = newInfo.getNumberOfEmptyCells();
//        }
//
//        if (newInfo.getNumberOfEmptyCells() < minNumberOfEmptyCells) {
//            minNumberOfEmptyCells = newInfo.getNumberOfEmptyCells();
//        }
//
//
//        if (newInfo.getConversionTimeNano() > maxConversionTime) {
//            maxConversionTime = newInfo.getConversionTimeNano();
//        }
//
//        if (newInfo.getConversionTimeNano() < minConversionTime) {
//            minConversionTime = newInfo.getConversionTimeNano();
//        }
//    }
}
