package algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Quick Sort Algorithm — Dynamic Customer Rating/Review Ranking
 * 
 * Purpose: Sorts products by customer rating using an efficient in-place
 * partitioning strategy. Uses median-of-three pivot selection to avoid
 * worst-case O(n²) behavior on already-sorted or nearly-sorted data.
 * 
 * Time Complexity: O(n log n) average, O(n²) worst case (mitigated by pivot strategy)
 * Space Complexity: O(log n) — recursion stack only (in-place sorting)
 * 
 * eCommerce Feature: When a customer clicks "Sort by Popularity/Rating",
 * this algorithm rapidly re-ranks products by their customer ratings,
 * putting the highest-rated items first for a better shopping experience.
 */
public class QuickSort {

    /**
     * Result container holding the sorted output and diagnostic trace.
     */
    public static class SortResult {
        public int[] sortedArray;       // The final sorted array
        public int[] originalIndices;   // Original index mapping for UI
        public List<String> traceSteps; // Step-by-step trace of partitions
        public int totalComparisons;    // Total comparisons performed
        public int totalSwaps;          // Total element swaps performed
        public long executionTimeNs;    // Execution time in nanoseconds
        public int inputSize;           // Size of input array

        public SortResult() {
            this.traceSteps = new ArrayList<>();
            this.totalComparisons = 0;
            this.totalSwaps = 0;
        }
    }

    private static SortResult currentResult;

    /**
     * Public entry point for quick sort.
     * Sorts in DESCENDING order (highest rating first) for the rating use case.
     *
     * @param ratings Array of product ratings to sort
     * @param indices Original indices for UI mapping
     * @param descending If true, sorts highest-first (for ratings)
     * @return SortResult with sorted array, trace, and diagnostics
     */
    public static SortResult sort(int[] ratings, int[] indices, boolean descending) {
        currentResult = new SortResult();
        currentResult.inputSize = ratings.length;

        // Quick sort is in-place, but we copy to avoid mutating original
        int[] workArray = Arrays.copyOf(ratings, ratings.length);
        int[] workIndices = Arrays.copyOf(indices, indices.length);

        String order = descending ? "DESCENDING (highest first)" : "ASCENDING (lowest first)";
        currentResult.traceSteps.add("Quick Sort initiated on " + ratings.length + " elements — " + order);
        currentResult.traceSteps.add("Initial array: " + arrayToString(workArray));
        currentResult.traceSteps.add("Using Median-of-Three pivot selection strategy");

        long startTime = System.nanoTime();

        // Launch recursive quick sort
        quickSort(workArray, workIndices, 0, workArray.length - 1, descending, 0);

        currentResult.executionTimeNs = System.nanoTime() - startTime;
        currentResult.sortedArray = workArray;
        currentResult.originalIndices = workIndices;

        currentResult.traceSteps.add("Sorted array: " + arrayToString(workArray));
        currentResult.traceSteps.add("Total comparisons: " + currentResult.totalComparisons +
            " | Total swaps: " + currentResult.totalSwaps);
        currentResult.traceSteps.add("Execution time: " + (currentResult.executionTimeNs / 1000.0) +
            " μs | Complexity: O(n log n) average");

        SortResult result = currentResult;
        currentResult = null;
        return result;
    }

    /**
     * Recursive quick sort with median-of-three pivot selection.
     * 
     * Strategy:
     * 1. Select pivot using median-of-three (first, middle, last elements)
     * 2. Partition array: elements on correct side of pivot
     * 3. Recursively sort left and right partitions
     *
     * @param arr        Array being sorted
     * @param indices    Index tracking array
     * @param low        Left boundary
     * @param high       Right boundary
     * @param descending Sort direction flag
     * @param depth      Recursion depth for trace formatting
     */
    private static void quickSort(int[] arr, int[] indices, int low, int high, boolean descending, int depth) {
        // Base case: subarray of size 0 or 1 is already sorted
        if (low >= high) return;

        // Use insertion sort for small subarrays (optimization)
        if (high - low < 10) {
            insertionSort(arr, indices, low, high, descending);
            return;
        }

        // Partition and get the pivot's final position
        int pivotIndex = partition(arr, indices, low, high, descending, depth);

        String indent = "  ".repeat(Math.min(depth, 6));
        currentResult.traceSteps.add(indent + "Partitioned [" + low + ".." + high +
            "] → pivot at index " + pivotIndex + " (value=" + arr[pivotIndex] + ")");

        // Recursively sort elements before and after pivot
        quickSort(arr, indices, low, pivotIndex - 1, descending, depth + 1);
        quickSort(arr, indices, pivotIndex + 1, high, descending, depth + 1);
    }

    /**
     * Median-of-Three Pivot Selection + Lomuto Partition Scheme.
     * 
     * Median-of-three: Choose the median of arr[low], arr[mid], arr[high]
     * as the pivot. This avoids worst-case O(n²) on sorted/reverse-sorted input.
     * 
     * Partition: Rearranges elements so that:
     * - All elements before pivot position satisfy the ordering condition
     * - All elements after pivot position satisfy the reverse condition
     *
     * @return The final index of the pivot element
     */
    private static int partition(int[] arr, int[] indices, int low, int high, boolean descending, int depth) {
        // --- Median-of-Three Pivot Selection ---
        int mid = low + (high - low) / 2;

        // Sort the three candidates: arr[low], arr[mid], arr[high]
        // Then use the median (middle value) as pivot
        if (shouldSwap(arr[low], arr[mid], descending)) { swap(arr, indices, low, mid); }
        if (shouldSwap(arr[low], arr[high], descending)) { swap(arr, indices, low, high); }
        if (shouldSwap(arr[mid], arr[high], descending)) { swap(arr, indices, mid, high); }

        // Place pivot at high-1 position for partitioning
        swap(arr, indices, mid, high);
        int pivot = arr[high];

        // --- Lomuto Partition ---
        int i = low - 1; // Boundary of "correctly placed" elements

        for (int j = low; j < high; j++) {
            currentResult.totalComparisons++;

            // Check if current element should go before the pivot
            boolean condition = descending ? (arr[j] >= pivot) : (arr[j] <= pivot);
            if (condition) {
                i++;
                swap(arr, indices, i, j);
            }
        }

        // Place pivot in its correct final position
        swap(arr, indices, i + 1, high);
        return i + 1;
    }

    /**
     * Determines if two elements should be swapped based on sort direction.
     */
    private static boolean shouldSwap(int a, int b, boolean descending) {
        return descending ? (a < b) : (a > b);
    }

    /**
     * Swaps elements at positions i and j in both arrays.
     */
    private static void swap(int[] arr, int[] indices, int i, int j) {
        if (i == j) return;
        currentResult.totalSwaps++;

        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;

        int tempIdx = indices[i];
        indices[i] = indices[j];
        indices[j] = tempIdx;
    }

    /**
     * Insertion sort for small subarrays — Quick Sort optimization.
     * For n < 10, insertion sort's low overhead makes it faster than
     * the recursive partitioning approach.
     */
    private static void insertionSort(int[] arr, int[] indices, int low, int high, boolean descending) {
        for (int i = low + 1; i <= high; i++) {
            int key = arr[i];
            int keyIdx = indices[i];
            int j = i - 1;

            while (j >= low) {
                currentResult.totalComparisons++;
                boolean condition = descending ? (arr[j] < key) : (arr[j] > key);
                if (!condition) break;
                arr[j + 1] = arr[j];
                indices[j + 1] = indices[j];
                currentResult.totalSwaps++;
                j--;
            }
            arr[j + 1] = key;
            indices[j + 1] = keyIdx;
        }
    }

    /**
     * Utility: Converts array to readable string for trace output.
     */
    private static String arrayToString(int[] arr) {
        if (arr.length > 20) {
            return "[" + arr[0] + ", " + arr[1] + ", ... " + arr[arr.length - 1] + "] (" + arr.length + " elements)";
        }
        return Arrays.toString(arr);
    }
}
