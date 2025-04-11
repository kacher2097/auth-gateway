import React, { useState, useEffect } from 'react';
import { Card, Row, Col, Statistic, List, Avatar, Typography, Button, Breadcrumb, Progress, Divider, Space, Tag, Tooltip, Skeleton } from 'antd';
import { UserOutlined, EyeOutlined, CheckCircleOutlined, SettingOutlined, AreaChartOutlined, FileTextOutlined, DashboardOutlined, RiseOutlined, ClockCircleOutlined } from '@ant-design/icons';
import { Link } from 'react-router-dom';
import AdminDashboardLayout from "../../../components/layout/AdminDashboardLayout";

const { Text, Title, Paragraph } = Typography;

interface Activity {
  id: string;
  user: string;
  action: string;
  time: string;
}

// Helper function to generate consistent avatar colors
const getAvatarColor = (name: string) => {
  const colors = ['#1890ff', '#52c41a', '#fa8c16', '#722ed1', '#eb2f96', '#faad14'];
  let hash = 0;
  for (let i = 0; i < name.length; i++) {
    hash = name.charCodeAt(i) + ((hash << 5) - hash);
  }
  hash = Math.abs(hash);
  return colors[hash % colors.length];
};

const Overview: React.FC = () => {
  const [stats, setStats] = useState({
    totalUsers: 0,
    activeSessions: 0,
    systemStatus: 'Healthy'
  });

  const [recentActivity, setRecentActivity] = useState<Activity[]>([]);
  const [loading, setLoading] = useState(true);

  // Fetch dashboard data
  useEffect(() => {
    // Simulate API call
    const fetchData = async () => {
      try {
        setLoading(true);

        // In a real app, this would be an API call
        // For demo purposes, we'll use mock data
        setTimeout(() => {
          setStats({
            totalUsers: 1248,
            activeSessions: 87,
            systemStatus: 'Healthy'
          });

          setRecentActivity([
            { id: '1', user: 'John Doe', action: 'Created a new user account', time: '5 minutes ago' },
            { id: '2', user: 'Jane Smith', action: 'Updated system settings', time: '1 hour ago' },
            { id: '3', user: 'Mike Johnson', action: 'Generated monthly report', time: '3 hours ago' },
            { id: '4', user: 'Sarah Williams', action: 'Logged in from a new device', time: '5 hours ago' }
          ]);

          setLoading(false);
        }, 1000);
      } catch (error) {
        console.error('Error fetching dashboard data:', error);
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  return (
    <AdminDashboardLayout title="Dashboard Overview">
      <Breadcrumb
        items={[
          { title: <Link to="/">Home</Link> },
          { title: <Link to="/admin/overview">Dashboard</Link> },
          { title: 'Overview' }
        ]}
        style={{ marginBottom: 16 }}
      />

      {/* Welcome Section */}
      <Card style={{ marginBottom: 16, borderRadius: '8px' }}>
        <Skeleton loading={loading} active paragraph={{ rows: 2 }}>
          <div style={{ display: 'flex', alignItems: 'center', marginBottom: 16 }}>
            <DashboardOutlined style={{ fontSize: 24, color: '#1890ff', marginRight: 12 }} />
            <Title level={4} style={{ margin: 0 }}>Welcome to the Admin Dashboard</Title>
          </div>
          <Paragraph>
            This dashboard provides an overview of your authentication system's performance and user statistics.
            Use the quick links below to navigate to different sections of the admin panel.
          </Paragraph>
          <div style={{ marginTop: 16 }}>
            <Tag color="blue">Last updated: {new Date().toLocaleString()}</Tag>
            {stats.systemStatus === 'Healthy' && (
              <Tag color="green" icon={<CheckCircleOutlined />}>System Status: {stats.systemStatus}</Tag>
            )}
          </div>
        </Skeleton>
      </Card>

      {/* Stats Cards */}
      <Row gutter={[16, 16]}>
        <Col xs={24} md={8}>
          <Card
            loading={loading}
            hoverable
            style={{ borderRadius: '8px', height: '100%' }}
            bodyStyle={{ padding: '24px' }}
          >
            <Statistic
              title={<span style={{ fontSize: '16px', fontWeight: 500 }}>Total Users</span>}
              value={stats.totalUsers}
              prefix={<UserOutlined style={{ backgroundColor: '#e6f7ff', padding: '8px', borderRadius: '8px', color: '#1890ff' }} />}
              valueStyle={{ color: '#1890ff', fontSize: '28px' }}
            />
            <div style={{ marginTop: '16px' }}>
              <Progress percent={75} showInfo={false} strokeColor="#1890ff" />
              <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '8px' }}>
                <Text type="secondary">75% of target</Text>
                <Text type="secondary" style={{ color: '#1890ff' }}>
                  <RiseOutlined /> 12% increase
                </Text>
              </div>
            </div>
          </Card>
        </Col>

        <Col xs={24} md={8}>
          <Card
            loading={loading}
            hoverable
            style={{ borderRadius: '8px', height: '100%' }}
            bodyStyle={{ padding: '24px' }}
          >
            <Statistic
              title={<span style={{ fontSize: '16px', fontWeight: 500 }}>Active Sessions</span>}
              value={stats.activeSessions}
              prefix={<EyeOutlined style={{ backgroundColor: '#f6ffed', padding: '8px', borderRadius: '8px', color: '#52c41a' }} />}
              valueStyle={{ color: '#52c41a', fontSize: '28px' }}
            />
            <div style={{ marginTop: '16px' }}>
              <Progress percent={60} showInfo={false} strokeColor="#52c41a" />
              <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '8px' }}>
                <Text type="secondary">Current active users</Text>
                <Text type="secondary" style={{ color: '#52c41a' }}>
                  <ClockCircleOutlined /> Updated just now
                </Text>
              </div>
            </div>
          </Card>
        </Col>

        <Col xs={24} md={8}>
          <Card
            loading={loading}
            hoverable
            style={{ borderRadius: '8px', height: '100%' }}
            bodyStyle={{ padding: '24px' }}
          >
            <Statistic
              title={<span style={{ fontSize: '16px', fontWeight: 500 }}>System Status</span>}
              value={stats.systemStatus}
              prefix={<CheckCircleOutlined style={{ backgroundColor: '#f9f0ff', padding: '8px', borderRadius: '8px', color: '#722ed1' }} />}
              valueStyle={{ color: '#722ed1', fontSize: '28px' }}
            />
            <div style={{ marginTop: '16px' }}>
              <Progress percent={100} showInfo={false} strokeColor="#722ed1" />
              <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '8px' }}>
                <Text type="secondary">All systems operational</Text>
                <Tooltip title="System is running smoothly">
                  <Tag color="purple">Optimal</Tag>
                </Tooltip>
              </div>
            </div>
          </Card>
        </Col>
      </Row>

      {/* Recent Activity */}
      <Card
        title={<div style={{ display: 'flex', alignItems: 'center' }}>
          <ClockCircleOutlined style={{ marginRight: 8, color: '#1890ff' }} />
          <span>Recent Activity</span>
        </div>}
        style={{ marginTop: 16, borderRadius: '8px' }}
        loading={loading}
        extra={<Button type="link">View All</Button>}
      >
        <List
          itemLayout="horizontal"
          dataSource={recentActivity}
          renderItem={(item) => (
            <List.Item
              style={{ padding: '12px 0' }}
              actions={[
                <Tooltip title={`Occurred ${item.time}`}>
                  <Tag color="blue">{item.time}</Tag>
                </Tooltip>
              ]}
            >
              <List.Item.Meta
                avatar={
                  <Avatar
                    style={{
                      backgroundColor: getAvatarColor(item.user),
                      boxShadow: '0 2px 8px rgba(0,0,0,0.1)'
                    }}
                  >
                    {item.user.charAt(0).toUpperCase()}
                  </Avatar>
                }
                title={<Text strong>{item.user}</Text>}
                description={
                  <Text style={{ fontSize: '14px' }}>{item.action}</Text>
                }
              />
            </List.Item>
          )}
        />
      </Card>

      {/* Quick Links */}
      <Card
        title={<div style={{ display: 'flex', alignItems: 'center' }}>
          <SettingOutlined style={{ marginRight: 8, color: '#1890ff' }} />
          <span>Quick Links</span>
        </div>}
        style={{ marginTop: 16, borderRadius: '8px' }}
      >
        <Row gutter={[16, 16]}>
          <Col xs={24} sm={12} md={6}>
            <Link to="/admin/users" style={{ display: 'block' }}>
              <Card
                hoverable
                style={{
                  textAlign: 'center',
                  height: '100%',
                  borderRadius: '8px',
                  border: '1px solid #f0f0f0'
                }}
                bodyStyle={{ padding: '24px 16px' }}
              >
                <div style={{
                  backgroundColor: '#e6f7ff',
                  width: '60px',
                  height: '60px',
                  borderRadius: '50%',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  margin: '0 auto 16px'
                }}>
                  <UserOutlined style={{ fontSize: '24px', color: '#1890ff' }} />
                </div>
                <Title level={5}>Manage Users</Title>
                <Text type="secondary">View and manage user accounts</Text>
              </Card>
            </Link>
          </Col>

          <Col xs={24} sm={12} md={6}>
            <Link to="/admin/analytics" style={{ display: 'block' }}>
              <Card
                hoverable
                style={{
                  textAlign: 'center',
                  height: '100%',
                  borderRadius: '8px',
                  border: '1px solid #f0f0f0'
                }}
                bodyStyle={{ padding: '24px 16px' }}
              >
                <div style={{
                  backgroundColor: '#f6ffed',
                  width: '60px',
                  height: '60px',
                  borderRadius: '50%',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  margin: '0 auto 16px'
                }}>
                  <AreaChartOutlined style={{ fontSize: '24px', color: '#52c41a' }} />
                </div>
                <Title level={5}>View Analytics</Title>
                <Text type="secondary">Track system performance</Text>
              </Card>
            </Link>
          </Col>

          <Col xs={24} sm={12} md={6}>
            <Link to="/admin/settings" style={{ display: 'block' }}>
              <Card
                hoverable
                style={{
                  textAlign: 'center',
                  height: '100%',
                  borderRadius: '8px',
                  border: '1px solid #f0f0f0'
                }}
                bodyStyle={{ padding: '24px 16px' }}
              >
                <div style={{
                  backgroundColor: '#fff7e6',
                  width: '60px',
                  height: '60px',
                  borderRadius: '50%',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  margin: '0 auto 16px'
                }}>
                  <SettingOutlined style={{ fontSize: '24px', color: '#fa8c16' }} />
                </div>
                <Title level={5}>System Settings</Title>
                <Text type="secondary">Configure system parameters</Text>
              </Card>
            </Link>
          </Col>

          <Col xs={24} sm={12} md={6}>
            <Link to="/admin/analytics/reports" style={{ display: 'block' }}>
              <Card
                hoverable
                style={{
                  textAlign: 'center',
                  height: '100%',
                  borderRadius: '8px',
                  border: '1px solid #f0f0f0'
                }}
                bodyStyle={{ padding: '24px 16px' }}
              >
                <div style={{
                  backgroundColor: '#f9f0ff',
                  width: '60px',
                  height: '60px',
                  borderRadius: '50%',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  margin: '0 auto 16px'
                }}>
                  <FileTextOutlined style={{ fontSize: '24px', color: '#722ed1' }} />
                </div>
                <Title level={5}>View Reports</Title>
                <Text type="secondary">Access system reports</Text>
              </Card>
            </Link>
          </Col>
        </Row>
      </Card>
    </AdminDashboardLayout>
  );
};

export default Overview;
