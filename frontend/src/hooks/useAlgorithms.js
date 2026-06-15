import { useState, useCallback } from 'react';
import * as api from '../services/api';

/**
 * Custom hook for managing all algorithm API calls with loading states,
 * execution timing, and diagnostic log collection.
 */
export function useAlgorithms(dispatch) {
  const [loading, setLoading] = useState({});

  const setAlgoLoading = (name, val) => {
    setLoading(prev => ({ ...prev, [name]: val }));
  };

  const addLog = useCallback((entry) => {
    dispatch({ type: 'ADD_LOG', payload: entry });
  }, [dispatch]);

  // Binary Search
  const runBinarySearch = useCallback(async (sortedNames, target) => {
    setAlgoLoading('search', true);
    try {
      const result = await api.searchProduct(sortedNames, target);
      addLog({
        algorithm: 'Binary Search',
        type: 'search',
        inputSize: result.inputSize,
        trace: result.trace,
        complexity: result.complexity,
        executionTime: result.executionTimeUs,
      });
      dispatch({ type: 'SET_SEARCH_RESULT', payload: result });
      return result;
    } catch (err) {
      addLog({ algorithm: 'Binary Search', type: 'search', trace: [`Error: ${err.message}`], complexity: 'O(log n)', executionTime: 0 });
      throw err;
    } finally {
      setAlgoLoading('search', false);
    }
  }, [dispatch, addLog]);

  // Merge Sort
  const runMergeSort = useCallback(async (prices, indices) => {
    setAlgoLoading('mergeSort', true);
    try {
      const result = await api.mergeSortPrices(prices, indices);
      addLog({
        algorithm: 'Merge Sort',
        type: 'sort',
        inputSize: result.inputSize,
        trace: result.trace,
        complexity: result.complexity,
        executionTime: result.executionTimeUs,
      });
      dispatch({ type: 'SET_SORT_RESULT', payload: { type: 'merge', result } });
      return result;
    } catch (err) {
      addLog({ algorithm: 'Merge Sort', type: 'sort', trace: [`Error: ${err.message}`], complexity: 'O(n log n)', executionTime: 0 });
      throw err;
    } finally {
      setAlgoLoading('mergeSort', false);
    }
  }, [dispatch, addLog]);

  // Quick Sort
  const runQuickSort = useCallback(async (ratings, indices) => {
    setAlgoLoading('quickSort', true);
    try {
      const result = await api.quickSortRatings(ratings, indices);
      addLog({
        algorithm: 'Quick Sort',
        type: 'sort',
        inputSize: result.inputSize,
        trace: result.trace,
        complexity: result.complexity,
        executionTime: result.executionTimeUs,
      });
      dispatch({ type: 'SET_SORT_RESULT', payload: { type: 'quick', result } });
      return result;
    } catch (err) {
      addLog({ algorithm: 'Quick Sort', type: 'sort', trace: [`Error: ${err.message}`], complexity: 'O(n log n)', executionTime: 0 });
      throw err;
    } finally {
      setAlgoLoading('quickSort', false);
    }
  }, [dispatch, addLog]);

  // Prim's Algorithm
  const runPrims = useCallback(async (matrix, nodeCount, nodeNames) => {
    setAlgoLoading('prims', true);
    try {
      const result = await api.runPrims(matrix, nodeCount, nodeNames);
      addLog({
        algorithm: "Prim's Algorithm",
        type: 'graph',
        inputSize: `${nodeCount} nodes`,
        trace: result.trace,
        complexity: result.complexity,
        executionTime: result.executionTimeUs,
      });
      dispatch({ type: 'SET_MST_RESULT', payload: { type: 'prims', result } });
      return result;
    } catch (err) {
      addLog({ algorithm: "Prim's Algorithm", type: 'graph', trace: [`Error: ${err.message}`], complexity: 'O(E log V)', executionTime: 0 });
      throw err;
    } finally {
      setAlgoLoading('prims', false);
    }
  }, [dispatch, addLog]);

  // Kruskal's Algorithm
  const runKruskals = useCallback(async (matrix, nodeCount, nodeNames) => {
    setAlgoLoading('kruskals', true);
    try {
      const result = await api.runKruskals(matrix, nodeCount, nodeNames);
      addLog({
        algorithm: "Kruskal's Algorithm",
        type: 'graph',
        inputSize: `${nodeCount} nodes`,
        trace: result.trace,
        complexity: result.complexity,
        executionTime: result.executionTimeUs,
      });
      dispatch({ type: 'SET_MST_RESULT', payload: { type: 'kruskals', result } });
      return result;
    } catch (err) {
      addLog({ algorithm: "Kruskal's Algorithm", type: 'graph', trace: [`Error: ${err.message}`], complexity: 'O(E log E)', executionTime: 0 });
      throw err;
    } finally {
      setAlgoLoading('kruskals', false);
    }
  }, [dispatch, addLog]);

  // Floyd-Warshall
  const runFloyd = useCallback(async (matrix, nodeCount, nodeNames) => {
    setAlgoLoading('floyd', true);
    try {
      const result = await api.runFloyd(matrix, nodeCount, nodeNames);
      addLog({
        algorithm: 'Floyd-Warshall',
        type: 'graph',
        inputSize: `${nodeCount} nodes, ${nodeCount}×${nodeCount} matrix`,
        trace: result.trace,
        complexity: result.complexity,
        executionTime: result.executionTimeUs,
      });
      dispatch({ type: 'SET_FLOYD_RESULT', payload: result });
      return result;
    } catch (err) {
      addLog({ algorithm: 'Floyd-Warshall', type: 'graph', trace: [`Error: ${err.message}`], complexity: 'O(V³)', executionTime: 0 });
      throw err;
    } finally {
      setAlgoLoading('floyd', false);
    }
  }, [dispatch, addLog]);

  // 0/1 Knapsack
  const runKnapsack = useCallback(async (values, weights, capacity, names) => {
    setAlgoLoading('knapsack', true);
    try {
      const result = await api.solveKnapsack(values, weights, capacity, names);
      addLog({
        algorithm: '0/1 Knapsack (DP)',
        type: 'optimization',
        inputSize: `${values.length} items, capacity ${capacity}`,
        trace: result.trace,
        complexity: result.complexity,
        executionTime: result.executionTimeUs,
      });
      dispatch({ type: 'SET_KNAPSACK_RESULT', payload: result });
      return result;
    } catch (err) {
      addLog({ algorithm: '0/1 Knapsack', type: 'optimization', trace: [`Error: ${err.message}`], complexity: 'O(n×W)', executionTime: 0 });
      throw err;
    } finally {
      setAlgoLoading('knapsack', false);
    }
  }, [dispatch, addLog]);

  // Sum of Subsets
  const runSubsetSum = useCallback(async (values, target, names) => {
    setAlgoLoading('subset', true);
    try {
      const result = await api.solveSubsetSum(values, target, names);
      addLog({
        algorithm: 'Sum of Subsets (Branch & Bound)',
        type: 'optimization',
        inputSize: `${values.length} coupons, target ${target}`,
        trace: result.trace,
        complexity: result.complexity,
        executionTime: result.executionTimeUs,
      });
      dispatch({ type: 'SET_SUBSET_RESULT', payload: result });
      return result;
    } catch (err) {
      addLog({ algorithm: 'Subset Sum B&B', type: 'optimization', trace: [`Error: ${err.message}`], complexity: 'O(2^n)', executionTime: 0 });
      throw err;
    } finally {
      setAlgoLoading('subset', false);
    }
  }, [dispatch, addLog]);

  return {
    loading,
    runBinarySearch,
    runMergeSort,
    runQuickSort,
    runPrims,
    runKruskals,
    runFloyd,
    runKnapsack,
    runSubsetSum,
  };
}
