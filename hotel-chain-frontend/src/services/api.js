import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/gateway';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor pentru autentificare
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('authToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor pentru gestionarea erorilor
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('authToken');
      localStorage.removeItem('currentUser');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Auth API
export const authAPI = {
  login: (credentials) => api.post('/auth/login', credentials),
  register: (userData) => api.post('/auth/register', userData),
};

// Rooms API
export const roomsAPI = {
  getAllRooms: () => api.get('/rooms'),
  getAvailableRooms: () => api.get('/rooms/available'),
  getRoomsSorted: () => api.get('/rooms/sorted'),
  filterRooms: (filters) => api.post('/rooms/filter', filters),
  createRoom: (roomData) => api.post('/rooms', roomData),
  updateRoom: (id, roomData) => api.put(`/rooms/${id}`, roomData),
  deleteRoom: (id) => api.delete(`/rooms/${id}`),
  getRoomById: (id) => api.get(`/rooms/${id}`),
};

// Bookings API
export const bookingsAPI = {
  getAllBookings: () => api.get('/bookings'),
  createBooking: (bookingData) => api.post('/bookings', bookingData),
  getUserBookings: (userId) => api.get(`/bookings/user/${userId}`),
  updateBooking: (id, bookingData) => api.put(`/bookings/${id}`, bookingData),
  cancelBooking: (id) => api.patch(`/bookings/${id}/cancel`),
  confirmBooking: (id) => api.patch(`/bookings/${id}/confirm`),
  exportBookings: (format, filename, bookingIds) => 
    api.post('/bookings/export', bookingIds || [], {
      params: { format, filename }
    }),
  checkAvailability: (roomId, checkInDate, checkOutDate) =>
    api.get(`/bookings/availability/${roomId}`, {
      params: { checkInDate, checkOutDate }
    }),
};

// Reviews API
export const reviewsAPI = {
  getRoomReviews: (roomId) => api.get(`/reviews/room/${roomId}`),
  createReview: (reviewData) => api.post('/reviews', reviewData),
  updateReview: (id, reviewData) => api.put(`/reviews/${id}`, reviewData),
  deleteReview: (id) => api.delete(`/reviews/${id}`),
  getUserReviews: (userId) => api.get(`/reviews/user/${userId}`),
  getAverageRating: (roomId) => api.get(`/reviews/room/${roomId}/average`),
};

// Users API
export const usersAPI = {
  getAllUsers: () => api.get('/users'),
  getUserById: (id) => api.get(`/users/${id}`),
  updateUser: (id, userData) => api.put(`/users/${id}`, userData),
  deleteUser: (id) => api.delete(`/users/${id}`),
  deactivateUser: (id) => api.patch(`/users/${id}/deactivate`),
  activateUser: (id) => api.patch(`/users/${id}/activate`),
  getUsersByType: (userType) => api.get(`/users/type/${userType}`),
};

// Dashboard API
export const dashboardAPI = {
  getDashboardData: () => api.get('/dashboard'),
  getHealthStatus: () => api.get('/health'),
  getStatistics: () => api.get('/statistics'),
};

export default api;