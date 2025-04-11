import React, { useState, useEffect } from 'react';
import { 
  Card, 
  Table, 
  Button, 
  Typography, 
  Space, 
  Popconfirm, 
  message,
  Tag,
  Tooltip,
} from 'antd';
import { 
  PlusOutlined, 
  DeleteOutlined, 
  ExclamationCircleOutlined,
  PlayCircleOutlined,
  DownloadOutlined,
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { useNavigate } from 'react-router-dom';
import adminService from '../../../services/admin.service';

const { Title, Text } = Typography;

interface ReportData {
  id: string;
  name: string;
  description: string;
  type: string;
  createdAt: string;
  createdBy: string;
  lastRun: string | null;
  schedule: string | null;
}

const Reports: React.FC = () => {
  const [reports, setReports] = useState<ReportData[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [runningReports, setRunningReports] = useState<Set<string>>(new Set());
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 10,
    total: 0,
  });
  const navigate = useNavigate();

  // Fetch reports on component mount and pagination change
  useEffect(() => {
    fetchReports();
  }, [pagination.current, pagination.pageSize]);

  const fetchReports = async () => {
    try {
      setLoading(true);
      const data = await adminService.getReports(pagination.current, pagination.pageSize);
      setReports(data.reports);
      setPagination({
        ...pagination,
        total: data.total,
      });
    } catch (error) {
      console.error('Failed to fetch reports:', error);
      message.error('Failed to load reports');
    } finally {
      setLoading(false);
    }
  };

  const handleRunReport = async (id: string) => {
    try {
      setRunningReports(prev => new Set(prev).add(id));
      await adminService.runReport(id);
      message.success('Report generation started');
      
      // In a real application, you would poll for report status
      // For now, we'll simulate completion after 3 seconds
      setTimeout(() => {
        setRunningReports(prev => {
          const newSet = new Set(prev);
          newSet.delete(id);
          return newSet;
        });
        message.success('Report generated successfully');
        fetchReports();
      }, 3000);
    } catch (error) {
      console.error('Failed to run report:', error);
      message.error('Failed to run report');
      setRunningReports(prev => {
        const newSet = new Set(prev);
        newSet.delete(id);
        return newSet;
      });
    }
  };

  const handleDeleteReport = async (id: string) => {
    try {
      await adminService.deleteReport(id);
      message.success('Report deleted successfully');
      fetchReports();
    } catch (error) {
      console.error('Failed to delete report:', error);
      message.error('Failed to delete report');
    }
  };

  const handleTableChange = (pagination: any) => {
    setPagination({
      ...pagination,
      current: pagination.current,
      pageSize: pagination.pageSize,
    });
  };

  const getReportTypeTag = (type: string) => {
    switch (type) {
      case 'user_activity':
        return <Tag color="blue">User Activity</Tag>;
      case 'login_attempts':
        return <Tag color="green">Login Attempts</Tag>;
      case 'security_audit':
        return <Tag color="red">Security Audit</Tag>;
      case 'system_usage':
        return <Tag color="orange">System Usage</Tag>;
      default:
        return <Tag>{type}</Tag>;
    }
  };

  const getScheduleTag = (schedule: string | null) => {
    if (!schedule) return <Tag>On Demand</Tag>;
    
    switch (schedule) {
      case 'daily':
        return <Tag color="blue">Daily</Tag>;
      case 'weekly':
        return <Tag color="green">Weekly</Tag>;
      case 'monthly':
        return <Tag color="purple">Monthly</Tag>;
      default:
        return <Tag>{schedule}</Tag>;
    }
  };

  const columns: ColumnsType<ReportData> = [
    {
      title: 'Report Name',
      dataIndex: 'name',
      key: 'name',
      render: (text: string) => (
        <span style={{ fontWeight: 'bold' }}>{text}</span>
      ),
    },
    {
      title: 'Type',
      dataIndex: 'type',
      key: 'type',
      render: (type: string) => getReportTypeTag(type),
    },
    {
      title: 'Schedule',
      dataIndex: 'schedule',
      key: 'schedule',
      render: (schedule: string | null) => getScheduleTag(schedule),
    },
    {
      title: 'Last Run',
      dataIndex: 'lastRun',
      key: 'lastRun',
      render: (lastRun: string | null) => 
        lastRun ? new Date(lastRun).toLocaleString() : 'Never',
    },
    {
      title: 'Created By',
      dataIndex: 'createdBy',
      key: 'createdBy',
    },
    {
      title: 'Actions',
      key: 'actions',
      width: 200,
      render: (_, record) => (
        <Space size="small">
          <Tooltip title="Run Report">
            <Button
              type="primary"
              icon={<PlayCircleOutlined />}
              size="small"
              loading={runningReports.has(record.id)}
              onClick={() => handleRunReport(record.id)}
            />
          </Tooltip>
          <Tooltip title="Download Report">
            <Button
              icon={<DownloadOutlined />}
              size="small"
              disabled={!record.lastRun}
            />
          </Tooltip>
          <Tooltip title="Delete Report">
            <Popconfirm
              title="Are you sure you want to delete this report?"
              onConfirm={() => handleDeleteReport(record.id)}
              okText="Yes"
              cancelText="No"
              icon={<ExclamationCircleOutlined style={{ color: 'red' }} />}
            >
              <Button
                type="primary"
                danger
                icon={<DeleteOutlined />}
                size="small"
              />
            </Popconfirm>
          </Tooltip>
        </Space>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <Title level={2}>Reports</Title>
        <Button
          type="primary"
          icon={<PlusOutlined />}
          onClick={() => navigate('/admin/analytics/reports/new')}
        >
          Create New Report
        </Button>
      </div>

      <Card>
        <Text className="block mb-4">
          View and manage analytics reports. Run reports on demand or schedule them to run automatically.
        </Text>
        <Table
          columns={columns}
          dataSource={reports}
          rowKey="id"
          loading={loading}
          pagination={pagination}
          onChange={handleTableChange}
        />
      </Card>
    </div>
  );
};

export default Reports;
