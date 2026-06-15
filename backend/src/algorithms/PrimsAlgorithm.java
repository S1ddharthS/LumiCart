package algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Prim's Algorithm — Regional Warehouse Grid Consolidation
 * 
 * Purpose: Finds the Minimum Spanning Tree (MST) of an undirected weighted graph
 * representing delivery hubs and logistical paths. Starting from a central
 * warehouse (root node), it greedily selects the cheapest edge that connects
 * a new hub to the growing network.
 * 
 * Time Complexity: O(E log V) using PriorityQueue
 * Space Complexity: O(V + E)
 * 
 * eCommerce Feature: Optimizes the regional supply chain backbone by finding
 * the minimum-cost network that connects all warehouse hubs, reducing
 * infrastructure and shipping costs.
 */
public class PrimsAlgorithm {

    /**
     * Represents an edge in the MST result.
     */
    public static class MSTEdge {
        public int from;
        public int to;
        public int weight;

        public MSTEdge(int from, int to, int weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }

    /**
     * Result container for the MST computation.
     */
    public static class MSTResult {
        public List<MSTEdge> mstEdges;    // Edges forming the MST
        public int totalCost;              // Total weight of the MST
        public List<String> traceSteps;    // Step-by-step execution trace
        public int nodesProcessed;         // Number of nodes added to MST
        public long executionTimeNs;       // Execution time in nanoseconds

        public MSTResult() {
            this.mstEdges = new ArrayList<>();
            this.traceSteps = new ArrayList<>();
            this.totalCost = 0;
            this.nodesProcessed = 0;
        }
    }

    /**
     * Internal edge representation for the priority queue.
     * Comparable by weight for min-heap ordering.
     */
    private static class Edge implements Comparable<Edge> {
        int to;
        int from;
        int weight;

        Edge(int from, int to, int weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }

        @Override
        public int compareTo(Edge other) {
            return Integer.compare(this.weight, other.weight);
        }
    }

    /**
     * Computes the Minimum Spanning Tree using Prim's algorithm.
     * 
     * Algorithm Steps:
     * 1. Start from the root node (index 0 = central warehouse)
     * 2. Mark root as visited, add all its edges to the priority queue
     * 3. While MST doesn't include all vertices:
     *    a. Extract the minimum-weight edge from the priority queue
     *    b. If the destination vertex is already in MST, skip (avoid cycles)
     *    c. Add the edge to MST, mark destination as visited
     *    d. Add all edges from the new vertex to unvisited vertices into the queue
     * 4. Return MST edges and total cost
     *
     * @param adjacencyMatrix Square matrix where [i][j] = weight of edge (0 = no edge)
     * @param nodeCount Number of vertices (warehouse hubs)
     * @param nodeNames Names of the warehouse hubs for display
     * @return MSTResult containing MST edges, cost, and trace
     */
    public static MSTResult compute(int[][] adjacencyMatrix, int nodeCount, String[] nodeNames) {
        MSTResult result = new MSTResult();

        result.traceSteps.add("Prim's Algorithm initiated");
        result.traceSteps.add("Graph: " + nodeCount + " warehouse hubs");
        result.traceSteps.add("Starting from root node: " + nodeNames[0] + " (Central Warehouse)");

        long startTime = System.nanoTime();

        // Track which vertices are already in the MST
        boolean[] inMST = new boolean[nodeCount];

        // Min-heap priority queue for selecting cheapest available edge
        PriorityQueue<Edge> pq = new PriorityQueue<>();

        // Step 1: Start from vertex 0 (central warehouse)
        inMST[0] = true;
        result.nodesProcessed = 1;
        result.traceSteps.add("Added " + nodeNames[0] + " to MST (root)");

        // Add all edges from the starting vertex to the priority queue
        for (int j = 0; j < nodeCount; j++) {
            if (adjacencyMatrix[0][j] > 0) {
                pq.offer(new Edge(0, j, adjacencyMatrix[0][j]));
            }
        }

        int step = 1;

        // Step 2: Greedily grow the MST by adding cheapest edge
        while (!pq.isEmpty() && result.nodesProcessed < nodeCount) {
            // Extract minimum-weight edge from the queue
            Edge minEdge = pq.poll();

            // Skip if destination is already in MST (would create a cycle)
            if (inMST[minEdge.to]) {
                continue;
            }

            // Add this edge to the MST
            inMST[minEdge.to] = true;
            result.nodesProcessed++;
            result.totalCost += minEdge.weight;
            result.mstEdges.add(new MSTEdge(minEdge.from, minEdge.to, minEdge.weight));

            result.traceSteps.add("Step " + step + ": Added edge " +
                nodeNames[minEdge.from] + " → " + nodeNames[minEdge.to] +
                " (cost: " + minEdge.weight + ") | Running MST cost: " + result.totalCost);

            // Add all edges from the newly added vertex to unvisited vertices
            for (int j = 0; j < nodeCount; j++) {
                if (!inMST[j] && adjacencyMatrix[minEdge.to][j] > 0) {
                    pq.offer(new Edge(minEdge.to, j, adjacencyMatrix[minEdge.to][j]));
                }
            }

            step++;
        }

        result.executionTimeNs = System.nanoTime() - startTime;

        // Check if we successfully connected all nodes
        if (result.nodesProcessed < nodeCount) {
            result.traceSteps.add("⚠ WARNING: Graph is disconnected! Only " +
                result.nodesProcessed + "/" + nodeCount + " hubs connected.");
        } else {
            result.traceSteps.add("✓ MST Complete! All " + nodeCount + " hubs connected.");
        }

        result.traceSteps.add("Total MST Cost: " + result.totalCost);
        result.traceSteps.add("Edges in MST: " + result.mstEdges.size());
        result.traceSteps.add("Execution time: " + (result.executionTimeNs / 1000.0) +
            " μs | Complexity: O(E log V)");

        return result;
    }
}
