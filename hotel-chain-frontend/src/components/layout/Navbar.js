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
    <nav className="bg-hotel-navy shadow-lg sticky top-0 z-50">
      <div className="container mx-auto px-4">
        <div className="flex justify-between items-center h-16">
          {/* Logo */}
          <Link to="/" className="flex items-center space-x-2">
            <div className="w-8 h-8 bg-hotel-gold rounded-full flex items-center justify-center">
              <Bed className="w-5 h-5 text-hotel-navy" />
            </div>
            <span className="text-white text-xl font-bold">Hotel Chain</span>
          </Link>

          {/* Desktop Navigation */}
          <div className="hidden md:flex items-center space-x-8">
            {/* Main Navigation */}
            <div className="flex space-x-6">
              {navigation.map((item) => {
                const Icon = item.icon;
                return (
                  <Link
                    key={item.name}
                    to={item.href}
                    className={`flex items-center space-x-1 px-3 py-2 rounded-md text-sm font-medium transition-colors ${
                      isActive(item.href)
                        ? 'bg-hotel-gold text-hotel-navy'
                        : 'text-white hover:bg-blue-700'
                    }`}
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
                    className={`flex items-center space-x-1 px-3 py-2 rounded-md text-sm font-medium transition-colors ${
                      isActive(item.href)
                        ? 'bg-hotel-gold text-hotel-navy'
                        : 'text-white hover:bg-blue-700'
                    }`}
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
                    className={`flex items-center space-x-1 px-3 py-2 rounded-md text-sm font-medium transition-colors ${
                      isActive(item.href)
                        ? 'bg-hotel-gold text-hotel-navy'
                        : 'text-white hover:bg-blue-700'
                    }`}
                  >
                    <Icon className="w-4 h-4" />
                    <span>{item.name}</span>
                  </Link>
                );
              })}
            </div>

            {/* Language Selector */}
            <div className="relative">
              <button
                onClick={() => setIsLangOpen(!isLangOpen)}
                className="flex items-center space-x-1 text-white hover:text-hotel-gold transition-colors"
              >
                <Globe className="w-4 h-4" />
                <span className="text-sm">{currentLanguage.flag}</span>
                <ChevronDown className="w-3 h-3" />
              </button>
              
              {isLangOpen && (
                <div className="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg py-1 z-50">
                  {languages.map((lang) => (
                    <button
                      key={lang.code}
                      onClick={() => changeLanguage(lang.code)}
                      className={`w-full text-left px-4 py-2 text-sm hover:bg-gray-100 flex items-center space-x-2 ${
                        i18n.language === lang.code ? 'bg-blue-50 text-blue-600' : 'text-gray-700'
                      }`}
                    >
                      <span>{lang.flag}</span>
                      <span>{lang.name}</span>
                    </button>
                  ))}
                </div>
              )}
            </div>

            {/* Auth Buttons */}
            <div className="flex items-center space-x-4">
              {isAuthenticated() ? (
                <div className="flex items-center space-x-4">
                  <span className="text-white text-sm">
                    {t('common.welcome')}, {currentUser?.firstName || currentUser?.username}
                  </span>
                  <button
                    onClick={handleLogout}
                    className="flex items-center space-x-1 bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded-md text-sm font-medium transition-colors"
                  >
                    <LogOut className="w-4 h-4" />
                    <span>{t('nav.logout')}</span>
                  </button>
                </div>
              ) : (
                <div className="flex space-x-2">
                  <Link
                    to="/login"
                    className="flex items-center space-x-1 text-white hover:text-hotel-gold px-3 py-2 rounded-md text-sm font-medium transition-colors"
                  >
                    <LogIn className="w-4 h-4" />
                    <span>{t('nav.login')}</span>
                  </Link>
                  <Link
                    to="/register"
                    className="flex items-center space-x-1 bg-hotel-gold hover:bg-yellow-500 text-hotel-navy px-4 py-2 rounded-md text-sm font-medium transition-colors"
                  >
                    <UserPlus className="w-4 h-4" />
                    <span>{t('nav.register')}</span>
                  </Link>
                </div>
              )}
            </div>
          </div>

          {/* Mobile menu button */}
          <div className="md:hidden">
            <button
              onClick={() => setIsOpen(!isOpen)}
              className="text-white hover:text-hotel-gold transition-colors"
            >
              {isOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
            </button>
          </div>
        </div>

        {/* Mobile Navigation */}
        {isOpen && (
          <div className="md:hidden">
            <div className="px-2 pt-2 pb-3 space-y-1 bg-hotel-navy border-t border-blue-800">
              {/* Main Navigation */}
              {navigation.map((item) => {
                const Icon = item.icon;
                return (
                  <Link
                    key={item.name}
                    to={item.href}
                    onClick={() => setIsOpen(false)}
                    className={`flex items-center space-x-2 px-3 py-2 rounded-md text-base font-medium transition-colors ${
                      isActive(item.href)
                        ? 'bg-hotel-gold text-hotel-navy'
                        : 'text-white hover:bg-blue-700'
                    }`}
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
                    className={`flex items-center space-x-2 px-3 py-2 rounded-md text-base font-medium transition-colors ${
                      isActive(item.href)
                        ? 'bg-hotel-gold text-hotel-navy'
                        : 'text-white hover:bg-blue-700'
                    }`}
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
                    className={`flex items-center space-x-2 px-3 py-2 rounded-md text-base font-medium transition-colors ${
                      isActive(item.href)
                        ? 'bg-hotel-gold text-hotel-navy'
                        : 'text-white hover:bg-blue-700'
                    }`}
                  >
                    <Icon className="w-5 h-5" />
                    <span>{item.name}</span>
                  </Link>
                );
              })}

              {/* Language Selection */}
              <div className="px-3 py-2">
                <div className="text-white text-sm font-medium mb-2 flex items-center space-x-2">
                  <Globe className="w-4 h-4" />
                  <span>Language</span>
                </div>
                <div className="space-y-1">
                  {languages.map((lang) => (
                    <button
                      key={lang.code}
                      onClick={() => {
                        changeLanguage(lang.code);
                        setIsOpen(false);
                      }}
                      className={`w-full text-left px-3 py-1 text-sm rounded flex items-center space-x-2 ${
                        i18n.language === lang.code ? 'bg-hotel-gold text-hotel-navy' : 'text-white hover:bg-blue-700'
                      }`}
                    >
                      <span>{lang.flag}</span>
                      <span>{lang.name}</span>
                    </button>
                  ))}
                </div>
              </div>

              {/* Auth Section */}
              <div className="border-t border-blue-800 pt-4">
                {isAuthenticated() ? (
                  <div className="space-y-2">
                    <div className="px-3 py-2 text-white text-sm">
                      {t('common.welcome')}, {currentUser?.firstName || currentUser?.username}
                    </div>
                    <button
                      onClick={handleLogout}
                      className="w-full flex items-center space-x-2 px-3 py-2 text-red-300 hover:text-red-100 hover:bg-red-600 rounded-md transition-colors"
                    >
                      <LogOut className="w-5 h-5" />
                      <span>{t('nav.logout')}</span>
                    </button>
                  </div>
                ) : (
                  <div className="space-y-2">
                    <Link
                      to="/login"
                      onClick={() => setIsOpen(false)}
                      className="w-full flex items-center space-x-2 px-3 py-2 text-white hover:bg-blue-700 rounded-md transition-colors"
                    >
                      <LogIn className="w-5 h-5" />
                      <span>{t('nav.login')}</span>
                    </Link>
                    <Link
                      to="/register"
                      onClick={() => setIsOpen(false)}
                      className="w-full flex items-center space-x-2 px-3 py-2 bg-hotel-gold text-hotel-navy hover:bg-yellow-500 rounded-md transition-colors"
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