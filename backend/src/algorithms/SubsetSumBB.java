package algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Sum of Subsets via Branch and Bound — Target-Value Vault Promo Codes
 * 
 * Purpose: Given a set of distinct coupon/gift card values and a target
 * discount amount, finds the exact subset of coupons whose values sum
 * precisely to the target. Uses Branch and Bound to prune the search
 * space efficiently.
 * 
 * Time Complexity: O(2^n) worst case, but Branch and Bound prunes
 *                  significantly, making average case much faster
 * Space Complexity: O(n) for the recursion stack
 * 
 * eCommerce Feature: When a customer wants to apply coupons that exactly
 * match a specific checkout discount, this algorithm finds the perfect
 * combination (or determines none exists) without trying all 2^n subsets.
 */
public class SubsetSumBB {

    /**
     * Result container for the subset sum computation.
     */
    public static class SubsetResult {
        public boolean found;                // Whether an exact match was found
        public List<Integer> selectedIndices; // Indices of selected items
        public List<Integer> selectedValues;  // Values of selected items
        public int targetSum;                 // The target sum we're looking for
        public int achievedSum;               // The sum actually achieved
        public List<String> traceSteps;       // Step-by-step execution trace
        public int nodesExplored;             // Total nodes explored in state-space tree
        public int nodesPruned;               // Nodes pruned by bounding
        public long executionTimeNs;          // Execution time in nanoseconds

        public SubsetResult() {
            this.selectedIndices = new ArrayList<>();
            this.selectedValues = new ArrayList<>();
            this.traceSteps = new ArrayList<>();
            this.found = false;
            this.nodesExplored = 0;
            this.nodesPruned = 0;
        }
    }

    // State variables for the recursive search
    private static SubsetResult currentResult;
    private static int[] sortedValues;
    private static int[] originalIndices;
    private static boolean solutionFound;

    /**
     * Solves the Sum of Subsets problem using Branch and Bound.
     * 
     * Algorithm Overview:
     * 1. Sort the values in ascending order (enables better bounding)
     * 2. Build a state-space tree where each level represents a decision
     *    about whether to include a particular coupon
     * 3. At each node, compute bounds:
     *    - Lower bound: current sum (what we've committed to)
     *    - Upper bound: current sum + remaining values (what's still possible)
     * 4. Prune branches where:
     *    a. currentSum + remaining < target (can't reach target even with all remaining)
     *    b. currentSum > target (already exceeded, no point continuing)
     *    c. currentSum + nextItem > target (adding the next smallest exceeds target)
     * 
     * @param values Array of distinct coupon/gift card values
     * @param target The exact target sum to achieve
     * @param names Names/labels for the coupons
     * @return SubsetResult with the matching subset or indication of failure
     */
    public static SubsetResult solve(int[] values, int target, String[] names) {
        currentResult = new SubsetResult();
        currentResult.targetSum = target;
        solutionFound = false;

        int n = values.length;

        currentResult.traceSteps.add("Sum of Subsets (Branch & Bound) initiated");
        currentResult.traceSteps.add("Coupon values: " + Arrays.toString(values));
        currentResult.traceSteps.add("Target discount: " + target);
        currentResult.traceSteps.add("Total coupons: " + n + " | Search space: 2^" + n + " = " + (1 << n) + " subsets");

        // Step 1: Sort values ascending for better bounding
        // We track original indices to map back to the input
        originalIndices = new int[n];
        sortedValues = new int[n];
        Integer[] indexArray = new Integer[n];
        for (int i = 0; i < n; i++) indexArray[i] = i;

        // Sort indices by their corresponding values
        Arrays.sort(indexArray, (a, b) -> Integer.compare(values[a], values[b]));

        for (int i = 0; i < n; i++) {
            sortedValues[i] = values[indexArray[i]];
            originalIndices[i] = indexArray[i];
        }

        currentResult.traceSteps.add("Sorted values: " + Arrays.toString(sortedValues));

        // Compute total sum — used for upper bounding
        int totalSum = 0;
        for (int v : sortedValues) totalSum += v;

        if (totalSum < target) {
            // Even using ALL coupons can't reach the target
            currentResult.traceSteps.add("✗ Total sum (" + totalSum + ") < target (" + target + "). No solution possible.");
            currentResult.executionTimeNs = 0;
            return currentResult;
        }

        long startTime = System.nanoTime();

        // Step 2: Start the branch and bound search
        boolean[] included = new boolean[n];
        branchAndBound(0, 0, totalSum, target, included, n);

        currentResult.executionTimeNs = System.nanoTime() - startTime;

        if (!currentResult.found) {
            currentResult.traceSteps.add("✗ No exact combination found for target " + target);
        }

        currentResult.traceSteps.add("Nodes explored: " + currentResult.nodesExplored +
            " | Nodes pruned: " + currentResult.nodesPruned +
            " | Effective search: " + String.format("%.1f%%",
                (currentResult.nodesExplored * 100.0 / (1 << n))));
        currentResult.traceSteps.add("Execution time: " + (currentResult.executionTimeNs / 1000.0) +
            " μs | Worst-case complexity: O(2^" + n + ")");

        SubsetResult result = currentResult;
        currentResult = null;
        return result;
    }

    /**
     * Recursive Branch and Bound search through the state-space tree.
     * 
     * At level k, we decide: include sortedValues[k] or exclude it?
     * 
     * Bounding Strategy:
     * - If currentSum == target → SOLUTION FOUND
     * - If currentSum + remaining < target → PRUNE (can't reach target)
     * - If currentSum > target → PRUNE (already exceeded)
     * - If currentSum + sortedValues[k] > target → PRUNE (next item too large)
     *
     * @param k Current level in the state-space tree (which item to decide on)
     * @param currentSum Sum of items included so far
     * @param remaining Sum of items not yet considered
     * @param target Target sum
     * @param included Boolean array tracking which items are included
     * @param n Total number of items
     */
    private static void branchAndBound(int k, int currentSum, int remaining, int target,
                                        boolean[] included, int n) {
        // Stop if we already found a solution
        if (solutionFound) return;

        currentResult.nodesExplored++;

        String indent = "  ".repeat(Math.min(k, 8));

        // Check if we've found an exact match
        if (currentSum == target) {
            solutionFound = true;
            currentResult.found = true;
            currentResult.achievedSum = target;

            currentResult.traceSteps.add(indent + "✓ SOLUTION FOUND! Exact match for target " + target);

            // Record the selected items
            for (int i = 0; i < n; i++) {
                if (included[i]) {
                    currentResult.selectedIndices.add(originalIndices[i]);
                    currentResult.selectedValues.add(sortedValues[i]);
                    currentResult.traceSteps.add(indent + "  Selected: index " +
                        originalIndices[i] + " (value=" + sortedValues[i] + ")");
                }
            }
            return;
        }

        // Base case: no more items to consider
        if (k >= n) return;

        // Update remaining (subtract current item since we're now deciding on it)
        remaining -= sortedValues[k];

        // --- Bound Check 1: Can we still reach the target? ---
        if (currentSum + remaining < target) {
            // Even including ALL remaining items won't reach target — PRUNE
            currentResult.nodesPruned++;
            currentResult.traceSteps.add(indent + "PRUNED at level " + k +
                ": sum=" + currentSum + " + remaining=" + remaining + " < target=" + target);
            return;
        }

        // --- Branch 1: INCLUDE item k ---
        if (currentSum + sortedValues[k] <= target) {
            included[k] = true;
            currentResult.traceSteps.add(indent + "Level " + k + ": INCLUDE value " +
                sortedValues[k] + " (sum becomes " + (currentSum + sortedValues[k]) + ")");

            branchAndBound(k + 1, currentSum + sortedValues[k], remaining, target, included, n);

            if (solutionFound) return; // Don't explore further if solution found
            included[k] = false; // Backtrack
        } else {
            // currentSum + sortedValues[k] > target — PRUNE this branch
            currentResult.nodesPruned++;
            currentResult.traceSteps.add(indent + "PRUNED at level " + k +
                ": sum=" + currentSum + " + " + sortedValues[k] + " > target=" + target);
            return; // Since array is sorted, all further items are even larger
        }

        // --- Branch 2: EXCLUDE item k ---
        currentResult.traceSteps.add(indent + "Level " + k + ": EXCLUDE value " + sortedValues[k]);
        branchAndBound(k + 1, currentSum, remaining, target, included, n);
    }
}
