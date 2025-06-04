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
      <div className="flex items-center justify-center min-h-64">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">{t('common.loading')}</p>
        </div>
      </div>
    );
  }

  if (!room) {
    return (
      <div className="text-center py-12">
        <h2 className="text-2xl font-bold text-gray-900 mb-4">Room not found</h2>
        <button
          onClick={() => navigate('/rooms')}
          className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700"
        >
          Back to Rooms
        </button>
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
        className="flex items-center text-gray-600 hover:text-gray-900 transition-colors"
      >
        <ArrowLeft className="w-5 h-5 mr-2" />
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
              className="absolute left-4 top-1/2 transform -translate-y-1/2 bg-black bg-opacity-50 text-white p-2 rounded-full hover:bg-opacity-75 transition-opacity"
            >
              <ChevronLeft className="w-6 h-6" />
            </button>
            <button
              onClick={nextImage}
              className="absolute right-4 top-1/2 transform -translate-y-1/2 bg-black bg-opacity-50 text-white p-2 rounded-full hover:bg-opacity-75 transition-opacity"
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
          <div className="bg-white p-6 rounded-lg shadow-md">
            <div className="flex items-start justify-between mb-4">
              <div>
                <h1 className="text-3xl font-bold text-gray-900">
                  Room {room.roomNumber}
                </h1>
                <p className="text-lg text-gray-600 mb-2">
                  {room.roomType?.replace('_', ' ')}
                </p>
                <p className="text-gray-600 flex items-center">
                  <MapPin className="w-4 h-4 mr-1" />
                  {room.hotel?.location || 'Location not specified'}
                </p>
              </div>
              <div className="text-right">
                <div className="text-3xl font-bold text-gray-900">
                  ${room.price}
                  <span className="text-lg font-normal text-gray-600">/night</span>
                </div>
                <div className={`inline-flex px-3 py-1 rounded-full text-sm font-medium ${
                  room.isAvailable 
                    ? 'bg-green-100 text-green-800' 
                    : 'bg-red-100 text-red-800'
                }`}>
                  {room.isAvailable ? 'Available' : 'Unavailable'}
                </div>
              </div>
            </div>

            {/* Room Stats */}
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-4">
              <div className="text-center p-3 bg-gray-50 rounded-lg">
                <Users className="w-6 h-6 mx-auto mb-2 text-gray-600" />
                <div className="text-sm font-medium text-gray-900">Max Guests</div>
                <div className="text-lg font-bold text-gray-700">{room.maxOccupancy}</div>
              </div>
              <div className="text-center p-3 bg-gray-50 rounded-lg">
                <div className="text-sm font-medium text-gray-900">Position</div>
                <div className="text-lg font-bold text-gray-700">
                  {room.position?.replace('_', ' ')}
                </div>
              </div>
              <div className="text-center p-3 bg-gray-50 rounded-lg">
                <Star className="w-6 h-6 mx-auto mb-2 text-yellow-500" />
                <div className="text-sm font-medium text-gray-900">Rating</div>
                <div className="text-lg font-bold text-gray-700">
                  {averageRating > 0 ? averageRating.toFixed(1) : 'N/A'}
                </div>
              </div>
              <div className="text-center p-3 bg-gray-50 rounded-lg">
                <MessageCircle className="w-6 h-6 mx-auto mb-2 text-gray-600" />
                <div className="text-sm font-medium text-gray-900">Reviews</div>
                <div className="text-lg font-bold text-gray-700">{reviews.length}</div>
              </div>
            </div>

            {/* Description */}
            {room.description && (
              <div>
                <h3 className="text-lg font-semibold text-gray-900 mb-2">Description</h3>
                <p className="text-gray-600">{room.description}</p>
              </div>
            )}
          </div>

          {/* Facilities */}
          {room.facilities && room.facilities.length > 0 && (
            <div className="bg-white p-6 rounded-lg shadow-md">
              <h3 className="text-lg font-semibold text-gray-900 mb-4">{t('rooms.facilities')}</h3>
              <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
                {room.facilities.map((facility, index) => (
                  <div key={index} className="flex items-center space-x-2">
                    <div className="w-8 h-8 bg-blue-100 rounded-lg flex items-center justify-center">
                      <Wifi className="w-4 h-4 text-blue-600" />
                    </div>
                    <div>
                      <div className="font-medium text-gray-900">{facility.facilityName}</div>
                      {facility.facilityDescription && (
                        <div className="text-sm text-gray-600">{facility.facilityDescription}</div>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Reviews */}
          <div className="bg-white p-6 rounded-lg shadow-md">
            <div className="flex items-center justify-between mb-6">
              <h3 className="text-lg font-semibold text-gray-900">{t('reviews.title')}</h3>
              {averageRating > 0 && (
                <div className="flex items-center space-x-2">
                  <div className="flex items-center">
                    {[...Array(5)].map((_, i) => (
                      <Star
                        key={i}
                        className={`w-5 h-5 ${
                          i < Math.floor(averageRating)
                            ? 'text-yellow-400 fill-current'
                            : 'text-gray-300'
                        }`}
                      />
                    ))}
                  </div>
                  <span className="text-sm text-gray-600">
                    {averageRating.toFixed(1)} ({reviews.length} reviews)
                  </span>
                </div>
              )}
            </div>

            {reviewsLoading ? (
              <div className="text-center py-4">
                <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mx-auto"></div>
              </div>
            ) : reviews.length > 0 ? (
              <div className="space-y-4 max-h-96 overflow-y-auto">
                {reviews.map((review) => (
                  <div key={review.id} className="border-b border-gray-200 pb-4 last:border-b-0">
                    <div className="flex items-center justify-between mb-2">
                      <div className="flex items-center">
                        {[...Array(5)].map((_, i) => (
                          <Star
                            key={i}
                            className={`w-4 h-4 ${
                              i < review.rating
                                ? 'text-yellow-400 fill-current'
                                : 'text-gray-300'
                            }`}
                          />
                        ))}
                      </div>
                      <span className="text-sm text-gray-500">
                        {new Date(review.createdAt).toLocaleDateString()}
                      </span>
                    </div>
                    {review.comment && (
                      <p className="text-gray-600">{review.comment}</p>
                    )}
                  </div>
                ))}
              </div>
            ) : (
              <p className="text-gray-600 text-center py-4">{t('reviews.noReviews')}</p>
            )}
          </div>
        </div>

        {/* Booking Form */}
        <div className="lg:col-span-1">
          {room.isAvailable && (
            <div className="bg-white p-6 rounded-lg shadow-md sticky top-6">
              <h3 className="text-lg font-semibold text-gray-900 mb-4">Book This Room</h3>
              
              {!isAuthenticated() ? (
                <div className="text-center py-4">
                  <p className="text-gray-600 mb-4">Please login to make a booking</p>
                  <button
                    onClick={() => navigate('/login')}
                    className="w-full bg-hotel-navy text-white py-3 px-4 rounded-lg hover:bg-blue-800 transition-colors font-medium"
                  >
                    Login to Book
                  </button>
                </div>
              ) : (
                <form onSubmit={handleSubmit(onBookingSubmit)} className="space-y-4">
                  {/* Check-in Date */}
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Check-in Date
                    </label>
                    <div className="relative">
                      <Calendar className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
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
                        className={`w-full pl-10 pr-3 py-3 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                          errors.checkInDate ? 'border-red-300' : 'border-gray-300'
                        }`}
                      />
                    </div>
                    {errors.checkInDate && (
                      <p className="mt-1 text-sm text-red-600">{errors.checkInDate.message}</p>
                    )}
                  </div>

                  {/* Check-out Date */}
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Check-out Date
                    </label>
                    <div className="relative">
                      <Calendar className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
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
                        className={`w-full pl-10 pr-3 py-3 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                          errors.checkOutDate ? 'border-red-300' : 'border-gray-300'
                        }`}
                      />
                    </div>
                    {errors.checkOutDate && (
                      <p className="mt-1 text-sm text-red-600">{errors.checkOutDate.message}</p>
                    )}
                  </div>

                  {/* Special Requests */}
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Special Requests (Optional)
                    </label>
                    <textarea
                      {...register('specialRequests')}
                      rows={3}
                      className="w-full px-3 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                      placeholder="Any special requirements or requests..."
                    />
                  </div>

                  {/* Price Calculation */}
                  {watch('checkInDate') && watch('checkOutDate') && (
                    <div className="bg-gray-50 p-4 rounded-lg">
                      <div className="flex justify-between items-center mb-2">
                        <span className="text-gray-600">Price per night:</span>
                        <span className="font-medium">${room.price}</span>
                      </div>
                      <div className="flex justify-between items-center mb-2">
                        <span className="text-gray-600">
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
                      <div className="border-t pt-2 flex justify-between items-center font-bold text-lg">
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
                  )}

                  {/* Submit Button */}
                  <button
                    type="submit"
                    className="w-full bg-hotel-gold text-hotel-navy py-3 px-4 rounded-lg hover:bg-yellow-500 transition-colors font-medium"
                  >
                    Book Now
                  </button>
                </form>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default RoomDetails;