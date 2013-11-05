/*
* $Header$
* $Revision 1 $
* $Author: wjackson $
* &copy; $Date: 10/22/13$ Expedia Inc. PROPRIETARY AND CONFIDENTIAL
*/

package hsort.summarizers;

import java.util.HashMap;

public class PriceSummarizer {

    public enum Range {
        UNDER100,
        BETWEEN100200,
        BETWEEN200300,
        BETWEEN300400,
        BETWEEN400500,
        OVER500;
    }

    private static int LEN = 11;
    private static HashMap<Range, PriceSummarizer> summarizers = new HashMap<>();

    public long totalSearches;

    private PriceSummarizer() {
    }

    public static PriceSummarizer getInstance(Range key) {
        if (!summarizers.containsKey(key)) {
            summarizers.put(key, new PriceSummarizer());
        }

        return summarizers.get(key);
    }

    public static void printSummary() {

        getInstance(Range.UNDER100).printSummary("Under 100");
        getInstance(Range.BETWEEN100200).printSummary("100 - 200");
        getInstance(Range.BETWEEN200300).printSummary("200 - 300");
        getInstance(Range.BETWEEN300400).printSummary("300 - 400");
        getInstance(Range.BETWEEN400500).printSummary("400 - 500");
        getInstance(Range.OVER500).printSummary("Over 500");
    }

    private void printSummary(String rangeLabel) {
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();

        addSummary(columns, values, rangeLabel, totalSearches);
//        addSummary(columns, values, "Saturdays", saturdayStays);
//        addSummary(columns, values, "Solo Adult", soloAdult);
//        addSummary(columns, values, "Two Adults", twoAdults);
//        addSummary(columns, values, "3+ Adults", threeOrMoreAdults);
//        addSummary(columns, values, "With Kids", withKids);
//        addSummary(columns, values, "2+ Rooms", multipleRooms);

        columns.append("|");
        values.append("|");

        System.out.println(columns.toString());
        System.out.println(values.toString());
        System.out.println("");
    }

    private void addSummary(StringBuilder columns, StringBuilder values, String col, long val) {

        columns.append(padRight("|" + col, LEN));

        values.append(padRight("|" + val, LEN));
    }

    private String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }
}
