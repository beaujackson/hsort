/*
* $Header$
* $Revision 1 $
* $Author: wjackson $
* &copy; $Date: 11/4/13$ Expedia Inc. PROPRIETARY AND CONFIDENTIAL
*/

package hsort.summarizers;

import java.util.HashMap;

public class CountSummarizer {
    private static int LEN = 16;

    private static HashMap<String, CountSummarizer> summarizers = new HashMap<>();

    public long total = 0;

    public CountSummarizer() {
    }

    public static CountSummarizer getInstance(String key) {
        if (!summarizers.containsKey(key)) {
            summarizers.put(key, new CountSummarizer());
        }

        return summarizers.get(key);
    }

    public static void printSummary() {

        for (String key : summarizers.keySet()) {
            getInstance(key).printSummary(key);
        }
    }

    private void printSummary(String key) {
        System.out.println("key: " + key);
        System.out.println("count: " + total);
    }
}
