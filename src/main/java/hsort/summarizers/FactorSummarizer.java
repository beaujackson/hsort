/*
* $Header$
* $Revision 1 $
* $Author: wjackson $
* &copy; $Date: 10/29/13$ Expedia Inc. PROPRIETARY AND CONFIDENTIAL
*/

package hsort.summarizers;

import hsort.analysis.CompareDouble;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class FactorSummarizer {

    private static HashMap<String, FactorSummarizer> summarizers = new HashMap<>();

    public String key = null;
    public Double totalSearches = 0.0;

    private FactorSummarizer() {
    }

    public static FactorSummarizer getInstance(String key) {
        if (!summarizers.containsKey(key)) {
            FactorSummarizer summarizer = new FactorSummarizer();
            summarizer.key = key;
            summarizers.put(key, summarizer);
        }

        return summarizers.get(key);
    }

    public static void printSummary(double threshold) {

        List<FactorSummarizer> keys = new ArrayList<>();

        for (FactorSummarizer summarizer : summarizers.values()) {
            keys.add(summarizer);
        }

        Collections.sort(keys, new Comparator<FactorSummarizer>() {
            @Override
            public int compare(FactorSummarizer o1, FactorSummarizer o2) {
                return CompareDouble.reverseCompare(o1.totalSearches, o2.totalSearches);
            }
        });

        for (FactorSummarizer summarizer : keys) {
            if (summarizer.totalSearches >= threshold) {
                summarizer.printSummary();
            }
        }
    }

    private void printSummary() {
        System.out.println("key: " + key);
        System.out.println("count: " + totalSearches);

        double perct = totalSearches / FactorSummarizer.getInstance("|booking|").totalSearches;

        NumberFormat percentFormat = NumberFormat.getPercentInstance();
        percentFormat.setMaximumFractionDigits(2);

        String result = percentFormat.format(perct);

        System.out.println("percent: " + result);
        System.out.println();
    }
}

