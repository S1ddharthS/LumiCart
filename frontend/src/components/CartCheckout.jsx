import { useState, useCallback } from 'react';

export default function CartCheckout({ cart, knapsackResult, onRemoveItem, onOptimize, loading }) {
  const [capacity, setCapacity] = useState(100);

  const handleOptimize = useCallback(async () => {
    if (cart.length === 0) return;
    const values = cart.map(item => item.price);
    const weights = cart.map(item => item.weight);
    const names = cart.map(item => item.name);
    try {
      await onOptimize(values, weights, capacity, names);
    } catch (err) {}
  }, [cart, capacity, onOptimize]);

  const selectedSet = new Set(knapsackResult?.selectedItems || []);

  return (
    <div>
      <div className="card-title">
        <span className="icon" style={{ fontSize: '1.2rem' }}>🛒</span>
        <span className="text-rose" style={{ fontSize: '1.05rem' }}>Cart & Knapsack Strategy</span>
        <span style={{ marginLeft: 'auto', fontSize: '0.75rem', fontWeight: 600, color: 'var(--text-muted)' }}>
          {cart.length} items
        </span>
      </div>

      {cart.length === 0 ? (
        <div style={{ textAlign: 'center', color: 'var(--text-light)', fontSize: '0.9rem', padding: '3rem 0' }}>
          Cart is empty. Add products from the catalog.
        </div>
      ) : (
        <>
          <div style={{ maxHeight: '240px', overflowY: 'auto', marginBottom: '1.5rem', marginTop: '1rem', padding: '0.5rem' }}>
            {cart.map((item, idx) => {
              const isSelected = selectedSet.has(idx);
              const isExcluded = knapsackResult && !isSelected;
              return (
                <div key={item.id} className={`cart-item ${isSelected ? 'optimized' : ''} ${isExcluded ? 'excluded' : ''}`}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                    <span style={{ fontSize: '1.5rem' }}>{item.emoji}</span>
                    <div>
                      <div style={{ fontSize: '0.85rem', fontWeight: 700, color: 'var(--text-primary)' }}>{item.name}</div>
                      <div style={{ fontSize: '0.75rem', color: 'var(--text-secondary)' }}>
                        <span className="text-blue font-bold">₹{item.price}</span> · {item.weight}kg
                      </div>
                    </div>
                  </div>
                  <button
                    className="btn-neo rose"
                    onClick={() => onRemoveItem(item.id)}
                    style={{ fontSize: '0.8rem', padding: '0.3rem 0.6rem', borderRadius: '50%' }}
                  >
                    ✕
                  </button>
                </div>
              );
            })}
          </div>

          <div style={{ marginBottom: '1.5rem' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.8rem', color: 'var(--text-secondary)', marginBottom: '0.5rem' }}>
              <span style={{ fontWeight: 600 }}>Weight Capacity</span>
              <span className="text-blue font-bold">{capacity} kg</span>
            </div>
            <input
              type="range"
              min="10"
              max="200"
              value={capacity}
              onChange={(e) => setCapacity(parseInt(e.target.value))}
              style={{ width: '100%', accentColor: 'var(--accent-blue)', height: '6px', borderRadius: '3px', outline: 'none' }}
            />
          </div>

          <button
            className="btn-neo filled-rose"
            onClick={handleOptimize}
            disabled={loading}
            style={{ width: '100%', justifyContent: 'center', padding: '0.8rem', fontSize: '0.9rem' }}
          >
            {loading ? <span className="spinner" /> : '💎'} Optimize My Purchase Value (0/1 Knapsack)
          </button>

          {knapsackResult && (
            <div className="animate-fade-in accent-bg-emerald" style={{ marginTop: '1.5rem', borderLeft: '4px solid var(--accent-emerald)' }}>
              <div style={{ fontSize: '0.8rem', fontWeight: 700, color: 'var(--accent-emerald)', marginBottom: '0.5rem' }}>
                ✓ Optimal Selection Found
              </div>
              <div style={{ fontSize: '0.75rem', color: 'var(--text-secondary)', lineHeight: 1.8 }}>
                <div>Max value: <span className="text-emerald font-bold">₹{knapsackResult.maxValue}</span></div>
                <div>Total weight: <span className="font-bold">{knapsackResult.totalWeight}/{knapsackResult.capacity} kg</span></div>
                <div>Items selected: <span className="font-bold">{knapsackResult.selectedItems.length}/{cart.length}</span></div>
              </div>
            </div>
          )}
        </>
      )}
    </div>
  );
}
