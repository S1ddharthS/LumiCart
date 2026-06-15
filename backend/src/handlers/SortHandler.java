package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import algorithms.MergeSort;
import algorithms.QuickSort;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * HTTP Handler for Sorting operations — Merge Sort and Quick Sort.
 * 
 * Endpoints:
 *   POST /api/sort/merge  → Merge Sort by price (ascending)
 *   POST /api/sort/quick  → Quick Sort by rating (descending)
 */
public class SortHandler implements HttpHandler {

    private final String sortType; // "merge" or "quick"

    public SortHandler(String sortType) {
        this.sortType = sortType;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // CORS headers
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().set("Content-Type", "application/json");

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendError(exchange, 405, "Method not allowed. Use POST.");
            return;
        }

        try {
            // Read request body
            String body = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining());

            // Parse input array — expected: {"values": [10, 25, 5, ...], "indices": [0, 1, 2, ...]}
            int[] values = extractIntArray(body, "values");
            int[] indices = extractIntArray(body, "indices");

            // If indices not provided, generate default sequential indices
            if (indices.length == 0) {
                indices = new int[values.length];
                for (int i = 0; i < values.length; i++) indices[i] = i;
            }

            String response;

            if ("merge".equals(sortType)) {
                // Execute Merge Sort (stable, ascending for prices)
                MergeSort.SortResult result = MergeSort.sort(values, indices);
                response = buildMergeSortResponse(result);
            } else {
                // Execute Quick Sort (descending for ratings — highest first)
                QuickSort.SortResult result = QuickSort.sort(values, indices, true);
                response = buildQuickSortResponse(result);
            }

            exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
            exchange.getResponseBody().write(response.getBytes(StandardCharsets.UTF_8));

        } catch (Exception e) {
            sendError(exchange, 500, e.getMessage());
        } finally {
            exchange.getResponseBody().close();
        }
    }

    /**
     * Builds JSON response for Merge Sort result.
     */
    private String buildMergeSortResponse(MergeSort.SortResult result) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"algorithm\": \"Merge Sort\",");
        json.append("\"sortType\": \"price_ascending\",");
        json.append("\"inputSize\": ").append(result.inputSize).append(",");
        json.append("\"totalComparisons\": ").append(result.totalComparisons).append(",");
        json.append("\"totalMerges\": ").append(result.totalMerges).append(",");
        json.append("\"executionTimeUs\": ").append(result.executionTimeNs / 1000.0).append(",");
        json.append("\"complexity\": \"O(n log n)\",");

        // Sorted array
        json.append("\"sortedArray\": ").append(intArrayToJson(result.sortedArray)).append(",");

        // Original indices mapping (so frontend knows how to reorder product cards)
        json.append("\"originalIndices\": ").append(intArrayToJson(result.originalIndices)).append(",");

        // Trace steps
        json.append("\"trace\": [");
        for (int i = 0; i < result.traceSteps.size(); i++) {
            if (i > 0) json.append(",");
            json.append("\"").append(escapeJson(result.traceSteps.get(i))).append("\"");
        }
        json.append("]");
        json.append("}");

        return json.toString();
    }

    /**
     * Builds JSON response for Quick Sort result.
     */
    private String buildQuickSortResponse(QuickSort.SortResult result) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"algorithm\": \"Quick Sort\",");
        json.append("\"sortType\": \"rating_descending\",");
        json.append("\"inputSize\": ").append(result.inputSize).append(",");
        json.append("\"totalComparisons\": ").append(result.totalComparisons).append(",");
        json.append("\"totalSwaps\": ").append(result.totalSwaps).append(",");
        json.append("\"executionTimeUs\": ").append(result.executionTimeNs / 1000.0).append(",");
        json.append("\"complexity\": \"O(n log n) average\",");

        json.append("\"sortedArray\": ").append(intArrayToJson(result.sortedArray)).append(",");
        json.append("\"originalIndices\": ").append(intArrayToJson(result.originalIndices)).append(",");

        json.append("\"trace\": [");
        for (int i = 0; i < result.traceSteps.size(); i++) {
            if (i > 0) json.append(",");
            json.append("\"").append(escapeJson(result.traceSteps.get(i))).append("\"");
        }
        json.append("]");
        json.append("}");

        return json.toString();
    }

    /**
     * Parses an integer array from JSON.
     * Handles format: "key": [1, 2, 3, ...]
     */
    private int[] extractIntArray(String json, String key) {
        int start = json.indexOf("\"" + key + "\"");
        if (start == -1) return new int[0];

        int arrStart = json.indexOf("[", start);
        int arrEnd = findMatchingBracket(json, arrStart);
        if (arrStart == -1 || arrEnd == -1) return new int[0];

        String arrContent = json.substring(arrStart + 1, arrEnd).trim();
        if (arrContent.isEmpty()) return new int[0];

        String[] parts = arrContent.split(",");
        int[] result = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = Integer.parseInt(parts[i].trim());
        }
        return result;
    }

    /**
     * Finds the matching closing bracket for an opening bracket.
     */
    private int findMatchingBracket(String json, int openIdx) {
        int depth = 0;
        for (int i = openIdx; i < json.length(); i++) {
            if (json.charAt(i) == '[') depth++;
            if (json.charAt(i) == ']') depth--;
            if (depth == 0) return i;
        }
        return -1;
    }

    private String intArrayToJson(int[] arr) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(arr[i]);
        }
        sb.append("]");
        return sb.toString();
    }

    private void sendError(HttpExchange exchange, int code, String message) throws IOException {
        String error = "{\"error\": \"" + escapeJson(message) + "\"}";
        exchange.sendResponseHeaders(code, error.getBytes().length);
        exchange.getResponseBody().write(error.getBytes());
        exchange.getResponseBody().close();
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
