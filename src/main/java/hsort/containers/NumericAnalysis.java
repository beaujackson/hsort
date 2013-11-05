/*
* $Header$
* $Revision 1 $
* $Author: wjackson $
* &copy; $Date: 10/29/13$ Expedia Inc. PROPRIETARY AND CONFIDENTIAL
*/

package hsort.containers;

public class NumericAnalysis {
    public double high = 0;
    public double low = Double.MAX_VALUE;
    public int count = 0;
    public double avg;
    private double runningTotal = 0;
    public double quartile;
    public double q1 = 0;
    public double q2 = 0;
    public double q3 = 0;

    public void setVal(Double value) {
        count++;

        if (value == null) {
            return;
        }

        runningTotal += value;

        if(value > high) {
            high = value;
        }

        if (value < low) {
            low = value;
        }

        avg = runningTotal / count;

        quartile = (high - low) / 4;

        q1 = low + quartile;
        q2 = low + (quartile * 2);
        q3 = low + (quartile * 3);
    }
}
