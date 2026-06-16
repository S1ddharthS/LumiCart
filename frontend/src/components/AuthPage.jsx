import { useState } from 'react';

export default function AuthPage({ onLogin }) {
  const [isLogin, setIsLogin] = useState(true);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [name, setName] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!email || !password) return;
    if (!isLogin && !name) return;
    
    // Mock authentication
    onLogin({
      email,
      name: isLogin ? email.split('@')[0] : name
    });
  };

  return (
    <div className="auth-container">
      <div className="auth-card glass-neo-card animate-float" style={{ animationDuration: '6s' }}>
        <div className="auth-header">
          <span className="logo-icon" style={{ fontSize: '2.5rem', marginBottom: '0.5rem' }}>🛍️</span>
          <h2 className="gradient-text" style={{ fontSize: '2rem', fontWeight: 800 }}>LumiCart</h2>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem', marginTop: '0.5rem' }}>
            Algorithm-Powered Shopping
          </p>
        </div>

        <form onSubmit={handleSubmit} className="auth-form">
          <div className="form-toggle">
            <button
              type="button"
              className={`toggle-btn ${isLogin ? 'active' : ''}`}
              onClick={() => setIsLogin(true)}
            >
              Sign In
            </button>
            <button
              type="button"
              className={`toggle-btn ${!isLogin ? 'active' : ''}`}
              onClick={() => setIsLogin(false)}
            >
              Sign Up
            </button>
          </div>

          <div className="input-group">
            {!isLogin && (
              <div className="input-wrapper animate-fade-in">
                <span className="input-icon">👤</span>
                <input
                  type="text"
                  className="neo-input"
                  placeholder="Full Name"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  required={!isLogin}
                />
              </div>
            )}
            <div className="input-wrapper">
              <span className="input-icon">✉️</span>
              <input
                type="email"
                className="neo-input"
                placeholder="Email Address"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>
            <div className="input-wrapper">
              <span className="input-icon">🔒</span>
              <input
                type="password"
                className="neo-input"
                placeholder="Password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>
          </div>

          <button type="submit" className="btn-neo filled-blue submit-btn">
            {isLogin ? 'Access Dashboard' : 'Create Account'}
          </button>
        </form>

        <div className="auth-footer">
          Mock Authentication enabled. Use any credentials.
        </div>
      </div>

      <style>{`
        .auth-container {
          min-height: 100vh;
          display: flex;
          align-items: center;
          justify-content: center;
          padding: 2rem;
          background: var(--bg-primary);
        }
        .auth-card {
          width: 100%;
          max-width: 420px;
          padding: 3rem 2.5rem;
          display: flex;
          flex-direction: column;
          align-items: center;
        }
        .auth-header {
          text-align: center;
          margin-bottom: 2rem;
          display: flex;
          flex-direction: column;
          align-items: center;
        }
        .auth-form {
          width: 100%;
          display: flex;
          flex-direction: column;
          gap: 1.5rem;
        }
        .form-toggle {
          display: flex;
          background: var(--bg-secondary);
          border-radius: var(--radius-lg);
          padding: 0.35rem;
          box-shadow: var(--neo-shadow-inset);
          margin-bottom: 1rem;
        }
        .toggle-btn {
          flex: 1;
          padding: 0.6rem;
          border: none;
          background: transparent;
          border-radius: var(--radius-md);
          font-weight: 600;
          font-size: 0.85rem;
          color: var(--text-muted);
          cursor: pointer;
          transition: all 0.3s ease;
        }
        .toggle-btn.active {
          background: var(--bg-primary);
          color: var(--accent-blue);
          box-shadow: var(--neo-shadow-raised-sm);
        }
        .input-group {
          display: flex;
          flex-direction: column;
          gap: 1.25rem;
        }
        .input-wrapper {
          position: relative;
          display: flex;
          align-items: center;
        }
        .input-icon {
          position: absolute;
          left: 1.25rem;
          font-size: 1.1rem;
          opacity: 0.6;
          pointer-events: none;
        }
        .input-wrapper .neo-input {
          width: 100%;
          padding-left: 3rem;
          padding-top: 0.9rem;
          padding-bottom: 0.9rem;
        }
        .submit-btn {
          margin-top: 0.5rem;
          padding: 1rem;
          font-size: 1rem;
          justify-content: center;
          letter-spacing: 0.05em;
        }
        .auth-footer {
          margin-top: 2rem;
          font-size: 0.7rem;
          color: var(--text-light);
          text-align: center;
          letter-spacing: 0.05em;
          text-transform: uppercase;
        }
      `}</style>
    </div>
  );
}
