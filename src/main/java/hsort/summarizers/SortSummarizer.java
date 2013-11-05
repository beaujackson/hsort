/*
* $Header$
* $Revision 1 $
* $Author: wjackson $
* &copy; $Date: 10/22/13$ Expedia Inc. PROPRIETARY AND CONFIDENTIAL
*/

package hsort.summarizers;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class SortSummarizer {

    private static int LEN = 12;
    private static HashMap<String, SortSummarizer> summarizers = new HashMap<>();

    public HashMap<String, Double> percents = new HashMap<>();
    public long totalSearches = 0;
    public long priceWins = 0;
    public long distanceWins = 0;
    public long starsWins = 0;
    public long reviewWins = 0;
    public long locationScore1Wins = 0;
    public long locationScore2Wins = 0;

    //training only
    public long noBooking = 0;
    public long noClick = 0;
    public long expediaPositionWins = 0;
    public long d1_wins = 0;
    public long d2_wins = 0;

    private SortSummarizer() {
    }

    public static SortSummarizer getInstance(String key) {
        if (!summarizers.containsKey(key)) {
            summarizers.put(key, new SortSummarizer());
        }

        return summarizers.get(key);
    }

    public static void printSummary() {

        getInstance("Total").printSummary("Total");

        List<String> keys = new ArrayList<>();

        for (String key : summarizers.keySet()) {
            if (!"Total".equals(key)) {
                keys.add(key);
            }
        }

        Collections.sort(keys, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });

        for (String key: keys) {
            getInstance(key).printSummary(key);
        }
    }

    private void printSummary(String instanceLabel) {
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();
        StringBuilder percents = new StringBuilder();
        StringBuilder change = new StringBuilder();

        addSummary(columns, values, percents, change, instanceLabel, totalSearches, false);
        addSummary(columns, values, percents, change, "Bookings", totalSearches - noBooking, false);
        addSummary(columns, values, percents, change, "Click Only", noBooking, false);

        addSummary(columns, values, percents, change, "Price", priceWins);
        addSummary(columns, values, percents, change, "Distance", distanceWins);
        addSummary(columns, values, percents, change, "Stars", starsWins);
        addSummary(columns, values, percents, change, "Review", reviewWins);
        addSummary(columns, values, percents, change, "Loc Score1", locationScore1Wins);
        addSummary(columns, values, percents, change, "Loc Score2", locationScore2Wins);
        addSummary(columns, values, percents, change, "Exp. Pos", expediaPositionWins);
        addSummary(columns, values, percents, change, "Beau D1", d1_wins);
        addSummary(columns, values, percents, change, "Beau D2", d2_wins);

        columns.append("|");
        values.append("|");
        percents.append("|");
        change.append("|");

        System.out.println(columns.toString());
        System.out.println(values.toString());
        System.out.println(percents.toString());
        System.out.println(change.toString());
        System.out.println("");
    }

    private void addSummary(StringBuilder columns, StringBuilder values, StringBuilder percents,
                            StringBuilder change, String col, double val) {

        addSummary(columns, values, percents, change, col, val, true);
    }

    private void addSummary(StringBuilder columns, StringBuilder values, StringBuilder percents,
                            StringBuilder change, String col, double val, boolean calcChange) {

        columns.append(padRight("|" + col, LEN));

        values.append(padRight("|" + val, LEN));

        double perct = val / totalSearches;
        this.percents.put(col, perct);

        NumberFormat percentFormat = NumberFormat.getPercentInstance();
        percentFormat.setMaximumFractionDigits(2);

        String result = percentFormat.format(perct);
        percents.append(padRight("|" + result, LEN));

        if (calcChange) {
            result = percentFormat.format((getInstance("Total").percents.get(col) - perct) * -1);
            change.append(padRight("|" + result, LEN));
        } else {
            change.append(padRight("|", LEN));
        }
    }

    private String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }
}
