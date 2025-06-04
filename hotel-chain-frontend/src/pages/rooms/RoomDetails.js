import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, useSearchParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useQuery } from 'react-query';
import { useAuth } from '../../contexts/AuthContext';
import { roomsAPI, reviewsAPI, bookingsAPI } from '../../services/api';
import { useForm } from 'react-hook-form';
import { 
  ArrowLeft, 
  Star, 
  Users, 
  MapPin, 
  Calendar,
  Clock,
  Wifi,
  Car,
  Coffee,
  Dumbbell,
  ChevronLeft,
  ChevronRight,
  MessageCircle
} from 'lucide-react';
import toast from 'react-hot-toast';

const RoomDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const { t } = useTranslation();
  const { isAuthenticated, currentUser } = useAuth();
  const [currentImageIndex, setCurrentImageIndex] = useState(0);
  const [showBookingForm, setShowBookingForm] = useState(searchParams.get('book') === 'true');

  const { register, handleSubmit, formState: { errors }, watch } = useForm({
    defaultValues: {
      checkInDate: new Date().toISOString().split('T')[0],
      checkOutDate: new Date(Date.now() + 24 * 60 * 60 * 1000).toISOString().split('T')[0],
    }
  });

  // Fetch room details
  const { data: room, isLoading: roomLoading } = useQuery(
    ['room', id],
    async () => {
      const response = await roomsAPI.getRoomById(id);
      return response.data.data || response.data;
    },
    {
      onError: () => {
        toast.error('Failed to load room details');
        navigate('/rooms');
      }
    }
  );

  // Fetch room reviews
  const { data: reviews = [], isLoading: reviewsLoading } = useQuery(
    ['reviews', id],
    async () => {
      const response = await reviewsAPI.getRoomReviews(id);
      return response.data.data || response.data;
    }
  );

  // Mock images for demo
  const roomImages = [
    `https://picsum.photos/800/600?random=${id}1`,
    `https://picsum.photos/800/600?random=${id}2`,
    `https://picsum.photos/800/600?random=${id}3`,
  ];

  const nextImage = () => {
    setCurrentImageIndex((prev) => (prev + 1) % roomImages.length);
  };

  const prevImage = () => {
    setCurrentImageIndex((prev) => (prev - 1 + roomImages.length) % roomImages.length);
  };

  const onBookingSubmit = async (data) => {
    if (!isAuthenticated()) {
      toast.error('Please login to make a booking');
      navigate('/login');
      return;
    }

    try {
      const bookingData = {
        userId: currentUser.id,
        roomId: parseInt(id),
        checkInDate: data.checkInDate,
        checkOutDate: data.checkOutDate,
        specialRequests: data.specialRequests,
      };

      await bookingsAPI.createBooking(bookingData);
      toast.success('Booking created successfully!');
      navigate('/bookings');
    } catch (error) {
      toast.error('Failed to create booking');
      console.error('Booking error:', error);
    }
  };

  if (roomLoading) {
    return (
      <div className="flex items-center justify-center" style={{ minHeight: '16rem' }}>
        <div className="text-center">
          <div className="spinner spinner-lg"></div>
          <p className="mt-4 text-secondary">{t('common.loading')}</p>
        </div>
      </div>
    );
  }

  if (!room) {
    return (
      <div className="card">
        <div className="card-body text-center py-12">
          <h2 className="text-2xl font-bold text-primary mb-4">Room not found</h2>
          <button
            onClick={() => navigate('/rooms')}
            className="btn btn-primary"
          >
            Back to Rooms
          </button>
        </div>
      </div>
    );
  }

  const averageRating = reviews.length > 0 
    ? reviews.reduce((sum, review) => sum + review.rating, 0) / reviews.length 
    : 0;

  return (
    <div className="space-y-8">
      {/* Back Button */}
      <button
        onClick={() => navigate('/rooms')}
        className="btn btn-ghost"
      >
        <ArrowLeft className="w-5 h-5" />
        Back to Rooms
      </button>

      {/* Room Images */}
      <div className="relative">
        <div className="aspect-w-16 aspect-h-9 rounded-lg overflow-hidden">
          <img
            src={roomImages[currentImageIndex]}
            alt={`Room ${room.roomNumber}`}
            className="w-full h-96 object-cover"
          />
        </div>
        
        {roomImages.length > 1 && (
          <>
            <button
              onClick={prevImage}
              className="absolute left-4 top-1/2 transform -translate-y-1/2 bg-primary-midnight bg-opacity-50 text-white p-2 rounded-full hover:bg-opacity-75 transition-opacity"
            >
              <ChevronLeft className="w-6 h-6" />
            </button>
            <button
              onClick={nextImage}
              className="absolute right-4 top-1/2 transform -translate-y-1/2 bg-primary-midnight bg-opacity-50 text-white p-2 rounded-full hover:bg-opacity-75 transition-opacity"
            >
              <ChevronRight className="w-6 h-6" />
            </button>
            
            {/* Image indicators */}
            <div className="absolute bottom-4 left-1/2 transform -translate-x-1/2 flex space-x-2">
              {roomImages.map((_, index) => (
                <button
                  key={index}
                  onClick={() => setCurrentImageIndex(index)}
                  className={`w-3 h-3 rounded-full ${
                    index === currentImageIndex ? 'bg-white' : 'bg-white bg-opacity-50'
                  }`}
                />
              ))}
            </div>
          </>
        )}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Room Details */}
        <div className="lg:col-span-2 space-y-6">
          {/* Basic Info */}
          <div className="card">
            <div className="card-body">
              <div className="flex items-start justify-between mb-4">
                <div>
                  <h1 className="text-3xl font-bold text-primary">
                    Room {room.roomNumber}
                  </h1>
                  <p className="text-lg text-secondary mb-2">
                    {room.roomType?.replace('_', ' ')}
                  </p>
                  <p className="text-secondary flex items-center">
                    <MapPin className="w-4 h-4 mr-1" />
                    {room.hotel?.location || 'Location not specified'}
                  </p>
                </div>
                <div className="text-right">
                  <div className="text-3xl font-bold text-primary">
                    ${room.price}
                    <span className="text-lg font-normal text-secondary">/night</span>
                  </div>
                  <div className={`badge ${room.isAvailable ? 'badge-success' : 'badge-error'}`}>
                    {room.isAvailable ? 'Available' : 'Unavailable'}
                  </div>
                </div>
              </div>

              {/* Room Stats */}
              <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-4">
                <div className="text-center p-3 bg-secondary rounded-lg">
                  <Users className="w-6 h-6 mx-auto mb-2 text-primary" />
                  <div className="text-sm font-medium text-primary">Max Guests</div>
                  <div className="text-lg font-bold text-secondary">{room.maxOccupancy}</div>
                </div>
                <div className="text-center p-3 bg-secondary rounded-lg">
                  <div className="text-sm font-medium text-primary">Position</div>
                  <div className="text-lg font-bold text-secondary">
                    {room.position?.replace('_', ' ')}
                  </div>
                </div>
                <div className="text-center p-3 bg-secondary rounded-lg">
                  <Star className="w-6 h-6 mx-auto mb-2 text-warning" />
                  <div className="text-sm font-medium text-primary">Rating</div>
                  <div className="text-lg font-bold text-secondary">
                    {averageRating > 0 ? averageRating.toFixed(1) : 'N/A'}
                  </div>
                </div>
                <div className="text-center p-3 bg-secondary rounded-lg">
                  <MessageCircle className="w-6 h-6 mx-auto mb-2 text-primary" />
                  <div className="text-sm font-medium text-primary">Reviews</div>
                  <div className="text-lg font-bold text-secondary">{reviews.length}</div>
                </div>
              </div>

              {/* Description */}
              {room.description && (
                <div>
                  <h3 className="text-lg font-semibold text-primary mb-2">Description</h3>
                  <p className="text-secondary">{room.description}</p>
                </div>
              )}
            </div>
          </div>

          {/* Facilities */}
          {room.facilities && room.facilities.length > 0 && (
            <div className="card">
              <div className="card-header">
                <h3 className="card-title">{t('rooms.facilities')}</h3>
              </div>
              <div className="card-body">
                <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
                  {room.facilities.map((facility, index) => (
                    <div key={index} className="flex items-center space-x-2">
                      <div className="w-8 h-8 bg-ocean rounded-lg flex items-center justify-center">
                        <Wifi className="w-4 h-4 text-white" />
                      </div>
                      <div>
                        <div className="font-medium text-primary">{facility.facilityName}</div>
                        {facility.facilityDescription && (
                          <div className="text-sm text-secondary">{facility.facilityDescription}</div>
                        )}
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          )}

          {/* Reviews */}
          <div className="card">
            <div className="card-header">
              <div className="flex items-center justify-between">
                <h3 className="card-title">{t('reviews.title')}</h3>
                {averageRating > 0 && (
                  <div className="flex items-center space-x-2">
                    <div className="flex items-center">
                      {[...Array(5)].map((_, i) => (
                        <Star
                          key={i}
                          className={`w-5 h-5 ${
                            i < Math.floor(averageRating)
                              ? 'text-warning fill-current'
                              : 'text-light'
                          }`}
                        />
                      ))}
                    </div>
                    <span className="text-sm text-secondary">
                      {averageRating.toFixed(1)} ({reviews.length} reviews)
                    </span>
                  </div>
                )}
              </div>
            </div>

            <div className="card-body">
              {reviewsLoading ? (
                <div className="text-center py-4">
                  <div className="spinner"></div>
                </div>
              ) : reviews.length > 0 ? (
                <div className="space-y-4 max-h-96 overflow-y-auto custom-scrollbar">
                  {reviews.map((review) => (
                    <div key={review.id} className="border-b border-secondary pb-4 last:border-b-0">
                      <div className="flex items-center justify-between mb-2">
                        <div className="flex items-center">
                          {[...Array(5)].map((_, i) => (
                            <Star
                              key={i}
                              className={`w-4 h-4 ${
                                i < review.rating
                                  ? 'text-warning fill-current'
                                  : 'text-light'
                              }`}
                            />
                          ))}
                        </div>
                        <span className="text-sm text-light">
                          {new Date(review.createdAt).toLocaleDateString()}
                        </span>
                      </div>
                      {review.comment && (
                        <p className="text-secondary">{review.comment}</p>
                      )}
                    </div>
                  ))}
                </div>
              ) : (
                <p className="text-secondary text-center py-4">{t('reviews.noReviews')}</p>
              )}
            </div>
          </div>
        </div>

        {/* Booking Form */}
        <div className="lg:col-span-1">
          {room.isAvailable && (
            <div className="card sticky" style={{ top: '1.5rem' }}>
              <div className="card-header">
                <h3 className="card-title">Book This Room</h3>
              </div>
              
              <div className="card-body">
                {!isAuthenticated() ? (
                  <div className="text-center py-4">
                    <p className="text-secondary mb-4">Please login to make a booking</p>
                    <button
                      onClick={() => navigate('/login')}
                      className="btn btn-primary w-full"
                    >
                      Login to Book
                    </button>
                  </div>
                ) : (
                  <form onSubmit={handleSubmit(onBookingSubmit)} className="space-y-4">
                    {/* Check-in Date */}
                    <div className="form-group">
                      <label className="form-label">
                        Check-in Date
                      </label>
                      <div className="input-icon">
                        <Calendar className="icon" />
                        <input
                          {...register('checkInDate', {
                            required: 'Check-in date is required',
                            validate: value => {
                              const date = new Date(value);
                              const today = new Date();
                              today.setHours(0, 0, 0, 0);
                              return date >= today || 'Check-in date cannot be in the past';
                            }
                          })}
                          type="date"
                          className={`form-input ${errors.checkInDate ? 'error' : ''}`}
                        />
                      </div>
                      {errors.checkInDate && (
                        <p className="form-error">{errors.checkInDate.message}</p>
                      )}
                    </div>

                    {/* Check-out Date */}
                    <div className="form-group">
                      <label className="form-label">
                        Check-out Date
                      </label>
                      <div className="input-icon">
                        <Calendar className="icon" />
                        <input
                          {...register('checkOutDate', {
                            required: 'Check-out date is required',
                            validate: value => {
                              const checkInDate = watch('checkInDate');
                              const checkIn = new Date(checkInDate);
                              const checkOut = new Date(value);
                              return checkOut > checkIn || 'Check-out date must be after check-in date';
                            }
                          })}
                          type="date"
                          className={`form-input ${errors.checkOutDate ? 'error' : ''}`}
                        />
                      </div>
                      {errors.checkOutDate && (
                        <p className="form-error">{errors.checkOutDate.message}</p>
                      )}
                    </div>

                    {/* Special Requests */}
                    <div className="form-group">
                      <label className="form-label">
                        Special Requests (Optional)
                      </label>
                      <textarea
                        {...register('specialRequests')}
                        rows={3}
                        className="form-textarea"
                        placeholder="Any special requirements or requests..."
                      />
                    </div>

                    {/* Price Calculation */}
                    {watch('checkInDate') && watch('checkOutDate') && (
                      <div className="card" style={{ backgroundColor: 'var(--gray-50)', border: '1px solid var(--gray-200)' }}>
                        <div className="card-body">
                          <div className="flex justify-between items-center mb-2">
                            <span className="text-secondary">Price per night:</span>
                            <span className="font-medium">${room.price}</span>
                          </div>
                          <div className="flex justify-between items-center mb-2">
                            <span className="text-secondary">
                              Nights: {(() => {
                                const checkIn = new Date(watch('checkInDate'));
                                const checkOut = new Date(watch('checkOutDate'));
                                const nights = Math.max(0, Math.ceil((checkOut - checkIn) / (1000 * 60 * 60 * 24)));
                                return nights;
                              })()}
                            </span>
                            <span className="font-medium">
                              ${(() => {
                                const checkIn = new Date(watch('checkInDate'));
                                const checkOut = new Date(watch('checkOutDate'));
                                const nights = Math.max(0, Math.ceil((checkOut - checkIn) / (1000 * 60 * 60 * 24)));
                                return (nights * room.price).toFixed(2);
                              })()}
                            </span>
                          </div>
                          <div className="border-t pt-2 flex justify-between items-center font-bold text-lg" style={{ borderColor: 'var(--gray-200)' }}>
                            <span>Total:</span>
                            <span>
                              ${(() => {
                                const checkIn = new Date(watch('checkInDate'));
                                const checkOut = new Date(watch('checkOutDate'));
                                const nights = Math.max(0, Math.ceil((checkOut - checkIn) / (1000 * 60 * 60 * 24)));
                                return (nights * room.price).toFixed(2);
                              })()}
                            </span>
                          </div>
                        </div>
                      </div>
                    )}

                    {/* Submit Button */}
                    <button
                      type="submit"
                      className="btn btn-secondary w-full"
                    >
                      Book Now
                    </button>
                  </form>
                )}
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default RoomDetails;