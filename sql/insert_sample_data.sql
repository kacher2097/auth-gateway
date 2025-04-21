-- Thêm dữ liệu mẫu cho bảng permissions
INSERT INTO permissions (id, name, display_name, description, category, created_at, updated_at)
VALUES
    (nextval('permission_sequence'), 'user:read', 'Xem người dùng', 'Quyền xem thông tin người dùng', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (nextval('permission_sequence'), 'user:create', 'Tạo người dùng', 'Quyền tạo người dùng mới', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (nextval('permission_sequence'), 'user:update', 'Cập nhật người dùng', 'Quyền cập nhật thông tin người dùng', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (nextval('permission_sequence'), 'user:delete', 'Xóa người dùng', 'Quyền xóa người dùng', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (nextval('permission_sequence'), 'role:read', 'Xem vai trò', 'Quyền xem thông tin vai trò', 'ROLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (nextval('permission_sequence'), 'role:create', 'Tạo vai trò', 'Quyền tạo vai trò mới', 'ROLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (nextval('permission_sequence'), 'role:update', 'Cập nhật vai trò', 'Quyền cập nhật thông tin vai trò', 'ROLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (nextval('permission_sequence'), 'role:delete', 'Xóa vai trò', 'Quyền xóa vai trò', 'ROLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (nextval('permission_sequence'), 'permission:read', 'Xem quyền', 'Quyền xem thông tin quyền', 'PERMISSION', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (nextval('permission_sequence'), 'permission:create', 'Tạo quyền', 'Quyền tạo quyền mới', 'PERMISSION', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (nextval('permission_sequence'), 'permission:update', 'Cập nhật quyền', 'Quyền cập nhật thông tin quyền', 'PERMISSION', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (nextval('permission_sequence'), 'permission:delete', 'Xóa quyền', 'Quyền xóa quyền', 'PERMISSION', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (nextval('permission_sequence'), 'proxy:read', 'Xem proxy', 'Quyền xem thông tin proxy', 'PROXY', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (nextval('permission_sequence'), 'proxy:create', 'Tạo proxy', 'Quyền tạo proxy mới', 'PROXY', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (nextval('permission_sequence'), 'proxy:update', 'Cập nhật proxy', 'Quyền cập nhật thông tin proxy', 'PROXY', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (nextval('permission_sequence'), 'proxy:delete', 'Xóa proxy', 'Quyền xóa proxy', 'PROXY', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (nextval('permission_sequence'), 'analytics:view', 'Xem phân tích', 'Quyền xem dữ liệu phân tích', 'ANALYTICS', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (nextval('permission_sequence'), 'analytics:export', 'Xuất phân tích', 'Quyền xuất dữ liệu phân tích', 'ANALYTICS', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (nextval('permission_sequence'), 'settings:read', 'Xem cài đặt', 'Quyền xem cài đặt hệ thống', 'SETTINGS', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (nextval('permission_sequence'), 'settings:update', 'Cập nhật cài đặt', 'Quyền cập nhật cài đặt hệ thống', 'SETTINGS', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Thêm dữ liệu mẫu cho bảng roles
INSERT INTO roles (id, name, display_name, description, is_system, created_at, updated_at)
VALUES
    (nextval('role_sequence'), 'ADMIN', 'Quản trị viên', 'Có tất cả các quyền trong hệ thống', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (nextval('role_sequence'), 'USER', 'Người dùng', 'Người dùng thông thường', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (nextval('role_sequence'), 'MODERATOR', 'Người kiểm duyệt', 'Có quyền kiểm duyệt nội dung', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Lấy ID của vai trò ADMIN
DO $$
DECLARE
    admin_role_id BIGINT;
BEGIN
    SELECT id INTO admin_role_id FROM roles WHERE name = 'ADMIN';
    
    -- Thêm tất cả các quyền cho vai trò ADMIN
    INSERT INTO role_permissions (role_id, permission_id)
    SELECT admin_role_id, name FROM permissions;
END $$;

-- Lấy ID của vai trò USER
DO $$
DECLARE
    user_role_id BIGINT;
BEGIN
    SELECT id INTO user_role_id FROM roles WHERE name = 'USER';
    
    -- Thêm một số quyền cơ bản cho vai trò USER
    INSERT INTO role_permissions (role_id, permission_id)
    VALUES
        (user_role_id, 'user:read'),
        (user_role_id, 'proxy:read');
END $$;

-- Lấy ID của vai trò MODERATOR
DO $$
DECLARE
    moderator_role_id BIGINT;
BEGIN
    SELECT id INTO moderator_role_id FROM roles WHERE name = 'MODERATOR';
    
    -- Thêm quyền cho vai trò MODERATOR
    INSERT INTO role_permissions (role_id, permission_id)
    VALUES
        (moderator_role_id, 'user:read'),
        (moderator_role_id, 'user:update'),
        (moderator_role_id, 'proxy:read'),
        (moderator_role_id, 'proxy:create'),
        (moderator_role_id, 'proxy:update'),
        (moderator_role_id, 'proxy:delete'),
        (moderator_role_id, 'analytics:view');
END $$;

-- Thêm dữ liệu mẫu cho bảng users
-- Mật khẩu mẫu: "password" đã được mã hóa bằng BCrypt
INSERT INTO users (id, username, email, password, full_name, role, active, created_at, updated_at)
VALUES
    (nextval('user_sequence'), 'admin', 'admin@example.com', '$2a$10$rPiEAgQNIT1TCoKi.XaLIO4rCPZpnLFzZqUtJCqDu.1S7tBGdVXbC', 'Admin User', 'ADMIN', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (nextval('user_sequence'), 'user', 'user@example.com', '$2a$10$rPiEAgQNIT1TCoKi.XaLIO4rCPZpnLFzZqUtJCqDu.1S7tBGdVXbC', 'Normal User', 'USER', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (nextval('user_sequence'), 'moderator', 'moderator@example.com', '$2a$10$rPiEAgQNIT1TCoKi.XaLIO4rCPZpnLFzZqUtJCqDu.1S7tBGdVXbC', 'Moderator User', 'MODERATOR', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Cập nhật role_id cho users
DO $$
DECLARE
    admin_role_id BIGINT;
    user_role_id BIGINT;
    moderator_role_id BIGINT;
BEGIN
    SELECT id INTO admin_role_id FROM roles WHERE name = 'ADMIN';
    SELECT id INTO user_role_id FROM roles WHERE name = 'USER';
    SELECT id INTO moderator_role_id FROM roles WHERE name = 'MODERATOR';
    
    UPDATE users SET role_id = admin_role_id::text WHERE username = 'admin';
    UPDATE users SET role_id = user_role_id::text WHERE username = 'user';
    UPDATE users SET role_id = moderator_role_id::text WHERE username = 'moderator';
END $$;

-- Thêm dữ liệu mẫu cho bảng free_proxies
INSERT INTO free_proxies (id, ip_address, port, protocol, country, city, is_active, response_time_ms, created_at, updated_at, success_count, fail_count, uptime)
VALUES
    (nextval('proxy_sequence'), '192.168.1.1', 8080, 'HTTP', 'United States', 'New York', TRUE, 150, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 100, 5, 95.2),
    (nextval('proxy_sequence'), '192.168.1.2', 3128, 'HTTPS', 'Germany', 'Berlin', TRUE, 200, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 80, 10, 88.9),
    (nextval('proxy_sequence'), '192.168.1.3', 1080, 'SOCKS5', 'Japan', 'Tokyo', TRUE, 300, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 50, 2, 96.2),
    (nextval('proxy_sequence'), '192.168.1.4', 8888, 'HTTP', 'France', 'Paris', FALSE, 500, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 20, 30, 40.0),
    (nextval('proxy_sequence'), '192.168.1.5', 80, 'HTTP', 'United Kingdom', 'London', TRUE, 180, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 120, 8, 93.8);

-- Thêm dữ liệu mẫu cho bảng payment_methods
INSERT INTO payment_methods (id, name, display_name, description, provider_type, is_active, fee_percentage, fixed_fee, currency, created_at, updated_at)
VALUES
    (nextval('payment_method_sequence'), 'PAYPAL', 'PayPal', 'Thanh toán qua PayPal', 'PAYPAL', TRUE, 2.9, 30, 'USD', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (nextval('payment_method_sequence'), 'STRIPE', 'Stripe', 'Thanh toán qua Stripe', 'STRIPE', TRUE, 2.9, 30, 'USD', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (nextval('payment_method_sequence'), 'BANK_TRANSFER', 'Chuyển khoản ngân hàng', 'Thanh toán qua chuyển khoản ngân hàng', 'BANK', TRUE, 0, 0, 'USD', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Thêm cấu hình cho các phương thức thanh toán
DO $$
DECLARE
    paypal_id BIGINT;
    stripe_id BIGINT;
    bank_id BIGINT;
BEGIN
    SELECT id INTO paypal_id FROM payment_methods WHERE name = 'PAYPAL';
    SELECT id INTO stripe_id FROM payment_methods WHERE name = 'STRIPE';
    SELECT id INTO bank_id FROM payment_methods WHERE name = 'BANK_TRANSFER';
    
    -- Cấu hình PayPal
    INSERT INTO payment_method_configs (payment_method_id, config_key, config_value)
    VALUES
        (paypal_id, 'client_id', 'paypal_client_id_here'),
        (paypal_id, 'client_secret', 'paypal_client_secret_here'),
        (paypal_id, 'mode', 'sandbox');
    
    -- Cấu hình Stripe
    INSERT INTO payment_method_configs (payment_method_id, config_key, config_value)
    VALUES
        (stripe_id, 'api_key', 'stripe_api_key_here'),
        (stripe_id, 'webhook_secret', 'stripe_webhook_secret_here');
    
    -- Cấu hình chuyển khoản ngân hàng
    INSERT INTO payment_method_configs (payment_method_id, config_key, config_value)
    VALUES
        (bank_id, 'account_name', 'Company Name'),
        (bank_id, 'account_number', '1234567890'),
        (bank_id, 'bank_name', 'Example Bank'),
        (bank_id, 'swift_code', 'EXAMPLECODE');
END $$;

-- Thêm dữ liệu mẫu cho bảng access_logs
INSERT INTO access_logs (id, user_id, username, ip_address, endpoint, method, status_code, timestamp, response_time_ms, country, device_type)
VALUES
    (nextval('access_log_sequence'), '1', 'admin', '127.0.0.1', '/api/users', 'GET', 200, CURRENT_TIMESTAMP - INTERVAL '1 hour', 50, 'United States', 'DESKTOP'),
    (nextval('access_log_sequence'), '1', 'admin', '127.0.0.1', '/api/roles', 'GET', 200, CURRENT_TIMESTAMP - INTERVAL '50 minutes', 45, 'United States', 'DESKTOP'),
    (nextval('access_log_sequence'), '2', 'user', '127.0.0.2', '/api/proxies', 'GET', 200, CURRENT_TIMESTAMP - INTERVAL '40 minutes', 60, 'Germany', 'MOBILE'),
    (nextval('access_log_sequence'), '3', 'moderator', '127.0.0.3', '/api/proxies/1', 'PUT', 200, CURRENT_TIMESTAMP - INTERVAL '30 minutes', 70, 'Japan', 'TABLET'),
    (nextval('access_log_sequence'), null, null, '127.0.0.4', '/api/login', 'POST', 401, CURRENT_TIMESTAMP - INTERVAL '20 minutes', 30, 'France', 'DESKTOP'),
    (nextval('access_log_sequence'), '2', 'user', '127.0.0.2', '/api/proxies/2', 'GET', 200, CURRENT_TIMESTAMP - INTERVAL '10 minutes', 55, 'Germany', 'MOBILE'),
    (nextval('access_log_sequence'), '1', 'admin', '127.0.0.1', '/api/users/2', 'GET', 200, CURRENT_TIMESTAMP - INTERVAL '5 minutes', 40, 'United States', 'DESKTOP');
