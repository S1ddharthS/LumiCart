import { useRef, useEffect, useCallback } from 'react';

export default function LogisticsOptimizer({ graphData, mstResult, onPrims, onKruskals, onFloyd, loadingPrims, loadingKruskals, loadingFloyd }) {
  const canvasRef = useRef(null);

  const getNodePositions = useCallback((nodeCount, width, height) => {
    const positions = [];
    const cx = width / 2;
    const cy = height / 2;
    const radius = Math.min(width, height) * 0.35;
    for (let i = 0; i < nodeCount; i++) {
      const angle = (2 * Math.PI * i) / nodeCount - Math.PI / 2;
      positions.push({
        x: cx + radius * Math.cos(angle),
        y: cy + radius * Math.sin(angle),
      });
    }
    return positions;
  }, []);

  useEffect(() => {
    if (!graphData || !canvasRef.current) return;

    const canvas = canvasRef.current;
    const ctx = canvas.getContext('2d');
    const dpr = window.devicePixelRatio || 1;
    const width = canvas.parentElement.clientWidth;
    const height = 400; // Taller for vertical layout

    canvas.width = width * dpr;
    canvas.height = height * dpr;
    canvas.style.width = width + 'px';
    canvas.style.height = height + 'px';
    ctx.scale(dpr, dpr);

    const { matrix, nodeCount, nodeNames } = graphData;
    const positions = getNodePositions(nodeCount, width, height);

    ctx.clearRect(0, 0, width, height);

    // Draw all edges (base layer - light theme)
    for (let i = 0; i < nodeCount; i++) {
      for (let j = i + 1; j < nodeCount; j++) {
        if (matrix[i][j] > 0) {
          ctx.beginPath();
          ctx.moveTo(positions[i].x, positions[i].y);
          ctx.lineTo(positions[j].x, positions[j].y);
          ctx.strokeStyle = 'rgba(0, 0, 0, 0.1)';
          ctx.lineWidth = 1.5;
          ctx.stroke();

          // Edge weight label
          const mx = (positions[i].x + positions[j].x) / 2;
          const my = (positions[i].y + positions[j].y) / 2;
          ctx.font = '500 11px Inter';
          ctx.fillStyle = 'rgba(0, 0, 0, 0.4)';
          ctx.textAlign = 'center';
          ctx.fillText(matrix[i][j], mx, my - 5);
        }
      }
    }

    // Draw MST edges if available
    if (mstResult?.result?.mstEdges) {
      const isPrims = mstResult.type === 'prims';
      const color = isPrims ? '#48bb78' : '#5b8def'; // Emerald or Blue
      
      mstResult.result.mstEdges.forEach(edge => {
        const from = positions[edge.from];
        const to = positions[edge.to];

        ctx.beginPath();
        ctx.moveTo(from.x, from.y);
        ctx.lineTo(to.x, to.y);
        ctx.strokeStyle = color;
        ctx.lineWidth = 4;
        ctx.stroke();

        // Weight label on MST edge
        const mx = (from.x + to.x) / 2;
        const my = (from.y + to.y) / 2;
        
        ctx.fillStyle = '#fff';
        ctx.beginPath();
        ctx.arc(mx, my - 6, 10, 0, 2*Math.PI);
        ctx.fill();

        ctx.font = 'bold 12px Inter';
        ctx.fillStyle = color;
        ctx.textAlign = 'center';
        ctx.textBaseline = 'middle';
        ctx.fillText(edge.weight, mx, my - 6);
      });
    }

    // Draw nodes
    positions.forEach((pos, i) => {
      // Node outer shadow
      ctx.beginPath();
      ctx.arc(pos.x, pos.y, 22, 0, 2 * Math.PI);
      ctx.fillStyle = 'rgba(163, 177, 198, 0.2)';
      ctx.fill();

      // Node circle
      ctx.beginPath();
      ctx.arc(pos.x, pos.y, 18, 0, 2 * Math.PI);
      ctx.fillStyle = '#f0f4f8';
      ctx.strokeStyle = 'rgba(102, 126, 234, 0.4)';
      ctx.lineWidth = 2;
      ctx.fill();
      ctx.stroke();

      // Node label
      ctx.font = 'bold 11px Inter';
      ctx.fillStyle = '#2d3748';
      ctx.textAlign = 'center';
      ctx.textBaseline = 'middle';
      ctx.fillText(nodeNames[i], pos.x, pos.y);
    });

  }, [graphData, mstResult, getNodePositions]);

  if (!graphData) return null;

  return (
    <div>
      <div className="card-title">
        <span className="icon" style={{ fontSize: '1.2rem' }}>🌐</span>
        <span className="text-emerald" style={{ fontSize: '1.05rem' }}>Logistics Optimizer</span>
        {mstResult?.result && (
          <span style={{ marginLeft: 'auto', fontSize: '0.8rem', fontWeight: 600, color: 'var(--text-secondary)' }}>
            MST Cost: <span className={mstResult.type === 'prims' ? 'text-emerald' : 'text-blue'}>₹{mstResult.result.totalCost}</span>
          </span>
        )}
      </div>

      <div className="graph-canvas-container" style={{ marginBottom: '1.5rem', marginTop: '1rem' }}>
        <canvas ref={canvasRef} />
      </div>

      <div style={{ display: 'flex', gap: '1rem', flexWrap: 'wrap', justifyContent: 'center' }}>
        <button className="btn-neo filled-emerald" onClick={onPrims} disabled={loadingPrims}>
          {loadingPrims ? <span className="spinner" /> : '🌿'} Prim's MST
        </button>
        <button className="btn-neo filled-blue" onClick={onKruskals} disabled={loadingKruskals}>
          {loadingKruskals ? <span className="spinner" /> : '🔗'} Kruskal's MST
        </button>
        <button className="btn-neo filled-purple" onClick={onFloyd} disabled={loadingFloyd}>
          {loadingFloyd ? <span className="spinner" /> : '📐'} Floyd Route Matrix
        </button>
      </div>

      <div style={{ marginTop: '1.5rem', display: 'flex', justifyContent: 'center', gap: '2rem', fontSize: '0.75rem', color: 'var(--text-muted)' }}>
        <span style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <div style={{ width: '20px', height: '4px', background: 'var(--accent-emerald)', borderRadius: '2px' }}></div>
          Prim's MST
        </span>
        <span style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <div style={{ width: '20px', height: '4px', background: 'var(--accent-blue)', borderRadius: '2px' }}></div>
          Kruskal's MST
        </span>
      </div>
    </div>
  );
}
