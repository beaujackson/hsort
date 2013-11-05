/*
* $Header$
* $Revision 1 $
* $Author: wjackson $
* &copy; $Date: 10/25/13$ Expedia Inc. PROPRIETARY AND CONFIDENTIAL
*/

package hsort.rankers;

import hsort.analysis.CompareDouble;
import hsort.analysis.SearchSet;
import hsort.containers.CompetitorData;
import hsort.containers.HotelDataContainer;
import hsort.containers.NumericAnalysis;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class D1Ranker {

    private double getLocScore1(Double score) {
        if (score == null) {
            return 0;
        }

        return score * .5;
    }

    private double getLocScore2(Double score) {
        if (score == null) {
            return 0;
        }

        return score * 11;
    }

    private double getStarReviewScore(Double score) {

        if (score == null) {
            return 0;
        }

        if (score > 2 && score <= 3) {
            return 1;
        } else if (score > 3 && score <= 4) {
            return 1;
        } else if (score > 4) {
            return  .5;
        }

        return 0;
    }

    private double bookingWindowPriceFactor(int bookingWindow, HotelDataContainer hotel) {
        double factor = 0.0;

        if (bookingWindow == 0) {
            factor +=  5 / hotel.priceQuartile;

            if (hotel.prop_starrating != null && hotel.prop_starrating > 3) {
                factor += 3;
            }
        }

        return factor;
    }

    private double distanceFactor(SearchSet searchSet, HotelDataContainer hotel) {

        double factor = 0.0;

        NumericAnalysis distance = searchSet.numericAnalyses.get("distance");

        if (distance != null && hotel.orig_destination_distance != null) {
            if (searchSet.bookingWindow == 0) {
                if (distance.high <= 40 && hotel.distanceQuartile <= 1) {
                    factor += 1;
                }
            }

            if (distance.high <= 10 && hotel.priceQuartile <= 2) {
                factor += 1;
            }

            if (distance.high <= 3) {
                if (hotel.priceQuartile == 1) {
                    factor += 4;
                }
            }

            if (distance.high <= 10 && hotel.prop_starrating >= 4) {
                factor += 1;
            }

            if (hotel.prop_review_score != null) {
                if (distance.avg > 300 && hotel.prop_review_score >= 3) {
                    factor += hotel.prop_review_score - 1;
                }
            }
        }

        return factor;
    }

    private double averagePriceFactor(double avgPrice, HotelDataContainer hotel) {

        double factor = 0.0;

        if (avgPrice <= 110) {
            if (hotel.prop_starrating != null &&hotel.prop_starrating > 3) {
                factor += 2.6;
            }

            if (hotel.prop_review_score != null && hotel.prop_review_score > 3) {
                factor += 1.2;
            }
        }

        if (avgPrice > 300 && avgPrice < 500) {
            if (hotel.price_usd < avgPrice - 50) {
                factor += 1.4;
            }
        }

        if (avgPrice > 500) {
            if (hotel.prop_starrating != null &&hotel.prop_starrating > 3) {
                factor += 1;
            }
        }

        return factor;
    }

    private double historicalPriceFactor(Double historicalPrice, double price) {
        if (historicalPrice == null || historicalPrice == 0.0) {
            return 0;
        }

        if (price < historicalPrice) {
            return 1.7;
        }

        return 0;
    }

    private double affinityFactor(NumericAnalysis affinity, Double affinityScore) {
        if (affinity == null || affinityScore == null) {
            return 0;
        }

        double score = affinityScore * -1;

        if (score <= affinity.q1) {
            return 1;
        }

        return 0;
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

    private double travelMonthPriceFactor(SearchSet searchSet) {
        Calendar c = Calendar.getInstance();
        c.setTime(searchSet.dateTime);
        c.add(Calendar.DATE, searchSet.bookingWindow);
        int month = c.get(Calendar.MONTH);

        if (month == 0 || month == 5) {
            return 2;
        }

        return 0;
    }

    private double visitorCountryPriceFactor(SearchSet searchSet) {

        if (searchSet.visitorCountryId == 55 || searchSet.visitorCountryId == 100) {
            return 2;
        }

        return 0;
    }

    private double travelerCountFactor(SearchSet searchSet, HotelDataContainer hotel) {
        double factor = 0.0;

        if (searchSet.childCount > 0 && searchSet.numericAnalyses.get("price").avg > 300) {
            factor += 1 / hotel.priceQuartile;
        }

        return factor;
    }

    public void rank(SearchSet searchSet) {

        List<HotelDataContainer> rows = searchSet.getRows();

        double travelMonthPriceFactor = travelMonthPriceFactor(searchSet);
        double visitorCountryPriceFactor = visitorCountryPriceFactor(searchSet);

        for (HotelDataContainer hotel : rows) {
            NumericAnalysis price = searchSet.numericAnalyses.get("price");

            //booking window
            hotel.d1_ranking += bookingWindowPriceFactor(searchSet.bookingWindow, hotel);
            //hotel.d1_ranking += bookingWindowDistanceFactor(hotel.distanceQuartile, searchSet.bookingWindow);

            //price ranking
            switch (hotel.priceQuartile) {
                case 1:
                    hotel.d1_ranking += 1;
                    hotel.d1_ranking += travelMonthPriceFactor;
                    hotel.d1_ranking += visitorCountryPriceFactor;
                    break;
                case 2:
                    hotel.d1_ranking += 2;
                    break;
                case 3:
                    hotel.d1_ranking += 2;
                    break;
                case 4:
                    hotel.d1_ranking += 1;
                    break;
                default:
                    break;
            }

            hotel.d1_ranking += averagePriceFactor(price.avg, hotel);
            hotel.d1_ranking += historicalPriceFactor(price.avg, hotel.price_usd);

            //star and review rating
            hotel.d1_ranking += getStarReviewScore(hotel.prop_starrating);
            hotel.d1_ranking += getStarReviewScore(hotel.prop_review_score);

            //location rating
            hotel.d1_ranking += getLocScore1(hotel.prop_location_score1);
            hotel.d1_ranking += getLocScore2(hotel.prop_location_score2);

            //brand and promo
            if (hotel.promotion_flag) {
                hotel.d1_ranking += 1;
            }
            if (hotel.prop_brand_bool) {
                hotel.d1_ranking += 1;
            }

            hotel.d1_ranking += affinityFactor(searchSet.numericAnalyses.get("affinity"), hotel.srch_query_affinity_score);

            hotel.d1_ranking += competitionFactor(hotel.competitors);

            hotel.d1_ranking += distanceFactor(searchSet, hotel);

            hotel.d1_ranking += travelerCountFactor(searchSet, hotel);
        }

        //sort by locationScore2
        Collections.sort(rows, new Comparator<HotelDataContainer>() {
            @Override
            public int compare(HotelDataContainer o1, HotelDataContainer o2) {
                return CompareDouble.reverseCompare(o1.prop_location_score2, o2.prop_location_score2);
            }
        });
        scoreSortedRows(rows, null, 4.5, 1.6, .5);

//        //sort by price
//        Collections.sort(rows, new Comparator<HotelDataContainer>() {
//            @Override
//            public int compare(HotelDataContainer o1, HotelDataContainer o2) {
//                return CompareDouble.compare(o1.price_usd, o2.price_usd);
//            }
//        });
//        scoreSortedRows(rows, null, 0, 0, 0);

        //sort by stars
        Collections.sort(rows, new Comparator<HotelDataContainer>() {
            @Override
            public int compare(HotelDataContainer o1, HotelDataContainer o2) {
                return CompareDouble.reverseCompare(o1.prop_starrating, o2.prop_starrating);
            }
        });
        scoreSortedRows(rows, null, 0, .3, 0);

//        //sort by locationScore1
//        Collections.sort(rows, new Comparator<HotelDataContainer>() {
//            @Override
//            public int compare(HotelDataContainer o1, HotelDataContainer o2) {
//                return CompareDouble.reverseCompare(o1.prop_location_score1, o2.prop_location_score1);
//            }
//        });
//        List<Integer> loc1Ids = scoreSortedRows(rows, null, 0, 0, 0);

        //sort by reviews
        Collections.sort(rows, new Comparator<HotelDataContainer>() {
            @Override
            public int compare(HotelDataContainer o1, HotelDataContainer o2) {
                return CompareDouble.reverseCompare(o1.prop_review_score, o2.prop_review_score);
            }
        });
        scoreSortedRows(rows, null, .6, 0, 0);

//        //sort by distance
//        Collections.sort(rows, new Comparator<HotelDataContainer>() {
//            @Override
//            public int compare(HotelDataContainer o1, HotelDataContainer o2) {
//                return CompareDouble.compare(o1.orig_destination_distance, o2.orig_destination_distance);
//            }
//        });
//        rows.get(0).d1_ranking += DISTANCE;
    }

    private List<Integer> scoreSortedRows(List<HotelDataContainer> rows, List<Integer> idList,
                                          double first, double second, double third) {

        List<Integer> hotelIds = new ArrayList<>();

        if (idList == null) {
            rows.get(0).d1_ranking += first;
            hotelIds.add(rows.get(0).prop_id);
            rows.get(1).d1_ranking += second;
            hotelIds.add(rows.get(1).prop_id);
            rows.get(2).d1_ranking += third;
            hotelIds.add(rows.get(2).prop_id);
        } else {
            for (int i=0; i<3; i++) {
                HotelDataContainer hotel = rows.get(i);
                if (idList.contains(hotel.prop_id)) {
                    if (i == 0) {
                        hotel.d1_ranking += first;
                    }

                    if (i == 1) {
                        hotel.d1_ranking += second;
                    }

                    if (i == 2) {
                        hotel.d1_ranking += third;
                    }
                }
            }
        }

        return hotelIds;
    }
}
