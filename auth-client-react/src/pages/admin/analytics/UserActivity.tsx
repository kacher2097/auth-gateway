import React, { useState, useEffect } from 'react';
import { 
  Card, 
  Row, 
  Col, 
  Typography, 
  DatePicker, 
  Button, 
  Spin, 
  Table,
  Statistic,
  Tabs
} from 'antd';
import { 
  ReloadOutlined, 
  UserOutlined, 
  UserAddOutlined,
  UserSwitchOutlined
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import adminService from '../../../services/admin.service';

const { Title, Text } = Typography;
const { RangePicker } = DatePicker;
const { TabPane } = Tabs;

interface UserActivityData {
  date: string;
  activeUsers: number;
  newUsers: number;
  returningUsers: number;
}

interface UserLoginData {
  id: string;
  username: string;
  fullName: string;
  lastLogin: string;
  loginCount: number;
  status: string;
}

const UserActivity: React.FC = () => {
  const [activityData, setActivityData] = useState<UserActivityData[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [dateRange, setDateRange] = useState<[Date, Date]>([
    new Date(Date.now() - 30 * 24 * 60 * 60 * 1000), // 30 days ago
    new Date()
  ]);
  const [userLogins, setUserLogins] = useState<UserLoginData[]>([]);
  const [userLoginsLoading, setUserLoginsLoading] = useState<boolean>(true);

  const fetchData = async () => {
    try {
      setLoading(true);
      
      // In a real application, you would pass the date range to the API
      const startDate = dateRange[0].toISOString();
      const endDate = dateRange[1].toISOString();
      
      // Fetch user activity data
      const data = await adminService.getUserActivityData(startDate, endDate);
      setActivityData(data);
      
      // Simulate fetching user login data
      // In a real application, this would be a separate API call
      setUserLoginsLoading(true);
      setTimeout(() => {
        // Mock data for demonstration
        const mockUserLogins: UserLoginData[] = [
          {
            id: '1',
            username: 'johndoe',
            fullName: 'John Doe',
            lastLogin: new Date().toISOString(),
            loginCount: 42,
            status: 'active',
          },
          {
            id: '2',
            username: 'janedoe',
            fullName: 'Jane Doe',
            lastLogin: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000).toISOString(),
            loginCount: 38,
            status: 'active',
          },
          {
            id: '3',
            username: 'bobsmith',
            fullName: 'Bob Smith',
            lastLogin: new Date(Date.now() - 5 * 24 * 60 * 60 * 1000).toISOString(),
            loginCount: 27,
            status: 'inactive',
          },
        ];
        setUserLogins(mockUserLogins);
        setUserLoginsLoading(false);
      }, 1000);
    } catch (error) {
      console.error('Failed to fetch user activity data:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleDateRangeChange = (dates: any) => {
    if (dates && dates.length === 2) {
      setDateRange([dates[0].toDate(), dates[1].toDate()]);
    }
  };

  const handleRefresh = () => {
    fetchData();
  };

  // Calculate summary statistics
  const calculateStats = () => {
    if (!activityData.length) return { totalActive: 0, totalNew: 0, totalReturning: 0 };
    
    return activityData.reduce((acc, curr) => {
      return {
        totalActive: acc.totalActive + curr.activeUsers,
        totalNew: acc.totalNew + curr.newUsers,
        totalReturning: acc.totalReturning + curr.returningUsers,
      };
    }, { totalActive: 0, totalNew: 0, totalReturning: 0 });
  };

  const stats = calculateStats();

  // Table columns for user logins
  const columns: ColumnsType<UserLoginData> = [
    {
      title: 'Username',
      dataIndex: 'username',
      key: 'username',
    },
    {
      title: 'Full Name',
      dataIndex: 'fullName',
      key: 'fullName',
    },
    {
      title: 'Last Login',
      dataIndex: 'lastLogin',
      key: 'lastLogin',
      render: (date: string) => new Date(date).toLocaleString(),
      sorter: (a, b) => new Date(b.lastLogin).getTime() - new Date(a.lastLogin).getTime(),
    },
    {
      title: 'Login Count',
      dataIndex: 'loginCount',
      key: 'loginCount',
      sorter: (a, b) => a.loginCount - b.loginCount,
    },
    {
      title: 'Status',
      dataIndex: 'status',
      key: 'status',
      render: (status: string) => (
        <span style={{ 
          color: status === 'active' ? '#52c41a' : '#f5222d',
          fontWeight: 'bold'
        }}>
          {status.charAt(0).toUpperCase() + status.slice(1)}
        </span>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center mb-6">
        <Title level={2} className="mb-0">User Activity</Title>
        
        <div className="flex flex-col sm:flex-row gap-4 mt-4 sm:mt-0">
          <RangePicker 
            onChange={handleDateRangeChange}
            defaultValue={[
              // @ts-ignore - The DatePicker expects Moment objects but we're using native Date
              dateRange[0],
              dateRange[1]
            ]}
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
        <Col xs={24} sm={8}>
          <Card>
            <Statistic
              title="Total Active Users"
              value={stats.totalActive}
              prefix={<UserOutlined />}
            />
          </Card>
        </Col>
        <Col xs={24} sm={8}>
          <Card>
            <Statistic
              title="New Users"
              value={stats.totalNew}
              prefix={<UserAddOutlined />}
            />
          </Card>
        </Col>
        <Col xs={24} sm={8}>
          <Card>
            <Statistic
              title="Returning Users"
              value={stats.totalReturning}
              prefix={<UserSwitchOutlined />}
            />
          </Card>
        </Col>
      </Row>

      <Card>
        <Tabs defaultActiveKey="activity">
          <TabPane tab="User Activity Over Time" key="activity">
            {loading ? (
              <div className="flex justify-center py-8">
                <Spin size="large" />
              </div>
            ) : activityData.length === 0 ? (
              <div className="text-center py-8">
                <Text type="secondary">No activity data available for the selected period.</Text>
              </div>
            ) : (
              <div style={{ height: '400px' }}>
                {/* In a real application, you would render a chart here */}
                <div className="flex justify-center items-center h-full">
                  <Text>Chart would be displayed here showing user activity over time</Text>
                </div>
              </div>
            )}
          </TabPane>
          <TabPane tab="User Logins" key="logins">
            <Table 
              columns={columns} 
              dataSource={userLogins} 
              rowKey="id"
              loading={userLoginsLoading}
              pagination={{ pageSize: 10 }}
            />
          </TabPane>
        </Tabs>
      </Card>
    </div>
  );
};

export default UserActivity;
