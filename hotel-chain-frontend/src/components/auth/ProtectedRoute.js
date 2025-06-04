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
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">{t('common.loading')}</p>
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
        <div className="max-w-md w-full bg-white shadow-lg rounded-lg p-8 text-center">
          <div className="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <Shield className="w-8 h-8 text-red-600" />
          </div>
          <h2 className="text-2xl font-bold text-gray-900 mb-2">
            Access Denied
          </h2>
          <p className="text-gray-600 mb-4">
            You don't have permission to access this page.
          </p>
          <div className="bg-yellow-50 border border-yellow-200 rounded-md p-3 mb-4">
            <div className="flex items-center">
              <AlertCircle className="w-5 h-5 text-yellow-600 mr-2" />
              <div className="text-sm text-yellow-800">
                <p>Required roles: {roles.join(', ')}</p>
                <p>Your role: {currentUser?.userType}</p>
              </div>
            </div>
          </div>
          <button
            onClick={() => window.history.back()}
            className="bg-blue-600 hover:bg-blue-700 text-white font-medium py-2 px-4 rounded-md transition-colors"
          >
            Go Back
          </button>
        </div>
      </div>
    );
  }

  // Render the protected component
  return children;
};

export default ProtectedRoute;