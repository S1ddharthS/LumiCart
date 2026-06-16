import { useState, useCallback } from 'react';

export default function SearchFilter({ catalog, searchResult, onSearch, loading, onClearHighlight }) {
  const [searchTerm, setSearchTerm] = useState('');

  const handleSearch = useCallback(async () => {
    if (!searchTerm.trim() || !catalog.length) return;
    const sorted = [...catalog].map(p => p.name).sort((a, b) => a.localeCompare(b));
    try {
      await onSearch(sorted, searchTerm.trim());
    } catch (err) {}
  }, [searchTerm, catalog, onSearch]);

  const handleKeyDown = (e) => {
    if (e.key === 'Enter') handleSearch();
  };

  return (
    <div>
      <div className="card-title">
        <span className="icon" style={{ fontSize: '1.2rem' }}>🔍</span>
        <span className="text-blue" style={{ fontSize: '1.05rem' }}>Smart Search & Filter</span>
      </div>

      <div style={{ display: 'flex', gap: '1rem', marginBottom: '1.5rem', marginTop: '1rem' }}>
        <input
          className="neo-input"
          placeholder="Enter product name..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          onKeyDown={handleKeyDown}
          style={{ flex: 1 }}
        />
        <button className="btn-neo filled-blue" onClick={handleSearch} disabled={loading}>
          {loading ? <span className="spinner" /> : '⚡'} Binary Search
        </button>
      </div>

      <div style={{ display: 'flex', gap: '0.75rem', marginBottom: '1.5rem', flexWrap: 'wrap' }}>
        {['All', '< ₹1,000', '₹1,000–₹5,000', '₹5,000+', '★★★★+'].map(filter => (
          <button key={filter} className="btn-neo" style={{ fontSize: '0.7rem', padding: '0.4rem 0.8rem' }}>
            {filter}
          </button>
        ))}
      </div>

      {searchResult && (
        <div className="animate-fade-in accent-bg-blue" style={{ marginTop: '1rem' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.75rem' }}>
            <span style={{ fontSize: '1.2rem' }}>{searchResult.foundIndex >= 0 ? '✅' : '❌'}</span>
            <span style={{ fontSize: '0.85rem', fontWeight: 700, color: searchResult.foundIndex >= 0 ? 'var(--accent-emerald)' : 'var(--accent-rose)' }}>
              {searchResult.foundIndex >= 0
                ? `Found "${searchResult.target}" at index ${searchResult.foundIndex + 1}`
                : `"${searchResult.target}" not found in catalog`}
            </span>
          </div>

          <div style={{ fontSize: '0.75rem', color: 'var(--text-secondary)', lineHeight: 1.8 }}>
            <div>Comparisons: <span className="text-blue font-bold">{searchResult.totalComparisons}</span></div>
            <div>Elements inspected: <span className="text-blue font-mono">[{searchResult.inspectedIndices?.map(i => i + 1).join(', ')}]</span></div>
            <div>Time: <span className="text-blue font-bold">{searchResult.executionTimeUs?.toFixed(2)} μs</span></div>
          </div>

          {searchResult.foundIndex >= 0 && (
            <button className="btn-neo blue" onClick={onClearHighlight} style={{ marginTop: '1rem', fontSize: '0.7rem' }}>
              Clear Highlight
            </button>
          )}
        </div>
      )}
    </div>
  );
}
