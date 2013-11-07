/*
* $Header$
* $Revision 1 $
* $Author: wjackson $
* &copy; $Date: 10/22/13$ Expedia Inc. PROPRIETARY AND CONFIDENTIAL
*/

package hsort.analyzers;

import hsort.analysis.CompareDouble;
import hsort.analysis.CompareInteger;
import hsort.analysis.SearchSet;
import hsort.containers.HotelDataContainer;
import hsort.containers.NumericAnalysis;
import hsort.summarizers.SortSummarizer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortAnalyzer {

    private static final List<Integer> destinations = Arrays.asList(4562, 8192, 8347, 9402, 10979, 13292);
    private static final List<Integer> sites = Arrays.asList(5, 14, 15, 24, 32);
    private static final List<Integer> countries = Arrays.asList(219, 100, 55);

    private static List<SortSummarizer> getSummarizers(SearchSet searchSet) {
        List<SortSummarizer> summarizers = new ArrayList<>();
        summarizers.add(SortSummarizer.getInstance("Total"));

//        if (searchSet.hasBooking) {
//            summarizers.add(SortSummarizer.getInstance("Bookings"));
//        } else if (!searchSet.hasBooking && searchSet.hasClick) {
//            summarizers.add(SortSummarizer.getInstance("ClickOnly"));
//        }

//        if (searchSet.numericAnalyses.get("loc2") == null) {
//            summarizers.add(SortSummarizer.getInstance("NoLoc2"));
//            CountSummarizer.getInstance("NoLoc2").total++;
//        }

        //if (searchSet.saturdayStay)
            //getDetailedSummarizers(summarizers, searchSet);

        return summarizers;
    }

    private static List<SortSummarizer> getDetailedSummarizers(List<SortSummarizer> summarizers, SearchSet searchSet) {

        if (countries.contains(searchSet.visitorCountryId)) {
            String key = "Country-" + searchSet.visitorCountryId;
            summarizers.add(SortSummarizer.getInstance(key));
        }

        NumericAnalysis distance = searchSet.numericAnalyses.get("distance");
        if (distance != null && distance.avg > 4000 && distance.avg <= 5000) {
            summarizers.add(SortSummarizer.getInstance("Far 4-5k"));
        }
        if (distance != null && distance.avg > 5000 && distance.avg <= 6000) {
            summarizers.add(SortSummarizer.getInstance("Far 5-6k"));
        }
        if (distance != null && distance.avg > 6000 && distance.avg <= 7000) {
            summarizers.add(SortSummarizer.getInstance("Far 6-7k"));
        }
        if (distance != null && distance.avg > 7000 && distance.avg <= 8000) {
            summarizers.add(SortSummarizer.getInstance("Far 7-8k"));
        }
        if (distance != null && distance.avg > 8000 && distance.avg <= 9000) {
            summarizers.add(SortSummarizer.getInstance("Far 8-9k"));
        }
        if (distance != null && distance.avg > 9000 && distance.avg <= 10000) {
            summarizers.add(SortSummarizer.getInstance("Far 9-10k"));
        }

        DecimalFormat df = new DecimalFormat("#00");
        Calendar c = Calendar.getInstance();
        c.setTime(searchSet.dateTime);
        c.add(Calendar.DATE, searchSet.bookingWindow);
        String monthKey = "Month-" + df.format(c.get(Calendar.MONTH));
        summarizers.add(SortSummarizer.getInstance(monthKey));

        if (searchSet.bookingWindow < 10) {
            summarizers.add(SortSummarizer.getInstance("Book-" + searchSet.bookingWindow));
        } else if (searchSet.bookingWindow >= 10 & searchSet.bookingWindow < 30) {
            summarizers.add(SortSummarizer.getInstance("Book-mid"));
        } else if (searchSet.bookingWindow >= 30) {
            summarizers.add(SortSummarizer.getInstance("Book-long"));
        }

        if (searchSet.saturdayStay) {
            summarizers.add(SortSummarizer.getInstance("Saturday"));
        }

        if (searchSet.adultsCount == 1 && searchSet.childCount == 0) {
            summarizers.add(SortSummarizer.getInstance("Solo Adult"));
        }

        if (searchSet.adultsCount == 2 && searchSet.childCount == 0) {
            summarizers.add(SortSummarizer.getInstance("Two Adults"));
        }

        if (searchSet.childCount > 0) {
            summarizers.add(SortSummarizer.getInstance("With Kids"));
        }

        if (destinations.contains(searchSet.destinationId)) {
            String key = "Dest " + searchSet.destinationId;
            summarizers.add(SortSummarizer.getInstance(key));
        }

        if (sites.contains(searchSet.siteId)) {
            String key = "Site " + searchSet.siteId;
            summarizers.add(SortSummarizer.getInstance(key));
        }

        double avgPrice = searchSet.numericAnalyses.get("price").avg;
        if (avgPrice <= 100) {
            summarizers.add(SortSummarizer.getInstance("100 -"));
        } else if (avgPrice > 100 && avgPrice <= 200) {
            summarizers.add(SortSummarizer.getInstance("100 - 200"));
        } else if (avgPrice > 200 && avgPrice <= 300) {
            summarizers.add(SortSummarizer.getInstance("200 - 300"));
        } else if (avgPrice > 300 && avgPrice <= 400) {
            summarizers.add(SortSummarizer.getInstance("300 - 400"));
        } else if (avgPrice > 400 && avgPrice <= 500) {
            summarizers.add(SortSummarizer.getInstance("400 - 500"));
        } else if (avgPrice > 500) {
            summarizers.add(SortSummarizer.getInstance("500 +"));
        }

        return summarizers;
    }

    public static void analyze(SearchSet searchSet) {

        List<HotelDataContainer> rows = searchSet.getRows();

        List<SortSummarizer> summarizers = getSummarizers(searchSet);

        for (SortSummarizer summarizer : summarizers)
            summarizer.totalSearches++;

        if (!searchSet.hasBooking) {
            for (SortSummarizer summarizer : summarizers)
                summarizer.noBooking++;
        }

        //sort by price
        Collections.sort(rows, new Comparator<HotelDataContainer>() {
            @Override
            public int compare(HotelDataContainer o1, HotelDataContainer o2) {
                return CompareDouble.compare(o1.price_usd, o2.price_usd);
            }
        });
        if (topThreeBooking(rows)) {
            for (SortSummarizer summarizer : summarizers)
                summarizer.priceWins++;
        }

        //sort by distance
        Collections.sort(rows, new Comparator<HotelDataContainer>() {
            @Override
            public int compare(HotelDataContainer o1, HotelDataContainer o2) {
                return CompareDouble.compare(o1.orig_destination_distance, o2.orig_destination_distance);
            }
        });
        if (topThreeBooking(rows)) {
            for (SortSummarizer summarizer : summarizers)
                summarizer.distanceWins++;
        }

        //sort by stars
        Collections.sort(rows, new Comparator<HotelDataContainer>() {
            @Override
            public int compare(HotelDataContainer o1, HotelDataContainer o2) {
                return CompareDouble.reverseCompare(o1.prop_starrating, o2.prop_starrating);
            }
        });
        if (topThreeBooking(rows)) {
            for (SortSummarizer summarizer : summarizers)
                summarizer.starsWins++;
        }

        //sort by reviews
        Collections.sort(rows, new Comparator<HotelDataContainer>() {
            @Override
            public int compare(HotelDataContainer o1, HotelDataContainer o2) {
                return CompareDouble.reverseCompare(o1.prop_review_score, o2.prop_review_score);
            }
        });
        if (topThreeBooking(rows)) {
            for (SortSummarizer summarizer : summarizers)
                summarizer.reviewWins++;
        }

        //sort by locationScore1
        Collections.sort(rows, new Comparator<HotelDataContainer>() {
            @Override
            public int compare(HotelDataContainer o1, HotelDataContainer o2) {
                return CompareDouble.reverseCompare(o1.prop_location_score1, o2.prop_location_score1);
            }
        });
        if (topThreeBooking(rows)) {
            for (SortSummarizer summarizer : summarizers)
                summarizer.locationScore1Wins++;
        }

        //sort by locationScore2
        Collections.sort(rows, new Comparator<HotelDataContainer>() {
            @Override
            public int compare(HotelDataContainer o1, HotelDataContainer o2) {
                return CompareDouble.reverseCompare(o1.prop_location_score2, o2.prop_location_score2);
            }
        });
        if (topThreeBooking(rows)) {
            for (SortSummarizer summarizer : summarizers)
                summarizer.locationScore2Wins++;
        }

        //sort by expedia position
        Collections.sort(rows, new Comparator<HotelDataContainer>() {
            @Override
            public int compare(HotelDataContainer o1, HotelDataContainer o2) {
                return CompareInteger.compare(o1.position, o2.position);
            }
        });
        if (topThreeBooking(rows)) {
            for (SortSummarizer summarizer : summarizers)
                summarizer.expediaPositionWins++;
        }

        //sort by d1_ranking
        Collections.sort(rows, new Comparator<HotelDataContainer>() {
            @Override
            public int compare(HotelDataContainer o1, HotelDataContainer o2) {
                return CompareDouble.reverseCompare(o1.d1_ranking, o2.d1_ranking);
            }
        });
        if (topThreeBooking(rows)) {
            for (SortSummarizer summarizer : summarizers) {
                summarizer.d1_wins++;
            }
        }

        //sort by d2_ranking
        Collections.sort(rows, new Comparator<HotelDataContainer>() {
            @Override
            public int compare(HotelDataContainer o1, HotelDataContainer o2) {
                return CompareDouble.reverseCompare(o1.d2_ranking, o2.d2_ranking);
            }
        });
        if (topThreeBooking(rows)) {
            for (SortSummarizer summarizer : summarizers) {
                summarizer.d2_wins++;
            }
        }
    }

    public static boolean topThreeBooking(List<HotelDataContainer> rows) {

        return rows.get(0).booking_bool || rows.get(1).booking_bool || rows.get(2).booking_bool
            || rows.get(0).click_bool || rows.get(1).click_bool || rows.get(2).click_bool;

    }
}
