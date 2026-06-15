/**
 * AetherShop API Service Layer
 * 
 * Provides clean async functions for communicating with the Java backend.
 * All endpoints accept JSON POST requests and return parsed JSON responses.
 */

const API_BASE = '/api';

/**
 * Generic POST request helper with error handling.
 */
async function post(endpoint, data) {
  const response = await fetch(`${API_BASE}${endpoint}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });

  if (!response.ok) {
    const error = await response.json().catch(() => ({ error: 'Unknown error' }));
    throw new Error(error.error || `HTTP ${response.status}`);
  }

  return response.json();
}

/**
 * Binary Search — Search for a product by name in the sorted catalog.
 * @param {string[]} sortedArray - Alphabetically sorted product names
 * @param {string} target - Product name to search for
 * @returns {Promise<Object>} Search result with foundIndex, trace, etc.
 */
export async function searchProduct(sortedArray, target) {
  return post('/search', { sortedArray, target });
}

/**
 * Merge Sort — Sort products by price (ascending, stable).
 * @param {number[]} values - Array of prices
 * @param {number[]} indices - Original index mapping
 * @returns {Promise<Object>} Sorted result with originalIndices mapping
 */
export async function mergeSortPrices(values, indices) {
  return post('/sort/merge', { values, indices });
}

/**
 * Quick Sort — Sort products by rating (descending).
 * @param {number[]} values - Array of ratings
 * @param {number[]} indices - Original index mapping
 * @returns {Promise<Object>} Sorted result with originalIndices mapping
 */
export async function quickSortRatings(values, indices) {
  return post('/sort/quick', { values, indices });
}

/**
 * Prim's Algorithm — Compute MST for warehouse network.
 * @param {number[][]} matrix - Adjacency matrix
 * @param {number} nodeCount - Number of nodes
 * @param {string[]} nodeNames - Node display names
 * @returns {Promise<Object>} MST edges and total cost
 */
export async function runPrims(matrix, nodeCount, nodeNames) {
  return post('/graph/prims', { matrix, nodeCount, nodeNames });
}

/**
 * Kruskal's Algorithm — Compute MST using edge ranking.
 * @param {number[][]} matrix - Adjacency matrix
 * @param {number} nodeCount - Number of nodes
 * @param {string[]} nodeNames - Node display names
 * @returns {Promise<Object>} MST edges and total cost
 */
export async function runKruskals(matrix, nodeCount, nodeNames) {
  return post('/graph/kruskals', { matrix, nodeCount, nodeNames });
}

/**
 * Floyd-Warshall — Compute all-pairs shortest paths.
 * @param {number[][]} matrix - Adjacency matrix
 * @param {number} nodeCount - Number of nodes
 * @param {string[]} nodeNames - Node display names
 * @returns {Promise<Object>} Distance matrix and trace
 */
export async function runFloyd(matrix, nodeCount, nodeNames) {
  return post('/graph/floyd', { matrix, nodeCount, nodeNames });
}

/**
 * 0/1 Knapsack — Optimize cart selection within budget.
 * @param {number[]} values - Item values
 * @param {number[]} weights - Item weights/costs
 * @param {number} capacity - Budget limit
 * @param {string[]} names - Item names
 * @returns {Promise<Object>} Selected items and max value
 */
export async function solveKnapsack(values, weights, capacity, names) {
  return post('/knapsack', { values, weights, capacity, names });
}

/**
 * Sum of Subsets — Find exact coupon combination for target discount.
 * @param {number[]} values - Coupon face values
 * @param {number} target - Target discount sum
 * @param {string[]} names - Coupon labels
 * @returns {Promise<Object>} Matching subset or not found
 */
export async function solveSubsetSum(values, target, names) {
  return post('/subset', { values, target, names });
}
