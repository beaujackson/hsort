/*
* $Header$
* $Revision 1 $
* $Author: wjackson $
* &copy; $Date: 10/23/13$ Expedia Inc. PROPRIETARY AND CONFIDENTIAL
*/

package hsort.analyzers;

import hsort.analysis.SearchSet;
import hsort.summarizers.BookingSummarizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BookingAnalyzer {

    private static final List<Integer> destinations = Arrays.asList(4562, 8192, 8347, 9402, 10979, 13292);

    private static List<BookingSummarizer> getSummarizers(SearchSet searchSet) {
        List<BookingSummarizer> summarizers = new ArrayList<>();
        summarizers.add(BookingSummarizer.getInstance("Total"));

        if (searchSet.saturdayStay) {
            summarizers.add(BookingSummarizer.getInstance("Saturday"));
        }

        if (searchSet.adultsCount == 1 && searchSet.childCount == 0) {
            summarizers.add(BookingSummarizer.getInstance("Solo Adult"));
        }

        if (searchSet.adultsCount == 2 && searchSet.adultsCount == 0) {
            summarizers.add(BookingSummarizer.getInstance("Two Adults"));
        }

        if (searchSet.childCount > 0) {
            summarizers.add(BookingSummarizer.getInstance("With Kids"));
        }

        if (destinations.contains(searchSet.destinationId)) {
            String key = "Dest " + searchSet.destinationId;
            summarizers.add(BookingSummarizer.getInstance(key));
        }

        double avgPrice = searchSet.numericAnalyses.get("price").avg;
        if (avgPrice <= 100) {
            summarizers.add(BookingSummarizer.getInstance("Under 100"));
        } else if (avgPrice > 100 && avgPrice <= 200) {
            summarizers.add(BookingSummarizer.getInstance("100 - 200"));
        } else if (avgPrice > 200 && avgPrice <= 300) {
            summarizers.add(BookingSummarizer.getInstance("200 - 300"));
        } else if (avgPrice > 300 && avgPrice <= 400) {
            summarizers.add(BookingSummarizer.getInstance("300 - 400"));
        } else if (avgPrice > 400 && avgPrice <= 500) {
            summarizers.add(BookingSummarizer.getInstance("400 - 500"));
        } else if (avgPrice > 500) {
            summarizers.add(BookingSummarizer.getInstance("Over 500"));
        }

        return summarizers;
    }

    public static void analyze(SearchSet searchSet) {

        if (searchSet.hasBooking) {
            List<BookingSummarizer> summarizers = getSummarizers(searchSet);

            for (BookingSummarizer summarizer : summarizers) {
                summarizer.totalBookings++;

                if (searchSet.branded) {
                    summarizer.branded++;
                }

                if (searchSet.promo) {
                    summarizer.promo++;
                }

                if (searchSet.promo && searchSet.branded) {
                    summarizer.brandedAndPromo++;
                }
            }
        }
    }
}