import { useRef, useEffect } from 'react';

export default function DiagnosticsTerminal({ logs, onClear }) {
  const termRef = useRef(null);

  useEffect(() => {
    if (termRef.current) {
      termRef.current.scrollTop = termRef.current.scrollHeight;
    }
  }, [logs]);

  return (
    <div>
      <div className="card-title" style={{ marginBottom: '1rem' }}>
        <span className="icon" style={{ fontSize: '1.2rem' }}>📟</span>
        <span className="text-amber" style={{ fontSize: '1.05rem' }}>Algorithm Analytics Terminal</span>
        <span style={{ marginLeft: 'auto' }}>
          <button className="btn-neo" onClick={onClear} style={{ fontSize: '0.7rem', padding: '0.3rem 0.6rem' }}>
            Clear Log
          </button>
        </span>
      </div>

      <div className="terminal" ref={termRef}>
        {logs.length === 0 ? (
          <div className="terminal-line info">
            {'>'} LumiCart Algorithm Engine v1.0.0 — Awaiting commands...{'\n'}
            {'>'} Run any algorithm to see execution traces here.
          </div>
        ) : (
          logs.map((log, idx) => (
            <div key={idx} style={{ marginBottom: '1rem' }}>
              <div className={`terminal-line ${log.type}`} style={{ fontWeight: 700 }}>
                {'━'.repeat(50)}
              </div>
              <div className={`terminal-line ${log.type}`} style={{ fontWeight: 700 }}>
                [{new Date(log.timestamp).toLocaleTimeString()}] ▶ {log.algorithm}
              </div>
              <div className="terminal-line info">
                Input: {typeof log.inputSize === 'number' ? `${log.inputSize} elements` : log.inputSize}
              </div>

              {log.trace?.map((step, sIdx) => (
                <div key={sIdx} className={`terminal-line ${log.type}`} style={{ opacity: 0.85 }}>
                  {step}
                </div>
              ))}

              <div className="terminal-line info" style={{ fontWeight: 600, marginTop: '0.25rem' }}>
                Complexity: <span className="font-bold text-primary">{log.complexity}</span> | Time: <span className="font-bold text-primary">{log.executionTime?.toFixed(2)} μs</span>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
}
