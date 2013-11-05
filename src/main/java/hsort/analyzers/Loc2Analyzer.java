/*
* $Header$
* $Revision 1 $
* $Author: wjackson $
* &copy; $Date: 11/4/13$ Expedia Inc. PROPRIETARY AND CONFIDENTIAL
*/

package hsort.analyzers;

import hsort.analysis.SearchSet;
import hsort.containers.HotelDataContainer;
import hsort.containers.NumericAnalysis;

public class Loc2Analyzer {

    public static void analyze(SearchSet searchSet) {

        NumericAnalysis loc2 = searchSet.numericAnalyses.get("loc2");
        if (loc2 == null || (loc2.high == 0 || loc2.low == Double.MAX_VALUE)) {
            return;
        }

        for (HotelDataContainer hotel : searchSet.getRows()) {

            if (hotel.prop_location_score2 == null) {
                continue;
            }

            if (hotel.prop_location_score2 < loc2.q1) {
                hotel.loc2Quartile = 1;
            } else if (hotel.prop_location_score2 >= loc2.q1 && hotel.prop_location_score2 < loc2.q2) {
                hotel.loc2Quartile = 2;
            } else if (hotel.prop_location_score2 >= loc2.q2 && hotel.prop_location_score2 < loc2.q3) {
                hotel.loc2Quartile = 3;
            } else if (hotel.prop_location_score2 >= loc2.q3) {
                hotel.loc2Quartile = 4;
            }
        }
    }

}
