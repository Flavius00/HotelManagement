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
      PENDING: { color: 'badge-warning', icon: AlertCircle },
      CONFIRMED: { color: 'badge-success', icon: CheckCircle },
      CANCELLED: { color: 'badge-error', icon: XCircle },
      COMPLETED: { color: 'badge-info', icon: CheckCircle }
    };

    const config = statusConfig[status] || statusConfig.PENDING;
    const Icon = config.icon;

    return (
      <span className={`badge ${config.color}`}>
        <Icon className="w-3 h-3" />
        {status}
      </span>
    );
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center" style={{ minHeight: '16rem' }}>
        <div className="text-center">
          <div className="spinner spinner-lg"></div>
          <p className="mt-4 text-secondary">{t('common.loading')}</p>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="card">
        <div className="card-header">
          <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between">
            <div>
              <h1 className="card-title">{t('bookings.title')}</h1>
              <p className="text-secondary">
                Manage your hotel reservations
              </p>
            </div>
            
            <div className="mt-4 sm:mt-0 flex items-center space-x-4">
              <select
                value={statusFilter}
                onChange={(e) => setStatusFilter(e.target.value)}
                className="form-select"
                style={{ minWidth: '150px' }}
              >
                <option value="ALL">All Bookings</option>
                <option value="PENDING">Pending</option>
                <option value="CONFIRMED">Confirmed</option>
                <option value="CANCELLED">Cancelled</option>
                <option value="COMPLETED">Completed</option>
              </select>
            </div>
          </div>
        </div>
      </div>

      {/* Bookings List */}
      {filteredBookings.length === 0 ? (
        <div className="card">
          <div className="card-body">
            <div className="text-center py-12">
              <div className="w-16 h-16 bg-secondary rounded-full flex items-center justify-center mx-auto mb-4">
                <Calendar className="w-8 h-8 text-light" />
              </div>
              <h3 className="text-lg font-medium text-primary mb-2">No bookings found</h3>
              <p className="text-secondary mb-4">
                {statusFilter === 'ALL' 
                  ? "You haven't made any bookings yet" 
                  : `No bookings with status: ${statusFilter}`
                }
              </p>
            </div>
          </div>
        </div>
      ) : (
        <div className="space-y-4">
          {filteredBookings.map((booking) => (
            <div key={booking.id} className="card hover-lift">
              <div className="card-body">
                <div className="flex items-start justify-between">
                  <div className="flex-1">
                    <div className="flex items-center justify-between mb-4">
                      <h3 className="text-lg font-semibold text-primary">
                        Room {booking.room?.roomNumber} - {booking.room?.roomType?.replace('_', ' ')}
                      </h3>
                      {getStatusBadge(booking.status)}
                    </div>
                    
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-4">
                      <div className="flex items-center text-secondary">
                        <Calendar className="w-4 h-4 mr-2" />
                        <div>
                          <div className="text-sm font-medium">Check-in</div>
                          <div className="text-sm">{new Date(booking.checkInDate).toLocaleDateString()}</div>
                        </div>
                      </div>
                      
                      <div className="flex items-center text-secondary">
                        <Calendar className="w-4 h-4 mr-2" />
                        <div>
                          <div className="text-sm font-medium">Check-out</div>
                          <div className="text-sm">{new Date(booking.checkOutDate).toLocaleDateString()}</div>
                        </div>
                      </div>
                      
                      <div className="flex items-center text-secondary">
                        <MapPin className="w-4 h-4 mr-2" />
                        <div>
                          <div className="text-sm font-medium">Location</div>
                          <div className="text-sm">{booking.room?.hotel?.location || 'N/A'}</div>
                        </div>
                      </div>
                      
                      <div className="flex items-center text-secondary">
                        <DollarSign className="w-4 h-4 mr-2" />
                        <div>
                          <div className="text-sm font-medium">Total Price</div>
                          <div className="text-sm font-bold">${booking.totalPrice}</div>
                        </div>
                      </div>
                    </div>
                    
                    {booking.specialRequests && (
                      <div className="mb-4">
                        <div className="text-sm font-medium text-primary mb-1">Special Requests:</div>
                        <div className="text-sm text-secondary bg-secondary p-2 rounded">
                          {booking.specialRequests}
                        </div>
                      </div>
                    )}
                    
                    <div className="flex items-center justify-between">
                      <div className="text-sm text-light">
                        Booking ID: {booking.id} • Created: {new Date(booking.createdAt).toLocaleDateString()}
                      </div>
                      
                      <div className="btn-group">
                        {booking.status === 'PENDING' && (
                          <button
                            onClick={() => handleCancelBooking(booking.id)}
                            className="btn btn-outline btn-sm"
                            style={{ color: 'var(--error)', borderColor: 'var(--error)' }}
                          >
                            Cancel
                          </button>
                        )}
                        <button className="btn btn-outline btn-sm">
                          View Details
                        </button>
                      </div>
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