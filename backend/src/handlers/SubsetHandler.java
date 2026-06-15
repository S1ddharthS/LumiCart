package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import algorithms.SubsetSumBB;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * HTTP Handler for Sum of Subsets (Branch and Bound) operations.
 * Endpoint: POST /api/subset
 * 
 * Receives coupon values and a target sum, finds the exact subset
 * that matches the target or reports no solution exists.
 */
public class SubsetHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
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
            String body = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining());

            // Expected: {"values": [5, 10, 15, 20], "target": 25, "names": ["C1", "C2", "C3", "C4"]}
            int[] values = extractIntArray(body, "values");
            int target = extractInt(body, "target");
            String[] names = extractStringArray(body, "names");

            if (names.length == 0) {
                names = new String[values.length];
                for (int i = 0; i < values.length; i++) names[i] = "Coupon-" + (i + 1);
            }

            SubsetSumBB.SubsetResult result = SubsetSumBB.solve(values, target, names);

            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"algorithm\": \"Sum of Subsets (Branch & Bound)\",");
            json.append("\"found\": ").append(result.found).append(",");
            json.append("\"targetSum\": ").append(result.targetSum).append(",");
            json.append("\"achievedSum\": ").append(result.achievedSum).append(",");
            json.append("\"nodesExplored\": ").append(result.nodesExplored).append(",");
            json.append("\"nodesPruned\": ").append(result.nodesPruned).append(",");
            json.append("\"executionTimeUs\": ").append(result.executionTimeNs / 1000.0).append(",");
            json.append("\"complexity\": \"O(2^n) worst case, pruned\",");

            // Selected indices
            json.append("\"selectedIndices\": [");
            for (int i = 0; i < result.selectedIndices.size(); i++) {
                if (i > 0) json.append(",");
                json.append(result.selectedIndices.get(i));
            }
            json.append("],");

            // Selected values
            json.append("\"selectedValues\": [");
            for (int i = 0; i < result.selectedValues.size(); i++) {
                if (i > 0) json.append(",");
                json.append(result.selectedValues.get(i));
            }
            json.append("],");

            // Trace
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
            sendError(exchange, 500, e.getMessage());
        } finally {
            exchange.getResponseBody().close();
        }
    }

    private int[] extractIntArray(String json, String key) {
        int start = json.indexOf("\"" + key + "\"");
        if (start == -1) return new int[0];
        int arrStart = json.indexOf("[", start);
        int arrEnd = findMatchingBracket(json, arrStart);
        if (arrStart == -1 || arrEnd == -1) return new int[0];
        String content = json.substring(arrStart + 1, arrEnd).trim();
        if (content.isEmpty()) return new int[0];
        String[] parts = content.split(",");
        int[] result = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = Integer.parseInt(parts[i].trim());
        }
        return result;
    }

    private int extractInt(String json, String key) {
        int start = json.indexOf("\"" + key + "\"");
        if (start == -1) return 0;
        int colonIdx = json.indexOf(":", start);
        StringBuilder numStr = new StringBuilder();
        for (int i = colonIdx + 1; i < json.length(); i++) {
            char c = json.charAt(i);
            if (Character.isDigit(c) || c == '-') numStr.append(c);
            else if (numStr.length() > 0) break;
        }
        return numStr.length() > 0 ? Integer.parseInt(numStr.toString()) : 0;
    }

    private String[] extractStringArray(String json, String key) {
        int start = json.indexOf("\"" + key + "\"");
        if (start == -1) return new String[0];
        int arrStart = json.indexOf("[", start);
        int arrEnd = findMatchingBracket(json, arrStart);
        if (arrStart == -1 || arrEnd == -1) return new String[0];
        String content = json.substring(arrStart + 1, arrEnd).trim();
        if (content.isEmpty()) return new String[0];
        String[] parts = content.split(",");
        String[] result = new String[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = parts[i].trim().replaceAll("^\"|\"$", "");
        }
        return result;
    }

    private int findMatchingBracket(String json, int openIdx) {
        int depth = 0;
        for (int i = openIdx; i < json.length(); i++) {
            if (json.charAt(i) == '[') depth++;
            if (json.charAt(i) == ']') depth--;
            if (depth == 0) return i;
        }
        return -1;
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
