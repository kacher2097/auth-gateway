import React, { useState } from 'react';
import {
  Card, Upload, Button, Radio, Typography, Breadcrumb, notification, Space, Divider,
  Alert, Steps, Table, Progress, Result, Spin, Row, Col, Statistic
} from 'antd';
import {
  UploadOutlined, FileExcelOutlined, FileTextOutlined, ArrowLeftOutlined,
  CheckCircleOutlined, CloseCircleOutlined, InfoCircleOutlined, DownloadOutlined
} from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import type { UploadProps, UploadFile } from 'antd/es/upload/interface';
import type { ColumnsType } from 'antd/es/table';
import proxyService from '../../../services/proxy.service';
import { ImportError, ImportResult } from '../../../types/proxy.types';

const { Title, Text, Paragraph } = Typography;
const { Dragger } = Upload;

type FileType = 'CSV' | 'EXCEL';

const ProxyImport: React.FC = () => {
  const [fileType, setFileType] = useState<FileType>('CSV');
  const [fileList, setFileList] = useState<UploadFile[]>([]);
  const [uploading, setUploading] = useState(false);
  const [currentStep, setCurrentStep] = useState(0);
  const [importResult, setImportResult] = useState<ImportResult | null>(null);
  const navigate = useNavigate();

  const handleFileTypeChange = (e: any) => {
    setFileType(e.target.value);
  };

  const handleUpload = async () => {
    if (fileList.length === 0) {
      notification.error({
        message: 'Error',
        description: 'Please select a file to upload',
      });
      return;
    }

    const file = fileList[0].originFileObj;
    if (!file) {
      notification.error({
        message: 'Error',
        description: 'Invalid file',
      });
      return;
    }

    setUploading(true);
    setCurrentStep(1);

    try {
      const result = await proxyService.importProxies(file, fileType);
      setImportResult(result);
      setCurrentStep(2);
      
      notification.success({
        message: 'Import Completed',
        description: `Processed ${result.totalProcessed} proxies: ${result.successCount} successful, ${result.failCount} failed`,
      });
    } catch (error) {
      console.error('Error importing proxies:', error);
      notification.error({
        message: 'Import Failed',
        description: 'An error occurred while importing proxies',
      });
      setCurrentStep(0);
    } finally {
      setUploading(false);
    }
  };

  const uploadProps: UploadProps = {
    onRemove: (file) => {
      setFileList([]);
    },
    beforeUpload: (file) => {
      // Check file extension
      const isCSV = fileType === 'CSV' && file.name.endsWith('.csv');
      const isExcel = fileType === 'EXCEL' && (file.name.endsWith('.xlsx') || file.name.endsWith('.xls'));
      
      if (!(isCSV || isExcel)) {
        notification.error({
          message: 'Wrong file type',
          description: `Please upload a ${fileType === 'CSV' ? '.csv' : '.xlsx/.xls'} file.`,
        });
        return Upload.LIST_IGNORE;
      }
      
      setFileList([file]);
      return false;
    },
    fileList,
  };

  const errorColumns: ColumnsType<ImportError> = [
    {
      title: 'Row',
      dataIndex: 'rowNumber',
      key: 'rowNumber',
      width: 80,
    },
    {
      title: 'Error Message',
      dataIndex: 'errorMessage',
      key: 'errorMessage',
    },
    {
      title: 'Raw Data',
      dataIndex: 'rawData',
      key: 'rawData',
      ellipsis: true,
    },
  ];

  const renderStepContent = () => {
    switch (currentStep) {
      case 0:
        return (
          <Card>
            <Alert
              message="File Format Requirements"
              description={
                <div>
                  <p>Your file should have the following columns in this order:</p>
                  <ol>
                    <li><strong>IP Address</strong> (required) - e.g., 192.168.1.1</li>
                    <li><strong>Port</strong> (required) - e.g., 8080</li>
                    <li><strong>Protocol</strong> (required) - One of: HTTP, HTTPS, SOCKS4, SOCKS5</li>
                    <li><strong>Country</strong> (optional) - e.g., United States</li>
                    <li><strong>City</strong> (optional) - e.g., New York</li>
                    <li><strong>Notes</strong> (optional) - Any additional information</li>
                  </ol>
                  <p>The first row should be a header row with column names.</p>
                </div>
              }
              type="info"
              showIcon
              style={{ marginBottom: 24 }}
            />

            <div style={{ marginBottom: 16 }}>
              <Text strong>Select File Type:</Text>
              <Radio.Group 
                onChange={handleFileTypeChange} 
                value={fileType}
                style={{ marginLeft: 16 }}
              >
                <Radio.Button value="CSV">
                  <FileTextOutlined /> CSV
                </Radio.Button>
                <Radio.Button value="EXCEL">
                  <FileExcelOutlined /> Excel
                </Radio.Button>
              </Radio.Group>
            </div>

            <Dragger {...uploadProps} style={{ marginBottom: 24 }}>
              <p className="ant-upload-drag-icon">
                <UploadOutlined />
              </p>
              <p className="ant-upload-text">Click or drag file to this area to upload</p>
              <p className="ant-upload-hint">
                Support for a single {fileType === 'CSV' ? '.csv' : '.xlsx/.xls'} file.
              </p>
            </Dragger>

            <Divider />

            <Button
              type="primary"
              onClick={handleUpload}
              disabled={fileList.length === 0}
              loading={uploading}
              icon={<UploadOutlined />}
            >
              {uploading ? 'Importing...' : 'Start Import'}
            </Button>
          </Card>
        );
      case 1:
        return (
          <Card>
            <div style={{ textAlign: 'center', padding: '30px 0' }}>
              <Spin size="large" />
              <Title level={4} style={{ marginTop: 24 }}>Processing Your File</Title>
              <Paragraph>
                Please wait while we process and validate your proxies...
              </Paragraph>
            </div>
          </Card>
        );
      case 2:
        if (!importResult) return null;
        
        const successRate = importResult.totalProcessed > 0 
          ? Math.round((importResult.successCount / importResult.totalProcessed) * 100) 
          : 0;
        
        return (
          <>
            <Card style={{ marginBottom: 24 }}>
              <Result
                status={importResult.successCount > 0 ? "success" : "warning"}
                title="Import Completed"
                subTitle={`Processed ${importResult.totalProcessed} proxies with ${importResult.successCount} successful and ${importResult.failCount} failed.`}
                extra={[
                  <Button 
                    type="primary" 
                    key="list" 
                    onClick={() => navigate('/admin/proxies/list')}
                  >
                    Go to Proxy List
                  </Button>,
                  <Button 
                    key="again" 
                    onClick={() => {
                      setCurrentStep(0);
                      setFileList([]);
                      setImportResult(null);
                    }}
                  >
                    Import Another File
                  </Button>,
                ]}
              />
              
              <Row gutter={16} style={{ marginTop: 24 }}>
                <Col span={8}>
                  <Statistic 
                    title="Total Processed" 
                    value={importResult.totalProcessed} 
                    prefix={<InfoCircleOutlined />} 
                  />
                </Col>
                <Col span={8}>
                  <Statistic 
                    title="Successfully Imported" 
                    value={importResult.successCount} 
                    valueStyle={{ color: '#3f8600' }}
                    prefix={<CheckCircleOutlined />} 
                  />
                </Col>
                <Col span={8}>
                  <Statistic 
                    title="Failed" 
                    value={importResult.failCount} 
                    valueStyle={{ color: '#cf1322' }}
                    prefix={<CloseCircleOutlined />} 
                  />
                </Col>
              </Row>
              
              <div style={{ marginTop: 24 }}>
                <Progress 
                  percent={successRate} 
                  status={successRate === 100 ? "success" : (successRate === 0 ? "exception" : "active")}
                  format={percent => `${percent}% Success Rate`}
                />
              </div>
            </Card>
            
            {importResult.errors.length > 0 && (
              <Card title="Import Errors" style={{ marginBottom: 24 }}>
                <Table
                  columns={errorColumns}
                  dataSource={importResult.errors}
                  rowKey={(record) => `${record.rowNumber}-${record.errorMessage}`}
                  pagination={{ pageSize: 5 }}
                />
              </Card>
            )}
          </>
        );
      default:
        return null;
    }
  };

  const downloadTemplateCSV = () => {
    const csvContent = "IP Address,Port,Protocol,Country,City,Notes\n192.168.1.1,8080,HTTP,United States,New York,Example proxy";
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', 'proxy_template.csv');
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  return (
    <div>
      <Breadcrumb
        items={[
          { title: 'Home' },
          { title: 'Admin' },
          { title: <a onClick={() => navigate('/admin/proxies/list')}>Proxies</a> },
          { title: 'Import Proxies' }
        ]}
        style={{ marginBottom: 16 }}
      />
      
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
        <Title level={4}>Import Proxies</Title>
        <Space>
          <Button 
            icon={<DownloadOutlined />} 
            onClick={downloadTemplateCSV}
          >
            Download Template
          </Button>
          <Button 
            icon={<ArrowLeftOutlined />} 
            onClick={() => navigate('/admin/proxies/list')}
          >
            Back to List
          </Button>
        </Space>
      </div>
      
      <Steps
        current={currentStep}
        items={[
          {
            title: 'Upload File',
            description: 'Select and upload file',
          },
          {
            title: 'Processing',
            description: 'Validating proxies',
          },
          {
            title: 'Complete',
            description: 'View results',
          },
        ]}
        style={{ marginBottom: 24 }}
      />
      
      {renderStepContent()}
      
      {currentStep === 0 && (
        <Card style={{ marginTop: 24 }}>
          <Title level={5}>
            <InfoCircleOutlined /> Tips for Successful Import
          </Title>
          <ul>
            <li>Make sure your file follows the required format</li>
            <li>IP addresses should be in the standard format (e.g., 192.168.1.1)</li>
            <li>Ports should be valid numbers between 1-65535</li>
            <li>Protocols must be one of: HTTP, HTTPS, SOCKS4, SOCKS5</li>
            <li>Each proxy will be automatically checked for validity during import</li>
            <li>Large files may take some time to process</li>
          </ul>
        </Card>
      )}
    </div>
  );
};

export default ProxyImport;
