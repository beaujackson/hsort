/*
* $Header$
* $Revision 1 $
* $Author: wjackson $
* &copy; $Date: 10/22/13$ Expedia Inc. PROPRIETARY AND CONFIDENTIAL
*/

package hsort.analysis;

public class CompareDouble {

    public static int compare(Double o1, Double o2) {
        if (o1 == null && o2 == null) {
            return 0;
        } else if (o1 == null && o2 != null) {
            return -1;
        } else if (o1 != null && o2 == null) {
            return 1;
        } else {
            return o1.compareTo(o2);
        }
    }

    public static int reverseCompare(Double o1, Double o2) {
        if (o1 == null && o2 == null) {
            return 0;
        } else if (o1 == null && o2 != null) {
            return 1;
        } else if (o1 != null && o2 == null) {
            return -1;
        } else {
            return o2.compareTo(o1);
        }
    }
}
