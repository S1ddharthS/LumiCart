import { useState } from 'react';

export default function SortingCenter({ catalog, onMergeSort, onQuickSort, loadingMerge, loadingQuick }) {
  const [lastResult, setLastResult] = useState(null);

  const handleMergeSort = async () => {
    const prices = catalog.map(p => p.price);
    const indices = catalog.map((_, i) => i);
    try {
      const result = await onMergeSort(prices, indices);
      setLastResult({ type: 'Merge Sort', ...result });
    } catch (err) {}
  };

  const handleQuickSort = async () => {
    const ratings = catalog.map(p => p.rating);
    const indices = catalog.map((_, i) => i);
    try {
      const result = await onQuickSort(ratings, indices);
      setLastResult({ type: 'Quick Sort', ...result });
    } catch (err) {}
  };

  return (
    <div style={{ position: 'relative' }}>
      <div className="card-title">
        <span className="icon" style={{ fontSize: '1.2rem' }}>⚡</span>
        <span className="text-indigo" style={{ fontSize: '1.05rem' }}>Sorting Command Center</span>
      </div>

      <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem', marginTop: '1rem' }}>
        <button
          className="btn-neo filled-purple"
          onClick={handleMergeSort}
          disabled={loadingMerge || !catalog.length}
          style={{ padding: '1rem', justifyContent: 'center', fontSize: '0.9rem' }}
        >
          {loadingMerge ? <span className="spinner" /> : '📊'} Sort by Price (Merge Sort)
        </button>

        <button
          className="btn-neo filled-blue"
          onClick={handleQuickSort}
          disabled={loadingQuick || !catalog.length}
          style={{ padding: '1rem', justifyContent: 'center', fontSize: '0.9rem' }}
        >
          {loadingQuick ? <span className="spinner" /> : '⚡'} Sort by Rating (Quick Sort)
        </button>
      </div>

      {lastResult && (
        <div className="animate-fade-in accent-bg-emerald" style={{ marginTop: '1.5rem', borderLeft: '4px solid var(--accent-emerald)' }}>
          <div style={{ fontSize: '0.8rem', fontWeight: 700, color: 'var(--accent-emerald)', marginBottom: '0.5rem' }}>
            ⏱ Execution Benchmark
          </div>
          <div style={{ fontSize: '0.75rem', color: 'var(--text-secondary)', lineHeight: 1.8 }}>
            <div>Algorithm: <span className="text-emerald font-bold">{lastResult.type}</span></div>
            <div>Input size: <span className="font-bold">{lastResult.inputSize} elements</span></div>
            <div>Comparisons: <span className="font-bold">{lastResult.totalComparisons}</span></div>
            {lastResult.totalMerges != null && (
              <div>Merge operations: <span className="font-bold">{lastResult.totalMerges}</span></div>
            )}
            {lastResult.totalSwaps != null && (
              <div>Swaps: <span className="font-bold">{lastResult.totalSwaps}</span></div>
            )}
            <div>Time: <span className="text-emerald font-bold">{lastResult.executionTimeUs?.toFixed(2)} μs</span></div>
            <div>Complexity: <span className="font-mono text-emerald font-bold">{lastResult.complexity}</span></div>
          </div>
        </div>
      )}
    </div>
  );
}
