import React, { useState, useEffect } from 'react';
import { Table, Card, Input, Button, Space, Tag, Avatar, Dropdown, Modal, Form, message, Breadcrumb, Typography } from 'antd';
import { SearchOutlined, UserOutlined, EditOutlined, DeleteOutlined, CheckCircleOutlined, StopOutlined, MoreOutlined, LockOutlined, UnlockOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import type { MenuProps } from 'antd';
import api from '../../../services/api.service';

const { Title } = Typography;

interface User {
  id: string;
  username: string;
  email: string;
  fullName: string;
  role: 'USER' | 'ADMIN';
  active: boolean;
  avatar?: string;
  socialProvider?: string;
  createdAt: string;
}

const UserList: React.FC = () => {
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchText, setSearchText] = useState('');
  const [editModalVisible, setEditModalVisible] = useState(false);
  const [editingUser, setEditingUser] = useState<User | null>(null);
  const [form] = Form.useForm();
  
  // Fetch users
  useEffect(() => {
    fetchUsers();
  }, []);
  
  const fetchUsers = async () => {
    try {
      setLoading(true);
      const response = await api.get<{ data: User[] }>('/admin/users');
      setUsers(response.data.data || []);
    } catch (error) {
      console.error('Error fetching users:', error);
      message.error('Failed to load users');
    } finally {
      setLoading(false);
    }
  };
  
  // Filter users based on search text
  const filteredUsers = users.filter(user => 
    user.username.toLowerCase().includes(searchText.toLowerCase()) ||
    user.email.toLowerCase().includes(searchText.toLowerCase()) ||
    user.fullName.toLowerCase().includes(searchText.toLowerCase()) ||
    user.role.toLowerCase().includes(searchText.toLowerCase())
  );
  
  // Edit user
  const handleEdit = (user: User) => {
    setEditingUser(user);
    form.setFieldsValue({
      fullName: user.fullName,
      email: user.email,
      avatar: user.avatar || '',
    });
    setEditModalVisible(true);
  };
  
  const handleSaveEdit = async () => {
    try {
      const values = await form.validateFields();
      
      if (!editingUser) return;
      
      const response = await api.put(`/admin/users/${editingUser.id}`, {
        fullName: values.fullName,
        email: values.email,
        avatar: values.avatar || null,
      });
      
      // Update user in the list
      const updatedUsers = users.map(user => 
        user.id === editingUser.id ? { ...user, ...response.data.data } : user
      );
      
      setUsers(updatedUsers);
      setEditModalVisible(false);
      message.success('User updated successfully');
    } catch (error) {
      console.error('Error updating user:', error);
      message.error('Failed to update user');
    }
  };
  
  // Activate/Deactivate user
  const handleActivateUser = async (user: User) => {
    try {
      await api.put(`/admin/users/${user.id}/activate`);
      
      // Update user in the list
      const updatedUsers = users.map(u => 
        u.id === user.id ? { ...u, active: true } : u
      );
      
      setUsers(updatedUsers);
      message.success(`User ${user.username} has been activated`);
    } catch (error) {
      console.error('Error activating user:', error);
      message.error('Failed to activate user');
    }
  };
  
  const handleDeactivateUser = async (user: User) => {
    try {
      await api.put(`/admin/users/${user.id}/deactivate`);
      
      // Update user in the list
      const updatedUsers = users.map(u => 
        u.id === user.id ? { ...u, active: false } : u
      );
      
      setUsers(updatedUsers);
      message.success(`User ${user.username} has been deactivated`);
    } catch (error) {
      console.error('Error deactivating user:', error);
      message.error('Failed to deactivate user');
    }
  };
  
  // Promote/Demote user
  const handlePromoteToAdmin = async (user: User) => {
    try {
      await api.put(`/admin/users/${user.id}/promote-to-admin`);
      
      // Update user in the list
      const updatedUsers = users.map(u => 
        u.id === user.id ? { ...u, role: 'ADMIN' } : u
      );
      
      setUsers(updatedUsers);
      message.success(`User ${user.username} has been promoted to admin`);
    } catch (error) {
      console.error('Error promoting user:', error);
      message.error('Failed to promote user');
    }
  };
  
  const handleDemoteToUser = async (user: User) => {
    try {
      await api.put(`/admin/users/${user.id}/demote-to-user`);
      
      // Update user in the list
      const updatedUsers = users.map(u => 
        u.id === user.id ? { ...u, role: 'USER' } : u
      );
      
      setUsers(updatedUsers);
      message.success(`User ${user.username} has been demoted to regular user`);
    } catch (error) {
      console.error('Error demoting user:', error);
      message.error('Failed to demote user');
    }
  };
  
  // Table columns
  const columns: ColumnsType<User> = [
    {
      title: 'User',
      dataIndex: 'fullName',
      key: 'fullName',
      render: (_, user) => (
        <div style={{ display: 'flex', alignItems: 'center' }}>
          <Avatar 
            src={user.avatar} 
            icon={!user.avatar && <UserOutlined />}
            style={{ marginRight: 8 }}
          />
          <div>
            <div>{user.fullName}</div>
            <div style={{ fontSize: '12px', color: 'rgba(0, 0, 0, 0.45)' }}>@{user.username}</div>
          </div>
        </div>
      ),
    },
    {
      title: 'Email',
      dataIndex: 'email',
      key: 'email',
    },
    {
      title: 'Role',
      dataIndex: 'role',
      key: 'role',
      render: (role) => (
        <Tag color={role === 'ADMIN' ? 'purple' : 'blue'}>
          {role}
        </Tag>
      ),
    },
    {
      title: 'Status',
      dataIndex: 'active',
      key: 'active',
      render: (active) => (
        <Tag color={active ? 'success' : 'error'}>
          {active ? 'Active' : 'Inactive'}
        </Tag>
      ),
    },
    {
      title: 'Login Type',
      dataIndex: 'socialProvider',
      key: 'socialProvider',
      render: (socialProvider) => socialProvider || 'Email/Password',
    },
    {
      title: 'Actions',
      key: 'actions',
      render: (_, user) => {
        const items: MenuProps['items'] = [
          {
            key: 'edit',
            label: 'Edit',
            icon: <EditOutlined />,
            onClick: () => handleEdit(user),
          },
          {
            key: 'status',
            label: user.active ? 'Deactivate' : 'Activate',
            icon: user.active ? <StopOutlined /> : <CheckCircleOutlined />,
            onClick: () => user.active ? handleDeactivateUser(user) : handleActivateUser(user),
          },
          {
            key: 'role',
            label: user.role === 'USER' ? 'Make Admin' : 'Remove Admin',
            icon: user.role === 'USER' ? <LockOutlined /> : <UnlockOutlined />,
            onClick: () => user.role === 'USER' ? handlePromoteToAdmin(user) : handleDemoteToUser(user),
          },
        ];
        
        return (
          <Space size="middle">
            <Button 
              type="text" 
              icon={<EditOutlined />} 
              onClick={() => handleEdit(user)}
            />
            <Dropdown menu={{ items }} placement="bottomRight">
              <Button type="text" icon={<MoreOutlined />} />
            </Dropdown>
          </Space>
        );
      },
    },
  ];
  
  return (
    <div>
      <Breadcrumb
        items={[
          { title: 'Home' },
          { title: 'Admin' },
          { title: 'Users' },
          { title: 'All Users' }
        ]}
        style={{ marginBottom: 16 }}
      />
      
      <Title level={4}>User Management</Title>
      
      <Card style={{ marginTop: 16 }}>
        <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between' }}>
          <Input
            placeholder="Search users..."
            prefix={<SearchOutlined />}
            value={searchText}
            onChange={(e) => setSearchText(e.target.value)}
            style={{ width: 300 }}
          />
          <Button type="primary" onClick={() => fetchUsers()}>Refresh</Button>
        </div>
        
        <Table
          columns={columns}
          dataSource={filteredUsers}
          rowKey="id"
          loading={loading}
          pagination={{ pageSize: 10 }}
        />
      </Card>
      
      {/* Edit User Modal */}
      <Modal
        title="Edit User"
        open={editModalVisible}
        onOk={handleSaveEdit}
        onCancel={() => setEditModalVisible(false)}
        confirmLoading={loading}
      >
        <Form
          form={form}
          layout="vertical"
        >
          <Form.Item
            name="fullName"
            label="Full Name"
            rules={[{ required: true, message: 'Please enter full name' }]}
          >
            <Input />
          </Form.Item>
          
          <Form.Item
            name="email"
            label="Email"
            rules={[
              { required: true, message: 'Please enter email' },
              { type: 'email', message: 'Please enter a valid email' }
            ]}
          >
            <Input />
          </Form.Item>
          
          <Form.Item
            name="avatar"
            label="Avatar URL"
          >
            <Input />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default UserList;
