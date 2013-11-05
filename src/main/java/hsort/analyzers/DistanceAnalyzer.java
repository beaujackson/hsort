/*
* $Header$
* $Revision 1 $
* $Author: wjackson $
* &copy; $Date: 10/29/13$ Expedia Inc. PROPRIETARY AND CONFIDENTIAL
*/

package hsort.analyzers;

import hsort.analysis.SearchSet;
import hsort.containers.HotelDataContainer;
import hsort.containers.NumericAnalysis;

public class DistanceAnalyzer {
    public static void analyze(SearchSet searchSet) {

        NumericAnalysis distance = searchSet.numericAnalyses.get("distance");
        if (distance == null || (distance.high == 0 || distance.low == Double.MAX_VALUE)) {
            return;
        }

        for (HotelDataContainer hotel : searchSet.getRows()) {

            if (hotel.orig_destination_distance == null) {
                continue;
            }

            if (hotel.orig_destination_distance < distance.q1) {
                hotel.distanceQuartile = 1;
            } else if (hotel.orig_destination_distance >= distance.q1 && hotel.orig_destination_distance < distance.q2) {
                hotel.distanceQuartile = 2;
            } else if (hotel.orig_destination_distance >= distance.q2 && hotel.orig_destination_distance < distance.q3) {
                hotel.distanceQuartile = 3;
            } else if (hotel.orig_destination_distance >= distance.q3) {
                hotel.distanceQuartile = 4;
            }
        }
    }
}
