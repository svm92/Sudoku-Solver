package sudokusolver;

import org.jfree.data.xy.*;

public class Plotter {
    public static XYDataset createDataset(DB.DBDataSet dataSet) {
        if (dataSet == null) return null;
        
        // Convert data to valid format (seconds, double-type)
        double[] y1 = new double[dataSet.backtrackTimes.size()];
        for(int i = 0; i < dataSet.backtrackTimes.size(); i++) 
            y1[i] = dataSet.backtrackTimes.get(i) / 1000000000d;
        
        double[] y2 = new double[dataSet.consPropTimes.size()];
        for(int i = 0; i < dataSet.consPropTimes.size(); i++) 
            y2[i] = dataSet.consPropTimes.get(i) / 1000000000d;
        
        double[] x = new double [dataSet.backtrackTimes.size()];
        for (int i = 0; i < dataSet.backtrackTimes.size(); i++) {x[i] = i;}
        
        double[][] data1 = { x, y1 };
        double[][] data2 = { x, y2 };
        
        // Create XYDataSet from this data
        DefaultXYDataset XYDataSet = new DefaultXYDataset();
        XYDataSet.addSeries("Backtrack", data1);
        XYDataSet.addSeries("Constraint Propagation", data2);
        
        return XYDataSet;
    }
}
