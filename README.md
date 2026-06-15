# LumiCart — Algorithm-Powered Shopping Platform

A **full-stack online shopping application** featuring a premium **Light Neomorphism** UI and a **Java Algorithms Engine** backend, showcasing 8 core Data Structures & Algorithms mapped to real eCommerce features.

---

## 🏗 Architecture

```
Frontend (React + Vite + Tailwind CSS)  ←→  Backend (Java HTTP Server)
         Port 5173                                Port 8080
         /api/* proxied to backend
```

### Frontend Stack
- **React 19** — Component-based UI with `useReducer` state management
- **Vite 8** — Blazing fast dev server and build tool
- **Tailwind CSS v4** — Utility-first styling with custom light neomorphism design system

### Backend Stack
- **Pure Java** — Zero external dependencies (uses `com.sun.net.httpserver`)
- **8 Algorithm Classes** — Each with step-by-step trace logging and execution timing

---

## 🚀 Quick Start (For Downloaded Zip Files)

If you have downloaded this project as a ZIP file, extract it and follow these steps to run both servers.

### Prerequisites
- Java Development Kit (JDK 11 or higher)
- Node.js (v18 or higher)

### 1. Start the Java Backend
Open a terminal in the root folder and run:
```bash
cd backend
compile.bat       # Compiles all Java sources to backend/out/
run.bat           # Starts the server on http://localhost:8080
```

### 2. Start the React Frontend
Open a **new** terminal in the root folder and run:
```bash
cd frontend
npm install       # Installs required dependencies (First time only)
npm run dev       # Starts dev server on http://localhost:5173
```

### 3. Open the App
Navigate to **http://localhost:5173** in your web browser and click **"✨ Generate Random Catalog"** to start.

---

## 📐 Implemented Algorithms

| # | Algorithm | Complexity | eCommerce Feature |
|---|-----------|-----------|-------------------|
| 1 | **Binary Search** | O(log n) | Rapid product lookup by name |
| 2 | **Merge Sort** | O(n log n) | Stable catalog price sorting |
| 3 | **Quick Sort** | O(n log n) avg | Dynamic rating/review ranking |
| 4 | **Prim's Algorithm** | O(E log V) | Regional warehouse MST |
| 5 | **Kruskal's Algorithm** | O(E log E) | Decentralized logistics MST |
| 6 | **Floyd-Warshall** | O(V³) | All-pairs delivery routing |
| 7 | **0/1 Knapsack (DP)** | O(n×W) | Smart cart checkout optimizer |
| 8 | **Subset Sum (B&B)** | O(2ⁿ) pruned | Exact coupon combination matcher |

---

## 🎨 UI Features

- **Light Neomorphism** — Soft shadows, bright aesthetics, inset and raised elements
- **Vertical Feed Layout** — Clean sections with smooth scroll-reveal animations
- **Interactive Graph Canvas** — HTML5 Canvas with MST visualization
- **Diagnostics Terminal** — Live algorithm execution trace log
- **Floyd Matrix Modal** — Full shortest-path distance grid
- **Indian Currency** — All prices, budgets, and coupons are localized to Rupees (₹)

---

## 📁 Project Structure

```
LumiCart/
├── backend/
│   ├── src/
│   │   ├── AetherShopServer.java          # Main HTTP server
│   │   ├── algorithms/
│   │   │   ├── BinarySearch.java
│   │   │   ├── MergeSort.java
│   │   │   ├── QuickSort.java
│   │   │   ├── PrimsAlgorithm.java
│   │   │   ├── KruskalsAlgorithm.java
│   │   │   ├── FloydWarshall.java
│   │   │   ├── Knapsack01.java
│   │   │   └── SubsetSumBB.java
│   │   └── handlers/
│   │       ├── SearchHandler.java
│   │       ├── SortHandler.java
│   │       ├── GraphHandler.java
│   │       ├── KnapsackHandler.java
│   │       └── SubsetHandler.java
│   ├── compile.bat
│   └── run.bat
│
└── frontend/
    ├── src/
    │   ├── components/
    │   ├── hooks/useAlgorithms.js
    │   ├── services/api.js
    │   ├── App.jsx
    │   ├── App.css
    │   ├── main.jsx
    │   └── index.css
    ├── index.html
    └── vite.config.js
```

---

## 🔌 API Endpoints

All endpoints accept `POST` with JSON body and return JSON responses.

| Endpoint | Algorithm | Input |
|----------|-----------|-------|
| `/api/search` | Binary Search | `{sortedArray, target}` |
| `/api/sort/merge` | Merge Sort | `{values, indices}` |
| `/api/sort/quick` | Quick Sort | `{values, indices}` |
| `/api/graph/prims` | Prim's | `{matrix, nodeCount, nodeNames}` |
| `/api/graph/kruskals` | Kruskal's | `{matrix, nodeCount, nodeNames}` |
| `/api/graph/floyd` | Floyd-Warshall | `{matrix, nodeCount, nodeNames}` |
| `/api/knapsack` | 0/1 Knapsack | `{values, weights, capacity, names}` |
| `/api/subset` | Subset Sum | `{values, target, names}` |
| `/api/health` | Health Check | GET (no body) |
