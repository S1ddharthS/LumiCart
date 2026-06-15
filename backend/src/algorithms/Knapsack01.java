package algorithms;

import java.util.ArrayList;
import java.util.List;

/**
 * 0/1 Knapsack Algorithm — Smart Cart Checkout Maximizer
 * 
 * Purpose: Given a shopping cart with items of varying values and costs,
 * and a budget constraint (capacity), finds the optimal subset of items
 * that maximizes total value without exceeding the budget.
 * 
 * "0/1" means each item can either be included (1) or excluded (0) — no fractions.
 * 
 * Time Complexity: O(n × W) where n = number of items, W = budget capacity
 * Space Complexity: O(n × W) for the DP table
 * 
 * eCommerce Feature: When a customer has a limited budget, this algorithm
 * automatically selects the best combination of products from their cart
 * that maximizes the total value/utility they receive.
 */
public class Knapsack01 {

    /**
     * Result container for the knapsack optimization.
     */
    public static class KnapsackResult {
        public int maxValue;                 // Maximum achievable value
        public List<Integer> selectedItems;  // Indices of items selected
        public int totalWeight;              // Total weight/cost of selected items
        public List<String> traceSteps;      // Step-by-step trace
        public int[][] dpTable;              // The complete DP table (for visualization)
        public long executionTimeNs;         // Execution time in nanoseconds

        public KnapsackResult() {
            this.selectedItems = new ArrayList<>();
            this.traceSteps = new ArrayList<>();
            this.maxValue = 0;
            this.totalWeight = 0;
        }
    }

    /**
     * Solves the 0/1 Knapsack problem using bottom-up Dynamic Programming.
     * 
     * Algorithm — DP Table Construction:
     * Let dp[i][w] = maximum value achievable using items {0..i-1} with capacity w
     * 
     * Base case: dp[0][w] = 0 for all w (no items → no value)
     * 
     * Recurrence for each item i and capacity w:
     *   If weight[i] > w (item doesn't fit):
     *     dp[i][w] = dp[i-1][w]  (skip this item)
     *   Else:
     *     dp[i][w] = max(
     *       dp[i-1][w],                        // Don't take item i
     *       dp[i-1][w - weight[i]] + value[i]  // Take item i
     *     )
     * 
     * Final answer: dp[n][W]
     * 
     * Backtracking: To find WHICH items were selected, trace back through
     * the DP table from dp[n][W] to dp[0][0].
     *
     * @param values  Array of item values (utility/benefit)
     * @param weights Array of item costs/weights
     * @param capacity Maximum budget/weight capacity
     * @param itemNames Names of items for display
     * @return KnapsackResult with optimal selection, value, and trace
     */
    public static KnapsackResult solve(int[] values, int[] weights, int capacity, String[] itemNames) {
        KnapsackResult result = new KnapsackResult();
        int n = values.length;

        result.traceSteps.add("0/1 Knapsack Problem — Smart Cart Optimizer");
        result.traceSteps.add("Items: " + n + " | Budget capacity: " + capacity);
        result.traceSteps.add("Item details:");
        for (int i = 0; i < n; i++) {
            result.traceSteps.add("  Item " + i + ": " + itemNames[i] +
                " (value=" + values[i] + ", cost=" + weights[i] + ")");
        }

        long startTime = System.nanoTime();

        // --- Step 1: Build the DP Table ---
        // dp[i][w] = max value using first i items with capacity w
        int[][] dp = new int[n + 1][capacity + 1];

        result.traceSteps.add("Building DP table of size " + (n + 1) + " × " + (capacity + 1) + "...");

        // Fill the table bottom-up
        for (int i = 1; i <= n; i++) {
            int itemValue = values[i - 1];    // Current item's value
            int itemWeight = weights[i - 1];   // Current item's weight/cost

            for (int w = 0; w <= capacity; w++) {
                if (itemWeight > w) {
                    // Item i doesn't fit in remaining capacity w
                    // Best we can do is same as without item i
                    dp[i][w] = dp[i - 1][w];
                } else {
                    // Choose the better option:
                    // Option A: Skip item i → dp[i-1][w]
                    // Option B: Take item i → value[i] + dp[i-1][w - weight[i]]
                    int skipValue = dp[i - 1][w];
                    int takeValue = itemValue + dp[i - 1][w - itemWeight];

                    dp[i][w] = Math.max(skipValue, takeValue);
                }
            }

            result.traceSteps.add("  Processed item " + (i - 1) + " (" + itemNames[i - 1] +
                "): best value at full capacity = " + dp[i][capacity]);
        }

        result.maxValue = dp[n][capacity];
        result.dpTable = dp;

        result.traceSteps.add("DP table complete. Maximum achievable value: " + result.maxValue);

        // --- Step 2: Backtrack to Find Selected Items ---
        result.traceSteps.add("Backtracking to identify selected items...");

        int w = capacity;
        for (int i = n; i > 0; i--) {
            if (dp[i][w] != dp[i - 1][w]) {
                // Item i was included in the optimal solution
                result.selectedItems.add(i - 1); // Convert to 0-indexed
                result.totalWeight += weights[i - 1];

                result.traceSteps.add("  ✓ Selected: " + itemNames[i - 1] +
                    " (value=" + values[i - 1] + ", cost=" + weights[i - 1] + ")");

                w -= weights[i - 1]; // Reduce remaining capacity
            } else {
                result.traceSteps.add("  ✗ Skipped: " + itemNames[i - 1]);
            }
        }

        result.executionTimeNs = System.nanoTime() - startTime;

        result.traceSteps.add("── Optimization Summary ──");
        result.traceSteps.add("Items selected: " + result.selectedItems.size() + " out of " + n);
        result.traceSteps.add("Total value: " + result.maxValue + " | Total cost: " + result.totalWeight + " / " + capacity);
        result.traceSteps.add("Budget utilization: " + String.format("%.1f%%", (result.totalWeight * 100.0 / capacity)));
        result.traceSteps.add("Execution time: " + (result.executionTimeNs / 1000.0) +
            " μs | Complexity: O(n × W) = O(" + n + " × " + capacity + ") = O(" + (n * capacity) + ")");

        return result;
    }
}
