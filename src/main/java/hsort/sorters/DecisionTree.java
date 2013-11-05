/*
* $Header$
* $Revision 1 $
* $Author: wjackson $
* &copy; $Date: 10/23/13$ Expedia Inc. PROPRIETARY AND CONFIDENTIAL
*/

package hsort.sorters;

import hsort.analysis.SearchSet;
import hsort.containers.HotelDataContainer;

import java.util.Comparator;

/*
NOTES:
- Consider analyzing each row for desirability based on the search criteria, and allocating a score based on that,
then sort by that score.

- prop_location_score1 and prop_location_score2 constitute static evaluation of a property's desirability.  This
number should be correlated with the search criteria as part of the overall desirability ranking.
 */
public class DecisionTree implements Comparator<HotelDataContainer> {

    private SearchSet searchSet;

    public DecisionTree(SearchSet searchSet) {
        this.searchSet = searchSet;
    }

    @Override
    public int compare(HotelDataContainer h1, HotelDataContainer h2) {

//        if (searchSet.getAvgPrice() > 300 && searchSet.getAvgPrice() <= 500) {
//            return h1.price_usd.compareTo(h2.price_usd);
//        }

//        if (searchSet.getDestinationId() == 8192) {
//            int c = h1.prop_brand_bool.compareTo(h2.prop_brand_bool);
//            if (c == 0)
//                c = h1.promotion_flag.compareTo(h2.promotion_flag);
//            if (c == 0)
//                c = h1.position.compareTo(h2.position);
//
//            return c;
//        }
//
//        return h1.position.compareTo(h2.position);









        //if all else fails, then they're probably just about the same anyway
        return 0;
    }
}
