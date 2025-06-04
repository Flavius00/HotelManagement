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
      <section className="hero-section">
        <div className="hero-overlay"></div>
        <div className="hero-content">
          <h1 className="hero-title">
            Welcome to Hotel Chain
          </h1>
          <p className="hero-subtitle">
            Experience luxury hospitality at its finest. Discover our premium accommodations 
            and world-class service across multiple destinations.
          </p>
          <div className="hero-buttons">
            <Link
              to="/rooms"
              className="hero-button-primary"
            >
              <Bed className="w-5 h-5" />
              {t('rooms.title')}
            </Link>
            {!isAuthenticated() && (
              <Link
                to="/register"
                className="hero-button-secondary"
              >
                Join Us Today
              </Link>
            )}
          </div>
        </div>
      </section>

      {/* Stats Section */}
      <section className="stats-section">
        {stats.map((stat, index) => (
          <div key={index} className="stat-item">
            <div className="stat-number">
              {stat.number}
            </div>
            <div className="stat-label">
              {stat.label}
            </div>
          </div>
        ))}
      </section>

      {/* Features Section */}
      <section className="features-section">
        <div className="section-header">
          <h2 className="section-title">
            Why Choose Hotel Chain?
          </h2>
          <p className="section-subtitle">
            We provide exceptional service and amenities to make your stay unforgettable.
          </p>
        </div>
        
        <div className="features-grid">
          {features.map((feature, index) => {
            const Icon = feature.icon;
            return (
              <div key={index} className="feature-card">
                <div className="feature-icon">
                  <Icon className="w-6 h-6" />
                </div>
                <h3 className="feature-title">
                  {feature.title}
                </h3>
                <p className="feature-description">
                  {feature.description}
                </p>
              </div>
            );
          })}
        </div>
      </section>

      {/* Quick Actions */}
      <section className="quick-actions">
        <div className="section-header">
          <h2 className="section-title">
            Quick Actions
          </h2>
          <p className="section-subtitle">
            Everything you need for your perfect stay
          </p>
        </div>
        
        <div className="quick-actions-grid">
          <Link
            to="/rooms"
            className="quick-action-card"
          >
            <div className="quick-action-content">
              <div className="quick-action-text">
                <h3>Browse Rooms</h3>
                <p>Explore our luxury accommodations</p>
              </div>
              <ArrowRight className="quick-action-icon" />
            </div>
          </Link>
          
          {isAuthenticated() ? (
            <>
              <Link
                to="/bookings"
                className="quick-action-card"
              >
                <div className="quick-action-content">
                  <div className="quick-action-text">
                    <h3>My Bookings</h3>
                    <p>Manage your reservations</p>
                  </div>
                  <ArrowRight className="quick-action-icon" />
                </div>
              </Link>
              
              <Link
                to="/reviews"
                className="quick-action-card"
              >
                <div className="quick-action-content">
                  <div className="quick-action-text">
                    <h3>Reviews</h3>
                    <p>Share your experience</p>
                  </div>
                  <ArrowRight className="quick-action-icon" />
                </div>
              </Link>
            </>
          ) : (
            <>
              <Link
                to="/login"
                className="quick-action-card"
              >
                <div className="quick-action-content">
                  <div className="quick-action-text">
                    <h3>Sign In</h3>
                    <p>Access your account</p>
                  </div>
                  <ArrowRight className="quick-action-icon" />
                </div>
              </Link>
              
              <Link
                to="/register"
                className="quick-action-card"
              >
                <div className="quick-action-content">
                  <div className="quick-action-text">
                    <h3>Join Us</h3>
                    <p>Create your account</p>
                  </div>
                  <ArrowRight className="quick-action-icon" />
                </div>
              </Link>
            </>
          )}
        </div>
      </section>

      {/* Testimonials */}
      <section className="testimonials-section">
        <div className="section-header">
          <h2 className="section-title">
            What Our Guests Say
          </h2>
          <p className="section-subtitle">
            Read reviews from our satisfied customers
          </p>
        </div>
        
        <div className="testimonials-grid">
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
            <div key={index} className="testimonial-card">
              <div className="testimonial-rating">
                {[...Array(review.rating)].map((_, i) => (
                  <Star key={i} className="testimonial-star" />
                ))}
              </div>
              <p className="testimonial-comment">
                "{review.comment}"
              </p>
              <div className="testimonial-author">
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