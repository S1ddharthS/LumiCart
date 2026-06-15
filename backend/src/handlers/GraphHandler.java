package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import algorithms.PrimsAlgorithm;
import algorithms.KruskalsAlgorithm;
import algorithms.FloydWarshall;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * HTTP Handler for Graph Algorithm operations.
 * 
 * Endpoints:
 *   POST /api/graph/prims    → Prim's MST Algorithm
 *   POST /api/graph/kruskals → Kruskal's MST Algorithm
 *   POST /api/graph/floyd    → Floyd-Warshall All-Pairs Shortest Path
 */
public class GraphHandler implements HttpHandler {

    private final String algorithm; // "prims", "kruskals", or "floyd"

    public GraphHandler(String algorithm) {
        this.algorithm = algorithm;
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
            String body = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining());

            // Parse common inputs: adjacency matrix and node names
            int nodeCount = extractInt(body, "nodeCount");
            String[] nodeNames = extractStringArray(body, "nodeNames");
            int[][] adjacencyMatrix = extractMatrix(body, "matrix", nodeCount);

            String response;

            switch (algorithm) {
                case "prims":
                    response = handlePrims(adjacencyMatrix, nodeCount, nodeNames);
                    break;
                case "kruskals":
                    response = handleKruskals(adjacencyMatrix, nodeCount, nodeNames);
                    break;
                case "floyd":
                    response = handleFloyd(adjacencyMatrix, nodeCount, nodeNames);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
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
     * Handles Prim's MST computation and builds JSON response.
     */
    private String handlePrims(int[][] matrix, int nodeCount, String[] nodeNames) {
        PrimsAlgorithm.MSTResult result = PrimsAlgorithm.compute(matrix, nodeCount, nodeNames);

        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"algorithm\": \"Prim's Algorithm\",");
        json.append("\"totalCost\": ").append(result.totalCost).append(",");
        json.append("\"nodesProcessed\": ").append(result.nodesProcessed).append(",");
        json.append("\"executionTimeUs\": ").append(result.executionTimeNs / 1000.0).append(",");
        json.append("\"complexity\": \"O(E log V)\",");
        json.append("\"nodeCount\": ").append(nodeCount).append(",");

        // MST edges
        json.append("\"mstEdges\": [");
        for (int i = 0; i < result.mstEdges.size(); i++) {
            if (i > 0) json.append(",");
            PrimsAlgorithm.MSTEdge e = result.mstEdges.get(i);
            json.append("{\"from\":").append(e.from)
                .append(",\"to\":").append(e.to)
                .append(",\"weight\":").append(e.weight).append("}");
        }
        json.append("],");

        // Trace
        appendTrace(json, result.traceSteps);
        json.append("}");

        return json.toString();
    }

    /**
     * Handles Kruskal's MST computation and builds JSON response.
     */
    private String handleKruskals(int[][] matrix, int nodeCount, String[] nodeNames) {
        KruskalsAlgorithm.MSTResult result = KruskalsAlgorithm.compute(matrix, nodeCount, nodeNames);

        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"algorithm\": \"Kruskal's Algorithm\",");
        json.append("\"totalCost\": ").append(result.totalCost).append(",");
        json.append("\"edgesAccepted\": ").append(result.edgesAccepted).append(",");
        json.append("\"edgesRejected\": ").append(result.edgesRejected).append(",");
        json.append("\"edgesConsidered\": ").append(result.edgesConsidered).append(",");
        json.append("\"executionTimeUs\": ").append(result.executionTimeNs / 1000.0).append(",");
        json.append("\"complexity\": \"O(E log E)\",");
        json.append("\"nodeCount\": ").append(nodeCount).append(",");

        // MST edges
        json.append("\"mstEdges\": [");
        for (int i = 0; i < result.mstEdges.size(); i++) {
            if (i > 0) json.append(",");
            KruskalsAlgorithm.Edge e = result.mstEdges.get(i);
            json.append("{\"from\":").append(e.from)
                .append(",\"to\":").append(e.to)
                .append(",\"weight\":").append(e.weight).append("}");
        }
        json.append("],");

        appendTrace(json, result.traceSteps);
        json.append("}");

        return json.toString();
    }

    /**
     * Handles Floyd-Warshall computation and builds JSON response.
     */
    private String handleFloyd(int[][] matrix, int nodeCount, String[] nodeNames) {
        // Prepare the adjacency matrix for Floyd-Warshall
        // Convert 0 (no edge) to INF, keep diagonal as 0
        int[][] floydMatrix = new int[nodeCount][nodeCount];
        for (int i = 0; i < nodeCount; i++) {
            for (int j = 0; j < nodeCount; j++) {
                if (i == j) {
                    floydMatrix[i][j] = 0;
                } else if (matrix[i][j] == 0) {
                    floydMatrix[i][j] = FloydWarshall.INF;
                } else {
                    floydMatrix[i][j] = matrix[i][j];
                }
            }
        }

        FloydWarshall.FloydResult result = FloydWarshall.compute(floydMatrix, nodeCount, nodeNames);

        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"algorithm\": \"Floyd-Warshall\",");
        json.append("\"relaxations\": ").append(result.relaxations).append(",");
        json.append("\"executionTimeUs\": ").append(result.executionTimeNs / 1000.0).append(",");
        json.append("\"complexity\": \"O(V³)\",");
        json.append("\"nodeCount\": ").append(nodeCount).append(",");

        // Distance matrix
        json.append("\"distanceMatrix\": [");
        for (int i = 0; i < nodeCount; i++) {
            if (i > 0) json.append(",");
            json.append("[");
            for (int j = 0; j < nodeCount; j++) {
                if (j > 0) json.append(",");
                json.append(result.distanceMatrix[i][j] >= FloydWarshall.INF ? -1 : result.distanceMatrix[i][j]);
            }
            json.append("]");
        }
        json.append("],");

        // Node names
        json.append("\"nodeNames\": [");
        for (int i = 0; i < nodeNames.length; i++) {
            if (i > 0) json.append(",");
            json.append("\"").append(escapeJson(nodeNames[i])).append("\"");
        }
        json.append("],");

        appendTrace(json, result.traceSteps);
        json.append("}");

        return json.toString();
    }

    // --- JSON Parsing Utilities ---

    private int extractInt(String json, String key) {
        int start = json.indexOf("\"" + key + "\"");
        if (start == -1) return 0;
        int colonIdx = json.indexOf(":", start);
        int end = colonIdx + 1;
        while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == ' ')) end++;
        String val = json.substring(colonIdx + 1, end).trim();
        return Integer.parseInt(val);
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

    private int[][] extractMatrix(String json, String key, int size) {
        int[][] matrix = new int[size][size];
        int start = json.indexOf("\"" + key + "\"");
        if (start == -1) return matrix;

        int outerStart = json.indexOf("[", start);
        if (outerStart == -1) return matrix;

        int pos = outerStart + 1;
        for (int i = 0; i < size; i++) {
            int rowStart = json.indexOf("[", pos);
            int rowEnd = json.indexOf("]", rowStart);
            if (rowStart == -1 || rowEnd == -1) break;

            String rowContent = json.substring(rowStart + 1, rowEnd).trim();
            String[] vals = rowContent.split(",");
            for (int j = 0; j < Math.min(vals.length, size); j++) {
                matrix[i][j] = Integer.parseInt(vals[j].trim());
            }
            pos = rowEnd + 1;
        }
        return matrix;
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

    private void appendTrace(StringBuilder json, List<String> trace) {
        json.append("\"trace\": [");
        for (int i = 0; i < trace.size(); i++) {
            if (i > 0) json.append(",");
            json.append("\"").append(escapeJson(trace.get(i))).append("\"");
        }
        json.append("]");
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
