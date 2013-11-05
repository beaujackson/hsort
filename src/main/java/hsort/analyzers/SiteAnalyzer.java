/*
* $Header$
* $Revision 1 $
* $Author: wjackson $
* &copy; $Date: 10/23/13$ Expedia Inc. PROPRIETARY AND CONFIDENTIAL
*/

package hsort.analyzers;

import hsort.analysis.SearchSet;
import hsort.summarizers.SiteSummarizer;

public class SiteAnalyzer {

    public static void analyze(SearchSet searchSet) {

        SiteSummarizer summarizer = SiteSummarizer.getInstance(searchSet.siteId);
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
