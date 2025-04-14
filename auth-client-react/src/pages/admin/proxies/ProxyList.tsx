import React, { useState, useEffect } from 'react';
import {
  Table, Button, Card, Space, Tag, Typography, Input, Breadcrumb,
  Dropdown, Modal, Form, InputNumber, Select, Tooltip, Badge, Popconfirm,
  notification, Tabs, Statistic, Row, Col, Divider
} from 'antd';
import {
  SearchOutlined, ReloadOutlined, PlusOutlined, EditOutlined,
  DeleteOutlined, CheckCircleOutlined, CloseCircleOutlined,
  GlobalOutlined, SettingOutlined, SyncOutlined, InfoCircleOutlined,
  UploadOutlined
} from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import type { ColumnsType } from 'antd/es/table';
import proxyService from '../../../services/proxy.service';
import { Proxy, ProxyRequest, PROXY_PROTOCOLS } from '../../../types/proxy.types';
import dayjs from 'dayjs';
import relativeTime from 'dayjs/plugin/relativeTime';

dayjs.extend(relativeTime);

const { Title, Text } = Typography;
const { TabPane } = Tabs;

const ProxyList: React.FC = () => {
  const [proxies, setProxies] = useState<Proxy[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [searchText, setSearchText] = useState<string>('');
  const [editModalVisible, setEditModalVisible] = useState<boolean>(false);
  const [currentProxy, setCurrentProxy] = useState<Proxy | null>(null);
  const [form] = Form.useForm();
  const [checkingId, setCheckingId] = useState<string | null>(null);
  const [activeTab, setActiveTab] = useState<string>('all');
  const navigate = useNavigate();

  // Stats
  const [stats, setStats] = useState({
    total: 0,
    active: 0,
    inactive: 0,
    http: 0,
    https: 0,
    socks4: 0,
    socks5: 0,
    avgResponseTime: 0,
    avgUptime: 0
  });

  useEffect(() => {
    fetchProxies();
  }, []);

  useEffect(() => {
    if (proxies.length > 0) {
      calculateStats();
    }
  }, [proxies]);

  const calculateStats = () => {
    const active = proxies.filter(p => p.isActive).length;
    const http = proxies.filter(p => p.protocol === 'HTTP').length;
    const https = proxies.filter(p => p.protocol === 'HTTPS').length;
    const socks4 = proxies.filter(p => p.protocol === 'SOCKS4').length;
    const socks5 = proxies.filter(p => p.protocol === 'SOCKS5').length;

    const totalResponseTime = proxies.reduce((sum, p) => sum + p.responseTimeMs, 0);
    const avgResponseTime = proxies.length > 0 ? Math.round(totalResponseTime / proxies.length) : 0;

    const totalUptime = proxies.reduce((sum, p) => sum + p.uptime, 0);
    const avgUptime = proxies.length > 0 ? Math.round(totalUptime / proxies.length * 10) / 10 : 0;

    setStats({
      total: proxies.length,
      active,
      inactive: proxies.length - active,
      http,
      https,
      socks4,
      socks5,
      avgResponseTime,
      avgUptime
    });
  };

  const fetchProxies = async () => {
    setLoading(true);
    try {
      let data: Proxy[] = [];

      switch (activeTab) {
        case 'active':
          data = await proxyService.getActiveProxies();
          break;
        case 'http':
          data = await proxyService.getProxiesByProtocol('HTTP');
          break;
        case 'https':
          data = await proxyService.getProxiesByProtocol('HTTPS');
          break;
        case 'socks4':
          data = await proxyService.getProxiesByProtocol('SOCKS4');
          break;
        case 'socks5':
          data = await proxyService.getProxiesByProtocol('SOCKS5');
          break;
        case 'fast':
          data = await proxyService.getFastProxies(500);
          break;
        case 'reliable':
          data = await proxyService.getReliableProxies(95);
          break;
        default:
          data = await proxyService.getAllProxies();
      }

      setProxies(data);
    } catch (error) {
      notification.error({
        message: 'Error',
        description: 'Failed to fetch proxies',
      });
      console.error('Error fetching proxies:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleTabChange = (key: string) => {
    setActiveTab(key);
    setSearchText('');
    fetchProxies();
  };

  const handleEdit = (proxy: Proxy) => {
    setCurrentProxy(proxy);
    form.setFieldsValue({
      ipAddress: proxy.ipAddress,
      port: proxy.port,
      protocol: proxy.protocol,
      country: proxy.country,
      city: proxy.city,
      notes: proxy.notes
    });
    setEditModalVisible(true);
  };

  const handleSaveEdit = async () => {
    try {
      const values = await form.validateFields();
      if (!currentProxy) return;

      setLoading(true);
      const updatedProxy = await proxyService.updateProxy(currentProxy.id, values as ProxyRequest);

      setProxies(proxies.map(p => p.id === updatedProxy.id ? updatedProxy : p));
      setEditModalVisible(false);
      notification.success({
        message: 'Success',
        description: 'Proxy updated successfully',
      });
    } catch (error) {
      notification.error({
        message: 'Error',
        description: 'Failed to update proxy',
      });
      console.error('Error updating proxy:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id: string) => {
    try {
      setLoading(true);
      await proxyService.deleteProxy(id);
      setProxies(proxies.filter(p => p.id !== id));
      notification.success({
        message: 'Success',
        description: 'Proxy deleted successfully',
      });
    } catch (error) {
      notification.error({
        message: 'Error',
        description: 'Failed to delete proxy',
      });
      console.error('Error deleting proxy:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCheck = async (id: string) => {
    try {
      setCheckingId(id);
      const result = await proxyService.checkProxy(id);

      // Update the proxy in the list
      const updatedProxies = proxies.map(p => {
        if (p.id === id) {
          return {
            ...p,
            isActive: result.isWorking,
            responseTimeMs: result.responseTimeMs,
            lastChecked: result.checkedAt,
            successCount: result.isWorking ? p.successCount + 1 : p.successCount,
            failCount: result.isWorking ? p.failCount : p.failCount + 1,
            uptime: ((p.successCount + (result.isWorking ? 1 : 0)) /
                    (p.successCount + p.failCount + 1)) * 100
          };
        }
        return p;
      });

      setProxies(updatedProxies);

      notification.success({
        message: 'Proxy Check',
        description: `Proxy is ${result.isWorking ? 'working' : 'not working'}. Response time: ${result.responseTimeMs}ms`,
      });
    } catch (error) {
      notification.error({
        message: 'Error',
        description: 'Failed to check proxy',
      });
      console.error('Error checking proxy:', error);
    } finally {
      setCheckingId(null);
    }
  };

  const filteredProxies = proxies.filter(proxy =>
    proxy.ipAddress.toLowerCase().includes(searchText.toLowerCase()) ||
    proxy.protocol.toLowerCase().includes(searchText.toLowerCase()) ||
    (proxy.country && proxy.country.toLowerCase().includes(searchText.toLowerCase())) ||
    (proxy.city && proxy.city.toLowerCase().includes(searchText.toLowerCase())) ||
    (proxy.notes && proxy.notes.toLowerCase().includes(searchText.toLowerCase()))
  );

  const columns: ColumnsType<Proxy> = [
    {
      title: 'Status',
      dataIndex: 'isActive',
      key: 'isActive',
      width: 80,
      render: (isActive) => (
        isActive ?
          <Badge status="success" text="Active" /> :
          <Badge status="error" text="Inactive" />
      ),
    },
    {
      title: 'IP Address',
      dataIndex: 'ipAddress',
      key: 'ipAddress',
      render: (_, proxy) => (
        <Space direction="vertical" size={0}>
          <Text strong>{proxy.ipAddress}:{proxy.port}</Text>
          <Text type="secondary" style={{ fontSize: '12px' }}>
            {proxy.country && <span>{proxy.country}{proxy.city ? `, ${proxy.city}` : ''}</span>}
          </Text>
        </Space>
      ),
    },
    {
      title: 'Protocol',
      dataIndex: 'protocol',
      key: 'protocol',
      width: 100,
      render: (protocol) => {
        let color = 'blue';
        switch (protocol) {
          case 'HTTP': color = 'green'; break;
          case 'HTTPS': color = 'cyan'; break;
          case 'SOCKS4': color = 'purple'; break;
          case 'SOCKS5': color = 'magenta'; break;
        }
        return <Tag color={color}>{protocol}</Tag>;
      },
    },
    {
      title: 'Performance',
      key: 'performance',
      render: (_, proxy) => (
        <Space direction="vertical" size={0}>
          <Text>Response: <Text strong>{proxy.responseTimeMs}ms</Text></Text>
          <Text>Uptime: <Text strong>{proxy.uptime.toFixed(1)}%</Text></Text>
        </Space>
      ),
    },
    {
      title: 'Last Checked',
      dataIndex: 'lastChecked',
      key: 'lastChecked',
      render: (lastChecked) => lastChecked ? dayjs(lastChecked).fromNow() : 'Never',
    },
    {
      title: 'Actions',
      key: 'actions',
      width: 200,
      render: (_, proxy) => (
        <Space>
          <Button
            icon={<SyncOutlined spin={checkingId === proxy.id} />}
            onClick={() => handleCheck(proxy.id)}
            loading={checkingId === proxy.id}
          >
            Check
          </Button>
          <Button icon={<EditOutlined />} onClick={() => handleEdit(proxy)} />
          <Popconfirm
            title="Are you sure you want to delete this proxy?"
            onConfirm={() => handleDelete(proxy.id)}
            okText="Yes"
            cancelText="No"
          >
            <Button danger icon={<DeleteOutlined />} />
          </Popconfirm>
        </Space>
      ),
    },
  ];

  const StatCards = () => (
    <Row gutter={[16, 16]} style={{ marginBottom: 16 }}>
      <Col xs={24} sm={12} md={8} lg={6}>
        <Card>
          <Statistic
            title="Total Proxies"
            value={stats.total}
            prefix={<GlobalOutlined />}
          />
        </Card>
      </Col>
      <Col xs={24} sm={12} md={8} lg={6}>
        <Card>
          <Statistic
            title="Active Proxies"
            value={stats.active}
            valueStyle={{ color: '#3f8600' }}
            prefix={<CheckCircleOutlined />}
            suffix={stats.total > 0 ? `(${Math.round(stats.active / stats.total * 100)}%)` : ''}
          />
        </Card>
      </Col>
      <Col xs={24} sm={12} md={8} lg={6}>
        <Card>
          <Statistic
            title="Avg Response Time"
            value={stats.avgResponseTime}
            suffix="ms"
            valueStyle={{ color: stats.avgResponseTime < 500 ? '#3f8600' : '#cf1322' }}
          />
        </Card>
      </Col>
      <Col xs={24} sm={12} md={8} lg={6}>
        <Card>
          <Statistic
            title="Avg Uptime"
            value={stats.avgUptime}
            suffix="%"
            valueStyle={{ color: stats.avgUptime > 90 ? '#3f8600' : '#cf1322' }}
          />
        </Card>
      </Col>
    </Row>
  );

  return (
    <div>
      <Breadcrumb
        items={[
          { title: 'Home' },
          { title: 'Admin' },
          { title: 'Proxies' }
        ]}
        style={{ marginBottom: 16 }}
      />

      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
        <Title level={4}>Proxy Management</Title>
        <Space>
          <Button
            icon={<UploadOutlined />}
            onClick={() => navigate('/admin/proxies/import')}
          >
            Import Proxies
          </Button>
          <Button
            type="primary"
            icon={<PlusOutlined />}
            onClick={() => navigate('/admin/proxies/new')}
          >
            Add New Proxy
          </Button>
        </Space>
      </div>

      <StatCards />

      <Card>
        <Tabs activeKey={activeTab} onChange={handleTabChange}>
          <TabPane tab="All Proxies" key="all" />
          <TabPane tab="Active" key="active" />
          <TabPane tab="HTTP" key="http" />
          <TabPane tab="HTTPS" key="https" />
          <TabPane tab="SOCKS4" key="socks4" />
          <TabPane tab="SOCKS5" key="socks5" />
          <TabPane tab="Fast (<500ms)" key="fast" />
          <TabPane tab="Reliable (>95%)" key="reliable" />
        </Tabs>

        <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between' }}>
          <Input
            placeholder="Search proxies..."
            prefix={<SearchOutlined />}
            value={searchText}
            onChange={(e) => setSearchText(e.target.value)}
            style={{ width: 300 }}
          />
          <Button
            type="default"
            icon={<ReloadOutlined />}
            onClick={fetchProxies}
            loading={loading}
          >
            Refresh
          </Button>
        </div>

        <Table
          columns={columns}
          dataSource={filteredProxies}
          rowKey="id"
          loading={loading}
          pagination={{ pageSize: 10 }}
        />
      </Card>

      {/* Edit Proxy Modal */}
      <Modal
        title="Edit Proxy"
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
            name="ipAddress"
            label="IP Address"
            rules={[{ required: true, message: 'Please enter IP address' }]}
          >
            <Input />
          </Form.Item>

          <Form.Item
            name="port"
            label="Port"
            rules={[{ required: true, message: 'Please enter port' }]}
          >
            <InputNumber min={1} max={65535} style={{ width: '100%' }} />
          </Form.Item>

          <Form.Item
            name="protocol"
            label="Protocol"
            rules={[{ required: true, message: 'Please select protocol' }]}
          >
            <Select>
              {PROXY_PROTOCOLS.map(protocol => (
                <Select.Option key={protocol} value={protocol}>{protocol}</Select.Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            name="country"
            label="Country"
          >
            <Input />
          </Form.Item>

          <Form.Item
            name="city"
            label="City"
          >
            <Input />
          </Form.Item>

          <Form.Item
            name="notes"
            label="Notes"
          >
            <Input.TextArea rows={3} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default ProxyList;
