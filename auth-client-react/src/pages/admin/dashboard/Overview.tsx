import React, { useState, useEffect } from 'react';
import { Card, Row, Col, Statistic, List, Avatar, Typography, Button, Breadcrumb, Progress, Divider, Space, Tag, Tooltip, Skeleton } from 'antd';
import {
  UserOutlined, EyeOutlined, CheckCircleOutlined, SettingOutlined,
  AreaChartOutlined, FileTextOutlined, DashboardOutlined, RiseOutlined,
  ClockCircleOutlined, SecurityScanOutlined, AlertOutlined, SyncOutlined,
  TeamOutlined, ApiOutlined, SafetyOutlined, GlobalOutlined
} from '@ant-design/icons';
import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import AdminDashboardLayout from "../../../components/layout/AdminDashboardLayout";

const { Text, Title, Paragraph } = Typography;

interface SystemStatus {
  status: 'healthy' | 'warning' | 'error';
  message: string;
  lastUpdated: string;
  uptime: number;
}

interface Stats {
  totalUsers: number;
  activeSessions: number;
  newUsers: number;
  totalRequests: number;
  successRate: number;
  avgResponseTime: number;
  cpuUsage: number;
  memoryUsage: number;
  diskUsage: number;
}

interface Activity {
  id: string;
  user: string;
  action: string;
  time: string;
  type: 'success' | 'warning' | 'error' | 'info';
  details?: string;
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

const MotionCard = motion(Card);
const MotionRow = motion(Row);

const cardVariants = {
  hidden: { opacity: 0, y: 20 },
  visible: { opacity: 1, y: 0 }
};

const Overview: React.FC = () => {
  const [stats, setStats] = useState<Stats>({
    totalUsers: 0,
    activeSessions: 0,
    newUsers: 0,
    totalRequests: 0,
    successRate: 0,
    avgResponseTime: 0,
    cpuUsage: 0,
    memoryUsage: 0,
    diskUsage: 0
  });

  const [systemStatus, setSystemStatus] = useState<SystemStatus>({
    status: 'healthy',
    message: 'All systems operational',
    lastUpdated: new Date().toISOString(),
    uptime: 99.99
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
            newUsers: 156,
            totalRequests: 25678,
            successRate: 99.8,
            avgResponseTime: 245,
            cpuUsage: 45,
            memoryUsage: 62,
            diskUsage: 38
          });

          setRecentActivity([
            { id: '1', user: 'John Doe', action: 'Đăng nhập thành công', time: '2 phút trước', type: 'success' },
            { id: '2', user: 'System', action: 'Backup tự động hoàn tất', time: '15 phút trước', type: 'info' },
            { id: '3', user: 'Jane Smith', action: 'Cập nhật cấu hình bảo mật', time: '1 giờ trước', type: 'warning' },
            { id: '4', user: 'Security System', action: 'Phát hiện đăng nhập bất thường', time: '2 giờ trước', type: 'error' },
            { id: '5', user: 'Mike Johnson', action: 'Tạo báo cáo hệ thống', time: '3 giờ trước', type: 'success' }
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

  const renderStatusBadge = (status: SystemStatus['status']) => {
    const colors = {
      healthy: '#52c41a',
      warning: '#faad14',
      error: '#ff4d4f'
    };
    
    return (
      <Tag color={colors[status]} icon={<CheckCircleOutlined />}>
        Hệ thống {status === 'healthy' ? 'Hoạt động tốt' : status === 'warning' ? 'Cần chú ý' : 'Có lỗi'}
      </Tag>
    );
  };

  const containerStyle = {
    padding: '24px',
    background: '#f0f2f5',
    minHeight: '100vh'
  };

  const cardStyle = {
    borderRadius: '12px',
    boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
    transition: 'all 0.3s ease'
  };

  const statCardStyle = {
    ...cardStyle,
    background: 'linear-gradient(135deg, #ffffff 0%, #f6f9fc 100%)',
  };

  const quickLinkCardStyle = {
    ...cardStyle,
    height: '100%',
    textAlign: 'center' as const,
    cursor: 'pointer',
    '&:hover': {
      transform: 'translateY(-5px)',
      boxShadow: '0 4px 12px rgba(0,0,0,0.15)'
    }
  };

  return (
    <AdminDashboardLayout title="Tổng quan hệ thống">
      <div style={containerStyle}>
        <motion.div
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5 }}
        >
          <Breadcrumb
            items={[
              { title: <Link to="/">Home</Link> },
              { title: <Link to="/admin/overview">Dashboard</Link> },
              { title: 'Overview' }
            ]}
            style={{ marginBottom: 24 }}
          />
        </motion.div>

        {/* System Status Card */}
        <MotionCard
          className="overview-card"
          loading={loading}
          style={cardStyle}
          variants={cardVariants}
          initial="hidden"
          animate="visible"
          transition={{ duration: 0.5 }}
        >
          <Row gutter={[16, 16]} align="middle">
            <Col xs={24} md={16}>
              <Space direction="vertical" size="small">
                <Title level={4}>
                  <DashboardOutlined /> Trạng thái hệ thống
                </Title>
                <Space>
                  {renderStatusBadge(systemStatus.status)}
                  <Tag icon={<ClockCircleOutlined />}>
                    Uptime: {systemStatus.uptime}%
                  </Tag>
                  <Tag icon={<SyncOutlined spin />}>
                    Cập nhật: {new Date(systemStatus.lastUpdated).toLocaleTimeString()}
                  </Tag>
                </Space>
                <Paragraph type="secondary">
                  {systemStatus.message}
                </Paragraph>
              </Space>
            </Col>
            <Col xs={24} md={8}>
              <Row gutter={[16, 16]}>
                <Col span={8}>
                  <Statistic
                    title="CPU"
                    value={stats.cpuUsage}
                    suffix="%"
                    prefix={<ApiOutlined />}
                  />
                  <Progress percent={stats.cpuUsage} size="small" status={stats.cpuUsage > 80 ? 'exception' : 'normal'} />
                </Col>
                <Col span={8}>
                  <Statistic
                    title="RAM"
                    value={stats.memoryUsage}
                    suffix="%"
                    prefix={<SafetyOutlined />}
                  />
                  <Progress percent={stats.memoryUsage} size="small" status={stats.memoryUsage > 80 ? 'exception' : 'normal'} />
                </Col>
                <Col span={8}>
                  <Statistic
                    title="Disk"
                    value={stats.diskUsage}
                    suffix="%"
                    prefix={<GlobalOutlined />}
                  />
                  <Progress percent={stats.diskUsage} size="small" status={stats.diskUsage > 80 ? 'exception' : 'normal'} />
                </Col>
              </Row>
            </Col>
          </Row>
        </MotionCard>

        {/* Statistics Cards */}
        <MotionRow
          gutter={[24, 24]}
          style={{ marginTop: 24 }}
          variants={cardVariants}
          initial="hidden"
          animate="visible"
          transition={{ staggerChildren: 0.1 }}
        >
          {[
            {
              title: "Tổng người dùng",
              value: stats.totalUsers,
              icon: <TeamOutlined />,
              color: "#1890ff",
              suffix: <Tooltip title="Tăng 12% so với tháng trước">
                <Tag color="green"><RiseOutlined /> 12%</Tag>
              </Tooltip>
            },
            {
              title: "Đang hoạt động",
              value: stats.activeSessions,
              icon: <EyeOutlined />,
              color: "#52c41a",
              suffix: <Tag color="blue">Online</Tag>
            },
            {
              title: "Tỷ lệ thành công",
              value: stats.successRate,
              icon: <CheckCircleOutlined />,
              color: "#faad14",
              suffix: "%"
            },
            {
              title: "Thời gian phản hồi",
              value: stats.avgResponseTime,
              icon: <ClockCircleOutlined />,
              color: "#722ed1",
              suffix: "ms"
            }
          ].map((stat, index) => (
            <Col xs={24} sm={12} lg={6} key={index}>
              <MotionCard
                hoverable
                style={statCardStyle}
                variants={cardVariants}
              >
                <Statistic
                  title={<Space><Text style={{ color: stat.color }}>{stat.icon}</Text>{stat.title}</Space>}
                  value={stat.value}
                  suffix={stat.suffix}
                  valueStyle={{ color: stat.color }}
                />
              </MotionCard>
            </Col>
          ))}
        </MotionRow>

        {/* Activity and Security Section */}
        <MotionRow gutter={[24, 24]} style={{ marginTop: 24 }}>
          <Col xs={24} lg={16}>
            <MotionCard
              title={
                <Space>
                  <AlertOutlined style={{ color: '#1890ff' }} />
                  <span style={{ fontWeight: 600 }}>Hoạt động gần đây</span>
                </Space>
              }
              extra={<Button type="link">Xem tất cả</Button>}
              style={cardStyle}
              loading={loading}
              variants={cardVariants}
            >
              <List
                itemLayout="horizontal"
                dataSource={recentActivity}
                renderItem={(item) => (
                  <motion.div
                    initial={{ opacity: 0, x: -20 }}
                    animate={{ opacity: 1, x: 0 }}
                    transition={{ duration: 0.3 }}
                  >
                    <List.Item>
                      <List.Item.Meta
                        avatar={
                          <Avatar style={{ backgroundColor: item.type === 'success' ? '#52c41a' : 
                                                          item.type === 'warning' ? '#faad14' : 
                                                          item.type === 'error' ? '#ff4d4f' : '#1890ff' }}>
                            {item.user[0].toUpperCase()}
                          </Avatar>
                        }
                        title={
                          <Space>
                            <Text strong>{item.user}</Text>
                            <Tag color={
                              item.type === 'success' ? 'success' :
                              item.type === 'warning' ? 'warning' :
                              item.type === 'error' ? 'error' : 'default'
                            }>
                              {item.type.toUpperCase()}
                            </Tag>
                          </Space>
                        }
                        description={
                          <Space direction="vertical" size={0}>
                            <Text>{item.action}</Text>
                            <Text type="secondary" style={{ fontSize: '12px' }}>{item.time}</Text>
                          </Space>
                        }
                      />
                    </List.Item>
                  </motion.div>
                )}
              />
            </MotionCard>
          </Col>
          
          <Col xs={24} lg={8}>
            <MotionCard
              title={
                <Space>
                  <SecurityScanOutlined style={{ color: '#52c41a' }} />
                  <span style={{ fontWeight: 600 }}>Bảo mật hệ thống</span>
                </Space>
              }
              style={cardStyle}
              loading={loading}
              variants={cardVariants}
            >
              <Space direction="vertical" style={{ width: '100%' }}>
                <div>
                  <Text>Cường độ bảo mật</Text>
                  <Progress percent={85} status="active" />
                </div>
                <div>
                  <Text>Số lần đăng nhập thất bại (24h)</Text>
                  <Progress percent={15} status="exception" />
                </div>
                <div>
                  <Text>Chứng chỉ SSL</Text>
                  <Progress percent={100} status="success" />
                </div>
                <Button type="primary" icon={<SafetyOutlined />} block>
                  Kiểm tra bảo mật
                </Button>
              </Space>
            </MotionCard>
          </Col>
        </MotionRow>

        {/* Quick Links */}
        <MotionCard
          title={
            <Space>
              <SettingOutlined style={{ color: '#1890ff' }} />
              <span style={{ fontWeight: 600 }}>Quick Links</span>
            </Space>
          }
          style={{ ...cardStyle, marginTop: 24 }}
          variants={cardVariants}
        >
          <Row gutter={[24, 24]}>
            {[
              {
                icon: <UserOutlined />,
                title: "Manage Users",
                description: "View and manage user accounts",
                color: "#e6f7ff",
                iconColor: "#1890ff",
                link: "/admin/users"
              },
              {
                icon: <AreaChartOutlined />,
                title: "View Analytics",
                description: "Track system performance",
                color: "#f6ffed",
                iconColor: "#52c41a",
                link: "/admin/analytics"
              },
              {
                icon: <SettingOutlined />,
                title: "System Settings",
                description: "Configure system parameters",
                color: "#fff7e6",
                iconColor: "#fa8c16",
                link: "/admin/settings"
              },
              {
                icon: <FileTextOutlined />,
                title: "View Reports",
                description: "Access system reports",
                color: "#f9f0ff",
                iconColor: "#722ed1",
                link: "/admin/analytics/reports"
              }
            ].map((item, index) => (
              <Col xs={24} sm={12} md={6} key={index}>
                <Link to={item.link}>
                  <MotionCard
                    hoverable
                    style={quickLinkCardStyle}
                    variants={cardVariants}
                    whileHover={{ y: -5 }}
                  >
                    <div style={{
                      backgroundColor: item.color,
                      width: '60px',
                      height: '60px',
                      borderRadius: '50%',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      margin: '0 auto 16px'
                    }}>
                      {React.cloneElement(item.icon as React.ReactElement, {
                        style: { fontSize: '24px', color: item.iconColor }
                      })}
                    </div>
                    <Title level={5}>{item.title}</Title>
                    <Text type="secondary">{item.description}</Text>
                  </MotionCard>
                </Link>
              </Col>
            ))}
          </Row>
        </MotionCard>
      </div>
    </AdminDashboardLayout>
  );
};

export default Overview;
