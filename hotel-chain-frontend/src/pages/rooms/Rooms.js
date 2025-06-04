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
  Grid3X3,
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
      <div className="flex items-center justify-center min-h-64">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">{t('common.loading')}</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="text-center py-12">
        <p className="text-red-600 mb-4">{t('common.error')}</p>
        <button 
          onClick={() => refetch()} 
          className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700"
        >
          Try Again
        </button>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">{t('rooms.title')}</h1>
          <p className="mt-1 text-gray-600">
            {filteredRooms.length} {filteredRooms.length === 1 ? 'room' : 'rooms'} available
          </p>
        </div>
        
        <div className="mt-4 sm:mt-0 flex items-center space-x-4">
          {/* View Mode Toggle */}
          <div className="flex bg-gray-100 rounded-lg p-1">
            <button
              onClick={() => setViewMode('grid')}
              className={`p-2 rounded-md transition-colors ${
                viewMode === 'grid' ? 'bg-white shadow-sm' : 'text-gray-500'
              }`}
            >
              <Grid3X3 className="w-4 h-4" />
            </button>
            <button
              onClick={() => setViewMode('list')}
              className={`p-2 rounded-md transition-colors ${
                viewMode === 'list' ? 'bg-white shadow-sm' : 'text-gray-500'
              }`}
            >
              <List className="w-4 h-4" />
            </button>
          </div>

          {/* Filter Toggle */}
          <button
            onClick={() => setShowFilters(!showFilters)}
            className="flex items-center space-x-2 bg-hotel-navy text-white px-4 py-2 rounded-lg hover:bg-blue-800 transition-colors"
          >
            <SlidersHorizontal className="w-4 h-4" />
            <span>Filters</span>
          </button>
        </div>
      </div>

      {/* Search Bar */}
      <div className="relative">
        <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
          <Search className="h-5 w-5 text-gray-400" />
        </div>
        <input
          type="text"
          placeholder="Search rooms by location, hotel, or room number..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="block w-full pl-10 pr-3 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
        />
      </div>

      {/* Filters Panel */}
      {showFilters && (
        <div className="bg-white p-6 rounded-lg shadow-lg border">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-lg font-semibold text-gray-900">{t('rooms.filterBy')}</h3>
            <button
              onClick={clearFilters}
              className="text-sm text-blue-600 hover:text-blue-700"
            >
              Clear All
            </button>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            {/* Location Filter */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                {t('rooms.location')}
              </label>
              <input
                type="text"
                value={filters.location}
                onChange={(e) => handleFilterChange('location', e.target.value)}
                placeholder="Enter location"
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            {/* Room Type Filter */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Room Type
              </label>
              <select
                value={filters.roomType}
                onChange={(e) => handleFilterChange('roomType', e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
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
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                {t('rooms.position')}
              </label>
              <select
                value={filters.position}
                onChange={(e) => handleFilterChange('position', e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
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
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                {t('rooms.availability')}
              </label>
              <select
                value={filters.isAvailable === null ? '' : filters.isAvailable.toString()}
                onChange={(e) => handleFilterChange('isAvailable', e.target.value === '' ? null : e.target.value === 'true')}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="">All Rooms</option>
                <option value="true">Available Only</option>
                <option value="false">Unavailable</option>
              </select>
            </div>

            {/* Price Range */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Min Price ($)
              </label>
              <input
                type="number"
                value={filters.minPrice}
                onChange={(e) => handleFilterChange('minPrice', e.target.value)}
                placeholder="0"
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Max Price ($)
              </label>
              <input
                type="number"
                value={filters.maxPrice}
                onChange={(e) => handleFilterChange('maxPrice', e.target.value)}
                placeholder="1000"
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
          </div>

          {/* Facilities Filter */}
          <div className="mt-4">
            <label className="block text-sm font-medium text-gray-700 mb-2">
              {t('rooms.facilities')}
            </label>
            <div className="flex flex-wrap gap-2">
              {commonFacilities.map(facility => (
                <button
                  key={facility}
                  onClick={() => handleFacilityToggle(facility)}
                  className={`px-3 py-1 rounded-full text-sm border transition-colors ${
                    filters.facilities.includes(facility)
                      ? 'bg-blue-600 text-white border-blue-600'
                      : 'bg-white text-gray-700 border-gray-300 hover:border-blue-600'
                  }`}
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
        <div className="text-center py-12">
          <div className="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <Search className="w-8 h-8 text-gray-400" />
          </div>
          <h3 className="text-lg font-medium text-gray-900 mb-2">No rooms found</h3>
          <p className="text-gray-600 mb-4">
            Try adjusting your search criteria or filters
          </p>
          <button
            onClick={clearFilters}
            className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700"
          >
            Clear Filters
          </button>
        </div>
      ) : (
        <div className={
          viewMode === 'grid'
            ? 'grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6'
            : 'space-y-4'
        }>
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
      <div className="bg-white rounded-lg shadow-md hover:shadow-lg transition-shadow overflow-hidden">
        <div className="flex">
          <div className="w-48 h-32 flex-shrink-0">
            <img
              src={roomImage}
              alt={`Room ${room.roomNumber}`}
              className="w-full h-full object-cover"
            />
          </div>
          <div className="flex-1 p-4 flex flex-col justify-between">
            <div>
              <div className="flex items-start justify-between">
                <div>
                  <h3 className="text-lg font-semibold text-gray-900">
                    Room {room.roomNumber} - {room.roomType?.replace('_', ' ')}
                  </h3>
                  <p className="text-sm text-gray-600 flex items-center mt-1">
                    <MapPin className="w-4 h-4 mr-1" />
                    {room.hotel?.location || 'Location not specified'}
                  </p>
                </div>
                <div className="text-right">
                  <div className="text-2xl font-bold text-gray-900">
                    ${room.price}
                    <span className="text-sm font-normal text-gray-600">/night</span>
                  </div>
                  <div className={`inline-flex px-2 py-1 rounded-full text-xs font-medium ${
                    room.isAvailable 
                      ? 'bg-green-100 text-green-800' 
                      : 'bg-red-100 text-red-800'
                  }`}>
                    {room.isAvailable ? 'Available' : 'Unavailable'}
                  </div>
                </div>
              </div>
              
              <div className="mt-2 flex items-center space-x-4 text-sm text-gray-600">
                <span className="flex items-center">
                  <Users className="w-4 h-4 mr-1" />
                  Max {room.maxOccupancy} guests
                </span>
                <span>
                  {room.position?.replace('_', ' ')}
                </span>
              </div>
              
              {room.description && (
                <p className="mt-2 text-sm text-gray-600 line-clamp-2">
                  {room.description}
                </p>
              )}
            </div>
            
            <div className="mt-4 flex justify-between items-center">
              <div className="flex space-x-2">
                {room.facilities?.slice(0, 3).map((facility, index) => (
                  <span key={index} className="inline-flex items-center px-2 py-1 rounded-full text-xs bg-blue-100 text-blue-800">
                    {facility.facilityName}
                  </span>
                ))}
              </div>
              
              <Link
                to={`/rooms/${room.id}`}
                className="bg-hotel-navy text-white px-4 py-2 rounded-lg hover:bg-blue-800 transition-colors text-sm font-medium"
              >
                {t('rooms.viewDetails')}
              </Link>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-lg shadow-md hover:shadow-lg transition-shadow overflow-hidden">
      <div className="relative">
        <img
          src={roomImage}
          alt={`Room ${room.roomNumber}`}
          className="w-full h-48 object-cover"
        />
        <div className="absolute top-4 right-4">
          <div className={`px-3 py-1 rounded-full text-sm font-medium ${
            room.isAvailable 
              ? 'bg-green-100 text-green-800' 
              : 'bg-red-100 text-red-800'
          }`}>
            {room.isAvailable ? 'Available' : 'Unavailable'}
          </div>
        </div>
      </div>
      
      <div className="p-4">
        <div className="flex items-start justify-between mb-2">
          <div>
            <h3 className="text-lg font-semibold text-gray-900">
              Room {room.roomNumber}
            </h3>
            <p className="text-sm text-gray-600">
              {room.roomType?.replace('_', ' ')}
            </p>
          </div>
          <div className="text-right">
            <div className="text-xl font-bold text-gray-900">
              ${room.price}
            </div>
            <div className="text-xs text-gray-600">per night</div>
          </div>
        </div>
        
        <p className="text-sm text-gray-600 flex items-center mb-2">
          <MapPin className="w-4 h-4 mr-1" />
          {room.hotel?.location || 'Location not specified'}
        </p>
        
        <div className="flex items-center space-x-4 text-sm text-gray-600 mb-3">
          <span className="flex items-center">
            <Users className="w-4 h-4 mr-1" />
            Max {room.maxOccupancy}
          </span>
          <span>
            {room.position?.replace('_', ' ')}
          </span>
        </div>
        
        {room.description && (
          <p className="text-sm text-gray-600 mb-3 line-clamp-2">
            {room.description}
          </p>
        )}
        
        {/* Facilities */}
        {room.facilities && room.facilities.length > 0 && (
          <div className="mb-4">
            <div className="flex flex-wrap gap-1">
              {room.facilities.slice(0, 3).map((facility, index) => (
                <span key={index} className="inline-flex items-center px-2 py-1 rounded-full text-xs bg-blue-100 text-blue-800">
                  {facility.facilityName}
                </span>
              ))}
              {room.facilities.length > 3 && (
                <span className="text-xs text-gray-500">
                  +{room.facilities.length - 3} more
                </span>
              )}
            </div>
          </div>
        )}
        
        <div className="flex space-x-2">
          <Link
            to={`/rooms/${room.id}`}
            className="flex-1 bg-hotel-navy text-white text-center py-2 px-4 rounded-lg hover:bg-blue-800 transition-colors text-sm font-medium"
          >
            {t('rooms.viewDetails')}
          </Link>
          {room.isAvailable && (
            <Link
              to={`/rooms/${room.id}?book=true`}
              className="bg-hotel-gold text-hotel-navy py-2 px-4 rounded-lg hover:bg-yellow-500 transition-colors text-sm font-medium"
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