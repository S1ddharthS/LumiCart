export default function GlobalControls({ onGenerate, onClear, hasCatalog }) {
  return (
    <div style={{
      display: 'flex', justifyContent: 'center', gap: '1.5rem',
      padding: '1rem', maxWidth: '1200px', margin: '0 auto'
    }}>
      <button className="btn-neo filled-emerald" onClick={onGenerate} style={{ padding: '0.8rem 1.8rem', fontSize: '0.9rem' }}>
        ✨ Generate Random Catalog
      </button>
      {hasCatalog && (
        <button className="btn-neo filled-rose" onClick={onClear} style={{ padding: '0.8rem 1.8rem', fontSize: '0.9rem' }}>
          🗑️ Clear Dashboard State
        </button>
      )}
    </div>
  );
}
