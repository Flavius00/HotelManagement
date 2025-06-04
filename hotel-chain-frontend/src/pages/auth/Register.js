import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../../contexts/AuthContext';
import { useForm } from 'react-hook-form';
import { UserPlus, Eye, EyeOff, User, Mail, Phone, Lock } from 'lucide-react';

const Register = () => {
  const { t } = useTranslation();
  const { register: registerUser, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const [showPassword, setShowPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  const { register, handleSubmit, formState: { errors }, watch } = useForm({
    defaultValues: {
      userType: 'CLIENT'
    }
  });

  const password = watch('password', '');

  // Redirect if already authenticated
  useEffect(() => {
    if (isAuthenticated()) {
      navigate('/', { replace: true });
    }
  }, [isAuthenticated, navigate]);

  const onSubmit = async (data) => {
    setIsLoading(true);
    try {
      await registerUser(data);
      navigate('/login');
    } catch (error) {
      console.error('Registration failed:', error);
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
            <UserPlus className="w-8 h-8" />
          </div>
          <h2 className="auth-title">
            {t('auth.register')}
          </h2>
          <p className="auth-subtitle">
            Create your account to get started
          </p>
        </div>

        {/* Registration Form */}
        <form className="auth-form" onSubmit={handleSubmit(onSubmit)}>
          <div className="space-y-6">
            {/* Username */}
            <div className="form-group">
              <label htmlFor="username" className="form-label">
                {t('auth.username')} *
              </label>
              <div className="input-icon">
                <User className="icon" />
                <input
                  {...register('username', {
                    required: 'Username is required',
                    minLength: {
                      value: 3,
                      message: 'Username must be at least 3 characters'
                    },
                    pattern: {
                      value: /^[a-zA-Z0-9_]+$/,
                      message: 'Username can only contain letters, numbers, and underscores'
                    }
                  })}
                  type="text"
                  className={`form-input ${errors.username ? 'error' : ''}`}
                  placeholder="Choose a username"
                />
              </div>
              {errors.username && (
                <p className="form-error">{errors.username.message}</p>
              )}
            </div>

            {/* Email */}
            <div className="form-group">
              <label htmlFor="email" className="form-label">
                {t('auth.email')} *
              </label>
              <div className="input-icon">
                <Mail className="icon" />
                <input
                  {...register('email', {
                    required: 'Email is required',
                    pattern: {
                      value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                      message: 'Invalid email address'
                    }
                  })}
                  type="email"
                  className={`form-input ${errors.email ? 'error' : ''}`}
                  placeholder="Enter your email"
                />
              </div>
              {errors.email && (
                <p className="form-error">{errors.email.message}</p>
              )}
            </div>

            {/* Password */}
            <div className="form-group">
              <label htmlFor="password" className="form-label">
                {t('auth.password')} *
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
                  placeholder="Create a password"
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

            {/* Confirm Password */}
            <div className="form-group">
              <label htmlFor="confirmPassword" className="form-label">
                Confirm Password *
              </label>
              <div className="input-icon">
                <Lock className="icon" />
                <input
                  {...register('confirmPassword', {
                    required: 'Please confirm your password',
                    validate: value => value === password || 'Passwords do not match'
                  })}
                  type="password"
                  className={`form-input ${errors.confirmPassword ? 'error' : ''}`}
                  placeholder="Confirm your password"
                />
              </div>
              {errors.confirmPassword && (
                <p className="form-error">{errors.confirmPassword.message}</p>
              )}
            </div>

            {/* First Name */}
            <div className="form-group">
              <label htmlFor="firstName" className="form-label">
                {t('auth.firstName')}
              </label>
              <input
                {...register('firstName')}
                type="text"
                className="form-input"
                placeholder="Enter your first name"
              />
            </div>

            {/* Last Name */}
            <div className="form-group">
              <label htmlFor="lastName" className="form-label">
                {t('auth.lastName')}
              </label>
              <input
                {...register('lastName')}
                type="text"
                className="form-input"
                placeholder="Enter your last name"
              />
            </div>

            {/* Phone Number */}
            <div className="form-group">
              <label htmlFor="phoneNumber" className="form-label">
                {t('auth.phoneNumber')}
              </label>
              <div className="input-icon">
                <Phone className="icon" />
                <input
                  {...register('phoneNumber', {
                    pattern: {
                      value: /^[+]?[0-9]{10,15}$/,
                      message: 'Invalid phone number format'
                    }
                  })}
                  type="tel"
                  className={`form-input ${errors.phoneNumber ? 'error' : ''}`}
                  placeholder="+1234567890"
                />
              </div>
              {errors.phoneNumber && (
                <p className="form-error">{errors.phoneNumber.message}</p>
              )}
            </div>

            {/* User Type */}
            <div className="form-group">
              <label htmlFor="userType" className="form-label">
                {t('auth.userType')} *
              </label>
              <select
                {...register('userType', { required: 'User type is required' })}
                className={`form-select ${errors.userType ? 'error' : ''}`}
              >
                <option value="CLIENT">Client</option>
                <option value="EMPLOYEE">Employee</option>
                <option value="MANAGER">Manager</option>
                <option value="ADMINISTRATOR">Administrator</option>
              </select>
              {errors.userType && (
                <p className="form-error">{errors.userType.message}</p>
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
                  Creating account...
                </div>
              ) : (
                <div className="auth-loading">
                  <UserPlus className="w-5 h-5" />
                  {t('auth.register')}
                </div>
              )}
            </button>
          </div>

          {/* Links */}
          <div className="auth-links">
            <p className="text-sm text-secondary">
              Already have an account?{' '}
              <Link
                to="/login"
                className="auth-link"
              >
                {t('auth.login')}
              </Link>
            </p>
          </div>
        </form>
      </div>
    </div>
  );
};

export default Register;