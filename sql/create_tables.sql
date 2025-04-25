-- Tạo các sequence cho các bảng
-- Sequence cho bảng users
CREATE SEQUENCE IF NOT EXISTS user_sequence
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

-- Sequence cho bảng roles
CREATE SEQUENCE IF NOT EXISTS role_sequence
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

-- Sequence cho bảng permissions
CREATE SEQUENCE IF NOT EXISTS permission_sequence
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

-- Sequence cho bảng free_proxies
CREATE SEQUENCE IF NOT EXISTS proxy_sequence
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

-- Sequence cho bảng access_logs
CREATE SEQUENCE IF NOT EXISTS access_log_sequence
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

-- Sequence cho bảng payment_methods
CREATE SEQUENCE IF NOT EXISTS payment_method_sequence
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

-- Sequence cho bảng password_reset_tokens
CREATE SEQUENCE IF NOT EXISTS password_reset_token_sequence
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

-- Tạo bảng permissions (Quyền)
CREATE TABLE IF NOT EXISTS permissions (
    -- ID tự tăng
    id BIGINT NOT NULL PRIMARY KEY,
    -- Tên quyền, không được trùng lặp
    name VARCHAR(255) NOT NULL UNIQUE,
    -- Tên hiển thị của quyền
    display_name VARCHAR(255) NOT NULL,
    -- Mô tả về quyền
    description TEXT,
    -- Danh mục của quyền
    category VARCHAR(100) NOT NULL,
    -- Thời gian tạo
    created_at TIMESTAMP,
    -- Thời gian cập nhật
    updated_at TIMESTAMP
);

-- Tạo bảng roles (Vai trò)
CREATE TABLE IF NOT EXISTS roles (
    -- ID tự tăng
    id BIGINT NOT NULL PRIMARY KEY,
    -- Tên vai trò, không được trùng lặp
    name VARCHAR(255) NOT NULL UNIQUE,
    -- Tên hiển thị của vai trò
    display_name VARCHAR(255) NOT NULL,
    -- Mô tả về vai trò
    description TEXT,
    -- Có phải là vai trò hệ thống không
    is_system BOOLEAN DEFAULT FALSE,
    -- Thời gian tạo
    created_at TIMESTAMP,
    -- Thời gian cập nhật
    updated_at TIMESTAMP
);

-- Tạo bảng role_permissions (Quyền của vai trò)
CREATE TABLE IF NOT EXISTS role_permissions (
    -- ID vai trò
    role_id BIGINT NOT NULL,
    -- ID quyền
    permission_id VARCHAR(255) NOT NULL,
    -- Khóa ngoại tới bảng roles
    CONSTRAINT fk_role_permissions_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);

-- Tạo bảng users (Người dùng)
CREATE TABLE IF NOT EXISTS users (
    -- ID tự tăng
    id BIGINT NOT NULL PRIMARY KEY,
    -- Tên đăng nhập, không được trùng lặp
    username VARCHAR(255) NOT NULL UNIQUE,
    -- Email, không được trùng lặp
    email VARCHAR(255) NOT NULL UNIQUE,
    -- Mật khẩu đã được mã hóa
    password VARCHAR(255) NOT NULL,
    -- Họ tên đầy đủ
    full_name VARCHAR(255),
    -- Đường dẫn đến avatar
    avatar VARCHAR(255),
    -- Vai trò (enum: ADMIN, USER, MODERATOR)
    role VARCHAR(50),
    -- ID vai trò (liên kết với bảng roles)
    role_id VARCHAR(255),
    -- Trạng thái hoạt động
    active BOOLEAN DEFAULT TRUE,
    -- Thời gian đăng nhập cuối cùng
    last_login TIMESTAMP,
    -- Thời gian tạo
    created_at TIMESTAMP,
    -- Thời gian cập nhật
    updated_at TIMESTAMP,
    -- Nhà cung cấp xác thực
    provider VARCHAR(50),
    -- Nhà cung cấp xã hội (Google, Facebook, etc.)
    social_provider VARCHAR(50),
    -- ID từ nhà cung cấp xã hội
    social_id VARCHAR(255)
);

-- Tạo bảng free_proxies (Proxy miễn phí)
CREATE TABLE IF NOT EXISTS free_proxies (
    -- ID tự tăng
    id BIGINT NOT NULL PRIMARY KEY,
    -- Địa chỉ IP
    ip_address VARCHAR(255) NOT NULL,
    -- Cổng
    port INTEGER NOT NULL,
    -- Giao thức (HTTP, HTTPS, SOCKS4, SOCKS5)
    protocol VARCHAR(50) NOT NULL,
    -- Quốc gia
    country VARCHAR(100),
    -- Thành phố
    city VARCHAR(100),
    -- Trạng thái hoạt động
    is_active BOOLEAN DEFAULT TRUE,
    -- Thời gian phản hồi (ms)
    response_time_ms BIGINT,
    -- Thời gian kiểm tra cuối cùng
    last_checked TIMESTAMP,
    -- Thời gian tạo
    created_at TIMESTAMP,
    -- Thời gian cập nhật
    updated_at TIMESTAMP,
    -- Người tạo (ID người dùng)
    created_by VARCHAR(255),
    -- Số lần thành công
    success_count INTEGER DEFAULT 0,
    -- Số lần thất bại
    fail_count INTEGER DEFAULT 0,
    -- Tỷ lệ uptime (%)
    uptime DOUBLE PRECISION DEFAULT 0,
    -- Ghi chú
    notes TEXT
);

-- Tạo bảng access_logs (Nhật ký truy cập)
CREATE TABLE IF NOT EXISTS access_logs (
    -- ID tự tăng
    id BIGINT NOT NULL PRIMARY KEY,
    -- ID người dùng
    user_id VARCHAR(255),
    -- Tên người dùng
    username VARCHAR(255),
    -- Địa chỉ IP
    ip_address VARCHAR(255),
    -- User-Agent
    user_agent TEXT,
    -- Endpoint được truy cập
    endpoint VARCHAR(500),
    -- Phương thức HTTP
    method VARCHAR(10),
    -- Mã trạng thái HTTP
    status_code INTEGER,
    -- Thời gian truy cập
    timestamp TIMESTAMP,
    -- ID phiên
    session_id VARCHAR(255),
    -- Trang giới thiệu
    referrer TEXT,
    -- Thời gian phản hồi (ms)
    response_time_ms BIGINT,
    -- Quốc gia
    country VARCHAR(100),
    -- Thành phố
    city VARCHAR(100),
    -- Trình duyệt
    browser VARCHAR(100),
    -- Hệ điều hành
    operating_system VARCHAR(100),
    -- Loại thiết bị (MOBILE, DESKTOP, TABLET)
    device_type VARCHAR(50)
);

-- Tạo bảng payment_methods (Phương thức thanh toán)
CREATE TABLE IF NOT EXISTS payment_methods (
    -- ID tự tăng
    id BIGINT NOT NULL PRIMARY KEY,
    -- Tên phương thức, không được trùng lặp
    name VARCHAR(255) NOT NULL UNIQUE,
    -- Tên hiển thị
    display_name VARCHAR(255) NOT NULL,
    -- Mô tả
    description TEXT,
    -- Loại nhà cung cấp
    provider_type VARCHAR(100) NOT NULL,
    -- Trạng thái hoạt động
    is_active BOOLEAN DEFAULT TRUE,
    -- URL của biểu tượng
    icon_url VARCHAR(255),
    -- Phần trăm phí
    fee_percentage DOUBLE PRECISION DEFAULT 0,
    -- Phí cố định
    fixed_fee BIGINT DEFAULT 0,
    -- Đơn vị tiền tệ
    currency VARCHAR(10),
    -- Thời gian tạo
    created_at TIMESTAMP,
    -- Thời gian cập nhật
    updated_at TIMESTAMP
);

-- Tạo bảng payment_method_configs (Cấu hình phương thức thanh toán)
CREATE TABLE IF NOT EXISTS payment_method_configs (
    -- ID phương thức thanh toán
    payment_method_id BIGINT NOT NULL,
    -- Khóa cấu hình
    config_key VARCHAR(255) NOT NULL,
    -- Giá trị cấu hình
    config_value TEXT,
    -- Khóa chính
    PRIMARY KEY (payment_method_id, config_key),
    -- Khóa ngoại tới bảng payment_methods
    CONSTRAINT fk_payment_method_configs_payment_method FOREIGN KEY (payment_method_id) REFERENCES payment_methods (id) ON DELETE CASCADE
);

-- Tạo bảng password_reset_tokens (Token đặt lại mật khẩu)
CREATE TABLE IF NOT EXISTS password_reset_tokens (
    -- ID tự tăng
    id BIGINT NOT NULL PRIMARY KEY,
    -- Token, không được trùng lặp
    token VARCHAR(255) NOT NULL UNIQUE,
    -- ID người dùng
    user_id VARCHAR(255) NOT NULL,
    -- Thời gian hết hạn
    expiry_date TIMESTAMP NOT NULL,
    -- Đã sử dụng chưa
    used BOOLEAN DEFAULT FALSE
);

SELECT setval('seq_payment_order', 2145, true);

SELECT nextval('permission_role_sequence');
alter sequence role_sequence cache 1 restart;

ALTER TABLE role_permissions
    ALTER COLUMN id SET DEFAULT nextval('permission_role_sequence');


SELECT terminal_id, (extra_data::json)->>'supportTranType' AS supportTranType
FROM movnpay.terminal_service
WHERE (extra_data::json)->>'supportTranType' = 'GD trả góp' AND service_id = 2;




-- Tạo các chỉ mục để tối ưu truy vấn
-- Chỉ mục cho bảng users
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_role_id ON users(role_id);
CREATE INDEX idx_users_active ON users(active);

-- Chỉ mục cho bảng roles
CREATE INDEX idx_roles_name ON roles(name);

-- Chỉ mục cho bảng permissions
CREATE INDEX idx_permissions_name ON permissions(name);
CREATE INDEX idx_permissions_category ON permissions(category);

-- Chỉ mục cho bảng free_proxies
CREATE INDEX idx_free_proxies_ip_port ON free_proxies(ip_address, port);
CREATE INDEX idx_free_proxies_protocol ON free_proxies(protocol);
CREATE INDEX idx_free_proxies_country ON free_proxies(country);
CREATE INDEX idx_free_proxies_is_active ON free_proxies(is_active);

-- Chỉ mục cho bảng access_logs
CREATE INDEX idx_access_logs_user_id ON access_logs(user_id);
CREATE INDEX idx_access_logs_timestamp ON access_logs(timestamp);
CREATE INDEX idx_access_logs_endpoint ON access_logs(endpoint);
CREATE INDEX idx_access_logs_ip_address ON access_logs(ip_address);

-- Chỉ mục cho bảng payment_methods
CREATE INDEX idx_payment_methods_name ON payment_methods(name);
CREATE INDEX idx_payment_methods_provider_type ON payment_methods(provider_type);
CREATE INDEX idx_payment_methods_is_active ON payment_methods(is_active);

-- Chỉ mục cho bảng password_reset_tokens
CREATE INDEX idx_password_reset_tokens_token ON password_reset_tokens(token);
CREATE INDEX idx_password_reset_tokens_user_id ON password_reset_tokens(user_id);
