import React, { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../../contexts/AuthContext';
import { useQuery } from 'react-query';
import { dashboardAPI, usersAPI, roomsAPI, bookingsAPI } from '../../services/api';
import { 
  Users, 
  Bed, 
  Calendar, 
  DollarSign,
  TrendingUp,
  TrendingDown,
  BarChart3,
  PieChart,
  Download,
  RefreshCw,
  Settings,
  Eye,
  UserCheck,
  AlertTriangle
} from 'lucide-react';
import toast from 'react-hot-toast';

const Dashboard = () => {
  const { t } = useTranslation();
  const { currentUser, canViewStatistics, canManageUsers, canManageRooms } = useAuth();
  const [timeRange, setTimeRange] = useState('30'); // days

  // Fetch dashboard data
  const { data: dashboardData, isLoading: dashboardLoading } = useQuery(
    ['dashboard'],
    async () => {
      const response = await dashboardAPI.getDashboardData();
      return response.data.data || response.data;
    },
    {
      onError: () => {
        toast.error('Failed to load dashboard data');
      }
    }
  );

  // Fetch users data if user can manage users
  const { data: users = [] } = useQuery(
    ['users'],
    async () => {
      const response = await usersAPI.getAllUsers();
      return response.data.data || response.data;
    },
    {
      enabled: canManageUsers(),
      onError: () => {
        toast.error('Failed to load users data');
      }
    }
  );

  // Fetch rooms data if user can manage rooms
  const { data: rooms = [] } = useQuery(
    ['rooms'],
    async () => {
      const response = await roomsAPI.getAllRooms();
      return response.data.data || response.data;
    },
    {
      enabled: canManageRooms(),
      onError: () => {
        toast.error('Failed to load rooms data');
      }
    }
  );

  // Mock statistics for demo
  const stats = {
    totalUsers: users.length || 156,
    totalRooms: rooms.length || 45,
    totalBookings: 89,
    revenue: 25678.50,
    occupancyRate: 78.5,
    averageRating: 4.6,
    pendingBookings: 12,
    todayCheckIns: 8
  };

  const StatCard = ({ title, value, icon: Icon, trend, trendValue, color = 'blue' }) => {
    const colorClasses = {
      blue: 'bg-blue-50 text-blue-600',
      green: 'bg-green-50 text-green-600',
      yellow: 'bg-yellow-50 text-yellow-600',
      red: 'bg-red-50 text-red-600'
    };

    return (
      <div className="bg-white p-6 rounded-lg shadow-md">
        <div className="flex items-center justify-between">
          <div>
            <p className="text-sm font-medium text-gray-600">{title}</p>
            <p className="text-2xl font-bold text-gray-900">{value}</p>
            {trend && (
              <div className="flex items-center mt-1">
                {trend === 'up' ? (
                  <TrendingUp className="w-4 h-4 text-green-500 mr-1" />
                ) : (
                  <TrendingDown className="w-4 h-4 text-red-500 mr-1" />
                )}
                <span className={`text-sm ${trend === 'up' ? 'text-green-600' : 'text-red-600'}`}>
                  {trendValue}%
                </span>
              </div>
            )}
          </div>
          <div className={`p-3 rounded-full ${colorClasses[color]}`}>
            <Icon className="w-6 h-6" />
          </div>
        </div>
      </div>
    );
  };

  if (dashboardLoading) {
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
          <h1 className="text-3xl font-bold text-gray-900">{t('dashboard.title')}</h1>
          <p className="mt-1 text-gray-600">
            Welcome back, {currentUser?.firstName || currentUser?.username}
          </p>
        </div>
        
        <div className="mt-4 sm:mt-0 flex items-center space-x-4">
          <select
            value={timeRange}
            onChange={(e) => setTimeRange(e.target.value)}
            className="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="7">Last 7 days</option>
            <option value="30">Last 30 days</option>
            <option value="90">Last 3 months</option>
            <option value="365">Last year</option>
          </select>
          
          <button className="bg-hotel-navy text-white px-4 py-2 rounded-lg hover:bg-blue-800 transition-colors inline-flex items-center">
            <RefreshCw className="w-4 h-4 mr-2" />
            Refresh
          </button>
        </div>
      </div>

      {/* Overview Stats */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <StatCard
          title="Total Users"
          value={stats.totalUsers.toLocaleString()}
          icon={Users}
          trend="up"
          trendValue="12"
          color="blue"
        />
        
        <StatCard
          title="Total Rooms"
          value={stats.totalRooms.toLocaleString()}
          icon={Bed}
          trend="up"
          trendValue="5"
          color="green"
        />
        
        <StatCard
          title="Total Bookings"
          value={stats.totalBookings.toLocaleString()}
          icon={Calendar}
          trend="up"
          trendValue="18"
          color="yellow"
        />
        
        <StatCard
          title="Revenue"
          value={`$${stats.revenue.toLocaleString()}`}
          icon={DollarSign}
          trend="up"
          trendValue="25"
          color="green"
        />
      </div>

      {/* Additional Stats Row */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <StatCard
          title="Occupancy Rate"
          value={`${stats.occupancyRate}%`}
          icon={BarChart3}
          trend="up"
          trendValue="3"
          color="blue"
        />
        
        <StatCard
          title="Average Rating"
          value={stats.averageRating}
          icon={TrendingUp}
          trend="up"
          trendValue="0.2"
          color="green"
        />
        
        <StatCard
          title="Pending Bookings"
          value={stats.pendingBookings}
          icon={AlertTriangle}
          color="yellow"
        />
        
        <StatCard
          title="Today's Check-ins"
          value={stats.todayCheckIns}
          icon={UserCheck}
          color="blue"
        />
      </div>

      {/* Management Sections */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Recent Bookings */}
        <div className="bg-white p-6 rounded-lg shadow-md">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-lg font-semibold text-gray-900">Recent Bookings</h3>
            <button className="text-sm text-blue-600 hover:text-blue-700">
              View All
            </button>
          </div>
          
          <div className="space-y-3">
            {[
              { id: 1, room: '101', guest: 'John Doe', date: '2024-06-05', status: 'CONFIRMED' },
              { id: 2, room: '205', guest: 'Jane Smith', date: '2024-06-06', status: 'PENDING' },
              { id: 3, room: '302', guest: 'Mike Johnson', date: '2024-06-07', status: 'CONFIRMED' }
            ].map((booking) => (
              <div key={booking.id} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                <div>
                  <div className="font-medium text-gray-900">Room {booking.room}</div>
                  <div className="text-sm text-gray-600">{booking.guest}</div>
                  <div className="text-xs text-gray-500">{booking.date}</div>
                </div>
                <div className={`px-2 py-1 rounded-full text-xs font-medium ${
                  booking.status === 'CONFIRMED' 
                    ? 'bg-green-100 text-green-800' 
                    : 'bg-yellow-100 text-yellow-800'
                }`}>
                  {booking.status}
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Quick Actions */}
        <div className="bg-white p-6 rounded-lg shadow-md">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Quick Actions</h3>
          
          <div className="space-y-3">
            {canManageUsers() && (
              <button className="w-full flex items-center justify-between p-3 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors">
                <div className="flex items-center">
                  <Users className="w-5 h-5 text-blue-600 mr-3" />
                  <span className="font-medium text-gray-900">Manage Users</span>
                </div>
                <span className="text-sm text-gray-500">{stats.totalUsers} users</span>
              </button>
            )}
            
            {canManageRooms() && (
              <button className="w-full flex items-center justify-between p-3 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors">
                <div className="flex items-center">
                  <Bed className="w-5 h-5 text-green-600 mr-3" />
                  <span className="font-medium text-gray-900">Manage Rooms</span>
                </div>
                <span className="text-sm text-gray-500">{stats.totalRooms} rooms</span>
              </button>
            )}
            
            <button className="w-full flex items-center justify-between p-3 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors">
              <div className="flex items-center">
                <Calendar className="w-5 h-5 text-yellow-600 mr-3" />
                <span className="font-medium text-gray-900">View Bookings</span>
              </div>
              <span className="text-sm text-gray-500">{stats.totalBookings} bookings</span>
            </button>
            
            <button className="w-full flex items-center justify-between p-3 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors">
              <div className="flex items-center">
                <Download className="w-5 h-5 text-purple-600 mr-3" />
                <span className="font-medium text-gray-900">Export Data</span>
              </div>
            </button>
            
            <button className="w-full flex items-center justify-between p-3 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors">
              <div className="flex items-center">
                <Settings className="w-5 h-5 text-gray-600 mr-3" />
                <span className="font-medium text-gray-900">Settings</span>
              </div>
            </button>
          </div>
        </div>
      </div>

      {/* Charts Section */}
      {canViewStatistics() && (
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {/* Revenue Chart Placeholder */}
          <div className="bg-white p-6 rounded-lg shadow-md">
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-lg font-semibold text-gray-900">Revenue Trends</h3>
              <select className="text-sm border border-gray-300 rounded px-2 py-1">
                <option>Monthly</option>
                <option>Weekly</option>
                <option>Daily</option>
              </select>
            </div>
            
            <div className="h-64 bg-gray-50 rounded-lg flex items-center justify-center">
              <div className="text-center">
                <BarChart3 className="w-12 h-12 text-gray-400 mx-auto mb-2" />
                <p className="text-gray-500">Revenue chart will be displayed here</p>
                <p className="text-sm text-gray-400">Integration with chart library needed</p>
              </div>
            </div>
          </div>

          {/* Occupancy Chart Placeholder */}
          <div className="bg-white p-6 rounded-lg shadow-md">
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-lg font-semibold text-gray-900">Room Occupancy</h3>
              <div className="flex items-center space-x-2">
                <span className="text-2xl font-bold text-blue-600">{stats.occupancyRate}%</span>
              </div>
            </div>
            
            <div className="h-64 bg-gray-50 rounded-lg flex items-center justify-center">
              <div className="text-center">
                <PieChart className="w-12 h-12 text-gray-400 mx-auto mb-2" />
                <p className="text-gray-500">Occupancy chart will be displayed here</p>
                <p className="text-sm text-gray-400">Integration with chart library needed</p>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* System Status */}
      <div className="bg-white p-6 rounded-lg shadow-md">
        <h3 className="text-lg font-semibold text-gray-900 mb-4">System Status</h3>
        
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="flex items-center justify-between p-4 bg-green-50 rounded-lg">
            <div>
              <div className="font-medium text-green-900">API Gateway</div>
              <div className="text-sm text-green-600">Operational</div>
            </div>
            <div className="w-3 h-3 bg-green-500 rounded-full"></div>
          </div>
          
          <div className="flex items-center justify-between p-4 bg-green-50 rounded-lg">
            <div>
              <div className="font-medium text-green-900">Database</div>
              <div className="text-sm text-green-600">Connected</div>
            </div>
            <div className="w-3 h-3 bg-green-500 rounded-full"></div>
          </div>
          
          <div className="flex items-center justify-between p-4 bg-green-50 rounded-lg">
            <div>
              <div className="font-medium text-green-900">Services</div>
              <div className="text-sm text-green-600">All running</div>
            </div>
            <div className="w-3 h-3 bg-green-500 rounded-full"></div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;