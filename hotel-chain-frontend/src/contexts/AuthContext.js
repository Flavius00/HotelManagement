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
    // Check if user is already logged in
    const token = localStorage.getItem('authToken');
    const user = localStorage.getItem('currentUser');
    
    if (token && user) {
      try {
        const parsedUser = JSON.parse(user);
        setCurrentUser(parsedUser);
        console.log('Restored user from localStorage:', parsedUser);
      } catch (error) {
        console.error('Error parsing stored user data:', error);
        // Clear corrupted data
        localStorage.removeItem('authToken');
        localStorage.removeItem('currentUser');
      }
    }
    setLoading(false);
  }, []);

  const login = async (credentials) => {
    try {
      console.log('Attempting login with credentials:', { username: credentials.username });
      
      const response = await authAPI.login(credentials);
      console.log('Login response:', response);
      
      let userData, token;
      
      // Handle different response structures
      if (response.data) {
        if (response.data.user) {
          // Response structure: { user: {...}, message: "...", success: true }
          userData = response.data.user;
          token = response.data.token || `token-${userData.id}-${Date.now()}`;
        } else if (response.data.data) {
          // Response structure: { data: {...}, success: true }
          userData = response.data.data;
          token = response.data.token || `token-${userData.id}-${Date.now()}`;
        } else {
          // Response structure: { id: ..., username: ..., ... }
          userData = response.data;
          token = response.data.token || `token-${userData.id}-${Date.now()}`;
        }
      } else {
        throw new Error('Invalid response format');
      }
      
      if (!userData || !userData.id) {
        throw new Error('Invalid user data received');
      }
      
      console.log('Setting user data:', userData);
      
      localStorage.setItem('authToken', token);
      localStorage.setItem('currentUser', JSON.stringify(userData));
      setCurrentUser(userData);
      
      toast.success('Login successful!');
      return true;
    } catch (error) {
      console.error('Login error:', error);
      
      let errorMessage = 'Login failed. Please check your credentials.';
      
      if (error.response) {
        if (error.response.status === 401) {
          errorMessage = 'Invalid username or password.';
        } else if (error.response.data && error.response.data.error) {
          errorMessage = error.response.data.error;
        } else if (error.response.data && typeof error.response.data === 'string') {
          errorMessage = error.response.data;
        }
      } else if (error.message) {
        errorMessage = error.message;
      }
      
      toast.error(errorMessage);
      throw error;
    }
  };

  const register = async (userData) => {
    try {
      console.log('Attempting registration with data:', { 
        username: userData.username, 
        email: userData.email,
        userType: userData.userType 
      });
      
      const response = await authAPI.register(userData);
      console.log('Registration response:', response);
      
      let successMessage = 'Registration successful! Please login.';
      
      if (response.data && response.data.message) {
        successMessage = response.data.message;
      }
      
      toast.success(successMessage);
      return response.data;
    } catch (error) {
      console.error('Registration error:', error);
      
      let errorMessage = 'Registration failed. Please try again.';
      
      if (error.response) {
        if (error.response.data && error.response.data.error) {
          errorMessage = error.response.data.error;
        } else if (error.response.data && typeof error.response.data === 'string') {
          errorMessage = error.response.data;
        } else if (error.response.status === 400) {
          errorMessage = 'Invalid registration data. Please check your inputs.';
        } else if (error.response.status === 409) {
          errorMessage = 'Username or email already exists.';
        }
      } else if (error.message) {
        errorMessage = error.message;
      }
      
      toast.error(errorMessage);
      throw error;
    }
  };

  const logout = () => {
    console.log('Logging out user');
    localStorage.removeItem('authToken');
    localStorage.removeItem('currentUser');
    setCurrentUser(null);
    toast.success('Logged out successfully!');
  };

  const isAuthenticated = () => {
    return !!currentUser && !!localStorage.getItem('authToken');
  };

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