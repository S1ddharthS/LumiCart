package algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Kruskal's Algorithm — Decentralized Third-Party Logistics Linking
 * 
 * Purpose: Finds the Minimum Spanning Tree (MST) of an undirected weighted graph
 * by sorting all edges by weight and greedily adding them if they don't create a cycle.
 * Uses Disjoint Set Union (Union-Find) with path compression and union by rank
 * for efficient cycle detection.
 * 
 * Time Complexity: O(E log E) for edge sorting + O(E · α(V)) for union-find operations
 *                  ≈ O(E log E) overall, where α is the inverse Ackermann function
 * Space Complexity: O(V + E)
 * 
 * eCommerce Feature: Optimizes a decentralized delivery network by linking
 * third-party logistics providers with minimum total connection cost,
 * ensuring no redundant (cyclic) routes.
 */
public class KruskalsAlgorithm {

    /**
     * Represents an edge with source, destination, and weight.
     */
    public static class Edge {
        public int from;
        public int to;
        public int weight;

        public Edge(int from, int to, int weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }

    /**
     * Result container for Kruskal's MST computation.
     */
    public static class MSTResult {
        public List<Edge> mstEdges;        // Edges forming the MST
        public int totalCost;               // Total weight of the MST
        public List<String> traceSteps;     // Step-by-step execution trace
        public int edgesConsidered;         // Total edges examined
        public int edgesAccepted;           // Edges added to MST
        public int edgesRejected;           // Edges rejected (would create cycle)
        public long executionTimeNs;        // Execution time in nanoseconds

        public MSTResult() {
            this.mstEdges = new ArrayList<>();
            this.traceSteps = new ArrayList<>();
            this.totalCost = 0;
            this.edgesConsidered = 0;
            this.edgesAccepted = 0;
            this.edgesRejected = 0;
        }
    }

    /**
     * Disjoint Set Union (Union-Find) Data Structure.
     * 
     * Two key optimizations:
     * 1. Path Compression: During find(), makes every node on the path
     *    point directly to the root, flattening the tree structure.
     * 2. Union by Rank: During union(), attaches the shorter tree under
     *    the taller tree to minimize tree height.
     * 
     * Together these achieve nearly O(1) amortized time per operation.
     */
    private static class DSU {
        int[] parent;  // parent[i] = parent of node i
        int[] rank;    // rank[i] = approximate height of subtree rooted at i
        List<String> traceSteps;

        /**
         * Initialize DSU: each element is its own set (self-parent).
         */
        DSU(int n, List<String> traceSteps) {
            parent = new int[n];
            rank = new int[n];
            this.traceSteps = traceSteps;
            for (int i = 0; i < n; i++) {
                parent[i] = i;  // Each node is its own root initially
                rank[i] = 0;    // All trees start with height 0
            }
        }

        /**
         * Find the root representative of the set containing element x.
         * Uses PATH COMPRESSION: after finding root, all nodes on the
         * path from x to root are made direct children of root.
         * This ensures future find() calls are nearly O(1).
         *
         * @param x The element to find the root of
         * @return The root representative of x's set
         */
        int find(int x) {
            if (parent[x] != x) {
                // Path compression: recursively find root and flatten
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }

        /**
         * Union (merge) the sets containing elements x and y.
         * Uses UNION BY RANK: the shorter tree is attached under the
         * taller tree to minimize the resulting tree height.
         *
         * @param x First element
         * @param y Second element
         * @return true if sets were merged, false if already in same set
         */
        boolean union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);

            // Already in the same set — merging would create a cycle
            if (rootX == rootY) return false;

            // Union by rank: attach smaller tree under larger tree
            if (rank[rootX] < rank[rootY]) {
                parent[rootX] = rootY;
            } else if (rank[rootX] > rank[rootY]) {
                parent[rootY] = rootX;
            } else {
                // Equal ranks: arbitrarily choose, increment rank of new root
                parent[rootY] = rootX;
                rank[rootX]++;
            }
            return true;
        }
    }

    /**
     * Computes the Minimum Spanning Tree using Kruskal's algorithm.
     * 
     * Algorithm Steps:
     * 1. Collect all edges from the adjacency matrix
     * 2. Sort edges by weight in ascending order
     * 3. Initialize DSU with V vertices
     * 4. For each edge (in sorted order):
     *    a. If the two endpoints are in different sets → add edge to MST, union the sets
     *    b. If same set → skip (adding would create a cycle)
     * 5. Stop when MST has V-1 edges (complete tree)
     *
     * @param adjacencyMatrix Square matrix where [i][j] = weight (0 = no edge)
     * @param nodeCount Number of vertices
     * @param nodeNames Display names for vertices
     * @return MSTResult containing MST edges, cost, and trace
     */
    public static MSTResult compute(int[][] adjacencyMatrix, int nodeCount, String[] nodeNames) {
        MSTResult result = new MSTResult();

        result.traceSteps.add("Kruskal's Algorithm initiated");
        result.traceSteps.add("Graph: " + nodeCount + " nodes");

        long startTime = System.nanoTime();

        // Step 1: Extract all edges from the adjacency matrix
        List<Edge> allEdges = new ArrayList<>();
        for (int i = 0; i < nodeCount; i++) {
            for (int j = i + 1; j < nodeCount; j++) { // j = i+1 to avoid duplicate edges
                if (adjacencyMatrix[i][j] > 0) {
                    allEdges.add(new Edge(i, j, adjacencyMatrix[i][j]));
                }
            }
        }

        result.traceSteps.add("Total edges extracted: " + allEdges.size());

        // Step 2: Sort all edges by weight (ascending) — the greedy criterion
        allEdges.sort(Comparator.comparingInt(e -> e.weight));

        result.traceSteps.add("Edges sorted by weight (ascending):");
        for (int i = 0; i < Math.min(allEdges.size(), 10); i++) {
            Edge e = allEdges.get(i);
            result.traceSteps.add("  " + nodeNames[e.from] + " ↔ " + nodeNames[e.to] + " (weight: " + e.weight + ")");
        }
        if (allEdges.size() > 10) {
            result.traceSteps.add("  ... and " + (allEdges.size() - 10) + " more edges");
        }

        // Step 3: Initialize Disjoint Set Union
        DSU dsu = new DSU(nodeCount, result.traceSteps);
        result.traceSteps.add("DSU initialized with " + nodeCount + " singleton sets");

        // Step 4: Process edges in order of increasing weight
        int step = 1;
        for (Edge edge : allEdges) {
            result.edgesConsidered++;

            // Try to merge the sets containing the edge's endpoints
            if (dsu.union(edge.from, edge.to)) {
                // Sets were different → edge is safe to add (no cycle)
                result.mstEdges.add(edge);
                result.totalCost += edge.weight;
                result.edgesAccepted++;

                result.traceSteps.add("Step " + step + ": ACCEPTED edge " +
                    nodeNames[edge.from] + " ↔ " + nodeNames[edge.to] +
                    " (weight: " + edge.weight + ") | Running MST cost: " + result.totalCost);

                step++;

                // MST is complete when we have V-1 edges
                if (result.edgesAccepted == nodeCount - 1) {
                    result.traceSteps.add("MST complete with " + (nodeCount - 1) + " edges!");
                    break;
                }
            } else {
                // Same set → adding this edge would create a cycle
                result.edgesRejected++;
                result.traceSteps.add("  REJECTED edge " +
                    nodeNames[edge.from] + " ↔ " + nodeNames[edge.to] +
                    " (weight: " + edge.weight + ") — would create cycle");
            }
        }

        result.executionTimeNs = System.nanoTime() - startTime;

        if (result.edgesAccepted < nodeCount - 1) {
            result.traceSteps.add("⚠ WARNING: Graph is disconnected! MST incomplete.");
        } else {
            result.traceSteps.add("✓ MST Complete! All " + nodeCount + " hubs connected.");
        }

        result.traceSteps.add("Total MST Cost: " + result.totalCost);
        result.traceSteps.add("Edges: " + result.edgesAccepted + " accepted, " + result.edgesRejected + " rejected out of " + result.edgesConsidered + " considered");
        result.traceSteps.add("Execution time: " + (result.executionTimeNs / 1000.0) +
            " μs | Complexity: O(E log E) where E=" + allEdges.size());

        return result;
    }
}
