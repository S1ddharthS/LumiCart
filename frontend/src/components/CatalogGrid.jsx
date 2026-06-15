export default function CatalogGrid({ catalog, highlightedProduct, onAddToCart, cart }) {
  const cartIds = new Set(cart.map(c => c.id));

  return (
    <div>
      <div className="card-title">
        <span className="icon" style={{ fontSize: '1.2rem' }}>📦</span>
        <span className="gradient-text" style={{ fontSize: '1.1rem' }}>Product Catalog</span>
        <span style={{ marginLeft: 'auto', fontSize: '0.75rem', color: 'var(--text-muted)', fontWeight: 600 }}>
          {catalog.length} items
        </span>
      </div>

      <div className="product-grid" style={{ marginTop: '1.5rem' }}>
        {catalog.map((product, index) => {
          const isHighlighted = highlightedProduct === index;
          const inCart = cartIds.has(product.id);

          return (
            <div
              key={product.id}
              className={`product-card ${isHighlighted ? 'highlighted' : ''} ${inCart ? 'selected' : ''}`}
            >
              {/* Image placeholder */}
              <div
                className="product-img"
                style={{
                  background: `linear-gradient(135deg, ${product.gradient[0]}, ${product.gradient[1]})`,
                  opacity: 0.85
                }}
              >
                <span style={{ fontSize: '2.5rem', filter: 'drop-shadow(2px 4px 6px rgba(0,0,0,0.15))' }}>
                  {product.emoji}
                </span>
              </div>

              {/* Info */}
              <div style={{ fontSize: '0.9rem', fontWeight: 700, marginBottom: '0.2rem', color: 'var(--text-primary)' }}>
                {product.name}
              </div>

              {/* Price */}
              <div style={{ fontSize: '1.1rem', fontWeight: 800, color: 'var(--accent-blue)', marginBottom: '0.4rem' }}>
                ₹{product.price}
              </div>

              {/* Rating */}
              <div className="star-rating" style={{ marginBottom: '0.4rem' }}>
                {[1, 2, 3, 4, 5].map(s => (
                  <span key={s} className={`star ${s <= product.rating ? 'filled' : ''}`}>★</span>
                ))}
                <span style={{ fontSize: '0.7rem', color: 'var(--text-muted)', marginLeft: '0.4rem', fontWeight: 600 }}>
                  ({product.reviews})
                </span>
              </div>

              {/* Weight */}
              <div style={{ fontSize: '0.7rem', color: 'var(--text-light)', marginBottom: '1rem', fontWeight: 500 }}>
                Weight: {product.weight} kg
              </div>

              {/* Action */}
              <button
                className={`btn-neo ${inCart ? 'filled-emerald' : 'filled-blue'}`}
                onClick={() => onAddToCart(product)}
                disabled={inCart}
                style={{ width: '100%', justifyContent: 'center', padding: '0.6rem' }}
              >
                {inCart ? '✓ In Cart' : '+ Add to Cart'}
              </button>

              {/* Highlight badge */}
              {isHighlighted && (
                <div style={{
                  position: 'absolute', top: '0.75rem', right: '0.75rem',
                  background: 'var(--accent-blue)', color: '#fff', fontSize: '0.65rem',
                  fontWeight: 800, padding: '0.2rem 0.5rem', borderRadius: 'var(--radius-sm)',
                  boxShadow: 'var(--neo-shadow-raised-sm)'
                }}>
                  FOUND
                </div>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
}
