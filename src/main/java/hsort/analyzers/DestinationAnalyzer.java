/*
* $Header$
* $Revision 1 $
* $Author: wjackson $
* &copy; $Date: 10/11/13$ Expedia Inc. PROPRIETARY AND CONFIDENTIAL
*/

package hsort.analyzers;

import hsort.analysis.SearchSet;
import hsort.summarizers.DestinationSummarizer;

public class DestinationAnalyzer {

    public static void analyze(SearchSet searchSet) {

        DestinationSummarizer summarizer = DestinationSummarizer.getInstance(searchSet.destinationId);
        summarizer.totalSearches++;

        if (searchSet.saturdayStay) {
            summarizer.saturdayStays++;
        }

        if (searchSet.adultsCount == 1 && searchSet.childCount == 0) {
            summarizer.soloAdult++;
        }

        if (searchSet.adultsCount == 2 && searchSet.childCount == 0) {
            summarizer.twoAdults++;
        }

        if (searchSet.adultsCount >= 3 && searchSet.childCount == 0) {
            summarizer.threeOrMoreAdults++;
        }

        if (searchSet.adultsCount > 0 && searchSet.childCount > 0) {
            summarizer.withKids++;
        }

        if (searchSet.roomCount > 1) {
            summarizer.multipleRooms++;
        }
    }
}
