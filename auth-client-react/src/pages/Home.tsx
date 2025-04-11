import React from 'react';
import { Typography, Card, Row, Col, Button } from 'antd';
import { RocketOutlined, BookOutlined, QuestionCircleOutlined } from '@ant-design/icons';
import MainLayout from '../components/layout/MainLayout';
import useAuth from '../hooks/useAuth';

const { Title, Paragraph } = Typography;

const Home: React.FC = () => {
  const { user } = useAuth();

  return (
    <MainLayout>
      <div style={{ padding: '24px' }}>
        <div style={{ marginBottom: '24px', textAlign: 'center' }}>
          <Title level={2}>Chào mừng đến với AuthenHub</Title>
          <Paragraph>
            {user ? `Xin chào, ${user.fullName}! ` : ''}
            Hệ thống xác thực an toàn và bảo mật cho ứng dụng của bạn
          </Paragraph>
        </div>

        <Row gutter={[16, 16]}>
          <Col xs={24} md={8}>
            <Card
              title="Bắt đầu nhanh"
              hoverable
              extra={<RocketOutlined />}
              actions={[
                <Button type="link" key="start">Xem hướng dẫn</Button>
              ]}
            >
              <p>Bắt đầu sử dụng hệ thống xác thực của chúng tôi một cách dễ dàng</p>
            </Card>
          </Col>
          <Col xs={24} md={8}>
            <Card
              title="Tài liệu"
              hoverable
              extra={<BookOutlined />}
              actions={[
                <Button type="link" key="docs">Xem tài liệu</Button>
              ]}
            >
              <p>Tìm hiểu thêm về các tính năng và API của chúng tôi</p>
            </Card>
          </Col>
          <Col xs={24} md={8}>
            <Card
              title="Hỗ trợ"
              hoverable
              extra={<QuestionCircleOutlined />}
              actions={[
                <Button type="link" key="support">Liên hệ hỗ trợ</Button>
              ]}
            >
              <p>Cần giúp đỡ? Liên hệ với đội ngũ hỗ trợ của chúng tôi</p>
            </Card>
          </Col>
        </Row>
      </div>
    </MainLayout>
  );
};

export default Home;