import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/gateway';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000, // 10 seconds timeout
});

// Request interceptor for authentication
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('authToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    console.log(`🚀 ${config.method?.toUpperCase()} ${config.url}`);
    if (config.data) {
      console.log('📤 Request data:', config.data);
    }
    
    return config;
  },
  (error) => {
    console.error('❌ Request error:', error);
    return Promise.reject(error);
  }
);

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => {
    console.log(`✅ ${response.config.method?.toUpperCase()} ${response.config.url} - ${response.status}`);
    if (response.data) {
      console.log('📥 Response data:', response.data);
    }
    return response;
  },
  (error) => {
    console.error(`❌ ${error.config?.method?.toUpperCase()} ${error.config?.url} - ${error.response?.status || 'Network Error'}`);
    
    if (error.response?.data) {
      console.error('📥 Error response:', error.response.data);
    }
    
    if (error.response?.status === 401) {
      console.warn('🔒 Unauthorized - clearing auth data');
      localStorage.removeItem('authToken');
      localStorage.removeItem('currentUser');
      window.location.href = '/login';
    }
    
    return Promise.reject(error);
  }
);

// Auth API
export const authAPI = {
  login: (credentials) => {
    console.log('🔐 Attempting login for:', credentials.username);
    return api.post('/auth/login', credentials);
  },
  register: (userData) => {
    console.log('📝 Attempting registration for:', userData.username);
    return api.post('/auth/register', userData);
  },
};

// Rooms API
export const roomsAPI = {
  getAllRooms: () => {
    console.log('🏠 Fetching all rooms');
    return api.get('/rooms');
  },
  getAvailableRooms: () => {
    console.log('🏠 Fetching available rooms');
    return api.get('/rooms/available');
  },
  getRoomsSorted: () => {
    console.log('🏠 Fetching sorted rooms');
    return api.get('/rooms/sorted');
  },
  filterRooms: (filters) => {
    console.log('🔍 Filtering rooms with:', filters);
    return api.post('/rooms/filter', filters);
  },
  createRoom: (roomData) => {
    console.log('➕ Creating room');
    return api.post('/rooms', roomData);
  },
  updateRoom: (id, roomData) => {
    console.log('✏️ Updating room:', id);
    return api.put(`/rooms/${id}`, roomData);
  },
  deleteRoom: (id) => {
    console.log('🗑️ Deleting room:', id);
    return api.delete(`/rooms/${id}`);
  },
  getRoomById: (id) => {
    console.log('🏠 Fetching room by ID:', id);
    return api.get(`/rooms/${id}`);
  },
};

// Bookings API
export const bookingsAPI = {
  getAllBookings: () => {
    console.log('📅 Fetching all bookings');
    return api.get('/bookings');
  },
  createBooking: (bookingData) => {
    console.log('➕ Creating booking');
    return api.post('/bookings', bookingData);
  },
  getUserBookings: (userId) => {
    console.log('📅 Fetching bookings for user:', userId);
    return api.get(`/bookings/user/${userId}`);
  },
  updateBooking: (id, bookingData) => {
    console.log('✏️ Updating booking:', id);
    return api.put(`/bookings/${id}`, bookingData);
  },
  cancelBooking: (id) => {
    console.log('❌ Cancelling booking:', id);
    return api.patch(`/bookings/${id}/cancel`);
  },
  confirmBooking: (id) => {
    console.log('✅ Confirming booking:', id);
    return api.patch(`/bookings/${id}/confirm`);
  },
  exportBookings: (format, filename, bookingIds) => {
    console.log('📥 Exporting bookings:', { format, filename });
    return api.post('/bookings/export', bookingIds || [], {
      params: { format, filename }
    });
  },
  checkAvailability: (roomId, checkInDate, checkOutDate) => {
    console.log('🔍 Checking availability for room:', roomId);
    return api.get(`/bookings/availability/${roomId}`, {
      params: { checkInDate, checkOutDate }
    });
  },
};

// Reviews API
export const reviewsAPI = {
  getRoomReviews: (roomId) => {
    console.log('⭐ Fetching reviews for room:', roomId);
    return api.get(`/reviews/room/${roomId}`);
  },
  createReview: (reviewData) => {
    console.log('➕ Creating review');
    return api.post('/reviews', reviewData);
  },
  updateReview: (id, reviewData) => {
    console.log('✏️ Updating review:', id);
    return api.put(`/reviews/${id}`, reviewData);
  },
  deleteReview: (id) => {
    console.log('🗑️ Deleting review:', id);
    return api.delete(`/reviews/${id}`);
  },
  getUserReviews: (userId) => {
    console.log('⭐ Fetching reviews for user:', userId);
    return api.get(`/reviews/user/${userId}`);
  },
  getAverageRating: (roomId) => {
    console.log('📊 Fetching average rating for room:', roomId);
    return api.get(`/reviews/room/${roomId}/average`);
  },
};

// Users API
export const usersAPI = {
  getAllUsers: () => {
    console.log('👥 Fetching all users');
    return api.get('/users');
  },
  getUserById: (id) => {
    console.log('👤 Fetching user by ID:', id);
    return api.get(`/users/${id}`);
  },
  updateUser: (id, userData) => {
    console.log('✏️ Updating user:', id);
    return api.put(`/users/${id}`, userData);
  },
  deleteUser: (id) => {
    console.log('🗑️ Deleting user:', id);
    return api.delete(`/users/${id}`);
  },
  deactivateUser: (id) => {
    console.log('⏸️ Deactivating user:', id);
    return api.patch(`/users/${id}/deactivate`);
  },
  activateUser: (id) => {
    console.log('▶️ Activating user:', id);
    return api.patch(`/users/${id}/activate`);
  },
  getUsersByType: (userType) => {
    console.log('👥 Fetching users by type:', userType);
    return api.get(`/users/type/${userType}`);
  },
};

// Dashboard API
export const dashboardAPI = {
  getDashboardData: () => {
    console.log('📊 Fetching dashboard data');
    return api.get('/dashboard');
  },
  getHealthStatus: () => {
    console.log('🏥 Checking health status');
    return api.get('/health');
  },
  getStatistics: () => {
    console.log('📈 Fetching statistics');
    return api.get('/statistics');
  },
};

export default api;