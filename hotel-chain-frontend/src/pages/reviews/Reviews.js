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
  const [selectedRating, setSelectedRating] = useState(0);

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
      const reviewData = {
        ...data,
        rating: selectedRating,
        userId: currentUser.id
      };

      if (editingReview) {
        await reviewsAPI.updateReview(editingReview.id, reviewData);
        toast.success('Review updated successfully');
        setEditingReview(null);
      } else {
        await reviewsAPI.createReview(reviewData);
        toast.success('Review added successfully');
        setShowAddReview(false);
      }
      
      reset();
      setSelectedRating(0);
      refetch();
    } catch (error) {
      toast.error('Failed to save review');
      console.error('Review error:', error);
    }
  };

  const handleEditReview = (review) => {
    setEditingReview(review);
    setValue('roomId', review.roomId);
    setValue('comment', review.comment);
    setSelectedRating(review.rating);
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
    setSelectedRating(0);
    reset();
  };

  const renderStars = (rating) => {
    return [...Array(5)].map((_, index) => (
      <Star
        key={index}
        className={`w-4 h-4 ${
          index < rating ? 'text-warning fill-current' : 'text-light'
        }`}
      />
    ));
  };

  const renderRatingInput = () => {
    return (
      <div className="flex space-x-1">
        {[1, 2, 3, 4, 5].map((rating) => (
          <button
            key={rating}
            type="button"
            onClick={() => setSelectedRating(rating)}
            className="cursor-pointer"
          >
            <Star 
              className={`w-6 h-6 transition-colors ${
                rating <= selectedRating 
                  ? 'text-warning fill-current' 
                  : 'text-light hover:text-warning'
              }`} 
            />
          </button>
        ))}
      </div>
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
              <h1 className="card-title">{t('reviews.title')}</h1>
              <p className="text-secondary">
                Share your experiences and read what others say
              </p>
            </div>
            
            <button
              onClick={() => setShowAddReview(true)}
              className="btn btn-primary mt-4 sm:mt-0"
            >
              <Plus className="w-4 h-4" />
              {t('reviews.addReview')}
            </button>
          </div>
        </div>
      </div>

      {/* Add/Edit Review Form */}
      {showAddReview && (
        <div className="card">
          <div className="card-header">
            <h3 className="card-title">
              {editingReview ? 'Edit Review' : t('reviews.addReview')}
            </h3>
          </div>
          
          <div className="card-body">
            <form onSubmit={handleSubmit(onSubmitReview)} className="space-y-6">
              {/* Room ID - Simple input for now */}
              <div className="form-group">
                <label className="form-label">
                  Room ID *
                </label>
                <input
                  {...register('roomId', { required: 'Room ID is required' })}
                  type="number"
                  className={`form-input ${errors.roomId ? 'error' : ''}`}
                  placeholder="Enter room ID"
                />
                {errors.roomId && (
                  <p className="form-error">{errors.roomId.message}</p>
                )}
              </div>

              {/* Rating */}
              <div className="form-group">
                <label className="form-label">
                  {t('reviews.rating')} *
                </label>
                {renderRatingInput()}
                {selectedRating === 0 && (
                  <p className="form-error">Rating is required</p>
                )}
              </div>

              {/* Comment */}
              <div className="form-group">
                <label className="form-label">
                  {t('reviews.comment')}
                </label>
                <textarea
                  {...register('comment')}
                  rows={4}
                  className="form-textarea"
                  placeholder="Share your experience..."
                />
              </div>

              {/* Buttons */}
              <div className="btn-group">
                <button
                  type="submit"
                  className="btn btn-primary"
                  disabled={selectedRating === 0}
                >
                  {editingReview ? 'Update Review' : t('reviews.submit')}
                </button>
                <button
                  type="button"
                  onClick={cancelEdit}
                  className="btn btn-outline"
                >
                  {t('common.cancel')}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Reviews List */}
      {reviews.length === 0 ? (
        <div className="card">
          <div className="card-body">
            <div className="text-center py-12">
              <div className="w-16 h-16 bg-secondary rounded-full flex items-center justify-center mx-auto mb-4">
                <MessageSquare className="w-8 h-8 text-light" />
              </div>
              <h3 className="text-lg font-medium text-primary mb-2">No reviews yet</h3>
              <p className="text-secondary mb-4">
                Share your experience by writing your first review
              </p>
              <button
                onClick={() => setShowAddReview(true)}
                className="btn btn-primary"
              >
                {t('reviews.addReview')}
              </button>
            </div>
          </div>
        </div>
      ) : (
        <div className="space-y-4">
          {reviews.map((review) => (
            <div key={review.id} className="card hover-lift">
              <div className="card-body">
                <div className="flex items-start justify-between mb-4">
                  <div>
                    <div className="flex items-center mb-2">
                      <div className="flex items-center">
                        {renderStars(review.rating)}
                      </div>
                      <span className="ml-2 text-sm text-secondary">
                        {review.rating}/5 stars
                      </span>
                    </div>
                    <h3 className="text-lg font-semibold text-primary">
                      Room {review.room?.roomNumber || review.roomId}
                    </h3>
                    {review.room?.hotel?.location && (
                      <p className="text-sm text-secondary flex items-center">
                        <MapPin className="w-4 h-4 mr-1" />
                        {review.room.hotel.location}
                      </p>
                    )}
                  </div>
                  
                  <div className="btn-group">
                    <button
                      onClick={() => handleEditReview(review)}
                      className="btn btn-ghost btn-sm"
                      title="Edit review"
                    >
                      <Edit className="w-4 h-4" />
                    </button>
                    <button
                      onClick={() => handleDeleteReview(review.id)}
                      className="btn btn-ghost btn-sm"
                      title="Delete review"
                      style={{ color: 'var(--error)' }}
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </div>
                </div>
                
                {review.comment && (
                  <div className="mb-4">
                    <p className="text-secondary">{review.comment}</p>
                  </div>
                )}
                
                <div className="flex items-center text-sm text-light">
                  <Calendar className="w-4 h-4 mr-1" />
                  <span>
                    Reviewed on {new Date(review.createdAt).toLocaleDateString()}
                  </span>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default Reviews;