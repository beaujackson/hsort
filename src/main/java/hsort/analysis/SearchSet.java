/*
* $Header$
* $Revision 1 $
* $Author: wjackson $
* &copy; $Date: 10/11/13$ Expedia Inc. PROPRIETARY AND CONFIDENTIAL
*/

package hsort.analysis;

import hsort.App;
import hsort.analyzers.DistanceAnalyzer;
import hsort.analyzers.FactorAnalyzer;
import hsort.analyzers.Loc2Analyzer;
import hsort.analyzers.PriceAnalyzer;
import hsort.analyzers.SortAnalyzer;
import hsort.containers.HotelDataContainer;
import hsort.containers.NumericAnalysis;
import hsort.rankers.D1Ranker;
import hsort.rankers.D2Ranker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SearchSet {

    /*
srch_id
date_time
site_id
visitor_location_country_id
visitor_hist_starrating
visitor_hist_adr_usd
srch_destination_id
srch_length_of_stay
srch_booking_window
srch_adults_count
srch_children_count
srch_room_count
srch_saturday_night_bool
srch_query_affinity_score
     */

    public Integer srch_id;
    public Date dateTime; //|date_time
    public int travelMonth;
    public int siteId; //|site_id
    public int visitorCountryId; //|visitor_location_country_id
    public Float visitorAvgStars; //|visitor_hist_starrating
    public Float visitorAvgPrice; //|visitor_hist_adr_usd

    public int bookingIndex = -1;
    public int destinationId; //|srch_destination_id
    public int lengthOfStay; //|srch_length_of_stay
    public int bookingWindow; //|srch_booking_window
    public int adultsCount; //|srch_adults_count
    public int childCount; //|srch_children_count
    public int roomCount; //|srch_room_count
    public boolean saturdayStay; //|srch_saturday_night_bool
    public Float destinationDistance; //|orig_destination_distance
    public boolean randomSort; //|random_bool
    public boolean branded = false;
    public boolean promo = false;

    public double bookPrice = 0;

    public boolean training;
    private HashMap<String, Integer> keys;
    private List<HotelDataContainer> rows = new ArrayList<>();
    public HashMap<String, NumericAnalysis> numericAnalyses = new HashMap<>();

    public boolean hasBooking = false;
    public boolean hasClick = false;

    public SearchSet(boolean training, HashMap<String, Integer> keys) {
        this.training = training;
        this.keys = keys;
    }

    public List<HotelDataContainer> getRows() {
        return rows;
    }

    private void setNumericAnalysis(String key, Double value) {
        NumericAnalysis analysis = numericAnalyses.get(key);
        if (analysis == null) {
            analysis = new NumericAnalysis();
            numericAnalyses.put(key, analysis);
        }

        analysis.setVal(value);
    }

    public void addRow(String[] row) {

        if (rows.size() == 0) {
            initDecisionPoints(row);
        }

        HotelDataContainer hotel = new HotelDataContainer(training, keys, row);

        rows.add(hotel);

        if (hotel.booking_bool) {
            hasBooking = true;
            bookingIndex = rows.size() - 1;
            bookPrice = hotel.price_usd;
        }

        if (hotel.click_bool) {
            hasClick = true;
        }

        branded = hotel.prop_brand_bool;
        promo = hotel.promotion_flag;

        setNumericAnalysis("price", hotel.price_usd);

        if (hotel.orig_destination_distance != null) {
            setNumericAnalysis("distance", hotel.orig_destination_distance);
        }

        if (hotel.srch_query_affinity_score != null) {
            setNumericAnalysis("affinity", hotel.srch_query_affinity_score * -1);
        }

        if (hotel.prop_location_score2 != null) {
            setNumericAnalysis("loc2", hotel.prop_location_score2);
        }
    }

    private void initDecisionPoints(String[] row) {
        try {
            dateTime = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.ENGLISH).parse(row[keys.get("date_time")]);
        } catch (ParseException e) {
            dateTime = null;
            e.printStackTrace();
        }

        siteId = Integer.parseInt(row[keys.get("site_id")]);
        srch_id = Integer.parseInt(row[keys.get("srch_id")]);
        visitorCountryId = Integer.parseInt(row[keys.get("visitor_location_country_id")]);
        visitorAvgStars = parseFloat(row[keys.get("visitor_hist_starrating")]);
        visitorAvgPrice = parseFloat(row[keys.get("visitor_hist_adr_usd")]);

        destinationId = Integer.parseInt(row[keys.get("srch_destination_id")]);
        lengthOfStay = Integer.parseInt(row[keys.get("srch_length_of_stay")]);
        bookingWindow = Integer.parseInt(row[keys.get("srch_booking_window")]);
        adultsCount = Integer.parseInt(row[keys.get("srch_adults_count")]);
        childCount = Integer.parseInt(row[keys.get("srch_children_count")]);
        roomCount = Integer.parseInt(row[keys.get("srch_room_count")]);
        saturdayStay = Integer.parseInt(row[keys.get("srch_saturday_night_bool")]) == 1 ? true : false;
        destinationDistance = parseFloat(row[keys.get("orig_destination_distance")]);
        randomSort = Integer.parseInt(row[keys.get("random_bool")]) == 1 ? true : false;

        Calendar c = Calendar.getInstance();
        c.setTime(dateTime);
        c.add(Calendar.DATE, bookingWindow);
        travelMonth = c.get(Calendar.MONTH);
    }

    private Float parseFloat(String val) {
        return "NULL".equals(val) ? null : Float.parseFloat(val);
    }

    public void rank() {
        new D1Ranker().rank(this);
        new D2Ranker().rank(this);
    }

    public void analyze() {
        PriceAnalyzer.analyze(this);
        DistanceAnalyzer.analyze(this);
        //VisitorCountryAnalyzer.analyze(this);
        FactorAnalyzer.analyze(this);
        //ClickAnalyzer.analyze(this);
        Loc2Analyzer.analyze(this);
    }

    public void train() {
        SortAnalyzer.analyze(this);
    }

    public void sort() {
        Collections.sort(rows, new Comparator<HotelDataContainer>() {
            @Override
            public int compare(HotelDataContainer o1, HotelDataContainer o2) {
                return CompareDouble.reverseCompare(o1.d2_ranking, o2.d2_ranking);
            }
        });

        File sortedSearches = new File(App.sortedSearchFile);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(sortedSearches, true));

            for (HotelDataContainer hotel : rows) {
                writer.write(srch_id + "," + hotel.prop_id);
                writer.newLine();
            }

            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
