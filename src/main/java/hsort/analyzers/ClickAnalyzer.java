/*
* $Header$
* $Revision 1 $
* $Author: wjackson $
* &copy; $Date: 11/4/13$ Expedia Inc. PROPRIETARY AND CONFIDENTIAL
*/

package hsort.analyzers;

import hsort.analysis.SearchSet;
import hsort.containers.HotelDataContainer;

public class ClickAnalyzer {

    public static void analyze(SearchSet searchSet) {
        int clickOnly = 0;
        int book = 0;

        for (HotelDataContainer hotel : searchSet.getRows()) {
            if (hotel.booking_bool) {
                book++;
            } else if (hotel.click_bool) {
                clickOnly++;
            }
        }

//        if (book == 0) {
//            if (clickOnly == 0) {
//                CountSummarizer.getInstance("No Click or Book").total++;
//            }
//
//            if (clickOnly == 1) {
//                CountSummarizer.getInstance("One click only").total++;
//            }
//
//            if (clickOnly > 1) {
//                CountSummarizer.getInstance("Multi-click no book").total++;
//            }
//        } else {
//            if (clickOnly == 0) {
//                CountSummarizer.getInstance("Book no click").total++;
//            }
//
//            if (clickOnly == 1) {
//                CountSummarizer.getInstance("Book plus one click").total++;
//            }
//
//            if (clickOnly > 1) {
//                CountSummarizer.getInstance("Book plus multi-click").total++;
//            }
//        }
    }
}
