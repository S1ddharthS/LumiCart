export default function FloydMatrixModal({ result, onClose }) {
  if (!result) return null;

  const { distanceMatrix, nodeNames, nodeCount, relaxations, executionTimeUs, complexity } = result;

  return (
    <div className="overlay-backdrop" onClick={onClose}>
      <div className="overlay-modal" onClick={(e) => e.stopPropagation()} style={{ maxWidth: '800px', width: '90%' }}>
        {/* Header */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
          <div>
            <h3 style={{ fontSize: '1.25rem', fontWeight: 800, color: 'var(--text-primary)', marginBottom: '0.25rem' }}>
              📐 Floyd-Warshall — All-Pairs Shortest Distance Matrix
            </h3>
            <p style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>
              Complete routing grid · <span className="font-bold text-blue">{relaxations}</span> relaxations · <span className="font-bold text-emerald">{executionTimeUs?.toFixed(2)} μs</span> · <span className="font-mono">{complexity}</span>
            </p>
          </div>
          <button className="btn-neo filled-rose" onClick={onClose} style={{ fontSize: '0.85rem', padding: '0.5rem 1rem' }}>
            ✕ Close
          </button>
        </div>

        {/* Matrix Table */}
        <div style={{ overflowX: 'auto', borderRadius: 'var(--radius-sm)', boxShadow: 'var(--neo-shadow-inset)', padding: '0.5rem' }}>
          <table className="matrix-table" style={{ width: '100%' }}>
            <thead>
              <tr>
                <th style={{ background: 'var(--bg-tertiary)', color: 'var(--text-primary)' }}>From ↓ To →</th>
                {nodeNames?.map((name, i) => (
                  <th key={i}>{name}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {distanceMatrix?.map((row, i) => (
                <tr key={i}>
                  <th>{nodeNames?.[i]}</th>
                  {row.map((val, j) => {
                    const isSelf = i === j;
                    const isInf = val === -1;
                    return (
                      <td
                        key={j}
                        className={`${isSelf ? 'self' : ''} ${isInf ? 'inf' : ''} ${!isSelf && !isInf ? 'highlight' : ''}`}
                      >
                        {isSelf ? '0' : isInf ? '∞' : `₹${val}`}
                      </td>
                    );
                  })}
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {/* Info */}
        <div style={{ marginTop: '1.25rem', fontSize: '0.75rem', color: 'var(--text-muted)', textAlign: 'center' }}>
          Each cell shows the minimum shipping cost in ₹ from the row hub to the column hub.
          ∞ indicates no path exists.
        </div>
      </div>
    </div>
  );
}
