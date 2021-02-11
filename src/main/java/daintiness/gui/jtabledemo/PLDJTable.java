package daintiness.gui.jtabledemo;

import javafx.collections.ObservableList;

import javax.swing.*;

import daintiness.clustering.Phase;
import daintiness.clustering.measurements.ChartGroupPhaseMeasurement;

import java.awt.*;

public class PLDJTable extends JPanel {

        public PLDJTable(ObservableList<ChartGroupPhaseMeasurement> observableList,
                         java.util.List<Phase> phases) {
            super(new GridLayout(1,0));

            JTable table = new JTable(new PLDJTableModel(observableList, phases));
            new Dimension(50,70);

        }
}
