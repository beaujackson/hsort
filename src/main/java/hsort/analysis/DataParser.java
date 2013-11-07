/*
 * $Header$
 * $Revision 1 $
 * $Author: wjackson $
 * &copy; $Date: 10/10/13$ Expedia Inc. PROPRIETARY AND CONFIDENTIAL
 */

package hsort.analysis;

import hsort.App;
import hsort.summarizers.CountSummarizer;
import hsort.summarizers.SortSummarizer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser {

    private String dataFilePath;

    public DataParser(String dataFilePath) {
        this.dataFilePath = dataFilePath;
    }

    public List<String[]> getSampleRows(int searchCount) {

        List<String[]> sampleRows = new ArrayList<String[]>();

        try {
            BufferedReader reader = getReader();
            String[] keys = reader.readLine().split(",");

            String line = null;
            String lastSearchId = "";

            while ((line = reader.readLine()) != null) {

                String[] vals = line.split(",");

                if (!lastSearchId.equals(vals[0])) {
                    searchCount--;

                    if (searchCount < 0) {
                        break;
                    }

                    sampleRows.add(keys);
                    lastSearchId = vals[0];
                }

                sampleRows.add(vals);
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sampleRows;
    }

    public void generatePropIds() {

        try {
            BufferedReader reader = getReader();

            HashMap<String, Integer> keyIndices = new HashMap<>();
            String[] keys = reader.readLine().split(",");
            for (int i=0; i<keys.length; i++) {
                keyIndices.put(keys[i], i);
            }

            Integer bookingIndex = keyIndices.get("booking_bool");
            Integer propIdIndex = keyIndices.get("prop_id");

            String line = reader.readLine();

            while (line != null) {
                String[] vals = line.split(",");
                String bookingVal = vals[bookingIndex];

                if ("1".equals(bookingVal)) {
                    Integer prop_id = Integer.parseInt(vals[propIdIndex]);
                    CountSummarizer.getInstance(prop_id).total++;
                }

                line = reader.readLine();
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void train(long searchCount) {

        double halfPercent = searchCount * .005;
        double onePercent = searchCount * .01;
        double threePercent = searchCount * .03;

        try {
            BufferedReader reader = getReader();

            HashMap<String, Integer> keyIndices = new HashMap<>();
            String[] keys = reader.readLine().split(",");
            for (int i=0; i<keys.length; i++) {
                keyIndices.put(keys[i], i);
            }

            String line = reader.readLine();
            String[] vals = line.split(",");
            String lastSearchId = vals[0];

            while (searchCount >= 0 && line != null) {

                //put together a search set
                SearchSet currentSearch = new SearchSet(true, keyIndices);

                while (lastSearchId.equals(vals[0])) {
                    currentSearch.addRow(vals);

                    line = reader.readLine();

                    if (line == null) {
                        break;
                    }

                    vals = line.split(",");
                }

                //do some analysis
                currentSearch.analyze();

                //do some ranking
                currentSearch.rank();

                //train the search set
                currentSearch.train();

                lastSearchId = vals[0];
                searchCount--;
                if ((searchCount % 10000) == 0) {
                    System.out.print(".");
                }
            }

            System.out.println();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        System.out.println("- Factor Summaries ----------------------------------------------------------------------");
//        FactorSummarizer.printSummary(halfPercent);

//        System.out.println("- Visitor Country Summaries ( >= 3% ) ---------------------------------------------------");
//        VisitorCountrySummarizer.printSummary(threePercent);

        System.out.println("- Sort Summaries ------------------------------------------------------------------------");
        SortSummarizer.printSummary();

//        System.out.println("- Destination Summaries ( >= .5% ) ------------------------------------------------------");
//        DestinationSummarizer.printSummary(halfPercent);
//
//        System.out.println("- Price Summaries -----------------------------------------------------------------------");
//        PriceSummarizer.printSummary();
//
//        System.out.println("- Booking Summaries ---------------------------------------------------------------------");
//        BookingSummarizer.printSummary();
//
//        System.out.println("- Site Summaries ( >= 3% ) --------------------------------------------------------------");
//        SiteSummarizer.printSummary(threePercent);
    }

    public void test(long searchCount) {

        //initialize the sorted file
        File sortedSearches = new File(App.sortedSearchFile);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(sortedSearches, true));

            writer.write("SearchId,PropertyId");
            writer.newLine();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            BufferedReader reader = getReader();

            HashMap<String, Integer> keyIndices = new HashMap<>();
            String[] keys = reader.readLine().split(",");
            for (int i=0; i<keys.length; i++) {
                keyIndices.put(keys[i], i);
            }

            String line = reader.readLine();
            String[] vals = line.split(",");
            String lastSearchId = vals[0];

            while (searchCount >= 0 && line != null) {

                //put together a search set
                SearchSet currentSearch = new SearchSet(false, keyIndices);

                while (lastSearchId.equals(vals[0])) {
                    currentSearch.addRow(vals);

                    line = reader.readLine();

                    if (line == null) {
                        break;
                    }

                    vals = line.split(",");
                }

                //do some analysis
                currentSearch.analyze();

                //do some ranking
                currentSearch.rank();

                //sort the search set
                currentSearch.sort();

                lastSearchId = vals[0];
                searchCount--;

                if ((searchCount % 10000) == 0) {
                    System.out.print(".");
                }
            }

            System.out.println();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BufferedReader getReader() throws IOException {
        File testData = new File(dataFilePath);
        return new BufferedReader(new FileReader(testData));
    }
}
