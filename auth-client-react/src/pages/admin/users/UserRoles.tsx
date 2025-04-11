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
  Tooltip
} from 'antd';
import { 
  PlusOutlined, 
  EditOutlined, 
  DeleteOutlined, 
  ExclamationCircleOutlined,
  KeyOutlined
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import adminService from '../../../services/admin.service';

const { Title, Text } = Typography;
const { Option } = Select;
const { TextArea } = Input;

interface RoleData {
  id: string;
  name: string;
  description: string;
  permissions: string[];
}

// Available permissions in the system
const availablePermissions = [
  { value: 'user:read', label: 'View Users' },
  { value: 'user:create', label: 'Create Users' },
  { value: 'user:update', label: 'Update Users' },
  { value: 'user:delete', label: 'Delete Users' },
  { value: 'role:read', label: 'View Roles' },
  { value: 'role:create', label: 'Create Roles' },
  { value: 'role:update', label: 'Update Roles' },
  { value: 'role:delete', label: 'Delete Roles' },
  { value: 'settings:read', label: 'View Settings' },
  { value: 'settings:update', label: 'Update Settings' },
  { value: 'analytics:read', label: 'View Analytics' },
  { value: 'reports:read', label: 'View Reports' },
  { value: 'reports:create', label: 'Create Reports' },
  { value: 'reports:update', label: 'Update Reports' },
  { value: 'reports:delete', label: 'Delete Reports' },
];

const UserRoles: React.FC = () => {
  const [roles, setRoles] = useState<RoleData[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [modalVisible, setModalVisible] = useState<boolean>(false);
  const [modalTitle, setModalTitle] = useState<string>('Add New Role');
  const [editingRole, setEditingRole] = useState<RoleData | null>(null);
  const [form] = Form.useForm();

  // Fetch roles on component mount
  useEffect(() => {
    fetchRoles();
  }, []);

  const fetchRoles = async () => {
    try {
      setLoading(true);
      const data = await adminService.getRoles();
      setRoles(data);
    } catch (error) {
      console.error('Failed to fetch roles:', error);
      message.error('Failed to load roles');
    } finally {
      setLoading(false);
    }
  };

  const showAddModal = () => {
    setModalTitle('Add New Role');
    setEditingRole(null);
    form.resetFields();
    setModalVisible(true);
  };

  const showEditModal = (role: RoleData) => {
    setModalTitle('Edit Role');
    setEditingRole(role);
    form.setFieldsValue({
      name: role.name,
      description: role.description,
      permissions: role.permissions,
    });
    setModalVisible(true);
  };

  const handleCancel = () => {
    setModalVisible(false);
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      
      if (editingRole) {
        // Update existing role
        await adminService.updateRole(editingRole.id, values);
        message.success('Role updated successfully');
      } else {
        // Create new role
        await adminService.createRole(values);
        message.success('Role created successfully');
      }
      
      setModalVisible(false);
      fetchRoles();
    } catch (error) {
      console.error('Form submission failed:', error);
    }
  };

  const handleDelete = async (id: string) => {
    try {
      await adminService.deleteRole(id);
      message.success('Role deleted successfully');
      fetchRoles();
    } catch (error) {
      console.error('Failed to delete role:', error);
      message.error('Failed to delete role');
    }
  };

  const columns: ColumnsType<RoleData> = [
    {
      title: 'Role Name',
      dataIndex: 'name',
      key: 'name',
      render: (text: string) => (
        <span style={{ fontWeight: 'bold' }}>{text}</span>
      ),
    },
    {
      title: 'Description',
      dataIndex: 'description',
      key: 'description',
    },
    {
      title: 'Permissions',
      dataIndex: 'permissions',
      key: 'permissions',
      render: (permissions: string[]) => (
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '4px' }}>
          {permissions.map(permission => (
            <Tag color="blue" key={permission}>
              {permission}
            </Tag>
          ))}
        </div>
      ),
    },
    {
      title: 'Actions',
      key: 'actions',
      width: 150,
      render: (_, record) => (
        <Space size="middle">
          <Tooltip title="Edit Role">
            <Button
              type="primary"
              icon={<EditOutlined />}
              size="small"
              onClick={() => showEditModal(record)}
            />
          </Tooltip>
          <Tooltip title="Delete Role">
            <Popconfirm
              title="Are you sure you want to delete this role?"
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
                disabled={record.name === 'ADMIN' || record.name === 'USER'}
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
        <Title level={2}>Role Management</Title>
        <Button
          type="primary"
          icon={<PlusOutlined />}
          onClick={showAddModal}
        >
          Add New Role
        </Button>
      </div>

      <Card>
        <Text className="block mb-4">
          Manage roles and permissions to control access to different parts of the application.
        </Text>
        <Table
          columns={columns}
          dataSource={roles}
          rowKey="id"
          loading={loading}
          pagination={false}
        />
      </Card>

      <Modal
        title={modalTitle}
        open={modalVisible}
        onCancel={handleCancel}
        onOk={handleSubmit}
        width={600}
      >
        <Form
          form={form}
          layout="vertical"
          initialValues={{ permissions: [] }}
        >
          <Form.Item
            name="name"
            label="Role Name"
            rules={[
              { required: true, message: 'Please enter a role name' },
              { pattern: /^[A-Z_]+$/, message: 'Role name must be uppercase with underscores only' },
            ]}
          >
            <Input 
              prefix={<KeyOutlined />} 
              placeholder="e.g., CONTENT_EDITOR" 
              disabled={editingRole?.name === 'ADMIN' || editingRole?.name === 'USER'}
            />
          </Form.Item>

          <Form.Item
            name="description"
            label="Description"
            rules={[{ required: true, message: 'Please enter a description' }]}
          >
            <TextArea
              rows={3}
              placeholder="Describe the role and its responsibilities"
            />
          </Form.Item>

          <Form.Item
            name="permissions"
            label="Permissions"
            rules={[{ required: true, message: 'Please select at least one permission' }]}
          >
            <Select
              mode="multiple"
              placeholder="Select permissions"
              style={{ width: '100%' }}
              optionFilterProp="label"
            >
              {availablePermissions.map(permission => (
                <Option key={permission.value} value={permission.value} label={permission.label}>
                  {permission.label}
                </Option>
              ))}
            </Select>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default UserRoles;
