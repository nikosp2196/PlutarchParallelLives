package daintiness.gui.jtabledemo;

import javafx.collections.ObservableList;

import javax.swing.table.AbstractTableModel;

import daintiness.clustering.Phase;
import daintiness.clustering.measurements.ChartGroupPhaseMeasurement;
import daintiness.models.measurement.EmptyIMeasurement;
import daintiness.models.measurement.IMeasurement;
import daintiness.utilities.Constants;

import java.util.List;

public class PLDJTableModel extends AbstractTableModel {

    private ObservableList<ChartGroupPhaseMeasurement> data;
    private List<Phase> phases;

    public PLDJTableModel(ObservableList<ChartGroupPhaseMeasurement> data,
                          List<Phase> phases) {
        this.data = data;
        this.phases = phases;
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return phases.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Phase phase = phases.get(columnIndex - 1);
        Constants.GPMType type = data.get(rowIndex).getEntityGroup().getGPMType(phase.getFirstPhaseBeat().getBeatId(), phase.getLastPhaseBeat().getBeatId());
        IMeasurement measurement;
        if (type.equals(Constants.GPMType.ACTIVE) && data.get(rowIndex).containsMeasurementInPhase(columnIndex - 1)) {
            measurement = data.get(rowIndex).getMeasurement(columnIndex - 1);
        } else {
            measurement = new EmptyIMeasurement(type);
        }
        return measurement;
    }
}
