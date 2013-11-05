/*
* $Header$
* $Revision 1 $
* $Author: wjackson $
* &copy; $Date: 10/22/13$ Expedia Inc. PROPRIETARY AND CONFIDENTIAL
*/

package hsort.summarizers;

import java.util.HashMap;

public class DestinationSummarizer {
    private static int LEN = 16;

    private static HashMap<Integer, DestinationSummarizer> summarizers = new HashMap<>();

    public long totalSearches = 0;
    public long saturdayStays = 0;
    public long soloAdult = 0;
    public long twoAdults = 0;
    public long threeOrMoreAdults = 0;
    public long withKids = 0;
    public long multipleRooms = 0;

    private DestinationSummarizer() {
    }

    public static DestinationSummarizer getInstance(Integer key) {
        if (!summarizers.containsKey(key)) {
            summarizers.put(key, new DestinationSummarizer());
        }

        return summarizers.get(key);
    }

    public static void printSummary(double threshold) {

        for (Integer key : summarizers.keySet()) {
            DestinationSummarizer summarizer = getInstance(key);

            if (summarizer.totalSearches >= threshold) {
                getInstance(key).printSummary(key);
            }
        }
    }

    private void printSummary(Integer destinationId) {
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();

        addSummary(columns, values, "Destination ID", destinationId);
        addSummary(columns, values, "Total", totalSearches);
        addSummary(columns, values, "Saturdays", saturdayStays);
        addSummary(columns, values, "Solo Adult", soloAdult);
        addSummary(columns, values, "Two Adults", twoAdults);
        addSummary(columns, values, "3+ Adults", threeOrMoreAdults);
        addSummary(columns, values, "With Kids", withKids);
        addSummary(columns, values, "2+ Rooms", multipleRooms);

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
