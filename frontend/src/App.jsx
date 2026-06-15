import { useReducer, useCallback, useEffect } from 'react';
import './App.css';
import Navbar from './components/Navbar';
import CatalogGrid from './components/CatalogGrid';
import SearchFilter from './components/SearchFilter';
import SortingCenter from './components/SortingCenter';
import LogisticsOptimizer from './components/LogisticsOptimizer';
import CartCheckout from './components/CartCheckout';
import PromoTargeter from './components/PromoTargeter';
import DiagnosticsTerminal from './components/DiagnosticsTerminal';
import GlobalControls from './components/GlobalControls';
import FloydMatrixModal from './components/FloydMatrixModal';
import { useAlgorithms } from './hooks/useAlgorithms';

/* ============================
   Product Data Generators
   ============================ */

const PRODUCT_NAMES = [
  'Smartphone', 'Laptop', 'Tablet', 'Smartwatch',
  'Headphones', 'Mouse', 'Keyboard', 'Monitor',
  'Camera', 'Speaker', 'Charger', 'Power Bank',
  'Router', 'Webcam', 'Microphone', 'Printer',
  'Scanner', 'Gamepad', 'Earbuds', 'Hard Drive'
];

const PRODUCT_EMOJIS = [
  '📱', '💻', '💊', '⌚',
  '🎧', '🖱️', '⌨️', '🖥️',
  '📷', '🔊', '🔌', '🔋',
  '📶', '📹', '🎤', '🖨️',
  '📠', '🎮', '🎵', '💾'
];

// Softer gradients for light theme
const GRADIENT_COLORS = [
  ['#a1c4fd', '#c2e9fb'], ['#ff9a9e', '#fecfef'], ['#fbc2eb', '#a6c1ee'],
  ['#84fab0', '#8fd3f4'], ['#a6c0fe', '#f68084'], ['#e0c3fc', '#8ec5fc'],
  ['#fccb90', '#d57eeb'], ['#e0c3fc', '#8ec5fc'], ['#fdfbfb', '#ebedee'],
  ['#cd9cf2', '#f6f3ff'], ['#89f7fe', '#66a6ff'], ['#48c6ef', '#6f86d6'],
  ['#feada6', '#f5efef'], ['#a18cd1', '#fbc2eb'], ['#ffecd2', '#fcb69f'],
  ['#cfd9df', '#e2ebf0'], ['#fdfcfb', '#e2d1c3'], ['#e6e9f0', '#eef1f5'],
  ['#c1dfc4', '#deecdd'], ['#fff1eb', '#ace0f9']
];

const WAREHOUSE_NAMES = ['Delhi Hub', 'Mumbai Depot', 'Bangalore DC', 'Chennai Port', 'Kolkata Store', 'Hyderabad Center'];

function generateProduct(index) {
  const nameIdx = index % PRODUCT_NAMES.length;
  return {
    id: index,
    name: PRODUCT_NAMES[nameIdx],
    emoji: PRODUCT_EMOJIS[nameIdx],
    gradient: GRADIENT_COLORS[nameIdx],
    price: Math.floor(Math.random() * 9500) + 500, // Adjusted for Rupees
    rating: Math.floor(Math.random() * 5) + 1,
    weight: Math.floor(Math.random() * 20) + 1,
    reviews: Math.floor(Math.random() * 500) + 10,
  };
}

function generateGraph() {
  const n = WAREHOUSE_NAMES.length;
  const matrix = Array.from({ length: n }, () => Array(n).fill(0));
  for (let i = 0; i < n; i++) {
    for (let j = i + 1; j < n; j++) {
      if (Math.random() < 0.7 || j === i + 1) {
        const w = Math.floor(Math.random() * 90) + 10;
        matrix[i][j] = w;
        matrix[j][i] = w;
      }
    }
  }
  return { matrix, nodeCount: n, nodeNames: [...WAREHOUSE_NAMES] };
}

function generateCoupons() {
  // Coupon values in Rupees
  const values = [50, 100, 150, 200, 250, 300, 500];
  const count = Math.floor(Math.random() * 3) + 5;
  const selected = [];
  const used = new Set();
  while (selected.length < count && selected.length < values.length) {
    const idx = Math.floor(Math.random() * values.length);
    if (!used.has(idx)) {
      used.add(idx);
      selected.push(values[idx]);
    }
  }
  return selected.sort((a, b) => a - b);
}

/* ============================
   State Reducer
   ============================ */

const initialState = {
  catalog: [],
  cart: [],
  graphData: null,
  coupons: [],
  searchResult: null,
  mstResult: null,
  floydResult: null,
  knapsackResult: null,
  subsetResult: null,
  logs: [],
  sessionToken: crypto.randomUUID().split('-')[0].toUpperCase(),
  showFloydModal: false,
  highlightedProduct: -1,
};

function reducer(state, action) {
  switch (action.type) {
    case 'GENERATE_CATALOG': {
      const count = Math.floor(Math.random() * 8) + 12;
      const catalog = Array.from({ length: count }, (_, i) => generateProduct(i));
      const graph = generateGraph();
      const coupons = generateCoupons();
      return { ...state, catalog, graphData: graph, coupons, cart: [], searchResult: null, mstResult: null, floydResult: null, knapsackResult: null, subsetResult: null, highlightedProduct: -1 };
    }
    case 'CLEAR_STATE':
      return { ...initialState, sessionToken: state.sessionToken, logs: [] };
    case 'ADD_TO_CART': {
      const item = action.payload;
      const exists = state.cart.find(c => c.id === item.id);
      if (exists) return state;
      return { ...state, cart: [...state.cart, { ...item }] };
    }
    case 'REMOVE_FROM_CART':
      return { ...state, cart: state.cart.filter(c => c.id !== action.payload), knapsackResult: null };
    case 'SET_SEARCH_RESULT':
      return { ...state, searchResult: action.payload, highlightedProduct: action.payload.foundIndex };
    case 'SET_SORT_RESULT': {
      const { type, result } = action.payload;
      const newCatalog = result.originalIndices.map(idx => state.catalog[idx]);
      return { ...state, catalog: newCatalog, highlightedProduct: -1 };
    }
    case 'SET_MST_RESULT':
      return { ...state, mstResult: action.payload };
    case 'SET_FLOYD_RESULT':
      return { ...state, floydResult: action.payload, showFloydModal: true };
    case 'SET_KNAPSACK_RESULT':
      return { ...state, knapsackResult: action.payload };
    case 'SET_SUBSET_RESULT':
      return { ...state, subsetResult: action.payload };
    case 'CLOSE_FLOYD_MODAL':
      return { ...state, showFloydModal: false };
    case 'ADD_LOG':
      return { ...state, logs: [...state.logs, { ...action.payload, timestamp: new Date().toISOString() }] };
    case 'CLEAR_LOGS':
      return { ...state, logs: [] };
    case 'CLEAR_HIGHLIGHT':
      return { ...state, highlightedProduct: -1 };
    default:
      return state;
  }
}

/* ============================
   Main Application Component
   ============================ */

function App() {
  const [state, dispatch] = useReducer(reducer, initialState);
  const algorithms = useAlgorithms(dispatch);

  const handleGenerateCatalog = useCallback(() => {
    dispatch({ type: 'GENERATE_CATALOG' });
  }, []);

  const handleClearState = useCallback(() => {
    dispatch({ type: 'CLEAR_STATE' });
  }, []);

  const mstCost = state.mstResult?.result?.totalCost ?? '—';

  // Scroll reveal logic
  useEffect(() => {
    const handleScroll = () => {
      const reveals = document.querySelectorAll('.scroll-reveal');
      reveals.forEach((element) => {
        const windowHeight = window.innerHeight;
        const elementTop = element.getBoundingClientRect().top;
        const elementVisible = 100;
        if (elementTop < windowHeight - elementVisible) {
          element.classList.add('visible');
        }
      });
    };
    window.addEventListener('scroll', handleScroll);
    handleScroll(); // Trigger once on mount
    return () => window.removeEventListener('scroll', handleScroll);
  }, [state.catalog.length]); // Re-run when catalog is generated

  return (
    <div className="app">
      <Navbar
        catalogCount={state.catalog.length}
        mstCost={mstCost}
        sessionToken={state.sessionToken}
        cartCount={state.cart.length}
        catalog={state.catalog}
        onSearch={algorithms.runBinarySearch}
        searchLoading={algorithms.loading.search}
      />

      <div style={{ marginTop: '2rem' }}>
        <GlobalControls
          onGenerate={handleGenerateCatalog}
          onClear={handleClearState}
          hasCatalog={state.catalog.length > 0}
        />
      </div>

      {state.catalog.length > 0 ? (
        <div className="section-container animate-fade-in" style={{ marginTop: '2rem' }}>
          
          {/* Section 1: Catalog */}
          <section className="section scroll-reveal visible">
            <div className="glass-neo-card">
              <CatalogGrid
                catalog={state.catalog}
                highlightedProduct={state.highlightedProduct}
                onAddToCart={(item) => dispatch({ type: 'ADD_TO_CART', payload: item })}
                cart={state.cart}
              />
            </div>
          </section>

          <div className="section-divider scroll-reveal"></div>

          {/* Section 2: Search and Sort */}
          <section className="section dual-section">
            <div className="glass-neo-card scroll-reveal from-left">
              <SearchFilter
                catalog={state.catalog}
                searchResult={state.searchResult}
                onSearch={algorithms.runBinarySearch}
                loading={algorithms.loading.search}
                onClearHighlight={() => dispatch({ type: 'CLEAR_HIGHLIGHT' })}
              />
            </div>
            <div className="glass-neo-card scroll-reveal from-right">
              <SortingCenter
                catalog={state.catalog}
                onMergeSort={algorithms.runMergeSort}
                onQuickSort={algorithms.runQuickSort}
                loadingMerge={algorithms.loading.mergeSort}
                loadingQuick={algorithms.loading.quickSort}
              />
            </div>
          </section>

          <div className="section-divider scroll-reveal"></div>

          {/* Section 3: Logistics (Graph) */}
          <section className="section scroll-reveal">
            <div className="glass-neo-card">
              <LogisticsOptimizer
                graphData={state.graphData}
                mstResult={state.mstResult}
                onPrims={() => algorithms.runPrims(state.graphData.matrix, state.graphData.nodeCount, state.graphData.nodeNames)}
                onKruskals={() => algorithms.runKruskals(state.graphData.matrix, state.graphData.nodeCount, state.graphData.nodeNames)}
                onFloyd={() => algorithms.runFloyd(state.graphData.matrix, state.graphData.nodeCount, state.graphData.nodeNames)}
                loadingPrims={algorithms.loading.prims}
                loadingKruskals={algorithms.loading.kruskals}
                loadingFloyd={algorithms.loading.floyd}
              />
            </div>
          </section>

          <div className="section-divider scroll-reveal"></div>

          {/* Section 4: Cart & Promos */}
          <section className="section dual-section">
            <div className="glass-neo-card scroll-reveal from-left">
              <CartCheckout
                cart={state.cart}
                knapsackResult={state.knapsackResult}
                onRemoveItem={(id) => dispatch({ type: 'REMOVE_FROM_CART', payload: id })}
                onOptimize={algorithms.runKnapsack}
                loading={algorithms.loading.knapsack}
              />
            </div>
            <div className="glass-neo-card scroll-reveal from-right">
              <PromoTargeter
                coupons={state.coupons}
                subsetResult={state.subsetResult}
                onSolve={algorithms.runSubsetSum}
                loading={algorithms.loading.subset}
              />
            </div>
          </section>

          <div className="section-divider scroll-reveal"></div>

          {/* Section 5: Diagnostics Terminal */}
          <section className="section scroll-reveal" style={{ paddingBottom: '4rem' }}>
            <div className="glass-neo-card">
              <DiagnosticsTerminal
                logs={state.logs}
                onClear={() => dispatch({ type: 'CLEAR_LOGS' })}
              />
            </div>
          </section>
          
        </div>
      ) : (
        <div className="empty-state">
          <div className="empty-state-content animate-float">
            <div className="empty-icon" style={{ filter: 'none', textShadow: '2px 2px 4px rgba(0,0,0,0.1)' }}>🛒</div>
            <h2 style={{ color: 'var(--text-primary)' }}>Welcome to <span className="gradient-text">LumiCart</span></h2>
            <p style={{ color: 'var(--text-secondary)' }}>Click <strong>"Generate Random Catalog"</strong> to populate the dashboard with products, warehouse data, and coupons.</p>
          </div>
        </div>
      )}

      {/* Floyd-Warshall Matrix Modal */}
      {state.showFloydModal && state.floydResult && (
        <FloydMatrixModal
          result={state.floydResult}
          onClose={() => dispatch({ type: 'CLOSE_FLOYD_MODAL' })}
        />
      )}
    </div>
  );
}

export default App;
