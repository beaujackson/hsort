/*
* $Header$
* $Revision 1 $
* $Author: wjackson $
* &copy; $Date: 10/29/13$ Expedia Inc. PROPRIETARY AND CONFIDENTIAL
*/

package hsort.containers;

public class CompetitorData {

    //+1 if Expedia has a lower price than competitor for the hotel;
    //0 if the same;
    //-1 if Expedia’s price is higher than competitor;
    //null signifies there is no competitive data
    public Integer rate = null;

    //+1 if competitor does not have availability in the hotel;
    //0 if both Expedia and competitor have availability;
    //null signifies there is no competitive data
    public Integer inventory = null;

    //The absolute percentage difference (if one exists) between Expedia and competitor’s price
    //(Expedia’s price the denominator);
    //null signifies there is no competitive data
    public Double rate_percent_diff = null;

}
