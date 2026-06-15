import { useState, useCallback } from 'react';

export default function Navbar({ catalogCount, mstCost, sessionToken, cartCount, catalog, onSearch, searchLoading }) {
  const [searchTerm, setSearchTerm] = useState('');

  const handleSearch = useCallback(async (e) => {
    e.preventDefault();
    if (!searchTerm.trim() || !catalog.length) return;
    const sorted = [...catalog].map(p => p.name).sort((a, b) => a.localeCompare(b));
    try {
      await onSearch(sorted, searchTerm.trim());
    } catch (err) {}
  }, [searchTerm, catalog, onSearch]);

  return (
    <nav className="navbar neo-card" style={{ borderRadius: '0 0 var(--radius-xl) var(--radius-xl)', margin: '0 1.5rem', padding: '1rem 2rem' }}>
      <div className="nav-content">
        {/* Logo */}
        <div className="nav-logo">
          <span className="logo-icon" style={{ fontSize: '1.8rem' }}>🛍️</span>
          <span className="logo-text gradient-text">LumiCart</span>
        </div>

        {/* Search Bar */}
        <form className="nav-search" onSubmit={handleSearch}>
          <input
            type="text"
            className="neo-input"
            placeholder="Search products... (Binary Search)"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            style={{ width: '320px' }}
          />
          <button type="submit" className="btn-neo filled-blue" disabled={searchLoading}>
            {searchLoading ? <span className="spinner" /> : '🔍'} Find
          </button>
        </form>

        {/* Metric Tickers */}
        <div className="nav-metrics">
          <div className="metric-item">
            <span className="metric-label">Catalog</span>
            <span className="metric-value text-indigo">{catalogCount}</span>
          </div>
          <div className="metric-divider" />
          <div className="metric-item">
            <span className="metric-label">MST Cost</span>
            <span className="metric-value text-emerald">₹{mstCost !== '—' ? mstCost : 0}</span>
          </div>
          <div className="metric-divider" />
          <div className="metric-item">
            <span className="metric-label">Session</span>
            <span className="metric-value text-purple" style={{ fontFamily: 'var(--font-mono)', fontSize: '0.75rem' }}>{sessionToken}</span>
          </div>
        </div>

        {/* Cart Badge */}
        <div className="nav-actions">
          <div className="cart-icon-wrapper neo-card" style={{ padding: '0.5rem 0.8rem', cursor: 'pointer', position: 'relative' }}>
            <span style={{ fontSize: '1.2rem' }}>🛒</span>
            {cartCount > 0 && (
              <span className="badge" style={{ background: 'var(--accent-rose)', color: '#fff', position: 'absolute', top: '-6px', right: '-6px', boxShadow: 'var(--neo-shadow-raised-sm)' }}>
                {cartCount}
              </span>
            )}
          </div>
        </div>
      </div>

      <style>{`
        .navbar {
          position: sticky;
          top: 0;
          z-index: 50;
        }
        .nav-content {
          display: flex;
          align-items: center;
          gap: 2rem;
          flex-wrap: wrap;
        }
        .nav-logo {
          display: flex;
          align-items: center;
          gap: 0.5rem;
          flex-shrink: 0;
        }
        .logo-text {
          font-size: 1.5rem;
          font-weight: 800;
          letter-spacing: -0.03em;
        }
        .nav-search {
          display: flex;
          align-items: center;
          gap: 0.75rem;
          flex: 1;
          min-width: 200px;
          max-width: 500px;
        }
        .nav-search .neo-input {
          flex: 1;
        }
        .nav-metrics {
          display: flex;
          align-items: center;
          gap: 1.25rem;
          flex-shrink: 0;
        }
        .metric-item {
          display: flex;
          flex-direction: column;
          align-items: center;
          gap: 0.15rem;
        }
        .metric-label {
          font-size: 0.65rem;
          text-transform: uppercase;
          letter-spacing: 0.05em;
          color: var(--text-muted);
          font-weight: 700;
        }
        .metric-value {
          font-size: 0.9rem;
          font-weight: 700;
        }
        .metric-divider {
          width: 1px;
          height: 28px;
          background: rgba(0,0,0,0.08);
        }
        .nav-actions {
          display: flex;
          align-items: center;
          gap: 0.75rem;
          flex-shrink: 0;
          margin-left: auto;
        }
        @media (max-width: 950px) {
          .nav-metrics { display: none; }
          .nav-search { max-width: 300px; }
        }
      `}</style>
    </nav>
  );
}
