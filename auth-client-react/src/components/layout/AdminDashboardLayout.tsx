import React from 'react';
import { Layout, Row, Col, Card, Statistic, Space, Button, Typography } from 'antd';
import { ArrowUpOutlined, ArrowDownOutlined, UserOutlined, TeamOutlined, LineChartOutlined, ClockCircleOutlined } from '@ant-design/icons';
import AdminLayout from './AdminLayout';
import { useNavigate } from 'react-router-dom';

const { Title } = Typography;

interface AdminDashboardLayoutProps {
    children: React.ReactNode;
    title?: string;
}

const AdminDashboardLayout: React.FC<AdminDashboardLayoutProps> = ({ children, title = 'Dashboard' }) => {
    const navigate = useNavigate();

    // Mock data - trong thực tế sẽ lấy từ API
    const stats = {
        totalUsers: 1234,
        activeUsers: 892,
        newUsers: 45,
        userGrowth: 12.3,
        totalSessions: 5678,
        avgSessionTime: '12:30',
        conversionRate: 3.2,
        bounceRate: 42.5
    };

    const QuickActions = () => (
        <Space size="middle" style={{ marginBottom: 24 }}>
            <Button type="primary" icon={<UserOutlined />} onClick={() => navigate('/admin/users/new')}>
                Thêm người dùng
            </Button>
            <Button type="default" icon={<LineChartOutlined />} onClick={() => navigate('/admin/analytics/reports/new')}>
                Tạo báo cáo
            </Button>
        </Space>
    );

    const StatCards = () => (
        <Row gutter={[16, 16]}>
            <Col xs={24} sm={12} lg={6}>
                <Card bordered={false} hoverable>
                    <Statistic
                        title="Tổng số người dùng"
                        value={stats.totalUsers}
                        prefix={<TeamOutlined />}
                        valueStyle={{ color: '#1890ff' }}
                    />
                    <div style={{ marginTop: 8, fontSize: 13, color: '#52c41a' }}>
                        <ArrowUpOutlined /> {stats.userGrowth}% so với tháng trước
                    </div>
                </Card>
            </Col>
            <Col xs={24} sm={12} lg={6}>
                <Card bordered={false} hoverable>
                    <Statistic
                        title="Người dùng đang hoạt động"
                        value={stats.activeUsers}
                        prefix={<UserOutlined />}
                        valueStyle={{ color: '#52c41a' }}
                    />
                    <div style={{ marginTop: 8, fontSize: 13 }}>
                        {((stats.activeUsers / stats.totalUsers) * 100).toFixed(1)}% tổng số người dùng
                    </div>
                </Card>
            </Col>
            <Col xs={24} sm={12} lg={6}>
                <Card bordered={false} hoverable>
                    <Statistic
                        title="Người dùng mới (30 ngày)"
                        value={stats.newUsers}
                        prefix={<UserOutlined />}
                        valueStyle={{ color: '#722ed1' }}
                    />
                    <div style={{ marginTop: 8, fontSize: 13, color: '#52c41a' }}>
                        <ArrowUpOutlined /> {stats.userGrowth}% tăng trưởng
                    </div>
                </Card>
            </Col>
            <Col xs={24} sm={12} lg={6}>
                <Card bordered={false} hoverable>
                    <Statistic
                        title="Thời gian trung bình"
                        value={stats.avgSessionTime}
                        prefix={<ClockCircleOutlined />}
                        valueStyle={{ color: '#fa8c16' }}
                    />
                    <div style={{ marginTop: 8, fontSize: 13 }}>
                        Thời gian sử dụng trung bình
                    </div>
                </Card>
            </Col>
        </Row>
    );

    const PerformanceMetrics = () => (
        <Row gutter={[16, 16]} style={{ marginTop: 24 }}>
            <Col xs={24} sm={12} lg={6}>
                <Card bordered={false} hoverable>
                    <Statistic
                        title="Tổng phiên truy cập"
                        value={stats.totalSessions}
                        valueStyle={{ color: '#1890ff' }}
                    />
                </Card>
            </Col>
            <Col xs={24} sm={12} lg={6}>
                <Card bordered={false} hoverable>
                    <Statistic
                        title="Tỷ lệ chuyển đổi"
                        value={stats.conversionRate}
                        suffix="%"
                        precision={1}
                        valueStyle={{ color: '#52c41a' }}
                    />
                </Card>
            </Col>
            <Col xs={24} sm={12} lg={6}>
                <Card bordered={false} hoverable>
                    <Statistic
                        title="Tỷ lệ thoát"
                        value={stats.bounceRate}
                        suffix="%"
                        precision={1}
                        valueStyle={stats.bounceRate > 50 ? { color: '#ff4d4f' } : { color: '#faad14' }}
                    />
                </Card>
            </Col>
            <Col xs={24} sm={12} lg={6}>
                <Card bordered={false} hoverable>
                    <Statistic
                        title="Đang hoạt động"
                        value={stats.activeUsers}
                        suffix="người dùng"
                        valueStyle={{ color: '#52c41a' }}
                    />
                </Card>
            </Col>
        </Row>
    );

    return (
        <AdminLayout title={title}>
            <div style={{ padding: '24px' }}>
                <div style={{ marginBottom: 24 }}>
                    <Title level={2}>{title}</Title>
                    <QuickActions />
                </div>
                <StatCards />
                <PerformanceMetrics />
                <div style={{ marginTop: 24 }}>
                    {children}
                </div>
            </div>
        </AdminLayout>
    );
};

export default AdminDashboardLayout;