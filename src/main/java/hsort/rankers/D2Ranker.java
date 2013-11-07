/*
* $Header$
* $Revision 1 $
* $Author: wjackson $
* &copy; $Date: 11/4/13$ Expedia Inc. PROPRIETARY AND CONFIDENTIAL
*/

package hsort.rankers;

import hsort.App;
import hsort.analysis.SearchSet;
import hsort.containers.CompetitorData;
import hsort.containers.HotelDataContainer;
import hsort.containers.NumericAnalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class D2Ranker {
    private static HashMap<Integer, Integer> propIds = new HashMap<>();

    static {
        File propIdFile = new File(App.propIdFile);
        if (propIdFile.exists()) {

            try {
                BufferedReader reader = new BufferedReader(new FileReader(propIdFile));

                String line;
                while((line = reader.readLine()) != null) {
                    if (line.trim().length() > 0) {
                        String[] vals = line.split(",");
                        propIds.put(Integer.parseInt(vals[0]), Integer.parseInt(vals[1]));
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    private double travelMonthPriceFactor(SearchSet searchSet) {

        if (searchSet.travelMonth == 0 || searchSet.travelMonth == 5) {
            return 1.5;
        }

        return 0;
    }

    private double visitorCountryPriceFactor(SearchSet searchSet) {

        if (searchSet.visitorCountryId == 55 || searchSet.visitorCountryId == 100) {
            return 2;
        }

        return 0;
    }

    private double bookingWindowPriceFactor(int bookingWindow, HotelDataContainer hotel) {
        double factor = 0.0;

        if (bookingWindow == 0) {
            factor +=  1 / hotel.priceQuartile;
        }

        return factor;
    }

    private double competitionFactor(HashMap<String, CompetitorData> competitors) {

        double rateFactor = 0;
        double inventoryFactor = 0;
        double diffFactor = 0;

        for (CompetitorData competitor : competitors.values()) {
            if (competitor.rate != null) {
                if (competitor.rate == 1) {
                    //expedia lower
                    rateFactor += .3;

                } else if (competitor.rate == -1) {
                    //competitor lower
                    //rateFactor -= .1;
                }
            }

            if (competitor.inventory != null) {
                if (competitor.inventory == 1) {
                    //expedia has inventory, competitor does not
                    inventoryFactor += .1;
                }
            }

            if (competitor.rate_percent_diff != null) {
                if (competitor.rate == 1) {
                    //expedia lower
                    diffFactor += competitor.rate_percent_diff / 100;
                } else if (competitor.rate == -1) {
                    //competitor lower
                    diffFactor -= competitor.rate_percent_diff / 100;
                }
            }
        }

        return rateFactor + inventoryFactor + diffFactor;
    }

    private double averagePriceFactor(NumericAnalysis price, HotelDataContainer hotel) {

        double factor = 0.0;

        if (price.avg <= 100) {
            if (hotel.prop_starrating != null && hotel.prop_starrating > 3) {
                factor += 1;
            }
        }

        if (price.avg > 210) { // && avgPrice < 500) {
            if (hotel.price_usd < price.avg - 60) {
                factor += 1.3;
            }
        }

        return factor;
    }

    private double historicalPriceFactor(Double historicalPrice, double price) {
        if (historicalPrice == null || historicalPrice == 0.0) {
            return 0;
        }

        if (price < historicalPrice) {
            return 1;
        }

        return 0;
    }

    private double affinityFactor(NumericAnalysis affinity, Double affinityScore) {
        if (affinity == null || affinityScore == null) {
            return 0;
        }

        double score = affinityScore * -1;

        if (score <= affinity.q1) {
            return 3;
        }

        return 0;
    }

    private double distanceFactor(SearchSet searchSet, HotelDataContainer hotel) {

        double factor = 0.0;

        NumericAnalysis distance = searchSet.numericAnalyses.get("distance");

        if (distance != null && hotel.orig_destination_distance != null) {
//            if (searchSet.bookingWindow == 0) {
//                if (distance.high <= 40 && hotel.distanceQuartile <= 1) {
//                    factor += 1;
//                }
//            }

            if (distance.high <= 10 && hotel.priceQuartile <= 2) {
                factor += 1;
            }

            if (distance.high <= 3) {
                if (hotel.priceQuartile == 1) {
                    factor += .3;
                }
            }

            if (hotel.prop_review_score != null) {
                if (distance.avg > 300 && hotel.prop_review_score >= 3) {
                    factor += hotel.prop_review_score - 2;
                }
            }
        }

        return factor;
    }

    private double travelerCountFactor(SearchSet searchSet, HotelDataContainer hotel) {
        double factor = 0.0;

        if (searchSet.childCount > 0 && searchSet.numericAnalyses.get("price").avg > 300) {
            factor += 2 / hotel.priceQuartile;
        }

        return factor;
    }

    public void rank(SearchSet searchSet) {

        double travelMonthPriceFactor = travelMonthPriceFactor(searchSet);

        List<HotelDataContainer> rows = searchSet.getRows();

        NumericAnalysis loc2 = searchSet.numericAnalyses.get("loc2");
        NumericAnalysis price = searchSet.numericAnalyses.get("price");
        NumericAnalysis affinity = searchSet.numericAnalyses.get("affinity");

        for (HotelDataContainer hotel : rows) {
            if (loc2 != null) {
                double factor = 10;

                if (loc2.high < .1) {
                    factor = 100;
                }

                if (hotel.prop_location_score2 == null) {
                    hotel.d2_ranking = -10.0;
                } else {
                    hotel.d2_ranking = hotel.prop_location_score2 * factor;
                }
            } else {
                double factor = (hotel.price_usd / price.high);
                hotel.d2_ranking = 1.5 - factor;
            }

            hotel.d2_ranking += hotel.prop_starrating;

            if (propIds.containsKey(hotel.prop_id)) {

                Integer bookings = propIds.get(hotel.prop_id);

                hotel.d2_ranking += bookings / 10;

                if (bookings >= 6) {
                    hotel.d2_ranking += .5;
                }


//                if (propIds.get(hotel.prop_id) >= 2) {
//                    hotel.d2_ranking += .7;
//                }
            }

            //booking window
            hotel.d2_ranking += bookingWindowPriceFactor(searchSet.bookingWindow, hotel);

            //price ranking
            switch (hotel.priceQuartile) {
                case 1:
                    hotel.d2_ranking += 1 + travelMonthPriceFactor;
                    break;
                case 2:
                    hotel.d2_ranking += .5;
                    break;
                default:
                    break;
            }

            hotel.d2_ranking += averagePriceFactor(price, hotel);
            hotel.d2_ranking += historicalPriceFactor(price.avg, hotel.price_usd);

            //brand and promo
            if (hotel.promotion_flag) {
                hotel.d2_ranking += .6;
            }

            hotel.d2_ranking += affinityFactor(affinity, hotel.srch_query_affinity_score);

            hotel.d2_ranking += distanceFactor(searchSet, hotel);
        }
    }

    private void scoreRows(List<HotelDataContainer> rows, Double score, Double increment) {
        int count = rows.size();
        int index = 0;
        while (count > 0 && score > 0) {
            HotelDataContainer hotel = rows.get(index);
            hotel.d2_ranking += score - (increment * index);
            index++;
            count--;
        }
    }

    private List<Integer> scoreSortedRows(List<HotelDataContainer> rows, List<Integer> idList,
                                          double first, double second, double third) {

        List<Integer> hotelIds = new ArrayList<>();

        if (idList == null) {
            rows.get(0).d2_ranking += first;
            hotelIds.add(rows.get(0).prop_id);
            rows.get(1).d2_ranking += second;
            hotelIds.add(rows.get(1).prop_id);
            rows.get(2).d2_ranking += third;
            hotelIds.add(rows.get(2).prop_id);
        } else {
            for (int i=0; i<3; i++) {
                HotelDataContainer hotel = rows.get(i);
                if (idList.contains(hotel.prop_id)) {
                    if (i == 0) {
                        hotel.d2_ranking += first;
                    }

                    if (i == 1) {
                        hotel.d2_ranking += second;
                    }

                    if (i == 2) {
                        hotel.d2_ranking += third;
                    }
                }
            }
        }

        return hotelIds;
    }
}