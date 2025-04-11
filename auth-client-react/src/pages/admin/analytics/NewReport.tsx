import React, { useState } from 'react';
import {
  Card,
  Form,
  Input,
  Button,
  Select,
  Typography,
  Divider,
  message,
  Alert,
  Radio,
  DatePicker,
  Space
} from 'antd';
import {
  SaveOutlined,
  FileTextOutlined,
} from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import adminService from '../../../services/admin.service';

const { Title, Text } = Typography;
const { Option } = Select;
const { TextArea } = Input;
const { RangePicker } = DatePicker;

const NewReport: React.FC = () => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [reportType, setReportType] = useState<string>('user_activity');
  const [scheduleType, setScheduleType] = useState<string>('on_demand');
  const navigate = useNavigate();

  const onFinish = async (values: any) => {
    try {
      setLoading(true);
      setError(null);

      // Process date range if provided
      if (values.dateRange) {
        values.startDate = values.dateRange[0].toISOString();
        values.endDate = values.dateRange[1].toISOString();
        delete values.dateRange;
      }

      // Process schedule
      if (values.scheduleType === 'on_demand') {
        values.schedule = null;
      } else {
        values.schedule = values.scheduleType;
      }
      delete values.scheduleType;

      await adminService.createReport(values);
      message.success('Report created successfully');

      // Navigate to reports list
      navigate('/admin/analytics/reports');
    } catch (err: any) {
      console.error('Failed to create report:', err);
      setError(err.message || 'Failed to create report');
    } finally {
      setLoading(false);
    }
  };

  const handleReportTypeChange = (value: string) => {
    setReportType(value);
  };

  const handleScheduleTypeChange = (e: any) => {
    setScheduleType(e.target.value);
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <Title level={2}>Create New Report</Title>
        <Button
          type="primary"
          onClick={() => navigate('/admin/analytics/reports')}
        >
          Back to Reports
        </Button>
      </div>

      <Card>
        {error && (
          <Alert
            message="Error"
            description={error}
            type="error"
            showIcon
            closable
            className="mb-6"
            onClose={() => setError(null)}
          />
        )}

        <Form
          form={form}
          layout="vertical"
          onFinish={onFinish}
          initialValues={{
            type: 'user_activity',
            scheduleType: 'on_demand',
          }}
        >
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <Title level={4}>Report Information</Title>
              <Text type="secondary" className="block mb-4">
                Enter the basic information for the new report.
              </Text>

              <Form.Item
                name="name"
                label="Report Name"
                rules={[{ required: true, message: 'Please enter report name' }]}
              >
                <Input prefix={<FileTextOutlined />} placeholder="Report Name" />
              </Form.Item>

              <Form.Item
                name="description"
                label="Description"
                rules={[{ required: true, message: 'Please enter description' }]}
              >
                <TextArea
                  rows={4}
                  placeholder="Describe the purpose of this report"
                />
              </Form.Item>

              <Form.Item
                name="type"
                label="Report Type"
                rules={[{ required: true, message: 'Please select report type' }]}
              >
                <Select onChange={handleReportTypeChange}>
                  <Option value="user_activity">User Activity</Option>
                  <Option value="login_attempts">Login Attempts</Option>
                  <Option value="security_audit">Security Audit</Option>
                  <Option value="system_usage">System Usage</Option>
                </Select>
              </Form.Item>
            </div>

            <div>
              <Title level={4}>Report Settings</Title>
              <Text type="secondary" className="block mb-4">
                Configure when and how the report should run.
              </Text>

              <Form.Item
                name="dateRange"
                label="Date Range"
                rules={[{ required: true, message: 'Please select date range' }]}
              >
                <RangePicker style={{ width: '100%' }} />
              </Form.Item>

              <Form.Item
                name="scheduleType"
                label="Schedule"
              >
                <Radio.Group onChange={handleScheduleTypeChange}>
                  <Space direction="vertical">
                    <Radio value="on_demand">Run on demand</Radio>
                    <Radio value="daily">Daily</Radio>
                    <Radio value="weekly">Weekly</Radio>
                    <Radio value="monthly">Monthly</Radio>
                  </Space>
                </Radio.Group>
              </Form.Item>

              {scheduleType !== 'on_demand' && (
                <Form.Item
                  name="scheduleTime"
                  label="Time to Run"
                  rules={[{ required: true, message: 'Please select time to run' }]}
                >
                  <Select>
                    <Option value="00:00">12:00 AM</Option>
                    <Option value="06:00">6:00 AM</Option>
                    <Option value="12:00">12:00 PM</Option>
                    <Option value="18:00">6:00 PM</Option>
                  </Select>
                </Form.Item>
              )}

              <Form.Item
                name="format"
                label="Output Format"
                rules={[{ required: true, message: 'Please select output format' }]}
                initialValue="pdf"
              >
                <Select>
                  <Option value="pdf">PDF</Option>
                  <Option value="csv">CSV</Option>
                  <Option value="excel">Excel</Option>
                  <Option value="json">JSON</Option>
                </Select>
              </Form.Item>
            </div>
          </div>

          <Divider />

          <div className="flex justify-end">
            <Button
              type="default"
              onClick={() => navigate('/admin/analytics/reports')}
              className="mr-2"
            >
              Cancel
            </Button>
            <Button
              type="primary"
              htmlType="submit"
              icon={<SaveOutlined />}
              loading={loading}
            >
              Create Report
            </Button>
          </div>
        </Form>
      </Card>
    </div>
  );
};

export default NewReport;
