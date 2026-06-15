import { useState, useCallback } from 'react';

export default function PromoTargeter({ coupons, subsetResult, onSolve, loading }) {
  const [target, setTarget] = useState('');

  const handleSolve = useCallback(async () => {
    const t = parseInt(target);
    if (isNaN(t) || t <= 0 || coupons.length === 0) return;
    const names = coupons.map((v, i) => `COUPON-${v}`);
    try {
      await onSolve(coupons, t, names);
    } catch (err) {}
  }, [coupons, target, onSolve]);

  const matchedSet = new Set(subsetResult?.selectedValues || []);

  return (
    <div>
      <div className="card-title">
        <span className="icon" style={{ fontSize: '1.2rem' }}>🎟️</span>
        <span className="text-purple" style={{ fontSize: '1.05rem' }}>Promo Targeter</span>
      </div>

      {coupons.length === 0 ? (
        <div style={{ textAlign: 'center', color: 'var(--text-light)', fontSize: '0.9rem', padding: '3rem 0' }}>
          Generate a catalog to load coupons.
        </div>
      ) : (
        <>
          <div style={{ marginBottom: '1.5rem', marginTop: '1rem' }}>
            <div style={{ fontSize: '0.7rem', color: 'var(--text-muted)', marginBottom: '0.75rem', textTransform: 'uppercase', letterSpacing: '0.05em', fontWeight: 700 }}>
              Available Coupons
            </div>
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem' }}>
              {coupons.map((value, idx) => (
                <span key={idx} className={`coupon-chip ${matchedSet.has(value) ? 'matched' : ''}`}>
                  ₹{value}
                </span>
              ))}
            </div>
          </div>

          <div style={{ display: 'flex', gap: '0.75rem', marginBottom: '1.5rem' }}>
            <input
              className="neo-input"
              type="number"
              placeholder="Target Discount in ₹"
              value={target}
              onChange={(e) => setTarget(e.target.value)}
              style={{ flex: 1 }}
            />
          </div>

          <button
            className="btn-neo filled-purple"
            onClick={handleSolve}
            disabled={loading}
            style={{ width: '100%', justifyContent: 'center', padding: '0.8rem', fontSize: '0.9rem' }}
          >
            {loading ? <span className="spinner" /> : '🎯'} Branch & Bound Coupon Match
          </button>

          {subsetResult && (
            <div className={`animate-fade-in ${subsetResult.found ? 'accent-bg-emerald' : 'accent-bg-rose'}`} style={{
              marginTop: '1.5rem',
              borderLeft: `4px solid ${subsetResult.found ? 'var(--accent-emerald)' : 'var(--accent-rose)'}`
            }}>
              <div style={{
                fontSize: '0.8rem', fontWeight: 700, marginBottom: '0.5rem',
                color: subsetResult.found ? 'var(--accent-emerald)' : 'var(--accent-rose)'
              }}>
                {subsetResult.found ? '✓ Exact Match Found!' : '✗ No Exact Combination Found'}
              </div>
              {subsetResult.found && (
                <div style={{ fontSize: '0.75rem', color: 'var(--text-secondary)' }}>
                  Coupons: <span className="font-bold">{subsetResult.selectedValues.map(v => `₹${v}`).join(' + ')}</span> = <span className="text-emerald font-bold">₹{subsetResult.targetSum}</span>
                </div>
              )}
              <div style={{ fontSize: '0.7rem', color: 'var(--text-muted)', marginTop: '0.5rem' }}>
                Nodes explored: {subsetResult.nodesExplored} · Pruned: {subsetResult.nodesPruned}
              </div>
            </div>
          )}
        </>
      )}
    </div>
  );
}
