import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useQuery } from 'react-query';
import { roomsAPI } from '../../services/api';
import { 
  Search, 
  Filter, 
  MapPin, 
  Star, 
  Users, 
  Wifi, 
  Car, 
  Coffee,
  Dumbbell,
  Grid,
  List,
  SlidersHorizontal,
  ArrowRight
} from 'lucide-react';
import toast from 'react-hot-toast';

const Rooms = () => {
  const { t } = useTranslation();
  const [viewMode, setViewMode] = useState('grid');
  const [showFilters, setShowFilters] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const [filters, setFilters] = useState({
    location: '',
    minPrice: '',
    maxPrice: '',
    roomType: '',
    position: '',
    isAvailable: null,
    facilities: []
  });

  // Fetch rooms data
  const { data: rooms = [], isLoading, error, refetch } = useQuery(
    ['rooms', filters],
    async () => {
      if (Object.values(filters).some(value => value !== '' && value !== null && (!Array.isArray(value) || value.length > 0))) {
        // If filters are applied, use filter API
        const response = await roomsAPI.filterRooms(filters);
        return response.data.data || response.data;
      } else {
        // Otherwise get sorted rooms
        const response = await roomsAPI.getRoomsSorted();
        return response.data.data || response.data;
      }
    },
    {
      onError: (error) => {
        toast.error('Failed to load rooms');
        console.error('Error fetching rooms:', error);
      }
    }
  );

  // Filter rooms by search term locally
  const filteredRooms = rooms.filter(room => 
    room.hotel?.name?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    room.hotel?.location?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    room.roomNumber?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const handleFilterChange = (key, value) => {
    setFilters(prev => ({
      ...prev,
      [key]: value
    }));
  };

  const handleFacilityToggle = (facility) => {
    setFilters(prev => ({
      ...prev,
      facilities: prev.facilities.includes(facility)
        ? prev.facilities.filter(f => f !== facility)
        : [...prev.facilities, facility]
    }));
  };

  const clearFilters = () => {
    setFilters({
      location: '',
      minPrice: '',
      maxPrice: '',
      roomType: '',
      position: '',
      isAvailable: null,
      facilities: []
    });
    setSearchTerm('');
  };

  const roomTypes = ['SINGLE', 'DOUBLE', 'SUITE', 'DELUXE', 'FAMILY'];
  const positions = ['GROUND_FLOOR', 'FIRST_FLOOR', 'SECOND_FLOOR', 'THIRD_FLOOR', 'TOP_FLOOR'];
  const commonFacilities = ['WiFi', 'Air Conditioning', 'Mini Bar', 'Room Service', 'Balcony', 'Sea View'];

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

  if (error) {
    return (
      <div className="text-center py-12">
        <p className="text-error mb-4">{t('common.error')}</p>
        <button 
          onClick={() => refetch()} 
          className="btn btn-primary"
        >
          Try Again
        </button>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="rooms-header">
        <div className="rooms-title-section">
          <h1>{t('rooms.title')}</h1>
          <p className="rooms-count">
            {filteredRooms.length} {filteredRooms.length === 1 ? 'room' : 'rooms'} available
          </p>
        </div>
        
        <div className="rooms-controls">
          {/* View Mode Toggle */}
          <div className="view-toggle">
            <button
              onClick={() => setViewMode('grid')}
              className={`view-toggle-btn ${viewMode === 'grid' ? 'active' : ''}`}
            >
              <Grid className="w-4 h-4" />
            </button>
            <button
              onClick={() => setViewMode('list')}
              className={`view-toggle-btn ${viewMode === 'list' ? 'active' : ''}`}
            >
              <List className="w-4 h-4" />
            </button>
          </div>

          {/* Filter Toggle */}
          <button
            onClick={() => setShowFilters(!showFilters)}
            className="filter-toggle-btn"
          >
            <SlidersHorizontal className="w-4 h-4" />
            <span>Filters</span>
          </button>
        </div>
      </div>

      {/* Search Bar */}
      <div className="search-container">
        <div className="search-box">
          <Search className="search-icon" />
          <input
            type="text"
            placeholder="Search rooms by location, hotel, or room number..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="search-input"
          />
        </div>
      </div>

      {/* Filters Panel */}
      {showFilters && (
        <div className="filter-panel">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-lg font-semibold text-primary">{t('rooms.filterBy')}</h3>
            <button
              onClick={clearFilters}
              className="text-sm text-ocean hover:text-coral"
            >
              Clear All
            </button>
          </div>
          
          <div className="form-row" style={{ gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))' }}>
            {/* Location Filter */}
            <div className="form-group">
              <label className="form-label">
                {t('rooms.location')}
              </label>
              <input
                type="text"
                value={filters.location}
                onChange={(e) => handleFilterChange('location', e.target.value)}
                placeholder="Enter location"
                className="form-input"
              />
            </div>

            {/* Room Type Filter */}
            <div className="form-group">
              <label className="form-label">
                Room Type
              </label>
              <select
                value={filters.roomType}
                onChange={(e) => handleFilterChange('roomType', e.target.value)}
                className="form-select"
              >
                <option value="">All Types</option>
                {roomTypes.map(type => (
                  <option key={type} value={type}>
                    {type.replace('_', ' ')}
                  </option>
                ))}
              </select>
            </div>

            {/* Position Filter */}
            <div className="form-group">
              <label className="form-label">
                {t('rooms.position')}
              </label>
              <select
                value={filters.position}
                onChange={(e) => handleFilterChange('position', e.target.value)}
                className="form-select"
              >
                <option value="">All Positions</option>
                {positions.map(position => (
                  <option key={position} value={position}>
                    {position.replace('_', ' ')}
                  </option>
                ))}
              </select>
            </div>

            {/* Availability Filter */}
            <div className="form-group">
              <label className="form-label">
                {t('rooms.availability')}
              </label>
              <select
                value={filters.isAvailable === null ? '' : filters.isAvailable.toString()}
                onChange={(e) => handleFilterChange('isAvailable', e.target.value === '' ? null : e.target.value === 'true')}
                className="form-select"
              >
                <option value="">All Rooms</option>
                <option value="true">Available Only</option>
                <option value="false">Unavailable</option>
              </select>
            </div>

            {/* Price Range */}
            <div className="form-group">
              <label className="form-label">
                Min Price ($)
              </label>
              <input
                type="number"
                value={filters.minPrice}
                onChange={(e) => handleFilterChange('minPrice', e.target.value)}
                placeholder="0"
                className="form-input"
              />
            </div>

            <div className="form-group">
              <label className="form-label">
                Max Price ($)
              </label>
              <input
                type="number"
                value={filters.maxPrice}
                onChange={(e) => handleFilterChange('maxPrice', e.target.value)}
                placeholder="1000"
                className="form-input"
              />
            </div>
          </div>

          {/* Facilities Filter */}
          <div className="form-group">
            <label className="form-label">
              {t('rooms.facilities')}
            </label>
            <div className="filter-tags">
              {commonFacilities.map(facility => (
                <button
                  key={facility}
                  onClick={() => handleFacilityToggle(facility)}
                  className={`filter-tag ${filters.facilities.includes(facility) ? 'active' : ''}`}
                >
                  {facility}
                </button>
              ))}
            </div>
          </div>
        </div>
      )}

      {/* Rooms List */}
      {filteredRooms.length === 0 ? (
        <div className="no-results">
          <div className="no-results-icon">
            <Search className="w-8 h-8" />
          </div>
          <h3>No rooms found</h3>
          <p>
            Try adjusting your search criteria or filters
          </p>
          <button
            onClick={clearFilters}
            className="btn btn-primary"
          >
            Clear Filters
          </button>
        </div>
      ) : (
        <div className={viewMode === 'grid' ? 'rooms-grid' : 'rooms-list'}>
          {filteredRooms.map((room) => (
            <RoomCard key={room.id} room={room} viewMode={viewMode} />
          ))}
        </div>
      )}
    </div>
  );
};

// Room Card Component
const RoomCard = ({ room, viewMode }) => {
  const { t } = useTranslation();
  
  // Mock image since we don't have real images
  const roomImage = `https://picsum.photos/400/250?random=${room.id}`;
  
  if (viewMode === 'list') {
    return (
      <div className="room-card-list">
        <div className="room-image-container">
          <img
            src={roomImage}
            alt={`Room ${room.roomNumber}`}
            className="room-image"
          />
        </div>
        <div className="room-content">
          <div>
            <div className="room-header">
              <div>
                <h3 className="room-title">
                  Room {room.roomNumber} - {room.roomType?.replace('_', ' ')}
                </h3>
                <p className="room-location">
                  <MapPin className="w-4 h-4" />
                  {room.hotel?.location || 'Location not specified'}
                </p>
              </div>
              <div className="room-price">
                <div className="room-price-amount">
                  ${room.price}
                  <span className="room-price-period">/night</span>
                </div>
                <div className={`room-status-badge ${room.isAvailable ? 'room-status-available' : 'room-status-unavailable'}`}>
                  {room.isAvailable ? 'Available' : 'Unavailable'}
                </div>
              </div>
            </div>
            
            <div className="room-details">
              <span className="room-detail">
                <Users className="w-4 h-4" />
                Max {room.maxOccupancy} guests
              </span>
              <span>
                {room.position?.replace('_', ' ')}
              </span>
            </div>
            
            {room.description && (
              <p className="room-description line-clamp-2">
                {room.description}
              </p>
            )}
          </div>
          
          <div className="room-actions">
            <div className="facilities-list">
              {room.facilities?.slice(0, 3).map((facility, index) => (
                <span key={index} className="facility-tag">
                  {facility.facilityName}
                </span>
              ))}
            </div>
            
            <Link
              to={`/rooms/${room.id}`}
              className="room-action-primary"
            >
              {t('rooms.viewDetails')}
            </Link>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="room-card">
      <div className="room-image-container">
        <img
          src={roomImage}
          alt={`Room ${room.roomNumber}`}
          className="room-image"
        />
        <div className={`room-status-badge ${room.isAvailable ? 'room-status-available' : 'room-status-unavailable'}`}>
          {room.isAvailable ? 'Available' : 'Unavailable'}
        </div>
      </div>
      
      <div className="room-content">
        <div className="room-header">
          <div>
            <h3 className="room-title">
              Room {room.roomNumber}
            </h3>
            <p className="room-type">
              {room.roomType?.replace('_', ' ')}
            </p>
          </div>
          <div className="room-price">
            <div className="room-price-amount">
              ${room.price}
            </div>
            <div className="room-price-period">per night</div>
          </div>
        </div>
        
        <p className="room-location">
          <MapPin className="w-4 h-4" />
          {room.hotel?.location || 'Location not specified'}
        </p>
        
        <div className="room-details">
          <span className="room-detail">
            <Users className="w-4 h-4" />
            Max {room.maxOccupancy}
          </span>
          <span>
            {room.position?.replace('_', ' ')}
          </span>
        </div>
        
        {room.description && (
          <p className="room-description line-clamp-2">
            {room.description}
          </p>
        )}
        
        {/* Facilities */}
        {room.facilities && room.facilities.length > 0 && (
          <div className="room-facilities">
            <div className="facilities-list">
              {room.facilities.slice(0, 3).map((facility, index) => (
                <span key={index} className="facility-tag">
                  {facility.facilityName}
                </span>
              ))}
              {room.facilities.length > 3 && (
                <span className="text-xs text-light">
                  +{room.facilities.length - 3} more
                </span>
              )}
            </div>
          </div>
        )}
        
        <div className="room-actions">
          <Link
            to={`/rooms/${room.id}`}
            className="room-action-primary"
          >
            {t('rooms.viewDetails')}
          </Link>
          {room.isAvailable && (
            <Link
              to={`/rooms/${room.id}?book=true`}
              className="room-action-secondary"
            >
              {t('rooms.bookNow')}
            </Link>
          )}
        </div>
      </div>
    </div>
  );
};

export default Rooms;