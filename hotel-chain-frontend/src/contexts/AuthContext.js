import React, { createContext, useContext, useState, useEffect } from 'react';
import { authAPI } from '../services/api';
import toast from 'react-hot-toast';

const AuthContext = createContext();

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [currentUser, setCurrentUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Verifică dacă există un utilizator logat
    const token = localStorage.getItem('authToken');
    const user = localStorage.getItem('currentUser');
    
    if (token && user) {
      try {
        setCurrentUser(JSON.parse(user));
      } catch (error) {
        // Dacă datele sunt corupte, le șterge
        localStorage.removeItem('authToken');
        localStorage.removeItem('currentUser');
      }
    }
    setLoading(false);
  }, []);

  const login = async (credentials) => {
    try {
      const response = await authAPI.login(credentials);
      
      // Adaptează în funcție de răspunsul de la backend
      let userData, token;
      
      if (response.data.data) {
        // Dacă răspunsul vine din API Gateway cu structură ServiceResponse
        userData = response.data.data;
        token = response.data.token || 'mock-token'; // Mock token pentru development
      } else {
        // Răspuns direct
        userData = response.data;
        token = response.data.token || 'mock-token';
      }
      
      localStorage.setItem('authToken', token);
      localStorage.setItem('currentUser', JSON.stringify(userData));
      setCurrentUser(userData);
      
      toast.success('Login successful!');
      return true;
    } catch (error) {
      console.error('Login error:', error);
      toast.error('Login failed. Please check your credentials.');
      throw error;
    }
  };

  const register = async (userData) => {
    try {
      const response = await authAPI.register(userData);
      toast.success('Registration successful! Please login.');
      return response.data;
    } catch (error) {
      console.error('Registration error:', error);
      toast.error('Registration failed. Please try again.');
      throw error;
    }
  };

  const logout = () => {
    localStorage.removeItem('authToken');
    localStorage.removeItem('currentUser');
    setCurrentUser(null);
    toast.success('Logged out successfully!');
  };

  const isAuthenticated = () => !!currentUser;

  const hasRole = (role) => {
    if (!currentUser) return false;
    if (Array.isArray(role)) {
      return role.includes(currentUser.userType);
    }
    return currentUser.userType === role;
  };

  const isClient = () => hasRole('CLIENT');
  const isEmployee = () => hasRole('EMPLOYEE');
  const isManager = () => hasRole('MANAGER');
  const isAdmin = () => hasRole('ADMINISTRATOR');

  const canManageUsers = () => hasRole(['ADMINISTRATOR']);
  const canManageRooms = () => hasRole(['EMPLOYEE', 'MANAGER', 'ADMINISTRATOR']);
  const canManageBookings = () => hasRole(['EMPLOYEE', 'MANAGER', 'ADMINISTRATOR']);
  const canViewDashboard = () => hasRole(['EMPLOYEE', 'MANAGER', 'ADMINISTRATOR']);
  const canViewStatistics = () => hasRole(['MANAGER', 'ADMINISTRATOR']);

  const value = {
    currentUser,
    login,
    register,
    logout,
    isAuthenticated,
    hasRole,
    isClient,
    isEmployee,
    isManager,
    isAdmin,
    canManageUsers,
    canManageRooms,
    canManageBookings,
    canViewDashboard,
    canViewStatistics,
    loading
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};