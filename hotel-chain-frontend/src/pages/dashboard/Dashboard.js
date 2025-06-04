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

  const StatCard = ({ title, value, icon: Icon, trend, trendValue, color = 'ocean' }) => {
    const iconColors = {
      ocean: 'bg-ocean',
      coral: 'bg-coral',
      success: 'bg-success',
      warning: 'bg-warning'
    };

    return (
      <div className="stat-card">
        <div className="stat-card-header">
          <div>
            <p className="stat-card-label">{title}</p>
            <p className="stat-card-value">{value}</p>
            {trend && (
              <div className="stat-card-trend">
                {trend === 'up' ? (
                  <TrendingUp className="w-4 h-4 trend-up" />
                ) : (
                  <TrendingDown className="w-4 h-4 trend-down" />
                )}
                <span className={trend === 'up' ? 'trend-up' : 'trend-down'}>
                  {trendValue}%
                </span>
              </div>
            )}
          </div>
          <div className={`stat-card-icon ${iconColors[color]}`}>
            <Icon className="w-6 h-6 text-white" />
          </div>
        </div>
      </div>
    );
  };

  if (dashboardLoading) {
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
    <div className="dashboard-container">
      {/* Header */}
      <div className="dashboard-header">
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between">
          <div>
            <h1 className="dashboard-title">{t('dashboard.title')}</h1>
            <p className="dashboard-subtitle">
              Welcome back, {currentUser?.firstName || currentUser?.username}
            </p>
          </div>
          
          <div className="mt-4 sm:mt-0 flex items-center space-x-4">
            <select
              value={timeRange}
              onChange={(e) => setTimeRange(e.target.value)}
              className="form-select"
            >
              <option value="7">Last 7 days</option>
              <option value="30">Last 30 days</option>
              <option value="90">Last 3 months</option>
              <option value="365">Last year</option>
            </select>
            
            <button className="btn btn-primary">
              <RefreshCw className="w-4 h-4" />
              Refresh
            </button>
          </div>
        </div>
      </div>

      {/* Overview Stats */}
      <div className="stats-cards">
        <StatCard
          title="Total Users"
          value={stats.totalUsers.toLocaleString()}
          icon={Users}
          trend="up"
          trendValue="12"
          color="ocean"
        />
        
        <StatCard
          title="Total Rooms"
          value={stats.totalRooms.toLocaleString()}
          icon={Bed}
          trend="up"
          trendValue="5"
          color="success"
        />
        
        <StatCard
          title="Total Bookings"
          value={stats.totalBookings.toLocaleString()}
          icon={Calendar}
          trend="up"
          trendValue="18"
          color="warning"
        />
        
        <StatCard
          title="Revenue"
          value={`$${stats.revenue.toLocaleString()}`}
          icon={DollarSign}
          trend="up"
          trendValue="25"
          color="success"
        />
      </div>

      {/* Additional Stats Row */}
      <div className="stats-cards">
        <StatCard
          title="Occupancy Rate"
          value={`${stats.occupancyRate}%`}
          icon={BarChart3}
          trend="up"
          trendValue="3"
          color="ocean"
        />
        
        <StatCard
          title="Average Rating"
          value={stats.averageRating}
          icon={TrendingUp}
          trend="up"
          trendValue="0.2"
          color="success"
        />
        
        <StatCard
          title="Pending Bookings"
          value={stats.pendingBookings}
          icon={AlertTriangle}
          color="warning"
        />
        
        <StatCard
          title="Today's Check-ins"
          value={stats.todayCheckIns}
          icon={UserCheck}
          color="ocean"
        />
      </div>

      {/* Management Sections */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Recent Bookings */}
        <div className="card">
          <div className="card-header">
            <div className="flex items-center justify-between">
              <h3 className="card-title">Recent Bookings</h3>
              <button className="text-sm text-ocean hover:text-coral">
                View All
              </button>
            </div>
          </div>
          
          <div className="card-body">
            <div className="space-y-3">
              {[
                { id: 1, room: '101', guest: 'John Doe', date: '2024-06-05', status: 'CONFIRMED' },
                { id: 2, room: '205', guest: 'Jane Smith', date: '2024-06-06', status: 'PENDING' },
                { id: 3, room: '302', guest: 'Mike Johnson', date: '2024-06-07', status: 'CONFIRMED' }
              ].map((booking) => (
                <div key={booking.id} className="list-item">
                  <div className="list-item-avatar">
                    {booking.guest.split(' ').map(n => n[0]).join('')}
                  </div>
                  <div className="list-item-content">
                    <div className="list-item-title">Room {booking.room}</div>
                    <div className="list-item-subtitle">{booking.guest}</div>
                    <div className="text-xs text-light">{booking.date}</div>
                  </div>
                  <div className="list-item-meta">
                    <div className={`badge ${
                      booking.status === 'CONFIRMED' 
                        ? 'badge-success' 
                        : 'badge-warning'
                    }`}>
                      {booking.status}
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Quick Actions */}
        <div className="card">
          <div className="card-header">
            <h3 className="card-title">Quick Actions</h3>
          </div>
          
          <div className="card-body">
            <div className="space-y-3">
              {canManageUsers() && (
                <button className="w-full card hover-lift" style={{ cursor: 'pointer', border: 'none', background: 'none' }}>
                  <div className="card-body">
                    <div className="flex items-center justify-between">
                      <div className="flex items-center">
                        <div className="w-12 h-12 bg-ocean rounded-xl flex items-center justify-center mr-3">
                          <Users className="w-6 h-6 text-white" />
                        </div>
                        <div>
                          <div className="font-medium text-primary">Manage Users</div>
                          <div className="text-sm text-secondary">Manage user accounts</div>
                        </div>
                      </div>
                      <span className="badge badge-neutral">{stats.totalUsers} users</span>
                    </div>
                  </div>
                </button>
              )}
              
              {canManageRooms() && (
                <button className="w-full card hover-lift" style={{ cursor: 'pointer', border: 'none', background: 'none' }}>
                  <div className="card-body">
                    <div className="flex items-center justify-between">
                      <div className="flex items-center">
                        <div className="w-12 h-12 bg-success rounded-xl flex items-center justify-center mr-3">
                          <Bed className="w-6 h-6 text-white" />
                        </div>
                        <div>
                          <div className="font-medium text-primary">Manage Rooms</div>
                          <div className="text-sm text-secondary">Room management</div>
                        </div>
                      </div>
                      <span className="badge badge-neutral">{stats.totalRooms} rooms</span>
                    </div>
                  </div>
                </button>
              )}
              
              <button className="w-full card hover-lift" style={{ cursor: 'pointer', border: 'none', background: 'none' }}>
                <div className="card-body">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center">
                      <div className="w-12 h-12 bg-warning rounded-xl flex items-center justify-center mr-3">
                        <Calendar className="w-6 h-6 text-white" />
                      </div>
                      <div>
                        <div className="font-medium text-primary">View Bookings</div>
                        <div className="text-sm text-secondary">Booking management</div>
                      </div>
                    </div>
                    <span className="badge badge-neutral">{stats.totalBookings} bookings</span>
                  </div>
                </div>
              </button>
              
              <button className="w-full card hover-lift" style={{ cursor: 'pointer', border: 'none', background: 'none' }}>
                <div className="card-body">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center">
                      <div className="w-12 h-12 bg-coral rounded-xl flex items-center justify-center mr-3">
                        <Download className="w-6 h-6 text-white" />
                      </div>
                      <div>
                        <div className="font-medium text-primary">Export Data</div>
                        <div className="text-sm text-secondary">Download reports</div>
                      </div>
                    </div>
                  </div>
                </div>
              </button>
              
              <button className="w-full card hover-lift" style={{ cursor: 'pointer', border: 'none', background: 'none' }}>
                <div className="card-body">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center">
                      <div className="w-12 h-12 bg-secondary rounded-xl flex items-center justify-center mr-3">
                        <Settings className="w-6 h-6 text-primary" />
                      </div>
                      <div>
                        <div className="font-medium text-primary">Settings</div>
                        <div className="text-sm text-secondary">System configuration</div>
                      </div>
                    </div>
                  </div>
                </div>
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* Charts Section */}
      {canViewStatistics() && (
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {/* Revenue Chart Placeholder */}
          <div className="card">
            <div className="card-header">
              <div className="flex items-center justify-between">
                <h3 className="card-title">Revenue Trends</h3>
                <select className="form-select" style={{ minWidth: '120px' }}>
                  <option>Monthly</option>
                  <option>Weekly</option>
                  <option>Daily</option>
                </select>
              </div>
            </div>
            
            <div className="card-body">
              <div className="h-64 bg-secondary rounded-lg flex items-center justify-center">
                <div className="text-center">
                  <BarChart3 className="w-12 h-12 text-light mx-auto mb-2" />
                  <p className="text-light">Revenue chart will be displayed here</p>
                  <p className="text-sm text-light">Integration with chart library needed</p>
                </div>
              </div>
            </div>
          </div>

          {/* Occupancy Chart Placeholder */}
          <div className="card">
            <div className="card-header">
              <div className="flex items-center justify-between">
                <h3 className="card-title">Room Occupancy</h3>
                <div className="flex items-center space-x-2">
                  <span className="text-2xl font-bold text-ocean">{stats.occupancyRate}%</span>
                </div>
              </div>
            </div>
            
            <div className="card-body">
              <div className="h-64 bg-secondary rounded-lg flex items-center justify-center">
                <div className="text-center">
                  <PieChart className="w-12 h-12 text-light mx-auto mb-2" />
                  <p className="text-light">Occupancy chart will be displayed here</p>
                  <p className="text-sm text-light">Integration with chart library needed</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* System Status */}
      <div className="card">
        <div className="card-header">
          <h3 className="card-title">System Status</h3>
        </div>
        
        <div className="card-body">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="flex items-center justify-between p-4 bg-success rounded-lg text-white">
              <div>
                <div className="font-medium">API Gateway</div>
                <div className="text-sm opacity-90">Operational</div>
              </div>
              <div className="w-3 h-3 bg-white rounded-full"></div>
            </div>
            
            <div className="flex items-center justify-between p-4 bg-success rounded-lg text-white">
              <div>
                <div className="font-medium">Database</div>
                <div className="text-sm opacity-90">Connected</div>
              </div>
              <div className="w-3 h-3 bg-white rounded-full"></div>
            </div>
            
            <div className="flex items-center justify-between p-4 bg-success rounded-lg text-white">
              <div>
                <div className="font-medium">Services</div>
                <div className="text-sm opacity-90">All running</div>
              </div>
              <div className="w-3 h-3 bg-white rounded-full"></div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;