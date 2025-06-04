import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../../contexts/AuthContext';
import { useForm } from 'react-hook-form';
import { LogIn, Eye, EyeOff, User, Lock } from 'lucide-react';
import toast from 'react-hot-toast';

const Login = () => {
  const { t } = useTranslation();
  const { login, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [showPassword, setShowPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  const { register, handleSubmit, formState: { errors } } = useForm();

  // Redirect if already authenticated
  useEffect(() => {
    if (isAuthenticated()) {
      const from = location.state?.from?.pathname || '/';
      navigate(from, { replace: true });
    }
  }, [isAuthenticated, navigate, location]);

  const onSubmit = async (data) => {
    setIsLoading(true);
    try {
      await login(data);
      const from = location.state?.from?.pathname || '/';
      navigate(from, { replace: true });
    } catch (error) {
      console.error('Login failed:', error);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        {/* Header */}
        <div className="auth-header">
          <div className="auth-icon">
            <LogIn className="w-8 h-8" />
          </div>
          <h2 className="auth-title">
            {t('auth.login')}
          </h2>
          <p className="auth-subtitle">
            Sign in to your account to continue
          </p>
        </div>

        {/* Login Form */}
        <form className="auth-form" onSubmit={handleSubmit(onSubmit)}>
          <div className="space-y-6">
            {/* Username Field */}
            <div className="form-group">
              <label htmlFor="username" className="form-label">
                {t('auth.username')}
              </label>
              <div className="input-icon">
                <User className="icon" />
                <input
                  {...register('username', {
                    required: 'Username is required',
                    minLength: {
                      value: 3,
                      message: 'Username must be at least 3 characters'
                    }
                  })}
                  type="text"
                  className={`form-input ${errors.username ? 'error' : ''}`}
                  placeholder="Enter your username"
                />
              </div>
              {errors.username && (
                <p className="form-error">{errors.username.message}</p>
              )}
            </div>

            {/* Password Field */}
            <div className="form-group">
              <label htmlFor="password" className="form-label">
                {t('auth.password')}
              </label>
              <div className="input-icon input-icon-right">
                <Lock className="icon" style={{ left: 'var(--space-3)' }} />
                <input
                  {...register('password', {
                    required: 'Password is required',
                    minLength: {
                      value: 6,
                      message: 'Password must be at least 6 characters'
                    }
                  })}
                  type={showPassword ? 'text' : 'password'}
                  className={`form-input ${errors.password ? 'error' : ''}`}
                  placeholder="Enter your password"
                  style={{ paddingLeft: '2.5rem', paddingRight: '2.5rem' }}
                />
                <button
                  type="button"
                  className="icon"
                  style={{ 
                    right: 'var(--space-3)', 
                    left: 'auto',
                    background: 'none',
                    border: 'none',
                    cursor: 'pointer',
                    color: 'var(--gray-400)'
                  }}
                  onClick={() => setShowPassword(!showPassword)}
                >
                  {showPassword ? (
                    <EyeOff className="w-5 h-5" />
                  ) : (
                    <Eye className="w-5 h-5" />
                  )}
                </button>
              </div>
              {errors.password && (
                <p className="form-error">{errors.password.message}</p>
              )}
            </div>
          </div>

          {/* Submit Button */}
          <div style={{ marginTop: 'var(--space-8)' }}>
            <button
              type="submit"
              disabled={isLoading}
              className="auth-submit"
            >
              {isLoading ? (
                <div className="auth-loading">
                  <div className="spinner"></div>
                  Signing in...
                </div>
              ) : (
                <div className="auth-loading">
                  <LogIn className="w-5 h-5" />
                  {t('auth.login')}
                </div>
              )}
            </button>
          </div>

          {/* Links */}
          <div className="auth-links">
            <p className="text-sm text-secondary">
              Don't have an account?{' '}
              <Link
                to="/register"
                className="auth-link"
              >
                {t('auth.register')}
              </Link>
            </p>
          </div>
        </form>

        {/* Demo Accounts */}
        <div className="demo-accounts">
          <h3 className="demo-title">Demo Accounts:</h3>
          <div className="demo-list">
            <div className="demo-item">
              <span>Client:</span>
              <span>client/password</span>
            </div>
            <div className="demo-item">
              <span>Employee:</span>
              <span>employee/password</span>
            </div>
            <div className="demo-item">
              <span>Manager:</span>
              <span>manager/password</span>
            </div>
            <div className="demo-item">
              <span>Admin:</span>
              <span>admin/password</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Login;