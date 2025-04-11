import React, { useState, useEffect } from 'react';
import { Card, Row, Col, Statistic, Typography, Table, Spin, DatePicker, Button } from 'antd';
import {
  UserOutlined,
  TeamOutlined,
  LoginOutlined,
  SafetyOutlined,
  ReloadOutlined
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import adminService from '../../../services/admin.service';
import dayjs from 'dayjs';

const { Title } = Typography;
const { RangePicker } = DatePicker;

interface StatisticsData {
  totalUsers: number;
  activeUsers: number;
  newUsers: number;
  loginAttempts: number;
  successfulLogins: number;
  failedLogins: number;
}

interface LoginActivityData {
  id: string;
  username: string;
  ip: string;
  status: 'success' | 'failed';
  reason?: string;
  timestamp: string;
  userAgent: string;
}

const columns: ColumnsType<LoginActivityData> = [
  {
    title: 'Username',
    dataIndex: 'username',
    key: 'username',
  },
  {
    title: 'IP Address',
    dataIndex: 'ip',
    key: 'ip',
  },
  {
    title: 'Status',
    dataIndex: 'status',
    key: 'status',
    render: (status: string) => (
      <span style={{
        color: status === 'success' ? '#52c41a' : '#f5222d',
        fontWeight: 'bold'
      }}>
        {status === 'success' ? 'Success' : 'Failed'}
      </span>
    ),
  },
  {
    title: 'Reason',
    dataIndex: 'reason',
    key: 'reason',
    render: (reason: string) => reason || '-',
  },
  {
    title: 'Time',
    dataIndex: 'timestamp',
    key: 'timestamp',
    render: (timestamp: string) => new Date(timestamp).toLocaleString(),
  },
];

const Statistics: React.FC = () => {
  const [statistics, setStatistics] = useState<StatisticsData | null>(null);
  const [loginActivity, setLoginActivity] = useState<LoginActivityData[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [dateRange, setDateRange] = useState<[dayjs.Dayjs, dayjs.Dayjs]>([
    dayjs().subtract(7, 'day'), // 7 days ago
    dayjs()
  ]);

  const fetchData = async () => {
    try {
      setLoading(true);

      // In a real application, you would pass the date range to the API
      const startDate = dateRange[0].toDate().toISOString();
      const endDate = dateRange[1].toDate().toISOString();

      // Fetch statistics data
      const statsData = await adminService.getStatistics(startDate, endDate);
      setStatistics(statsData);

      // Fetch login activity data
      const activityData = await adminService.getLoginActivity(startDate, endDate);
      setLoginActivity(activityData);
    } catch (error) {
      console.error('Failed to fetch statistics data:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleDateRangeChange = (dates: any) => {
    if (dates && dates.length === 2) {
      setDateRange([dates[0], dates[1]]);
    }
  };

  const handleRefresh = () => {
    fetchData();
  };

  if (loading && !statistics) {
    return (
      <div className="flex h-full items-center justify-center">
        <Spin size="large" />
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center mb-6">
        <Title level={2} className="mb-0">Authentication Statistics</Title>

        <div className="flex flex-col sm:flex-row gap-4 mt-4 sm:mt-0">
          <RangePicker
            onChange={handleDateRangeChange}
            defaultValue={dateRange}
          />
          <Button
            type="primary"
            icon={<ReloadOutlined />}
            onClick={handleRefresh}
          >
            Refresh
          </Button>
        </div>
      </div>

      <Row gutter={[16, 16]}>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Total Users"
              value={statistics?.totalUsers || 0}
              prefix={<TeamOutlined />}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Active Users"
              value={statistics?.activeUsers || 0}
              prefix={<UserOutlined />}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="New Users"
              value={statistics?.newUsers || 0}
              prefix={<UserOutlined />}
              suffix="this period"
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Login Success Rate"
              value={statistics ?
                Math.round((statistics.successfulLogins /
                  (statistics.successfulLogins + statistics.failedLogins || 1)) * 100) : 0
              }
              prefix={<SafetyOutlined />}
              suffix="%"
            />
          </Card>
        </Col>
      </Row>

      <Row gutter={[16, 16]}>
        <Col xs={24} sm={8}>
          <Card>
            <Statistic
              title="Login Attempts"
              value={statistics?.loginAttempts || 0}
              prefix={<LoginOutlined />}
            />
          </Card>
        </Col>
        <Col xs={24} sm={8}>
          <Card>
            <Statistic
              title="Successful Logins"
              value={statistics?.successfulLogins || 0}
              valueStyle={{ color: '#52c41a' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={8}>
          <Card>
            <Statistic
              title="Failed Logins"
              value={statistics?.failedLogins || 0}
              valueStyle={{ color: '#f5222d' }}
            />
          </Card>
        </Col>
      </Row>

      <Card title="Recent Login Activity">
        <Table
          columns={columns}
          dataSource={loginActivity}
          rowKey="id"
          loading={loading}
          pagination={{ pageSize: 10 }}
        />
      </Card>
    </div>
  );
};

export default Statistics;
