/*
* $Header$
* $Revision 1 $
* $Author: wjackson $
* &copy; $Date: 10/22/13$ Expedia Inc. PROPRIETARY AND CONFIDENTIAL
*/

package hsort.containers;

import java.util.HashMap;

public class HotelDataContainer {

    //meta data
    public Double d1_ranking = 0.0;
    public Double d2_ranking = 0.0;

    public int priceQuartile = 0;
    public int distanceQuartile = 0;
    public int loc2Quartile = 0;

    //hotel data
    public Integer prop_id = null;
    public Double prop_starrating = null;
    public Double prop_review_score = null;
    public Boolean prop_brand_bool = false;
    public Double prop_location_score1 = null;
    public Double prop_location_score2 = null;
    public Double prop_log_historical_price = null;
    public Double price_usd = null;
    public Boolean promotion_flag = false;
    public Double orig_destination_distance = null;
    public Double srch_query_affinity_score = null;

    // comp1_rate|comp1_inv|comp1_rate_percent_diff|comp2_rate|comp2_inv|comp2_rate_percent_diff|comp3_rate|comp3_inv|comp3_rate_percent_diff|comp4_rate|comp4_inv|comp4_rate_percent_diff|comp5_rate|comp5_inv|comp5_rate_percent_diff|comp6_rate|comp6_inv|comp6_rate_percent_diff|comp7_rate|comp7_inv|comp7_rate_percent_diff|comp8_rate|comp8_inv|comp8_rate_percent_diff
    public HashMap<String, CompetitorData> competitors = new HashMap<>();

    //training data only
    public Integer position = null;
    public Boolean booking_bool = false;
    public Boolean click_bool = false;
    public Double gross_bookings_usd = null;

    private HashMap<String, Integer> keys;
    private boolean training;

    public HotelDataContainer(boolean training, HashMap<String, Integer> keys, String[] row) {
        this.keys = keys;
        this.training = training;

        prop_id = parseInt(row[keys.get("prop_id")]);
        prop_starrating = parseDouble(row[keys.get("prop_starrating")]);
        prop_review_score = parseDouble(row[keys.get("prop_review_score")]);
        prop_location_score1 = parseDouble(row[keys.get("prop_location_score1")]);
        prop_location_score2 = parseDouble(row[keys.get("prop_location_score2")]);
        prop_log_historical_price = parseDouble(row[keys.get("prop_log_historical_price")]);
        price_usd = parseDouble(row[keys.get("price_usd")]);
        orig_destination_distance = parseDouble(row[keys.get("orig_destination_distance")]);
        prop_brand_bool = parseBool(row[keys.get("prop_brand_bool")]);
        promotion_flag = parseBool(row[keys.get("promotion_flag")]);
        srch_query_affinity_score = parseDouble(row[keys.get("srch_query_affinity_score")]);

        for (int i=1; i<=8; i++) {
            String key = "comp" + i;
            CompetitorData data = new CompetitorData();
            data.rate = parseInt(row[keys.get(key + "_rate")]);
            data.inventory = parseInt(row[keys.get(key + "_inv")]);
            data.rate_percent_diff = parseDouble(row[keys.get(key + "_rate_percent_diff")]);

            competitors.put(key, data);
        }

        if (this.training) {
            gross_bookings_usd = parseDouble(row[keys.get("gross_bookings_usd")]);
            position = parseInt(row[keys.get("position")]);
            click_bool = parseBool(row[keys.get("click_bool")]);
            booking_bool = parseBool(row[keys.get("booking_bool")]);
        }
    }

    private Integer parseInt(String val) {
        return "NULL".equals(val) ? null : Integer.parseInt(val);
    }

    private Double parseDouble(String val) {
        return "NULL".equals(val) ? null : Double.parseDouble(val);
    }

    private boolean parseBool(String val) {
        return "1".equals(val) ? true : false;
    }
}
