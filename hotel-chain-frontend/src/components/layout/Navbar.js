import React, { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../../contexts/AuthContext';
import { 
  Home, 
  Bed, 
  Calendar, 
  Star, 
  LogIn, 
  LogOut, 
  UserPlus,
  BarChart3,
  Globe,
  Menu,
  X,
  ChevronDown
} from 'lucide-react';

const Navbar = () => {
  const { t, i18n } = useTranslation();
  const { currentUser, logout, isAuthenticated, canViewDashboard } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();
  const [isOpen, setIsOpen] = useState(false);
  const [isLangOpen, setIsLangOpen] = useState(false);

  const changeLanguage = (lng) => {
    i18n.changeLanguage(lng);
    setIsLangOpen(false);
  };

  const handleLogout = () => {
    logout();
    navigate('/');
    setIsOpen(false);
  };

  const navigation = [
    { name: t('nav.home'), href: '/', icon: Home },
    { name: t('nav.rooms'), href: '/rooms', icon: Bed },
  ];

  const authenticatedNavigation = [
    { name: t('nav.bookings'), href: '/bookings', icon: Calendar },
    { name: t('nav.reviews'), href: '/reviews', icon: Star },
  ];

  const adminNavigation = canViewDashboard() ? [
    { name: t('nav.dashboard'), href: '/dashboard', icon: BarChart3 },
  ] : [];

  const isActive = (path) => location.pathname === path;

  const languages = [
    { code: 'en', name: 'English', flag: '🇺🇸' },
    { code: 'ro', name: 'Română', flag: '🇷🇴' },
    { code: 'fr', name: 'Français', flag: '🇫🇷' },
  ];

  const currentLanguage = languages.find(lang => lang.code === i18n.language) || languages[0];

  return (
    <nav className="navbar">
      <div className="navbar-container">
        <div className="navbar-content">
          {/* Logo */}
          <Link to="/" className="navbar-logo">
            <div className="logo-icon">
              <Bed className="w-5 h-5" />
            </div>
            <span className="logo-text">Hotel Chain</span>
          </Link>

          {/* Desktop Navigation */}
          <div className="navbar-desktop">
            {/* Main Navigation */}
            <div className="navbar-nav">
              {navigation.map((item) => {
                const Icon = item.icon;
                return (
                  <Link
                    key={item.name}
                    to={item.href}
                    className={`navbar-nav-item ${isActive(item.href) ? 'active' : ''}`}
                  >
                    <Icon className="w-4 h-4" />
                    <span>{item.name}</span>
                  </Link>
                );
              })}

              {/* Authenticated Navigation */}
              {isAuthenticated() && authenticatedNavigation.map((item) => {
                const Icon = item.icon;
                return (
                  <Link
                    key={item.name}
                    to={item.href}
                    className={`navbar-nav-item ${isActive(item.href) ? 'active' : ''}`}
                  >
                    <Icon className="w-4 h-4" />
                    <span>{item.name}</span>
                  </Link>
                );
              })}

              {/* Admin Navigation */}
              {adminNavigation.map((item) => {
                const Icon = item.icon;
                return (
                  <Link
                    key={item.name}
                    to={item.href}
                    className={`navbar-nav-item ${isActive(item.href) ? 'active' : ''}`}
                  >
                    <Icon className="w-4 h-4" />
                    <span>{item.name}</span>
                  </Link>
                );
              })}
            </div>

            {/* Language Selector */}
            <div className="language-selector">
              <button
                onClick={() => setIsLangOpen(!isLangOpen)}
                className="language-button"
              >
                <Globe className="w-4 h-4" />
                <span>{currentLanguage.flag}</span>
                <ChevronDown className="w-3 h-3" />
              </button>
              
              {isLangOpen && (
                <div className="language-dropdown">
                  {languages.map((lang) => (
                    <button
                      key={lang.code}
                      onClick={() => changeLanguage(lang.code)}
                      className={`language-option ${i18n.language === lang.code ? 'active' : ''}`}
                    >
                      <span>{lang.flag}</span>
                      <span>{lang.name}</span>
                    </button>
                  ))}
                </div>
              )}
            </div>

            {/* Auth Buttons */}
            <div className="navbar-auth">
              {isAuthenticated() ? (
                <div className="auth-buttons">
                  <span className="user-info">
                    {t('common.welcome')}, {currentUser?.firstName || currentUser?.username}
                  </span>
                  <button
                    onClick={handleLogout}
                    className="auth-button auth-button-logout"
                  >
                    <LogOut className="w-4 h-4" />
                    <span>{t('nav.logout')}</span>
                  </button>
                </div>
              ) : (
                <div className="auth-buttons">
                  <Link
                    to="/login"
                    className="auth-button auth-button-login"
                  >
                    <LogIn className="w-4 h-4" />
                    <span>{t('nav.login')}</span>
                  </Link>
                  <Link
                    to="/register"
                    className="auth-button auth-button-register"
                  >
                    <UserPlus className="w-4 h-4" />
                    <span>{t('nav.register')}</span>
                  </Link>
                </div>
              )}
            </div>
          </div>

          {/* Mobile menu button */}
          <div className="mobile-menu-button">
            <button
              onClick={() => setIsOpen(!isOpen)}
              className="mobile-menu-button"
            >
              {isOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
            </button>
          </div>
        </div>

        {/* Mobile Navigation */}
        {isOpen && (
          <div className="navbar-mobile">
            <div className="mobile-nav-content">
              {/* Main Navigation */}
              {navigation.map((item) => {
                const Icon = item.icon;
                return (
                  <Link
                    key={item.name}
                    to={item.href}
                    onClick={() => setIsOpen(false)}
                    className={`mobile-nav-item ${isActive(item.href) ? 'active' : ''}`}
                  >
                    <Icon className="w-5 h-5" />
                    <span>{item.name}</span>
                  </Link>
                );
              })}

              {/* Authenticated Navigation */}
              {isAuthenticated() && authenticatedNavigation.map((item) => {
                const Icon = item.icon;
                return (
                  <Link
                    key={item.name}
                    to={item.href}
                    onClick={() => setIsOpen(false)}
                    className={`mobile-nav-item ${isActive(item.href) ? 'active' : ''}`}
                  >
                    <Icon className="w-5 h-5" />
                    <span>{item.name}</span>
                  </Link>
                );
              })}

              {/* Admin Navigation */}
              {adminNavigation.map((item) => {
                const Icon = item.icon;
                return (
                  <Link
                    key={item.name}
                    to={item.href}
                    onClick={() => setIsOpen(false)}
                    className={`mobile-nav-item ${isActive(item.href) ? 'active' : ''}`}
                  >
                    <Icon className="w-5 h-5" />
                    <span>{item.name}</span>
                  </Link>
                );
              })}

              {/* Language Selection */}
              <div className="mobile-nav-section">
                <div className="mobile-nav-title">
                  <Globe className="w-4 h-4" />
                  <span>Language</span>
                </div>
                <div className="mobile-language-options">
                  {languages.map((lang) => (
                    <button
                      key={lang.code}
                      onClick={() => {
                        changeLanguage(lang.code);
                        setIsOpen(false);
                      }}
                      className={`mobile-language-option ${i18n.language === lang.code ? 'active' : ''}`}
                    >
                      <span>{lang.flag}</span>
                      <span>{lang.name}</span>
                    </button>
                  ))}
                </div>
              </div>

              {/* Auth Section */}
              <div className="mobile-auth-section">
                {isAuthenticated() ? (
                  <>
                    <div className="mobile-user-info">
                      {t('common.welcome')}, {currentUser?.firstName || currentUser?.username}
                    </div>
                    <button
                      onClick={handleLogout}
                      className="mobile-auth-button mobile-auth-button-logout"
                    >
                      <LogOut className="w-5 h-5" />
                      <span>{t('nav.logout')}</span>
                    </button>
                  </>
                ) : (
                  <div className="mobile-auth-buttons">
                    <Link
                      to="/login"
                      onClick={() => setIsOpen(false)}
                      className="mobile-auth-button mobile-auth-button-login"
                    >
                      <LogIn className="w-5 h-5" />
                      <span>{t('nav.login')}</span>
                    </Link>
                    <Link
                      to="/register"
                      onClick={() => setIsOpen(false)}
                      className="mobile-auth-button mobile-auth-button-register"
                    >
                      <UserPlus className="w-5 h-5" />
                      <span>{t('nav.register')}</span>
                    </Link>
                  </div>
                )}
              </div>
            </div>
          </div>
        )}
      </div>
    </nav>
  );
};

export default Navbar;