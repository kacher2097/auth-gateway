import React, { useState, useEffect } from 'react';
import { 
  Card, 
  Table, 
  Button, 
  Typography, 
  Space, 
  Modal, 
  Form, 
  Input, 
  Select, 
  Popconfirm, 
  message,
  Tag,
  Badge
} from 'antd';
import { 
  PlusOutlined, 
  SendOutlined, 
  DeleteOutlined, 
  ExclamationCircleOutlined,
  MailOutlined
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import adminService from '../../../services/admin.service';

const { Title, Text } = Typography;
const { Option } = Select;

interface InviteData {
  id: string;
  email: string;
  role: string;
  status: 'pending' | 'accepted' | 'expired';
  createdAt: string;
  expiresAt: string;
  createdBy: string;
}

const UserInvites: React.FC = () => {
  const [invites, setInvites] = useState<InviteData[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [modalVisible, setModalVisible] = useState<boolean>(false);
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 10,
    total: 0,
  });
  const [form] = Form.useForm();

  // Fetch invites on component mount and pagination change
  useEffect(() => {
    fetchInvites();
  }, [pagination.current, pagination.pageSize]);

  const fetchInvites = async () => {
    try {
      setLoading(true);
      const data = await adminService.getInvites(pagination.current, pagination.pageSize);
      setInvites(data.invites);
      setPagination({
        ...pagination,
        total: data.total,
      });
    } catch (error) {
      console.error('Failed to fetch invites:', error);
      message.error('Failed to load invites');
    } finally {
      setLoading(false);
    }
  };

  const showAddModal = () => {
    form.resetFields();
    setModalVisible(true);
  };

  const handleCancel = () => {
    setModalVisible(false);
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      
      await adminService.createInvite(values);
      message.success('Invitation sent successfully');
      
      setModalVisible(false);
      fetchInvites();
    } catch (error) {
      console.error('Form submission failed:', error);
    }
  };

  const handleResend = async (id: string) => {
    try {
      await adminService.resendInvite(id);
      message.success('Invitation resent successfully');
      fetchInvites();
    } catch (error) {
      console.error('Failed to resend invite:', error);
      message.error('Failed to resend invitation');
    }
  };

  const handleDelete = async (id: string) => {
    try {
      await adminService.deleteInvite(id);
      message.success('Invitation deleted successfully');
      fetchInvites();
    } catch (error) {
      console.error('Failed to delete invite:', error);
      message.error('Failed to delete invitation');
    }
  };

  const handleTableChange = (pagination: any) => {
    setPagination({
      ...pagination,
      current: pagination.current,
      pageSize: pagination.pageSize,
    });
  };

  const getStatusTag = (status: string) => {
    switch (status) {
      case 'pending':
        return <Tag color="blue">Pending</Tag>;
      case 'accepted':
        return <Tag color="green">Accepted</Tag>;
      case 'expired':
        return <Tag color="red">Expired</Tag>;
      default:
        return <Tag>Unknown</Tag>;
    }
  };

  const columns: ColumnsType<InviteData> = [
    {
      title: 'Email',
      dataIndex: 'email',
      key: 'email',
    },
    {
      title: 'Role',
      dataIndex: 'role',
      key: 'role',
      render: (role: string) => (
        <Tag color="purple">{role}</Tag>
      ),
    },
    {
      title: 'Status',
      dataIndex: 'status',
      key: 'status',
      render: (status: string) => getStatusTag(status),
    },
    {
      title: 'Created',
      dataIndex: 'createdAt',
      key: 'createdAt',
      render: (date: string) => new Date(date).toLocaleDateString(),
    },
    {
      title: 'Expires',
      dataIndex: 'expiresAt',
      key: 'expiresAt',
      render: (date: string) => {
        const expiryDate = new Date(date);
        const now = new Date();
        const isExpired = expiryDate < now;
        
        return (
          <span style={{ color: isExpired ? '#f5222d' : 'inherit' }}>
            {expiryDate.toLocaleDateString()}
          </span>
        );
      },
    },
    {
      title: 'Created By',
      dataIndex: 'createdBy',
      key: 'createdBy',
    },
    {
      title: 'Actions',
      key: 'actions',
      width: 150,
      render: (_, record) => (
        <Space size="middle">
          {record.status === 'pending' && (
            <Button
              type="primary"
              icon={<SendOutlined />}
              size="small"
              onClick={() => handleResend(record.id)}
            >
              Resend
            </Button>
          )}
          <Popconfirm
            title="Are you sure you want to delete this invitation?"
            onConfirm={() => handleDelete(record.id)}
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
        </Space>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <Title level={2}>User Invitations</Title>
        <Button
          type="primary"
          icon={<PlusOutlined />}
          onClick={showAddModal}
        >
          Invite User
        </Button>
      </div>

      <Card>
        <Text className="block mb-4">
          Manage invitations for new users to join the platform.
        </Text>
        <Table
          columns={columns}
          dataSource={invites}
          rowKey="id"
          loading={loading}
          pagination={pagination}
          onChange={handleTableChange}
        />
      </Card>

      <Modal
        title="Invite New User"
        open={modalVisible}
        onCancel={handleCancel}
        onOk={handleSubmit}
      >
        <Form
          form={form}
          layout="vertical"
        >
          <Form.Item
            name="email"
            label="Email Address"
            rules={[
              { required: true, message: 'Please enter an email address' },
              { type: 'email', message: 'Please enter a valid email address' },
            ]}
          >
            <Input prefix={<MailOutlined />} placeholder="user@example.com" />
          </Form.Item>

          <Form.Item
            name="role"
            label="Role"
            rules={[{ required: true, message: 'Please select a role' }]}
          >
            <Select placeholder="Select a role">
              <Option value="USER">User</Option>
              <Option value="ADMIN">Admin</Option>
              <Option value="CONTENT_EDITOR">Content Editor</Option>
              <Option value="ANALYST">Analyst</Option>
            </Select>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default UserInvites;
