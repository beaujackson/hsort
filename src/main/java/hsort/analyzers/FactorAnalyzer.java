/*
* $Header$
* $Revision 1 $
* $Author: wjackson $
* &copy; $Date: 10/29/13$ Expedia Inc. PROPRIETARY AND CONFIDENTIAL
*/

package hsort.analyzers;

import hsort.analysis.CompareDouble;
import hsort.analysis.CompareInteger;
import hsort.analysis.SearchSet;
import hsort.containers.HotelDataContainer;
import hsort.summarizers.FactorSummarizer;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FactorAnalyzer {
    private static final List<Integer> destinations = Arrays.asList(4562, 8192, 8347, 9402, 10979, 13292);
    private static final List<Integer> sites = Arrays.asList(5, 14, 15, 24, 32);
    private static final List<Integer> countries = Arrays.asList(219, 100, 55);

    public static void analyze(SearchSet searchSet) {

        if (!searchSet.hasBooking) {
            FactorSummarizer.getInstance("|none|").totalSearches++;
            return;
        } else {
            FactorSummarizer.getInstance("|booking|").totalSearches++;
        }

        StringBuilder key = new StringBuilder();

        List<HotelDataContainer> rows = searchSet.getRows();

        //sort by price
        Collections.sort(rows, new Comparator<HotelDataContainer>() {
            @Override
            public int compare(HotelDataContainer o1, HotelDataContainer o2) {
                return CompareDouble.compare(o1.price_usd, o2.price_usd);
            }
        });
        if (sortHasBooking(rows)) {
            key.append("|price");
        }

        //sort by distance
//        Collections.sort(rows, new Comparator<HotelDataContainer>() {
//            @Override
//            public int compare(HotelDataContainer o1, HotelDataContainer o2) {
//                return CompareDouble.compare(o1.orig_destination_distance, o2.orig_destination_distance);
//            }
//        });
//        if (sortHasBooking(rows)) {
//            key.append("|distance");
//        }

        //sort by stars
        Collections.sort(rows, new Comparator<HotelDataContainer>() {
            @Override
            public int compare(HotelDataContainer o1, HotelDataContainer o2) {
                return CompareDouble.reverseCompare(o1.prop_starrating, o2.prop_starrating);
            }
        });
        if (sortHasBooking(rows)) {
            key.append("|stars");
        }

        //sort by reviews
        Collections.sort(rows, new Comparator<HotelDataContainer>() {
            @Override
            public int compare(HotelDataContainer o1, HotelDataContainer o2) {
                return CompareDouble.reverseCompare(o1.prop_review_score, o2.prop_review_score);
            }
        });
        if (sortHasBooking(rows)) {
            key.append("|reviews");
        }

        //sort by locationScore1
        Collections.sort(rows, new Comparator<HotelDataContainer>() {
            @Override
            public int compare(HotelDataContainer o1, HotelDataContainer o2) {
                return CompareDouble.reverseCompare(o1.prop_location_score1, o2.prop_location_score1);
            }
        });
        if (sortHasBooking(rows)) {
            key.append("|locScore1");
        }

        //sort by locationScore2
        Collections.sort(rows, new Comparator<HotelDataContainer>() {
            @Override
            public int compare(HotelDataContainer o1, HotelDataContainer o2) {
                return CompareDouble.reverseCompare(o1.prop_location_score2, o2.prop_location_score2);
            }
        });
        if (sortHasBooking(rows)) {
            key.append("|locScore2");
        }

        key.append("|");
        FactorSummarizer summarizer = FactorSummarizer.getInstance(key.toString());
        summarizer.totalSearches++;

        //sort by expedia position
        Collections.sort(rows, new Comparator<HotelDataContainer>() {
            @Override
            public int compare(HotelDataContainer o1, HotelDataContainer o2) {
                return CompareInteger.compare(o1.position, o2.position);
            }
        });
        if (sortHasBooking(rows)) {
            FactorSummarizer.getInstance("|expedia|").totalSearches++;
        }

        //sort by d1_ranking
        Collections.sort(rows, new Comparator<HotelDataContainer>() {
            @Override
            public int compare(HotelDataContainer o1, HotelDataContainer o2) {
                return CompareDouble.reverseCompare(o1.d1_ranking, o2.d1_ranking);
            }
        });
        if (sortHasBooking(rows)) {
            FactorSummarizer.getInstance("|beau|").totalSearches++;
        }

    }

    public static boolean sortHasBooking(List<HotelDataContainer> rows) {
        return rows.get(0).booking_bool || rows.get(1).booking_bool || rows.get(2).booking_bool;
    }
}
