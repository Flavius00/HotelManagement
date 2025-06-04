import React from 'react';
import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../contexts/AuthContext';
import { 
  Bed, 
  Calendar, 
  Star, 
  MapPin, 
  Wifi, 
  Car, 
  Coffee,
  Dumbbell,
  Utensils,
  ArrowRight
} from 'lucide-react';

const Home = () => {
  const { t } = useTranslation();
  const { isAuthenticated } = useAuth();

  const features = [
    {
      icon: Bed,
      title: 'Luxury Rooms',
      description: 'Experience comfort in our beautifully designed rooms with modern amenities.'
    },
    {
      icon: MapPin,
      title: 'Prime Locations',
      description: 'Located in the heart of major cities with easy access to attractions.'
    },
    {
      icon: Utensils,
      title: 'Fine Dining',
      description: 'Enjoy exquisite cuisine at our world-class restaurants.'
    },
    {
      icon: Dumbbell,
      title: 'Fitness Center',
      description: 'Stay fit with our state-of-the-art gym and wellness facilities.'
    },
    {
      icon: Wifi,
      title: 'Free WiFi',
      description: 'Stay connected with complimentary high-speed internet.'
    },
    {
      icon: Car,
      title: 'Valet Parking',
      description: 'Convenient parking service for our guests.'
    }
  ];

  const stats = [
    { number: '500+', label: 'Luxury Rooms' },
    { number: '50+', label: 'Locations Worldwide' },
    { number: '10M+', label: 'Happy Guests' },
    { number: '25+', label: 'Years Experience' }
  ];

  return (
    <div className="space-y-16">
      {/* Hero Section */}
      <section className="relative bg-gradient-to-r from-hotel-navy to-blue-800 text-white rounded-2xl overflow-hidden">
        <div className="absolute inset-0 bg-black bg-opacity-30"></div>
        <div className="relative px-8 py-20 text-center">
          <h1 className="text-5xl md:text-6xl font-bold mb-6">
            Welcome to Hotel Chain
          </h1>
          <p className="text-xl md:text-2xl mb-8 max-w-3xl mx-auto">
            Experience luxury hospitality at its finest. Discover our premium accommodations 
            and world-class service across multiple destinations.
          </p>
          <div className="flex flex-col sm:flex-row gap-4 justify-center">
            <Link
              to="/rooms"
              className="bg-hotel-gold hover:bg-yellow-500 text-hotel-navy font-semibold py-4 px-8 rounded-lg transition-colors inline-flex items-center justify-center"
            >
              <Bed className="w-5 h-5 mr-2" />
              {t('rooms.title')}
            </Link>
            {!isAuthenticated() && (
              <Link
                to="/register"
                className="border-2 border-white hover:bg-white hover:text-hotel-navy text-white font-semibold py-4 px-8 rounded-lg transition-colors"
              >
                Join Us Today
              </Link>
            )}
          </div>
        </div>
      </section>

      {/* Stats Section */}
      <section className="grid grid-cols-2 md:grid-cols-4 gap-8">
        {stats.map((stat, index) => (
          <div key={index} className="text-center">
            <div className="text-3xl md:text-4xl font-bold text-hotel-navy mb-2">
              {stat.number}
            </div>
            <div className="text-gray-600 font-medium">
              {stat.label}
            </div>
          </div>
        ))}
      </section>

      {/* Features Section */}
      <section>
        <div className="text-center mb-12">
          <h2 className="text-3xl md:text-4xl font-bold text-gray-900 mb-4">
            Why Choose Hotel Chain?
          </h2>
          <p className="text-lg text-gray-600 max-w-2xl mx-auto">
            We provide exceptional service and amenities to make your stay unforgettable.
          </p>
        </div>
        
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          {features.map((feature, index) => {
            const Icon = feature.icon;
            return (
              <div key={index} className="bg-white p-6 rounded-xl shadow-lg hover:shadow-xl transition-shadow">
                <div className="w-12 h-12 bg-hotel-gold bg-opacity-20 rounded-lg flex items-center justify-center mb-4">
                  <Icon className="w-6 h-6 text-hotel-navy" />
                </div>
                <h3 className="text-xl font-semibold text-gray-900 mb-2">
                  {feature.title}
                </h3>
                <p className="text-gray-600">
                  {feature.description}
                </p>
              </div>
            );
          })}
        </div>
      </section>

      {/* Quick Actions */}
      <section className="bg-hotel-cream rounded-2xl p-8">
        <div className="text-center mb-8">
          <h2 className="text-3xl font-bold text-gray-900 mb-4">
            Quick Actions
          </h2>
          <p className="text-gray-600">
            Everything you need for your perfect stay
          </p>
        </div>
        
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <Link
            to="/rooms"
            className="bg-white p-6 rounded-xl shadow-md hover:shadow-lg transition-shadow group"
          >
            <div className="flex items-center justify-between">
              <div>
                <h3 className="text-lg font-semibold text-gray-900 mb-2">
                  Browse Rooms
                </h3>
                <p className="text-gray-600 text-sm">
                  Explore our luxury accommodations
                </p>
              </div>
              <ArrowRight className="w-5 h-5 text-hotel-navy group-hover:transform group-hover:translate-x-1 transition-transform" />
            </div>
          </Link>
          
          {isAuthenticated() ? (
            <>
              <Link
                to="/bookings"
                className="bg-white p-6 rounded-xl shadow-md hover:shadow-lg transition-shadow group"
              >
                <div className="flex items-center justify-between">
                  <div>
                    <h3 className="text-lg font-semibold text-gray-900 mb-2">
                      My Bookings
                    </h3>
                    <p className="text-gray-600 text-sm">
                      Manage your reservations
                    </p>
                  </div>
                  <ArrowRight className="w-5 h-5 text-hotel-navy group-hover:transform group-hover:translate-x-1 transition-transform" />
                </div>
              </Link>
              
              <Link
                to="/reviews"
                className="bg-white p-6 rounded-xl shadow-md hover:shadow-lg transition-shadow group"
              >
                <div className="flex items-center justify-between">
                  <div>
                    <h3 className="text-lg font-semibold text-gray-900 mb-2">
                      Reviews
                    </h3>
                    <p className="text-gray-600 text-sm">
                      Share your experience
                    </p>
                  </div>
                  <ArrowRight className="w-5 h-5 text-hotel-navy group-hover:transform group-hover:translate-x-1 transition-transform" />
                </div>
              </Link>
            </>
          ) : (
            <>
              <Link
                to="/login"
                className="bg-white p-6 rounded-xl shadow-md hover:shadow-lg transition-shadow group"
              >
                <div className="flex items-center justify-between">
                  <div>
                    <h3 className="text-lg font-semibold text-gray-900 mb-2">
                      Sign In
                    </h3>
                    <p className="text-gray-600 text-sm">
                      Access your account
                    </p>
                  </div>
                  <ArrowRight className="w-5 h-5 text-hotel-navy group-hover:transform group-hover:translate-x-1 transition-transform" />
                </div>
              </Link>
              
              <Link
                to="/register"
                className="bg-white p-6 rounded-xl shadow-md hover:shadow-lg transition-shadow group"
              >
                <div className="flex items-center justify-between">
                  <div>
                    <h3 className="text-lg font-semibold text-gray-900 mb-2">
                      Join Us
                    </h3>
                    <p className="text-gray-600 text-sm">
                      Create your account
                    </p>
                  </div>
                  <ArrowRight className="w-5 h-5 text-hotel-navy group-hover:transform group-hover:translate-x-1 transition-transform" />
                </div>
              </Link>
            </>
          )}
        </div>
      </section>

      {/* Testimonials */}
      <section>
        <div className="text-center mb-12">
          <h2 className="text-3xl md:text-4xl font-bold text-gray-900 mb-4">
            What Our Guests Say
          </h2>
          <p className="text-lg text-gray-600">
            Read reviews from our satisfied customers
          </p>
        </div>
        
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          {[
            {
              name: 'Sarah Johnson',
              rating: 5,
              comment: 'Exceptional service and beautiful rooms. The staff went above and beyond to make our stay memorable.'
            },
            {
              name: 'Michael Chen',
              rating: 5,
              comment: 'Perfect location and amazing amenities. The breakfast was outstanding and the room was spotless.'
            },
            {
              name: 'Emily Rodriguez',
              rating: 5,
              comment: 'Best hotel experience ever! The attention to detail and luxury amenities exceeded our expectations.'
            }
          ].map((review, index) => (
            <div key={index} className="bg-white p-6 rounded-xl shadow-lg">
              <div className="flex items-center mb-4">
                {[...Array(review.rating)].map((_, i) => (
                  <Star key={i} className="w-5 h-5 text-yellow-400 fill-current" />
                ))}
              </div>
              <p className="text-gray-600 mb-4 italic">
                "{review.comment}"
              </p>
              <div className="font-semibold text-gray-900">
                {review.name}
              </div>
            </div>
          ))}
        </div>
      </section>
    </div>
  );
};

export default Home;