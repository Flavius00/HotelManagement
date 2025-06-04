import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import { useTranslation } from 'react-i18next';
import { Shield, AlertCircle } from 'lucide-react';

const ProtectedRoute = ({ children, roles = [] }) => {
  const { isAuthenticated, hasRole, loading, currentUser } = useAuth();
  const location = useLocation();
  const { t } = useTranslation();

  // Show loading while checking authentication
  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <div className="spinner spinner-lg"></div>
          <p className="mt-4 text-secondary">{t('common.loading')}</p>
        </div>
      </div>
    );
  }

  // Redirect to login if not authenticated
  if (!isAuthenticated()) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  // Check role-based access if roles are specified
  if (roles.length > 0 && !hasRole(roles)) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="card" style={{ maxWidth: '28rem', width: '100%' }}>
          <div className="card-body text-center">
            <div className="w-16 h-16 bg-error rounded-full flex items-center justify-center mx-auto mb-4">
              <Shield className="w-8 h-8 text-white" />
            </div>
            <h2 className="text-2xl font-bold text-primary mb-2">
              Access Denied
            </h2>
            <p className="text-secondary mb-4">
              You don't have permission to access this page.
            </p>
            <div className="alert alert-warning">
              <div className="flex items-center">
                <AlertCircle className="alert-icon" />
                <div className="alert-content">
                  <div className="alert-title">Permission Required</div>
                  <div className="alert-description">
                    <p>Required roles: {roles.join(', ')}</p>
                    <p>Your role: {currentUser?.userType}</p>
                  </div>
                </div>
              </div>
            </div>
            <button
              onClick={() => window.history.back()}
              className="btn btn-primary"
            >
              Go Back
            </button>
          </div>
        </div>
      </div>
    );
  }

  // Render the protected component
  return children;
};

export default ProtectedRoute;