package daintiness.app;

import java.io.File;

import daintiness.maincontroller.IMainController;
import daintiness.maincontroller.MainControllerFactory;

/**
 * IRClient stands for Intermediate Representation Client.
 * <p\>
 * Enter the desired schema evolution project folder path as argument.
 * <p\>
 * If the given path and the project folder structure are valid, then <br>
 *     a detailedPLD.tsv file be created in the figures/PPL/ folder.
 */
public class IRClient {
    public static void main(String[] args) {

        // Check if the given file is valid
        if (args.length != 1) {
            System.out.println("You must enter only the input path as argument. Exiting...");
            System.exit(-1);
        }

        File projectFolder = new File(args[0]);
        if (!projectFolder.isDirectory()) {
            System.out.println("This path is not a folder: " + args[0]);
            System.out.println("Please enter the path of a schema evolution project folder. Exiting...");
            System.exit(-1);
        }
        MainControllerFactory factory = new MainControllerFactory();
        IMainController mainController = factory.getMainController("SIMPLE_MAIN_CONTROLLER");
        mainController.load(projectFolder);


//        // Load file in the DataHandler
//        FileHandlerFactory factory = new FileHandlerFactory();
//        IFileHandler fileHandler = factory.getFileHandler("SIMPLE_FILE_HANDLER");
//        File resultsFolder = new File(projectFolder.getPath() + File.separator + "results");
//
//        if (!resultsFolder.exists() || !resultsFolder.isDirectory()) {
//            System.out.println("Couldn't find the results folder in the project folder. Exiting...");
//            System.exit(-1);
//        }
//
//        fileHandler.setGivenFile(resultsFolder);
//        IDataHandler dataHandler = fileHandler.loadTEM();
//
//
//        // Create the PPL folder in figures if doesn't already exist
//        File pplFolder = new File(projectFolder.getPath() + File.separator +
//                "figures" + File.separator +
//                "PPL");
//
//        if (!pplFolder.exists() || !pplFolder.isDirectory()) {
//            pplFolder.mkdirs();
//        }
//
//        // Assemble the path of the output
//        File outputFile = new File(pplFolder.getPath() + File.separator +
//                projectFolder.getName() + "_detailedPLD.tsv");
//
//        // Write data
//        fileHandler.writeDataToFile(outputFile, dataHandler.getTimeEntityMeasurementAsString());
//
//        System.out.println("The file was created successfully at:\n " + outputFile.getPath() + "\n");
    }
}
