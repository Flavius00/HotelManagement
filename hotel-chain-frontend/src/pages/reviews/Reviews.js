import React, { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../../contexts/AuthContext';
import { useQuery } from 'react-query';
import { reviewsAPI } from '../../services/api';
import { useForm } from 'react-hook-form';
import { 
  Star, 
  MessageSquare, 
  Plus,
  Edit,
  Trash2,
  Calendar,
  MapPin
} from 'lucide-react';
import toast from 'react-hot-toast';

const Reviews = () => {
  const { t } = useTranslation();
  const { currentUser } = useAuth();
  const [showAddReview, setShowAddReview] = useState(false);
  const [editingReview, setEditingReview] = useState(null);

  const { register, handleSubmit, formState: { errors }, reset, setValue } = useForm();

  // Fetch user reviews
  const { data: reviews = [], isLoading, refetch } = useQuery(
    ['userReviews', currentUser?.id],
    async () => {
      if (!currentUser?.id) return [];
      const response = await reviewsAPI.getUserReviews(currentUser.id);
      return response.data.data || response.data;
    },
    {
      enabled: !!currentUser?.id,
      onError: () => {
        toast.error('Failed to load reviews');
      }
    }
  );

  const onSubmitReview = async (data) => {
    try {
      if (editingReview) {
        await reviewsAPI.updateReview(editingReview.id, data);
        toast.success('Review updated successfully');
        setEditingReview(null);
      } else {
        const reviewData = {
          ...data,
          userId: currentUser.id
        };
        await reviewsAPI.createReview(reviewData);
        toast.success('Review added successfully');
        setShowAddReview(false);
      }
      
      reset();
      refetch();
    } catch (error) {
      toast.error('Failed to save review');
      console.error('Review error:', error);
    }
  };

  const handleEditReview = (review) => {
    setEditingReview(review);
    setValue('roomId', review.roomId);
    setValue('rating', review.rating);
    setValue('comment', review.comment);
    setShowAddReview(true);
  };

  const handleDeleteReview = async (reviewId) => {
    if (!window.confirm('Are you sure you want to delete this review?')) {
      return;
    }

    try {
      await reviewsAPI.deleteReview(reviewId);
      toast.success('Review deleted successfully');
      refetch();
    } catch (error) {
      toast.error('Failed to delete review');
    }
  };

  const cancelEdit = () => {
    setEditingReview(null);
    setShowAddReview(false);
    reset();
  };

  const renderStars = (rating) => {
    return [...Array(5)].map((_, index) => (
      <Star
        key={index}
        className={`w-4 h-4 ${
          index < rating ? 'text-yellow-400 fill-current' : 'text-gray-300'
        }`}
      />
    ));
  };

  const renderRatingInput = () => {
    return (
      <div className="flex space-x-1">
        {[1, 2, 3, 4, 5].map((rating) => (
          <label key={rating} className="cursor-pointer">
            <input
              {...register('rating', { required: 'Rating is required' })}
              type="radio"
              value={rating}
              className="sr-only"
            />
            <Star className="w-6 h-6 text-yellow-400 hover:fill-current transition-colors" />
          </label>
        ))}
      </div>
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
          <h1 className="text-3xl font-bold text-gray-900">{t('reviews.title')}</h1>
          <p className="mt-1 text-gray-600">
            Share your experiences and read what others say
          </p>
        </div>
        
        <button
          onClick={() => setShowAddReview(true)}
          className="mt-4 sm:mt-0 bg-hotel-navy text-white px-4 py-2 rounded-lg hover:bg-blue-800 transition-colors inline-flex items-center"
        >
          <Plus className="w-4 h-4 mr-2" />
          {t('reviews.addReview')}
        </button>
      </div>

      {/* Add/Edit Review Form */}
      {showAddReview && (
        <div className="bg-white p-6 rounded-lg shadow-md">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">
            {editingReview ? 'Edit Review' : t('reviews.addReview')}
          </h3>
          
          <form onSubmit={handleSubmit(onSubmitReview)} className="space-y-4">
            {/* Room ID - Simple input for now */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Room ID *
              </label>
              <input
                {...register('roomId', { required: 'Room ID is required' })}
                type="number"
                className={`w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                  errors.roomId ? 'border-red-300' : 'border-gray-300'
                }`}
                placeholder="Enter room ID"
              />
              {errors.roomId && (
                <p className="mt-1 text-sm text-red-600">{errors.roomId.message}</p>
              )}
            </div>

            {/* Rating */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                {t('reviews.rating')} *
              </label>
              <div className="flex space-x-1">
                {[1, 2, 3, 4, 5].map((rating) => (
                  <label key={rating} className="cursor-pointer">
                    <input
                      {...register('rating', { required: 'Rating is required' })}
                      type="radio"
                      value={rating}
                      className="sr-only"
                    />
                    <Star className="w-6 h-6 text-gray-300 hover:text-yellow-400 transition-colors" />
                  </label>
                ))}
              </div>
              {errors.rating && (
                <p className="mt-1 text-sm text-red-600">{errors.rating.message}</p>
              )}
            </div>

            {/* Comment */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                {t('reviews.comment')}
              </label>
              <textarea
                {...register('comment')}
                rows={4}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="Share your experience..."
              />
            </div>

            {/* Buttons */}
            <div className="flex space-x-2">
              <button
                type="submit"
                className="bg-hotel-navy text-white px-4 py-2 rounded-lg hover:bg-blue-800 transition-colors"
              >
                {editingReview ? 'Update Review' : t('reviews.submit')}
              </button>
              <button
                type="button"
                onClick={cancelEdit}
                className="bg-gray-200 text-gray-800 px-4 py-2 rounded-lg hover:bg-gray-300 transition-colors"
              >
                {t('common.cancel')}
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Reviews List */}
      {reviews.length === 0 ? (
        <div className="text-center py-12">
          <div className="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <MessageSquare className="w-8 h-8 text-gray-400" />
          </div>
          <h3 className="text-lg font-medium text-gray-900 mb-2">No reviews yet</h3>
          <p className="text-gray-600 mb-4">
            Share your experience by writing your first review
          </p>
          <button
            onClick={() => setShowAddReview(true)}
            className="bg-hotel-navy text-white px-4 py-2 rounded-lg hover:bg-blue-800 transition-colors"
          >
            {t('reviews.addReview')}
          </button>
        </div>
      ) : (
        <div className="space-y-4">
          {reviews.map((review) => (
            <div key={review.id} className="bg-white rounded-lg shadow-md p-6">
              <div className="flex items-start justify-between mb-4">
                <div>
                  <div className="flex items-center mb-2">
                    <div className="flex items-center">
                      {renderStars(review.rating)}
                    </div>
                    <span className="ml-2 text-sm text-gray-600">
                      {review.rating}/5 stars
                    </span>
                  </div>
                  <h3 className="text-lg font-semibold text-gray-900">
                    Room {review.room?.roomNumber || review.roomId}
                  </h3>
                  {review.room?.hotel?.location && (
                    <p className="text-sm text-gray-600 flex items-center">
                      <MapPin className="w-4 h-4 mr-1" />
                      {review.room.hotel.location}
                    </p>
                  )}
                </div>
                
                <div className="flex space-x-2">
                  <button
                    onClick={() => handleEditReview(review)}
                    className="p-2 text-gray-400 hover:text-blue-600 transition-colors"
                  >
                    <Edit className="w-4 h-4" />
                  </button>
                  <button
                    onClick={() => handleDeleteReview(review.id)}
                    className="p-2 text-gray-400 hover:text-red-600 transition-colors"
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                </div>
              </div>
              
              {review.comment && (
                <div className="mb-4">
                  <p className="text-gray-700">{review.comment}</p>
                </div>
              )}
              
              <div className="flex items-center text-sm text-gray-500">
                <Calendar className="w-4 h-4 mr-1" />
                <span>
                  Reviewed on {new Date(review.createdAt).toLocaleDateString()}
                </span>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default Reviews;