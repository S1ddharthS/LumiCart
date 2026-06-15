package algorithms;

import java.util.ArrayList;
import java.util.List;

/**
 * Floyd-Warshall Algorithm — All-Pairs Next-Day Express Matrix
 * 
 * Purpose: Computes the shortest path between ALL pairs of warehouse hubs
 * in the delivery network. Uses dynamic programming with a triple-nested
 * loop to systematically consider every possible intermediate hub.
 * 
 * Time Complexity: O(V³) — three nested loops over all vertices
 * Space Complexity: O(V²) — distance matrix + next-hop matrix
 * 
 * eCommerce Feature: Generates a complete routing grid that shows the
 * minimum shipping time/cost between ANY two hubs, enabling instant
 * route lookups for next-day delivery planning.
 */
public class FloydWarshall {

    /**
     * Special constant representing infinity (no direct path exists).
     * Using Integer.MAX_VALUE / 2 to prevent overflow during addition.
     */
    public static final int INF = 99999;

    /**
     * Result container for the all-pairs shortest path computation.
     */
    public static class FloydResult {
        public int[][] distanceMatrix;   // Shortest distances between all pairs
        public int[][] nextHop;          // Next hop matrix for path reconstruction
        public List<String> traceSteps;  // Step-by-step execution trace
        public int nodeCount;            // Number of vertices
        public int relaxations;          // Number of distance relaxations performed
        public long executionTimeNs;     // Execution time in nanoseconds

        public FloydResult() {
            this.traceSteps = new ArrayList<>();
            this.relaxations = 0;
        }
    }

    /**
     * Computes all-pairs shortest paths using Floyd-Warshall dynamic programming.
     * 
     * Algorithm — The DP Recurrence:
     * For each intermediate vertex k (from 0 to V-1):
     *   For each source vertex i:
     *     For each destination vertex j:
     *       If dist[i][k] + dist[k][j] < dist[i][j]:
     *         Update dist[i][j] = dist[i][k] + dist[k][j]
     *         Update next[i][j] = next[i][k]  (for path reconstruction)
     * 
     * Key Insight: After considering intermediate vertex k, dist[i][j] holds
     * the shortest path from i to j using only vertices {0, 1, ..., k} as
     * intermediate hops. After all V iterations, we have the global optimum.
     *
     * @param adjacencyMatrix Square matrix: [i][j] = direct distance (0 on diagonal, INF if no edge)
     * @param nodeCount Number of vertices in the graph
     * @param nodeNames Display names for vertices
     * @return FloydResult containing distance matrix, path info, and trace
     */
    public static FloydResult compute(int[][] adjacencyMatrix, int nodeCount, String[] nodeNames) {
        FloydResult result = new FloydResult();
        result.nodeCount = nodeCount;

        result.traceSteps.add("Floyd-Warshall Algorithm initiated");
        result.traceSteps.add("Graph: " + nodeCount + " vertices × " + nodeCount + " = " +
            (nodeCount * nodeCount) + " pairs to compute");

        long startTime = System.nanoTime();

        // Initialize the distance matrix as a copy of the adjacency matrix
        int[][] dist = new int[nodeCount][nodeCount];
        int[][] next = new int[nodeCount][nodeCount];

        for (int i = 0; i < nodeCount; i++) {
            for (int j = 0; j < nodeCount; j++) {
                dist[i][j] = adjacencyMatrix[i][j];
                // Initialize next-hop: if there's a direct edge, next hop is j
                if (adjacencyMatrix[i][j] != INF && i != j) {
                    next[i][j] = j;
                } else {
                    next[i][j] = -1; // No path known yet
                }
            }
        }

        result.traceSteps.add("Initial distance matrix loaded. INF=" + INF + " represents no direct path.");

        // --- Core Floyd-Warshall Triple Loop ---
        // k = intermediate vertex being considered
        for (int k = 0; k < nodeCount; k++) {
            result.traceSteps.add("Considering intermediate hub: " + nodeNames[k] + " (k=" + k + ")");
            int relaxationsThisRound = 0;

            // i = source vertex
            for (int i = 0; i < nodeCount; i++) {
                // j = destination vertex
                for (int j = 0; j < nodeCount; j++) {
                    // Skip self-loops and unreachable intermediate paths
                    if (i == j) continue;
                    if (dist[i][k] >= INF || dist[k][j] >= INF) continue;

                    // DP transition: can we get a shorter path via vertex k?
                    int newDist = dist[i][k] + dist[k][j];

                    if (newDist < dist[i][j]) {
                        // Relaxation: found a shorter path through vertex k
                        result.traceSteps.add("  Relaxed: dist[" + nodeNames[i] + "][" + nodeNames[j] +
                            "] = " + (dist[i][j] >= INF ? "INF" : dist[i][j]) +
                            " → " + newDist + " (via " + nodeNames[k] + ")");

                        dist[i][j] = newDist;
                        next[i][j] = next[i][k]; // Update path reconstruction
                        result.relaxations++;
                        relaxationsThisRound++;
                    }
                }
            }

            if (relaxationsThisRound == 0) {
                result.traceSteps.add("  No relaxations in this round (hub " + nodeNames[k] + " didn't improve any paths)");
            } else {
                result.traceSteps.add("  " + relaxationsThisRound + " path(s) improved through " + nodeNames[k]);
            }
        }

        result.executionTimeNs = System.nanoTime() - startTime;
        result.distanceMatrix = dist;
        result.nextHop = next;

        // Log final matrix summary
        result.traceSteps.add("✓ All-pairs shortest path computation complete!");
        result.traceSteps.add("Total relaxations: " + result.relaxations);
        result.traceSteps.add("Execution time: " + (result.executionTimeNs / 1000.0) +
            " μs | Complexity: O(V³) = O(" + nodeCount + "³) = O(" + (nodeCount * nodeCount * nodeCount) + ")");

        return result;
    }

    /**
     * Reconstructs the actual shortest path from source to destination
     * using the next-hop matrix computed by Floyd-Warshall.
     *
     * @param next The next-hop matrix
     * @param source Source vertex index
     * @param dest Destination vertex index
     * @param nodeNames Vertex display names
     * @return List of vertex names along the shortest path
     */
    public static List<String> reconstructPath(int[][] next, int source, int dest, String[] nodeNames) {
        List<String> path = new ArrayList<>();

        if (next[source][dest] == -1) {
            return path; // No path exists
        }

        path.add(nodeNames[source]);
        int current = source;

        // Follow the next-hop pointers from source to destination
        while (current != dest) {
            current = next[current][dest];
            if (current == -1) break; // Safety check
            path.add(nodeNames[current]);
        }

        return path;
    }
}
