/*
* $Header$
* $Revision 1 $
* $Author: wjackson $
* &copy; $Date: 10/11/13$ Expedia Inc. PROPRIETARY AND CONFIDENTIAL
*/

package hsort.analyzers;

import hsort.analysis.SearchSet;
import hsort.containers.HotelDataContainer;
import hsort.containers.NumericAnalysis;
import hsort.summarizers.PriceSummarizer;

public class PriceAnalyzer {

    public static void analyze(SearchSet searchSet) {

        NumericAnalysis price = searchSet.numericAnalyses.get("price");

        for (HotelDataContainer hotel : searchSet.getRows()) {

            if (hotel.price_usd < price.q1) {
                hotel.priceQuartile = 1;
            } else if (hotel.price_usd >= price.q1 && hotel.price_usd < price.q2) {
                hotel.priceQuartile = 2;
            } else if (hotel.price_usd >= price.q2 && hotel.price_usd < price.q3) {
                hotel.priceQuartile = 3;
            } else if (hotel.price_usd >= price.q3) {
                hotel.priceQuartile = 4;
            }

            if (hotel.booking_bool) {
                if (hotel.price_usd <= 100) {
                    summarize(PriceSummarizer.getInstance(PriceSummarizer.Range.UNDER100), hotel);
                } else if (hotel.price_usd > 100 && hotel.price_usd <= 200) {
                    summarize(PriceSummarizer.getInstance(PriceSummarizer.Range.BETWEEN100200), hotel);
                } else if (hotel.price_usd > 200 && hotel.price_usd <= 300) {
                    summarize(PriceSummarizer.getInstance(PriceSummarizer.Range.BETWEEN200300), hotel);
                } else if (hotel.price_usd > 300 && hotel.price_usd <= 400) {
                    summarize(PriceSummarizer.getInstance(PriceSummarizer.Range.BETWEEN300400), hotel);
                } else if (hotel.price_usd > 400 && hotel.price_usd <= 500) {
                    summarize(PriceSummarizer.getInstance(PriceSummarizer.Range.BETWEEN400500), hotel);
                } else if (hotel.price_usd > 500) {
                    summarize(PriceSummarizer.getInstance(PriceSummarizer.Range.OVER500), hotel);
                }
            }
        }
    }

    private static void summarize(PriceSummarizer summarizer, HotelDataContainer hotel) {
         summarizer.totalSearches++;
    }
}
