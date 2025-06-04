import React, { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../../contexts/AuthContext';
import { useQuery } from 'react-query';
import { bookingsAPI } from '../../services/api';
import { 
  Calendar, 
  Clock, 
  MapPin, 
  DollarSign,
  Download,
  MoreHorizontal,
  CheckCircle,
  XCircle,
  AlertCircle
} from 'lucide-react';
import toast from 'react-hot-toast';

const Bookings = () => {
  const { t } = useTranslation();
  const { currentUser } = useAuth();
  const [statusFilter, setStatusFilter] = useState('ALL');

  // Fetch user bookings
  const { data: bookings = [], isLoading, refetch } = useQuery(
    ['bookings', currentUser?.id],
    async () => {
      if (!currentUser?.id) return [];
      const response = await bookingsAPI.getUserBookings(currentUser.id);
      return response.data.data || response.data;
    },
    {
      enabled: !!currentUser?.id,
      onError: () => {
        toast.error('Failed to load bookings');
      }
    }
  );

  const handleCancelBooking = async (bookingId) => {
    if (!window.confirm('Are you sure you want to cancel this booking?')) {
      return;
    }

    try {
      await bookingsAPI.cancelBooking(bookingId);
      toast.success('Booking cancelled successfully');
      refetch();
    } catch (error) {
      toast.error('Failed to cancel booking');
    }
  };

  const filteredBookings = bookings.filter(booking => {
    if (statusFilter === 'ALL') return true;
    return booking.status === statusFilter;
  });

  const getStatusBadge = (status) => {
    const statusConfig = {
      PENDING: { color: 'bg-yellow-100 text-yellow-800', icon: AlertCircle },
      CONFIRMED: { color: 'bg-green-100 text-green-800', icon: CheckCircle },
      CANCELLED: { color: 'bg-red-100 text-red-800', icon: XCircle },
      COMPLETED: { color: 'bg-blue-100 text-blue-800', icon: CheckCircle }
    };

    const config = statusConfig[status] || statusConfig.PENDING;
    const Icon = config.icon;

    return (
      <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${config.color}`}>
        <Icon className="w-3 h-3 mr-1" />
        {status}
      </span>
    );
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-64">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">{t('common.loading')}</p>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">{t('bookings.title')}</h1>
          <p className="mt-1 text-gray-600">
            Manage your hotel reservations
          </p>
        </div>
        
        <div className="mt-4 sm:mt-0 flex items-center space-x-4">
          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="ALL">All Bookings</option>
            <option value="PENDING">Pending</option>
            <option value="CONFIRMED">Confirmed</option>
            <option value="CANCELLED">Cancelled</option>
            <option value="COMPLETED">Completed</option>
          </select>
        </div>
      </div>

      {/* Bookings List */}
      {filteredBookings.length === 0 ? (
        <div className="text-center py-12">
          <div className="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <Calendar className="w-8 h-8 text-gray-400" />
          </div>
          <h3 className="text-lg font-medium text-gray-900 mb-2">No bookings found</h3>
          <p className="text-gray-600 mb-4">
            {statusFilter === 'ALL' 
              ? "You haven't made any bookings yet" 
              : `No bookings with status: ${statusFilter}`
            }
          </p>
        </div>
      ) : (
        <div className="space-y-4">
          {filteredBookings.map((booking) => (
            <div key={booking.id} className="bg-white rounded-lg shadow-md p-6 hover:shadow-lg transition-shadow">
              <div className="flex items-start justify-between">
                <div className="flex-1">
                  <div className="flex items-center justify-between mb-4">
                    <h3 className="text-lg font-semibold text-gray-900">
                      Room {booking.room?.roomNumber} - {booking.room?.roomType?.replace('_', ' ')}
                    </h3>
                    {getStatusBadge(booking.status)}
                  </div>
                  
                  <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-4">
                    <div className="flex items-center text-gray-600">
                      <Calendar className="w-4 h-4 mr-2" />
                      <div>
                        <div className="text-sm font-medium">Check-in</div>
                        <div className="text-sm">{new Date(booking.checkInDate).toLocaleDateString()}</div>
                      </div>
                    </div>
                    
                    <div className="flex items-center text-gray-600">
                      <Calendar className="w-4 h-4 mr-2" />
                      <div>
                        <div className="text-sm font-medium">Check-out</div>
                        <div className="text-sm">{new Date(booking.checkOutDate).toLocaleDateString()}</div>
                      </div>
                    </div>
                    
                    <div className="flex items-center text-gray-600">
                      <MapPin className="w-4 h-4 mr-2" />
                      <div>
                        <div className="text-sm font-medium">Location</div>
                        <div className="text-sm">{booking.room?.hotel?.location || 'N/A'}</div>
                      </div>
                    </div>
                    
                    <div className="flex items-center text-gray-600">
                      <DollarSign className="w-4 h-4 mr-2" />
                      <div>
                        <div className="text-sm font-medium">Total Price</div>
                        <div className="text-sm font-bold">${booking.totalPrice}</div>
                      </div>
                    </div>
                  </div>
                  
                  {booking.specialRequests && (
                    <div className="mb-4">
                      <div className="text-sm font-medium text-gray-700 mb-1">Special Requests:</div>
                      <div className="text-sm text-gray-600 bg-gray-50 p-2 rounded">
                        {booking.specialRequests}
                      </div>
                    </div>
                  )}
                  
                  <div className="flex items-center justify-between">
                    <div className="text-sm text-gray-500">
                      Booking ID: {booking.id} • Created: {new Date(booking.createdAt).toLocaleDateString()}
                    </div>
                    
                    <div className="flex space-x-2">
                      {booking.status === 'PENDING' && (
                        <button
                          onClick={() => handleCancelBooking(booking.id)}
                          className="px-3 py-1 text-sm text-red-600 border border-red-600 rounded hover:bg-red-50 transition-colors"
                        >
                          Cancel
                        </button>
                      )}
                      <button className="px-3 py-1 text-sm text-blue-600 border border-blue-600 rounded hover:bg-blue-50 transition-colors">
                        View Details
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default Bookings;