import com.sun.net.httpserver.HttpServer;
import handlers.*;

import java.net.InetSocketAddress;

/**
 * AetherShop Backend Server
 * 
 * A lightweight HTTP server built on Java's built-in com.sun.net.httpserver.
 * Zero external dependencies — runs on any JDK installation.
 * 
 * Exposes REST API endpoints for 8 core algorithms:
 * - Binary Search: /api/search
 * - Merge Sort:    /api/sort/merge
 * - Quick Sort:    /api/sort/quick
 * - Prim's:        /api/graph/prims
 * - Kruskal's:     /api/graph/kruskals
 * - Floyd-Warshall:/api/graph/floyd
 * - 0/1 Knapsack:  /api/knapsack
 * - Subset Sum:    /api/subset
 * 
 * All endpoints accept POST requests with JSON body and return JSON responses.
 * CORS is enabled for all origins (development mode).
 * 
 * Usage:
 *   javac -d out src\AetherShopServer.java src\algorithms\*.java src\handlers\*.java
 *   java -cp out AetherShopServer
 */
public class AetherShopServer {

    private static final int PORT = 8080;

    public static void main(String[] args) throws Exception {
        // Create HTTP server bound to port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // --- Register Algorithm Endpoint Handlers ---

        // Binary Search — Rapid product lookup by name/ID
        server.createContext("/api/search", new SearchHandler());

        // Merge Sort — Stable sort by price (ascending)
        server.createContext("/api/sort/merge", new SortHandler("merge"));

        // Quick Sort — Fast sort by rating (descending)
        server.createContext("/api/sort/quick", new SortHandler("quick"));

        // Prim's Algorithm — MST for regional warehouse network
        server.createContext("/api/graph/prims", new GraphHandler("prims"));

        // Kruskal's Algorithm — MST for decentralized logistics
        server.createContext("/api/graph/kruskals", new GraphHandler("kruskals"));

        // Floyd-Warshall — All-pairs shortest delivery routes
        server.createContext("/api/graph/floyd", new GraphHandler("floyd"));

        // 0/1 Knapsack — Smart cart checkout optimization
        server.createContext("/api/knapsack", new KnapsackHandler());

        // Sum of Subsets — Exact coupon bundle matching
        server.createContext("/api/subset", new SubsetHandler());

        // Health check endpoint
        server.createContext("/api/health", exchange -> {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            String response = "{\"status\": \"ok\", \"server\": \"AetherShop Java Engine\", \"version\": \"1.0.0\"}";
            exchange.sendResponseHeaders(200, response.getBytes().length);
            exchange.getResponseBody().write(response.getBytes());
            exchange.getResponseBody().close();
        });

        // Use default executor (single-threaded for simplicity, can be upgraded)
        server.setExecutor(java.util.concurrent.Executors.newFixedThreadPool(4));
        server.start();

        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║     AetherShop Java Algorithm Engine v1.0.0     ║");
        System.out.println("║──────────────────────────────────────────────────║");
        System.out.println("║  Server running on http://localhost:" + PORT + "        ║");
        System.out.println("║──────────────────────────────────────────────────║");
        System.out.println("║  Endpoints:                                     ║");
        System.out.println("║    POST /api/search        (Binary Search)      ║");
        System.out.println("║    POST /api/sort/merge    (Merge Sort)         ║");
        System.out.println("║    POST /api/sort/quick    (Quick Sort)         ║");
        System.out.println("║    POST /api/graph/prims   (Prim's MST)        ║");
        System.out.println("║    POST /api/graph/kruskals(Kruskal's MST)     ║");
        System.out.println("║    POST /api/graph/floyd   (Floyd-Warshall)    ║");
        System.out.println("║    POST /api/knapsack      (0/1 Knapsack)      ║");
        System.out.println("║    POST /api/subset        (Subset Sum B&B)    ║");
        System.out.println("║    GET  /api/health        (Health Check)      ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
    }
}
