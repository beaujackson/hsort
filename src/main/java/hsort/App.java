package hsort;

import hsort.analysis.DataParser;
import hsort.summarizers.CountSummarizer;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * Main application.
 */
public class App
{
    private static String testFile = "/Users/wjackson/projects/hsort/data/test.csv";
    private static String trainingFile = "/Users/wjackson/projects/hsort/data/train.csv";
    public static String sortedSearchFile = "/Users/wjackson/projects/hsort/data/sorted.csv";
    public static String propIdFile = "/Users/wjackson/project/hsorts/data/prop_id.txt";

    private static HashMap<String, String> parseArgs(String[] args) {
        HashMap<String, String> parsed = new HashMap<>();

        for (int i=0; i<args.length - 1; i++) {
            String arg = args[i];

            switch (arg) {

                case "-r":
                case "-rows":
                    parsed.put("rows", args[++i]);
                    break;

                case "-m":
                case "-meta":
                    parsed.put("meta", "true");
                    break;

                case "-s":
                case "-sample":
                    parsed.put("sample", args[++i]);
                    break;

                case "-train":
                    parsed.put("dataFile", trainingFile);
                    parsed.put("train", "true");
                    break;

                case "-test":
                    parsed.put("dataFile", testFile);
                    parsed.put("test", "true");
                    break;

                case "-generate":
                    parsed.put("dataFile", trainingFile);
                    parsed.put("generate", "true");

                default:
                    break;
            }
        }

        return parsed;
    }

    private static void showSamples(List<String[]> vals) {

        int[] colWidths = new int[vals.get(0).length];
        for (int i=0; i<colWidths.length; i++) {
            colWidths[i] = 1;
        }

        for (String[] row : vals) {
            for (int i=0; i<row.length; i++) {
                if (colWidths[i] < row[i].length()) {
                    colWidths[i] = row[i].length() + 1;
                }
            }
        }

        for (String[] row : vals) {
            for (int i=0; i<row.length; i++) {
                System.out.print(padRight("|" + row[i], colWidths[i]));
            }

            System.out.println("|");
        }
    }

    public static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }

    public static void main( String[] args ) {

        HashMap<String, String> parsed = parseArgs(args);

        long rowCount = Long.MAX_VALUE;

        String rows = parsed.get("rows");
        if (rows != null) {
            rowCount = Long.parseLong(rows);
        }

        if (!parsed.containsKey("dataFile")) {
            parsed.put("dataFile", trainingFile);
        }

        try {
            DataParser dataParser = new DataParser(parsed.get("dataFile"));

            if (parsed.containsKey("generate")) {
                dataParser.generatePropIds();
                CountSummarizer.writeQ3List();
                return;
            }

            if (parsed.containsKey("sample")) {
                showSamples(dataParser.getSampleRows(Integer.parseInt(parsed.get("sample"))));
                return;
            }

            if (parsed.containsKey("train")) {
                System.out.print("Training");
                dataParser.train(rowCount);
            }

            if (parsed.containsKey("test")) {
                System.out.print("Testing");
                File sortedSearches = new File(sortedSearchFile);
                if (sortedSearches.exists()) {
                    sortedSearches.delete();
                }
                sortedSearches.createNewFile();
                dataParser.test(rowCount);
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
