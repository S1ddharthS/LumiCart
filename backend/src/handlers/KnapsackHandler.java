package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import algorithms.Knapsack01;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * HTTP Handler for 0/1 Knapsack optimization.
 * Endpoint: POST /api/knapsack
 * 
 * Receives items with values and weights plus a capacity constraint,
 * returns the optimal selection maximizing value.
 */
public class KnapsackHandler implements HttpHandler {

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

            // Expected: {"values": [60, 100, 120], "weights": [10, 20, 30], "capacity": 50, "names": ["A", "B", "C"]}
            int[] values = extractIntArray(body, "values");
            int[] weights = extractIntArray(body, "weights");
            int capacity = extractInt(body, "capacity");
            String[] names = extractStringArray(body, "names");

            // Default names if not provided
            if (names.length == 0) {
                names = new String[values.length];
                for (int i = 0; i < values.length; i++) names[i] = "Item " + i;
            }

            Knapsack01.KnapsackResult result = Knapsack01.solve(values, weights, capacity, names);

            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"algorithm\": \"0/1 Knapsack (Dynamic Programming)\",");
            json.append("\"maxValue\": ").append(result.maxValue).append(",");
            json.append("\"totalWeight\": ").append(result.totalWeight).append(",");
            json.append("\"capacity\": ").append(capacity).append(",");
            json.append("\"executionTimeUs\": ").append(result.executionTimeNs / 1000.0).append(",");
            json.append("\"complexity\": \"O(n × W) = O(").append(values.length).append(" × ").append(capacity).append(")\",");

            // Selected items
            json.append("\"selectedItems\": [");
            for (int i = 0; i < result.selectedItems.size(); i++) {
                if (i > 0) json.append(",");
                json.append(result.selectedItems.get(i));
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
