import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from 'react-query';
import { Toaster } from 'react-hot-toast';
import { AuthProvider } from './contexts/AuthContext';
import './i18n';
import './index.css';
import './App.css';

// Components
import Navbar from './components/layout/Navbar';
import ProtectedRoute from './components/auth/ProtectedRoute';

// Pages
import Home from './pages/Home';
import Login from './pages/auth/Login';
import Register from './pages/auth/Register';
import Rooms from './pages/rooms/Rooms';
import RoomDetails from './pages/rooms/RoomDetails';
import Bookings from './pages/bookings/Bookings';
import Reviews from './pages/reviews/Reviews';
import Dashboard from './pages/dashboard/Dashboard';
import NotFound from './pages/NotFound';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
      retry: 1,
      staleTime: 5 * 60 * 1000, // 5 minutes
    },
  },
});

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        <Router>
          <div className="App">
            <Navbar />
            <main className="main-content-area">
              <div className="container">
                <Routes>
                  <Route path="/" element={<Home />} />
                  <Route path="/login" element={<Login />} />
                  <Route path="/register" element={<Register />} />
                  <Route path="/rooms" element={<Rooms />} />
                  <Route path="/rooms/:id" element={<RoomDetails />} />
                  
                  {/* Protected Routes */}
                  <Route 
                    path="/bookings" 
                    element={
                      <ProtectedRoute>
                        <Bookings />
                      </ProtectedRoute>
                    } 
                  />
                  <Route 
                    path="/reviews" 
                    element={
                      <ProtectedRoute>
                        <Reviews />
                      </ProtectedRoute>
                    } 
                  />
                  
                  {/* Admin/Employee/Manager Routes */}
                  <Route 
                    path="/dashboard" 
                    element={
                      <ProtectedRoute roles={['EMPLOYEE', 'MANAGER', 'ADMINISTRATOR']}>
                        <Dashboard />
                      </ProtectedRoute>
                    } 
                  />
                  
                  {/* 404 Route */}
                  <Route path="*" element={<NotFound />} />
                </Routes>
              </div>
            </main>
            
            {/* Toast Notifications */}
            <Toaster 
              position="top-right" 
              toastOptions={{
                duration: 4000,
                style: {
                  background: '#1e293b',
                  color: '#fff',
                  borderRadius: '12px',
                  boxShadow: '0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05)',
                },
                success: {
                  duration: 3000,
                  style: {
                    background: '#10b981',
                  },
                },
                error: {
                  duration: 5000,
                  style: {
                    background: '#ef4444',
                  },
                },
              }}
            />
          </div>
        </Router>
      </AuthProvider>
    </QueryClientProvider>
  );
}

export default App;