package daintiness.experiments;

import java.text.NumberFormat;
import java.util.Locale;

public class DatasetValidationInfo {
    private final String datasetName;
    private double populationSize;
    private double timelineSize;
    private double numberOfCells;
    private double numberOfNonEmptyCells;
    private double numberOfEmptyCells;
    private double conversionTimeNano;

    public DatasetValidationInfo(String datasetName, double populationSize, double timelineSize, double numberOfTEMs) {
        this.datasetName = datasetName;
        this.populationSize = populationSize;
        this.timelineSize = timelineSize;
        this.numberOfCells = populationSize * timelineSize;
        this.numberOfNonEmptyCells = numberOfTEMs / numberOfCells;
        this.numberOfEmptyCells = 1 - numberOfNonEmptyCells;
    }

    public void setConversionTimeNano(double conversionTimeNano) {
        this.conversionTimeNano = conversionTimeNano;
    }

    public double getPopulationSize() {
        return populationSize;
    }

    public double getTimelineSize() {
        return timelineSize;
    }

    public double getNumberOfCells() {
        return numberOfCells;
    }

    public double getNumberOfNonEmptyCells() {
        return numberOfNonEmptyCells;
    }

    public double getNumberOfEmptyCells() {
        return numberOfEmptyCells;
    }

    public double getConversionTimeNano() {
        return conversionTimeNano;
    }

    public String getDatasetName() {
        return datasetName;
    }

    public void setPopulationSize(double populationSize) {
        this.populationSize = populationSize;
    }

    public void setTimelineSize(double timelineSize) {
        this.timelineSize = timelineSize;
    }

    public void setNumberOfCells(double numberOfCells) {
        this.numberOfCells = numberOfCells;
    }

    public void setNumberOfNonEmptyCells(double numberOfNonEmptyCells) {
        this.numberOfNonEmptyCells = numberOfNonEmptyCells;
    }

    public void setNumberOfEmptyCells(double numberOfEmptyCells) {
        this.numberOfEmptyCells = numberOfEmptyCells;
    }

    @Override
    public String toString() {
        return datasetName + "\t" +
                NumberFormat.getInstance(Locale.US).format(populationSize) + "\t" +
                NumberFormat.getInstance(Locale.US).format(timelineSize) + "\t" +
                NumberFormat.getInstance(Locale.US).format(numberOfCells) + "\t" +
                NumberFormat.getInstance(Locale.US).format(numberOfNonEmptyCells) + "\t" +
                NumberFormat.getInstance(Locale.US).format(numberOfEmptyCells) + "\t" +
//                conversionTimeNano;
                NumberFormat.getInstance(Locale.US).format(conversionTimeNano);
    }
}
