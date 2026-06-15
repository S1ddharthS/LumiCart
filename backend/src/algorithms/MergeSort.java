package algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Merge Sort Algorithm — High-Volume Catalog Price Sorting
 * 
 * Purpose: Sorts product catalog by price using a stable, divide-and-conquer
 * approach. Stability ensures products with equal prices maintain their
 * original relative order.
 * 
 * Time Complexity: O(n log n) — guaranteed for all cases (best, average, worst)
 * Space Complexity: O(n) — requires auxiliary arrays for merging
 * 
 * eCommerce Feature: When a customer clicks "Sort by Price (Low to High)",
 * this algorithm ensures a smooth, consistent sorting experience even for
 * catalogs with thousands of items.
 */
public class MergeSort {

    /**
     * Result container holding the sorted output and diagnostic trace.
     */
    public static class SortResult {
        public int[] sortedArray;       // The final sorted array
        public int[] originalIndices;   // Original indices tracking (for UI mapping)
        public List<String> traceSteps; // Step-by-step trace of merge operations
        public int totalComparisons;    // Total number of comparisons made
        public int totalMerges;         // Total number of merge operations
        public long executionTimeNs;    // Execution time in nanoseconds
        public int inputSize;           // Size of input array

        public SortResult() {
            this.traceSteps = new ArrayList<>();
            this.totalComparisons = 0;
            this.totalMerges = 0;
        }
    }

    // Shared result object for collecting trace data during recursion
    private static SortResult currentResult;

    /**
     * Public entry point for merge sort.
     * Creates a copy of the input to avoid modifying the original array.
     *
     * @param prices Array of product prices to sort
     * @param indices Original indices of products (for mapping back to UI)
     * @return SortResult containing sorted array, trace, and diagnostics
     */
    public static SortResult sort(int[] prices, int[] indices) {
        currentResult = new SortResult();
        currentResult.inputSize = prices.length;

        // Create working copies — merge sort is out-of-place
        int[] workArray = Arrays.copyOf(prices, prices.length);
        int[] workIndices = Arrays.copyOf(indices, indices.length);

        currentResult.traceSteps.add("Merge Sort initiated on " + prices.length + " elements");
        currentResult.traceSteps.add("Initial array: " + arrayToString(workArray));

        long startTime = System.nanoTime();

        // Launch the recursive divide-and-conquer process
        mergeSort(workArray, workIndices, 0, workArray.length - 1, 0);

        currentResult.executionTimeNs = System.nanoTime() - startTime;
        currentResult.sortedArray = workArray;
        currentResult.originalIndices = workIndices;

        currentResult.traceSteps.add("Sorted array: " + arrayToString(workArray));
        currentResult.traceSteps.add("Total comparisons: " + currentResult.totalComparisons +
            " | Total merges: " + currentResult.totalMerges);
        currentResult.traceSteps.add("Execution time: " + (currentResult.executionTimeNs / 1000.0) +
            " μs | Complexity: O(n log n) = O(" + currentResult.inputSize +
            " × " + String.format("%.2f", Math.log(currentResult.inputSize) / Math.log(2)) + ")");

        SortResult result = currentResult;
        currentResult = null; // Clear static reference
        return result;
    }

    /**
     * Recursive merge sort implementation.
     * 
     * Divide Phase: Split array into two halves recursively until
     * each subarray has only one element (base case).
     * 
     * Conquer Phase: Merge the sorted halves back together in order.
     *
     * @param arr    The array being sorted
     * @param indices Index tracking array
     * @param left   Left boundary of current subarray
     * @param right  Right boundary of current subarray
     * @param depth  Recursion depth (for trace indentation)
     */
    private static void mergeSort(int[] arr, int[] indices, int left, int right, int depth) {
        // Base case: single element is already sorted
        if (left >= right) return;

        // Calculate midpoint — divides array into two halves
        int mid = left + (right - left) / 2;

        String indent = "  ".repeat(Math.min(depth, 6));
        currentResult.traceSteps.add(indent + "Dividing [" + left + ".." + right + "] at mid=" + mid);

        // Recursively sort the left half: arr[left..mid]
        mergeSort(arr, indices, left, mid, depth + 1);

        // Recursively sort the right half: arr[mid+1..right]
        mergeSort(arr, indices, mid + 1, right, depth + 1);

        // Merge the two sorted halves back together
        merge(arr, indices, left, mid, right, depth);
    }

    /**
     * Merges two sorted subarrays arr[left..mid] and arr[mid+1..right].
     * 
     * Uses temporary arrays to hold copies of both halves, then writes
     * back the merged result in sorted order.
     *
     * @param arr     The main array
     * @param indices Index tracking array
     * @param left    Start of left subarray
     * @param mid     End of left subarray / midpoint
     * @param right   End of right subarray
     * @param depth   Recursion depth for trace formatting
     */
    private static void merge(int[] arr, int[] indices, int left, int mid, int right, int depth) {
        currentResult.totalMerges++;

        // Create temporary arrays for left and right halves
        int leftSize = mid - left + 1;
        int rightSize = right - mid;

        int[] leftArr = new int[leftSize];
        int[] rightArr = new int[rightSize];
        int[] leftIdx = new int[leftSize];
        int[] rightIdx = new int[rightSize];

        // Copy data to temporary arrays
        for (int i = 0; i < leftSize; i++) {
            leftArr[i] = arr[left + i];
            leftIdx[i] = indices[left + i];
        }
        for (int j = 0; j < rightSize; j++) {
            rightArr[j] = arr[mid + 1 + j];
            rightIdx[j] = indices[mid + 1 + j];
        }

        // Merge the two halves by comparing elements one-by-one
        int i = 0, j = 0, k = left;

        while (i < leftSize && j < rightSize) {
            currentResult.totalComparisons++;

            // Stable sort: use <= so equal elements maintain original order
            if (leftArr[i] <= rightArr[j]) {
                arr[k] = leftArr[i];
                indices[k] = leftIdx[i];
                i++;
            } else {
                arr[k] = rightArr[j];
                indices[k] = rightIdx[j];
                j++;
            }
            k++;
        }

        // Copy remaining elements from left half (if any)
        while (i < leftSize) {
            arr[k] = leftArr[i];
            indices[k] = leftIdx[i];
            i++;
            k++;
        }

        // Copy remaining elements from right half (if any)
        while (j < rightSize) {
            arr[k] = rightArr[j];
            indices[k] = rightIdx[j];
            j++;
            k++;
        }

        String indent = "  ".repeat(Math.min(depth, 6));
        currentResult.traceSteps.add(indent + "Merged [" + left + ".." + mid + "] + [" +
            (mid + 1) + ".." + right + "] → " + subArrayToString(arr, left, right));
    }

    /**
     * Utility: Converts full array to a readable string for trace output.
     */
    private static String arrayToString(int[] arr) {
        if (arr.length > 20) {
            return "[" + arr[0] + ", " + arr[1] + ", ... " + arr[arr.length - 1] + "] (" + arr.length + " elements)";
        }
        return Arrays.toString(arr);
    }

    /**
     * Utility: Converts a subarray range to a readable string.
     */
    private static String subArrayToString(int[] arr, int from, int to) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = from; i <= to; i++) {
            if (i > from) sb.append(", ");
            sb.append(arr[i]);
        }
        sb.append("]");
        return sb.toString();
    }
}
