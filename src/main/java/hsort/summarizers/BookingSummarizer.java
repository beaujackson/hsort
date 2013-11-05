/*
* $Header$
* $Revision 1 $
* $Author: wjackson $
* &copy; $Date: 10/23/13$ Expedia Inc. PROPRIETARY AND CONFIDENTIAL
*/

package hsort.summarizers;

import java.util.HashMap;

public class BookingSummarizer {

    private static int LEN = 11;
    private static HashMap<String, BookingSummarizer> summarizers = new HashMap<>();

    public long totalBookings = 0;
    public long branded = 0;
    public long promo = 0;
    public long brandedAndPromo = 0;

    public static BookingSummarizer getInstance(String key) {
        if (!summarizers.containsKey(key)) {
            summarizers.put(key, new BookingSummarizer());
        }

        return summarizers.get(key);
    }

    private BookingSummarizer() {
    }

    public static void printSummary() {

        for (String key : summarizers.keySet()) {
            getInstance(key).printSummary(key);
        }
    }

    private void printSummary(String label) {
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();

        addSummary(columns, values, label, totalBookings);
        addSummary(columns, values, "Branded", branded);
        addSummary(columns, values, "Promo", promo);
        addSummary(columns, values, "Both", brandedAndPromo);

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

