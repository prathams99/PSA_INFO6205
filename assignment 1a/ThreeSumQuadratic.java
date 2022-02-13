package edu.neu.coe.info6205.threesum;

import java.util.*;

/**
 * Implementation of ThreeSum which follows the approach of dividing the solution-space into
 * N sub-spaces where each sub-space corresponds to a fixed value for the middle index of the three values.
 * Each sub-space is then solved by expanding the scope of the other two indices outwards from the starting point.
 * Since each sub-space can be solved in O(N) time, the overall complexity is O(N^2).
 * <p>
 * NOTE: The array provided in the constructor MUST be ordered.
 */
public class ThreeSumQuadratic implements ThreeSum {

    private final int[] a;
    private final int length;

    /**
     * Construct a ThreeSumQuadratic on a.
     *
     * @param a a sorted array.
     */
    public ThreeSumQuadratic(int[] a) {
        this.a = a;
        length = a.length;
    }

    public Triple[] getTriples() {
        List<Triple> triples = new ArrayList<>();
        for (int i = 0; i < length; i++) triples.addAll(getTriples(i));
        Collections.sort(triples);
        return triples.stream().distinct().toArray(Triple[]::new);
    }

    /**
     * Get a list of Triples such that the middle index is the given value j.
     *
     * @param j the index of the middle value.
     * @return a Triple such that
     */
    public List<Triple> getTriples(int j) {
        List<Triple> triples = new ArrayList<>();
//        Arrays.sort(a);
//        System.out.println(Arrays.toString(a));
//        System.out.println();
//        int cnt = 0;
//        int n = a.length;
//        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
//        for (int i = 0; i < n; i++) {
//            map.put(a[i], 1); //Dvaluekey, index/map fi
//        }
//        for (int i = 0; i < n - 1; i++) {
//            for (int x = i + 1; x < n; x++) {
//                int smallValue = a[i] + a[x];
//                if (smallValue > length) //a[i]+a[j]>target7, BEERSRE
//                    break;
//                int bigvalue = length - smallValue;
//                Integer bigIndex = map.get(bigvalue);
//                if (bigIndex != null && bigIndex > i && bigIndex > x) {
//                }
//                // END
//            }
//        }
//        return triples;
//    }
        int low = 0, high = length - 1;
        while (low < j && high > j) {
            int sum = this.a[j] + this.a[low] + this.a[high];
            if (sum > 0) {
                high--;
            } else if (sum < 0) {
                low++;
            } else {
                triples.add(new Triple(this.a[j], this.a[low], this.a[high]));
                low++;
                high--;
            }
        }
        return triples;
    }
}
