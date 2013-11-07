/*
* $Header$
* $Revision 1 $
* $Author: wjackson $
* &copy; $Date: 11/4/13$ Expedia Inc. PROPRIETARY AND CONFIDENTIAL
*/

package hsort.summarizers;

import hsort.App;
import hsort.containers.NumericAnalysis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class CountSummarizer {
    private static int LEN = 16;

    private static HashMap<Integer, CountSummarizer> summarizers = new HashMap<>();

    public long total = 0;

    public CountSummarizer() {
    }

    public static CountSummarizer getInstance(Integer key) {
        if (!summarizers.containsKey(key)) {
            summarizers.put(key, new CountSummarizer());
        }

        return summarizers.get(key);
    }

    public static NumericAnalysis getAnalysis() {
        NumericAnalysis bookings = new NumericAnalysis();

        for (CountSummarizer summarizer: summarizers.values()) {
            bookings.setVal((double) summarizer.total);
        }

        return bookings;
    }

    public static void writeQ3List() {

        File propIdFile = new File(App.propIdFile);
        if (propIdFile.exists()) {
            propIdFile.delete();
        }

        try {
            propIdFile.createNewFile();

            BufferedWriter writer = new BufferedWriter(new FileWriter(propIdFile, true));

            for (Integer key : summarizers.keySet()) {
                writer.write(key + "," + getInstance(key).total);
                writer.newLine();
            }

            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void printAnalysis() {
        NumericAnalysis bookings = getAnalysis();

        System.out.println("Count Summarizer Analysis:");
        System.out.println("Low:  " + bookings.low);
        System.out.println("High: " + bookings.high);
        System.out.println("Avg:  " + bookings.avg);
        System.out.println("Q:    " + bookings.quartile);
        System.out.println("--");
    }

    public static void printSummary(NumericAnalysis analysis) {

        for (Integer key : summarizers.keySet()) {
            if (getInstance(key).total > analysis.q3) {
                getInstance(key).printSummary(key);
            }
        }
    }

    private void printSummary(Integer key) {
        System.out.println("key: " + key);
        System.out.println("count: " + total);
        System.out.println("--");
    }
}
