import React, { useState, useEffect } from 'react';
import { Card, Row, Col, Statistic, Select, Button, Table, Breadcrumb, Typography, DatePicker } from 'antd';
import { ReloadOutlined, EyeOutlined, DesktopOutlined, MobileOutlined, GlobalOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import api from '../../../services/api.service';
import { ApiResponse } from '@/types/auth.types.ts';
import dayjs from 'dayjs';

const { Title } = Typography;
const { RangePicker } = DatePicker;

interface AnalyticsData {
  totalVisits: number;
  browserStats: Array<{ _id: string; count: number }>;
  topEndpoints: Array<{ _id: string; count: number }>;
  topUsers: Array<{ username: string; count: number }>;
  deviceStats: Array<{ _id: string; count: number }>;
}

interface DashboardData {
  totalUsers: number;
  adminUsers: number;
}

const Traffic: React.FC = () => {
  const [timeRange, setTimeRange] = useState<string>('30');
  const [dateRange, setDateRange] = useState<[dayjs.Dayjs, dayjs.Dayjs]>([
    dayjs().subtract(30, 'day'),
    dayjs()
  ]);
  const [loading, setLoading] = useState<boolean>(true);
  const [analyticsData, setAnalyticsData] = useState<AnalyticsData>({
    totalVisits: 0,
    browserStats: [],
    topEndpoints: [],
    topUsers: [],
    deviceStats: []
  });
  const [dashboardData, setDashboardData] = useState<DashboardData>({
    totalUsers: 0,
    adminUsers: 0
  });

  // Fetch analytics data
  useEffect(() => {
    fetchAnalytics();
  }, []);

  const fetchAnalytics = async () => {
    try {
      setLoading(true);

      // Calculate date range
      const startDate = dateRange[0].toISOString();
      const endDate = dateRange[1].toISOString();

      // Fetch analytics data
      const response = await api.post<ApiResponse<AnalyticsData>>('/admin/analytics/access-stats', {
        startDate,
        endDate
      });

      setAnalyticsData(response.data.data || {
        totalVisits: 0,
        browserStats: [],
        topEndpoints: [],
        topUsers: [],
        deviceStats: []
      });

      // Fetch dashboard data for user counts
      await fetchDashboardData();

    } catch (error) {
      console.error('Error fetching analytics:', error);
    } finally {
      setLoading(false);
    }
  };

  // Fetch dashboard data for user counts
  const fetchDashboardData = async () => {
    try {
      const response = await api.get<ApiResponse<DashboardData>>('/admin/dashboard');
      setDashboardData(response.data.data || {
        totalUsers: 0,
        adminUsers: 0
      });
    } catch (error) {
      console.error('Error fetching dashboard data:', error);
    }
  };

  // Handle time range change
  const handleTimeRangeChange = (value: string) => {
    setTimeRange(value);

    // Update date range based on selected time range
    const end = dayjs();
    const start = dayjs().subtract(parseInt(value), 'day');
    setDateRange([start, end]);
  };

  // Handle date range change
  const handleDateRangeChange = (dates: any) => {
    if (dates && dates.length === 2) {
      setDateRange(dates);
    }
  };

  // // Calculate login rate
  // const loginRate = dashboardData.totalUsers > 0
  //   ? Math.round((analyticsData.totalVisits / dashboardData.totalUsers) * 100)
  //   : 0;

  // Browser stats columns
  const browserColumns: ColumnsType<any> = [
    {
      title: 'Browser',
      dataIndex: '_id',
      key: '_id',
      render: (text) => text || 'Unknown'
    },
    {
      title: 'Visits',
      dataIndex: 'count',
      key: 'count',
      sorter: (a, b) => a.count - b.count,
    },
    {
      title: 'Percentage',
      key: 'percentage',
      render: (_, record) => `${Math.round((record.count / analyticsData.totalVisits) * 100)}%`
    }
  ];

  // Top endpoints columns
  const endpointColumns: ColumnsType<any> = [
    {
      title: 'Endpoint',
      dataIndex: '_id',
      key: '_id',
    },
    {
      title: 'Hits',
      dataIndex: 'count',
      key: 'count',
      sorter: (a, b) => a.count - b.count,
    }
  ];

  return (
    <div>
      <Breadcrumb
        items={[
          { title: 'Home' },
          { title: 'Admin' },
          { title: 'Analytics' },
          { title: 'Traffic' }
        ]}
        style={{ marginBottom: 16 }}
      />

      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
        <Title level={4}>Traffic Analytics</Title>

        <div style={{ display: 'flex', gap: 16 }}>
          <Select
            value={timeRange}
            onChange={handleTimeRangeChange}
            style={{ width: 150 }}
            options={[
              { value: '7', label: 'Last 7 days' },
              { value: '30', label: 'Last 30 days' },
              { value: '90', label: 'Last 90 days' },
              { value: '365', label: 'Last year' },
            ]}
          />

          <RangePicker
            value={dateRange}
            onChange={handleDateRangeChange}
          />

          <Button
            type="primary"
            icon={<ReloadOutlined />}
            onClick={fetchAnalytics}
          >
            Refresh
          </Button>
        </div>
      </div>

      {/* Stats Cards */}
      <Row gutter={[16, 16]}>
        <Col xs={24} sm={12} md={6}>
          <Card loading={loading}>
            <Statistic
              title="Total Visits"
              value={analyticsData.totalVisits}
              prefix={<EyeOutlined />}
              valueStyle={{ color: '#1890ff' }}
            />
          </Card>
        </Col>

        <Col xs={24} sm={12} md={6}>
          <Card loading={loading}>
            <Statistic
              title="Total Users"
              value={dashboardData.totalUsers}
              prefix={<GlobalOutlined />}
              valueStyle={{ color: '#52c41a' }}
            />
          </Card>
        </Col>

        <Col xs={24} sm={12} md={6}>
          <Card loading={loading}>
            <Statistic
              title="Desktop Users"
              value={analyticsData.deviceStats.find(d => d._id === 'desktop')?.count || 0}
              prefix={<DesktopOutlined />}
              valueStyle={{ color: '#722ed1' }}
            />
          </Card>
        </Col>

        <Col xs={24} sm={12} md={6}>
          <Card loading={loading}>
            <Statistic
              title="Mobile Users"
              value={analyticsData.deviceStats.find(d => d._id === 'mobile')?.count || 0}
              prefix={<MobileOutlined />}
              valueStyle={{ color: '#fa8c16' }}
            />
          </Card>
        </Col>
      </Row>

      {/* Browser Stats */}
      <Card title="Browser Statistics" style={{ marginTop: 16 }} loading={loading}>
        <Table
          dataSource={analyticsData.browserStats}
          columns={browserColumns}
          rowKey="_id"
          pagination={false}
        />
      </Card>

      {/* Top Endpoints */}
      <Card title="Top Endpoints" style={{ marginTop: 16 }} loading={loading}>
        <Table
          dataSource={analyticsData.topEndpoints}
          columns={endpointColumns}
          rowKey="_id"
          pagination={false}
        />
      </Card>
    </div>
  );
};

export default Traffic;
