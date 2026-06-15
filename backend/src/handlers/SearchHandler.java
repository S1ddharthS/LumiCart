package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import algorithms.BinarySearch;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * HTTP Handler for Binary Search operations.
 * Endpoint: POST /api/search
 * 
 * Receives a JSON payload with a sorted array and target value,
 * invokes the BinarySearch algorithm, and returns the result
 * with full diagnostic trace.
 */
public class SearchHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Set CORS headers for cross-origin requests from the Vite dev server
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().set("Content-Type", "application/json");

        // Handle CORS preflight
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            String error = "{\"error\": \"Method not allowed. Use POST.\"}";
            exchange.sendResponseHeaders(405, error.getBytes().length);
            exchange.getResponseBody().write(error.getBytes());
            exchange.getResponseBody().close();
            return;
        }

        try {
            // Read the request body
            String body = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining());

            // Parse JSON manually (no external library dependency)
            // Expected format: {"sortedArray": ["a","b","c"], "target": "b"}
            String[] items = extractStringArray(body, "sortedArray");
            String target = extractString(body, "target");

            // Execute Binary Search algorithm
            BinarySearch.SearchResult result = BinarySearch.search(items, target);

            // Build JSON response
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"algorithm\": \"Binary Search\",");
            json.append("\"foundIndex\": ").append(result.foundIndex).append(",");
            json.append("\"target\": \"").append(escapeJson(result.targetValue)).append("\",");
            json.append("\"totalComparisons\": ").append(result.totalComparisons).append(",");
            json.append("\"executionTimeUs\": ").append(result.executionTimeNs / 1000.0).append(",");
            json.append("\"complexity\": \"O(log n)\",");
            json.append("\"inputSize\": ").append(items.length).append(",");

            // Inspected indices
            json.append("\"inspectedIndices\": [");
            for (int i = 0; i < result.inspected.size(); i++) {
                if (i > 0) json.append(",");
                json.append(result.inspected.get(i));
            }
            json.append("],");

            // Trace steps
            json.append("\"trace\": [");
            for (int i = 0; i < result.traceSteps.size(); i++) {
                if (i > 0) json.append(",");
                json.append("\"").append(escapeJson(result.traceSteps.get(i))).append("\"");
            }
            json.append("]");
            json.append("}");

            String response = json.toString();
            exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
            exchange.getResponseBody().write(response.getBytes(StandardCharsets.UTF_8));

        } catch (Exception e) {
            String error = "{\"error\": \"" + escapeJson(e.getMessage()) + "\"}";
            exchange.sendResponseHeaders(500, error.getBytes().length);
            exchange.getResponseBody().write(error.getBytes());
        } finally {
            exchange.getResponseBody().close();
        }
    }

    /**
     * Extracts a string array from a simple JSON object.
     * Handles format: "key": ["val1", "val2", ...]
     */
    private String[] extractStringArray(String json, String key) {
        int start = json.indexOf("\"" + key + "\"");
        if (start == -1) return new String[0];

        int arrStart = json.indexOf("[", start);
        int arrEnd = json.indexOf("]", arrStart);
        if (arrStart == -1 || arrEnd == -1) return new String[0];

        String arrContent = json.substring(arrStart + 1, arrEnd).trim();
        if (arrContent.isEmpty()) return new String[0];

        String[] parts = arrContent.split(",");
        String[] result = new String[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = parts[i].trim().replaceAll("^\"|\"$", "");
        }
        return result;
    }

    /**
     * Extracts a string value from a simple JSON object.
     * Handles format: "key": "value"
     */
    private String extractString(String json, String key) {
        int start = json.indexOf("\"" + key + "\"");
        if (start == -1) return "";
        int colonIdx = json.indexOf(":", start);
        int quoteStart = json.indexOf("\"", colonIdx + 1);
        int quoteEnd = json.indexOf("\"", quoteStart + 1);
        if (quoteStart == -1 || quoteEnd == -1) return "";
        return json.substring(quoteStart + 1, quoteEnd);
    }

    /**
     * Escapes special characters for JSON string values.
     */
    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
