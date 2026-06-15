package algorithms;

import java.util.ArrayList;
import java.util.List;

/**
 * Binary Search Algorithm — Rapid SKU/Product Identifier Lookup
 * 
 * Purpose: Efficiently searches for a specific product by name or ID
 * within a sorted catalog array.
 * 
 * Time Complexity: O(log n) — halves the search space each iteration
 * Space Complexity: O(1) for iterative approach
 * 
 * eCommerce Feature: When a customer types a product name or ID into
 * the AetherShop search bar, this algorithm instantly locates the
 * exact item from potentially thousands of products.
 */
public class BinarySearch {

    /**
     * Result container holding the search outcome and diagnostic trace.
     */
    public static class SearchResult {
        public int foundIndex;          // Index where item was found (-1 if not found)
        public String targetValue;      // The value being searched for
        public List<String> traceSteps; // Step-by-step trace of the search process
        public List<Integer> inspected; // Indices of elements inspected during search
        public int totalComparisons;    // Total number of comparisons made
        public long executionTimeNs;    // Execution time in nanoseconds

        public SearchResult() {
            this.foundIndex = -1;
            this.traceSteps = new ArrayList<>();
            this.inspected = new ArrayList<>();
            this.totalComparisons = 0;
        }
    }

    /**
     * Performs iterative binary search on a sorted array of strings.
     * 
     * Algorithm Steps:
     * 1. Initialize low = 0, high = n - 1
     * 2. While low <= high:
     *    a. Compute mid = low + (high - low) / 2  (avoids integer overflow)
     *    b. Compare target with array[mid]
     *    c. If equal → found, return mid
     *    d. If target < array[mid] → search left half (high = mid - 1)
     *    e. If target > array[mid] → search right half (low = mid + 1)
     * 3. If loop ends without finding → target not in array
     *
     * @param sortedArray The sorted array of product names/IDs
     * @param target      The product name/ID to search for
     * @return SearchResult containing index, trace, and diagnostics
     */
    public static SearchResult search(String[] sortedArray, String target) {
        SearchResult result = new SearchResult();
        result.targetValue = target;

        long startTime = System.nanoTime();

        int low = 0;
        int high = sortedArray.length - 1;
        int step = 1;

        result.traceSteps.add("Binary Search initiated for target: \"" + target + "\"");
        result.traceSteps.add("Array size: " + sortedArray.length + " elements (sorted alphabetically)");

        // Core binary search loop — O(log n) iterations
        while (low <= high) {
            // Calculate midpoint using overflow-safe formula
            int mid = low + (high - low) / 2;
            result.inspected.add(mid);
            result.totalComparisons++;

            String midValue = sortedArray[mid];
            int comparison = target.compareToIgnoreCase(midValue);

            result.traceSteps.add(
                "Step " + step + ": low=" + low + ", high=" + high + ", mid=" + mid +
                " → Comparing \"" + target + "\" with \"" + midValue + "\""
            );

            if (comparison == 0) {
                // Target found at index mid
                result.foundIndex = mid;
                result.traceSteps.add("  ✓ MATCH FOUND at index " + mid + "!");
                break;
            } else if (comparison < 0) {
                // Target is alphabetically before mid — search left half
                result.traceSteps.add("  → Target < mid value, narrowing to LEFT half [" + low + ".." + (mid - 1) + "]");
                high = mid - 1;
            } else {
                // Target is alphabetically after mid — search right half
                result.traceSteps.add("  → Target > mid value, narrowing to RIGHT half [" + (mid + 1) + ".." + high + "]");
                low = mid + 1;
            }
            step++;
        }

        if (result.foundIndex == -1) {
            result.traceSteps.add("✗ Target \"" + target + "\" NOT FOUND in the catalog.");
        }

        result.executionTimeNs = System.nanoTime() - startTime;
        result.traceSteps.add("Completed in " + result.totalComparisons + " comparisons | Time: " +
            (result.executionTimeNs / 1000.0) + " μs | Complexity: O(log " + sortedArray.length + ") = O(" +
            String.format("%.2f", Math.log(sortedArray.length) / Math.log(2)) + ")");

        return result;
    }

    /**
     * Overloaded method: Binary search on integer array (for numeric ID lookup).
     *
     * @param sortedArray Sorted integer array of product IDs
     * @param target      The numeric ID to find
     * @return SearchResult with trace diagnostics
     */
    public static SearchResult search(int[] sortedArray, int target) {
        SearchResult result = new SearchResult();
        result.targetValue = String.valueOf(target);

        long startTime = System.nanoTime();

        int low = 0;
        int high = sortedArray.length - 1;
        int step = 1;

        result.traceSteps.add("Binary Search initiated for numeric ID: " + target);
        result.traceSteps.add("Array size: " + sortedArray.length + " elements (sorted ascending)");

        while (low <= high) {
            int mid = low + (high - low) / 2;
            result.inspected.add(mid);
            result.totalComparisons++;

            result.traceSteps.add(
                "Step " + step + ": low=" + low + ", high=" + high + ", mid=" + mid +
                " → array[" + mid + "] = " + sortedArray[mid]
            );

            if (sortedArray[mid] == target) {
                result.foundIndex = mid;
                result.traceSteps.add("  ✓ MATCH FOUND at index " + mid + "!");
                break;
            } else if (target < sortedArray[mid]) {
                result.traceSteps.add("  → Target < mid, search LEFT [" + low + ".." + (mid - 1) + "]");
                high = mid - 1;
            } else {
                result.traceSteps.add("  → Target > mid, search RIGHT [" + (mid + 1) + ".." + high + "]");
                low = mid + 1;
            }
            step++;
        }

        if (result.foundIndex == -1) {
            result.traceSteps.add("✗ Target ID " + target + " NOT FOUND.");
        }

        result.executionTimeNs = System.nanoTime() - startTime;
        result.traceSteps.add("Completed in " + result.totalComparisons + " comparisons | Time: " +
            (result.executionTimeNs / 1000.0) + " μs | Complexity: O(log n)");

        return result;
    }
}
