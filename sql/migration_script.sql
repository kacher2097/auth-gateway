-- Script chuyển đổi dữ liệu từ MongoDB sang PostgreSQL
-- Lưu ý: Script này giả định rằng bạn đã xuất dữ liệu từ MongoDB sang các bảng tạm trong PostgreSQL

-- Tạo bảng tạm để lưu trữ dữ liệu từ MongoDB
CREATE TABLE IF NOT EXISTS temp_mongo_users (
    id VARCHAR(255),
    username VARCHAR(255),
    email VARCHAR(255),
    password VARCHAR(255),
    full_name VARCHAR(255),
    avatar VARCHAR(255),
    role VARCHAR(50),
    role_id VARCHAR(255),
    active BOOLEAN,
    last_login TIMESTAMP,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    provider VARCHAR(50),
    social_provider VARCHAR(50),
    social_id VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS temp_mongo_roles (
    id VARCHAR(255),
    name VARCHAR(255),
    display_name VARCHAR(255),
    description TEXT,
    is_system BOOLEAN,
    permission_ids TEXT, -- JSON array as text
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS temp_mongo_permissions (
    id VARCHAR(255),
    name VARCHAR(255),
    display_name VARCHAR(255),
    description TEXT,
    category VARCHAR(100),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS temp_mongo_free_proxies (
    id VARCHAR(255),
    ip_address VARCHAR(255),
    port INTEGER,
    protocol VARCHAR(50),
    country VARCHAR(100),
    city VARCHAR(100),
    is_active BOOLEAN,
    response_time_ms BIGINT,
    last_checked TIMESTAMP,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    success_count INTEGER,
    fail_count INTEGER,
    uptime DOUBLE PRECISION,
    notes TEXT
);

CREATE TABLE IF NOT EXISTS temp_mongo_access_logs (
    id VARCHAR(255),
    user_id VARCHAR(255),
    username VARCHAR(255),
    ip_address VARCHAR(255),
    user_agent TEXT,
    endpoint VARCHAR(500),
    method VARCHAR(10),
    status_code INTEGER,
    timestamp TIMESTAMP,
    session_id VARCHAR(255),
    referrer TEXT,
    response_time_ms BIGINT,
    country VARCHAR(100),
    city VARCHAR(100),
    browser VARCHAR(100),
    operating_system VARCHAR(100),
    device_type VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS temp_mongo_payment_methods (
    id VARCHAR(255),
    name VARCHAR(255),
    display_name VARCHAR(255),
    description TEXT,
    provider_type VARCHAR(100),
    is_active BOOLEAN,
    config TEXT, -- JSON object as text
    icon_url VARCHAR(255),
    fee_percentage DOUBLE PRECISION,
    fixed_fee BIGINT,
    currency VARCHAR(10),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS temp_mongo_password_reset_tokens (
    id VARCHAR(255),
    token VARCHAR(255),
    user_id VARCHAR(255),
    expiry_date TIMESTAMP,
    used BOOLEAN
);

-- Hàm để chuyển đổi dữ liệu từ bảng tạm sang bảng chính thức
CREATE OR REPLACE FUNCTION migrate_mongo_data() RETURNS VOID AS $$
DECLARE
    mongo_permission_id VARCHAR(255);
    postgres_permission_id BIGINT;
    mongo_role_id VARCHAR(255);
    postgres_role_id BIGINT;
    mongo_user_id VARCHAR(255);
    postgres_user_id BIGINT;
    mongo_proxy_id VARCHAR(255);
    postgres_proxy_id BIGINT;
    mongo_payment_method_id VARCHAR(255);
    postgres_payment_method_id BIGINT;
    permission_ids_json JSON;
    permission_id VARCHAR(255);
    config_json JSON;
    config_key VARCHAR(255);
    config_value TEXT;
BEGIN
    -- Chuyển đổi permissions
    FOR mongo_permission_id IN SELECT id FROM temp_mongo_permissions LOOP
        INSERT INTO permissions (
            id, name, display_name, description, category, created_at, updated_at
        )
        SELECT 
            nextval('permission_sequence'), name, display_name, description, category, created_at, updated_at
        FROM 
            temp_mongo_permissions
        WHERE 
            id = mongo_permission_id
        RETURNING id INTO postgres_permission_id;
        
        -- Lưu mapping giữa MongoDB ID và PostgreSQL ID
        INSERT INTO mongo_postgres_id_mapping (mongo_id, postgres_id, entity_type)
        VALUES (mongo_permission_id, postgres_permission_id, 'permission');
    END LOOP;
    
    -- Chuyển đổi roles
    FOR mongo_role_id IN SELECT id FROM temp_mongo_roles LOOP
        INSERT INTO roles (
            id, name, display_name, description, is_system, created_at, updated_at
        )
        SELECT 
            nextval('role_sequence'), name, display_name, description, is_system, created_at, updated_at
        FROM 
            temp_mongo_roles
        WHERE 
            id = mongo_role_id
        RETURNING id INTO postgres_role_id;
        
        -- Lưu mapping giữa MongoDB ID và PostgreSQL ID
        INSERT INTO mongo_postgres_id_mapping (mongo_id, postgres_id, entity_type)
        VALUES (mongo_role_id, postgres_role_id, 'role');
        
        -- Chuyển đổi permission_ids từ JSON array sang các bản ghi trong bảng role_permissions
        SELECT permission_ids::json INTO permission_ids_json FROM temp_mongo_roles WHERE id = mongo_role_id;
        
        IF permission_ids_json IS NOT NULL THEN
            FOR permission_id IN SELECT json_array_elements_text(permission_ids_json) LOOP
                INSERT INTO role_permissions (role_id, permission_id)
                VALUES (postgres_role_id, permission_id);
            END LOOP;
        END IF;
    END LOOP;
    
    -- Chuyển đổi users
    FOR mongo_user_id IN SELECT id FROM temp_mongo_users LOOP
        INSERT INTO users (
            id, username, email, password, full_name, avatar, role, role_id, active, 
            last_login, created_at, updated_at, provider, social_provider, social_id
        )
        SELECT 
            nextval('user_sequence'), username, email, password, full_name, avatar, role, 
            (SELECT postgres_id FROM mongo_postgres_id_mapping WHERE mongo_id = role_id AND entity_type = 'role')::text,
            active, last_login, created_at, updated_at, provider, social_provider, social_id
        FROM 
            temp_mongo_users
        WHERE 
            id = mongo_user_id
        RETURNING id INTO postgres_user_id;
        
        -- Lưu mapping giữa MongoDB ID và PostgreSQL ID
        INSERT INTO mongo_postgres_id_mapping (mongo_id, postgres_id, entity_type)
        VALUES (mongo_user_id, postgres_user_id, 'user');
    END LOOP;
    
    -- Chuyển đổi free_proxies
    FOR mongo_proxy_id IN SELECT id FROM temp_mongo_free_proxies LOOP
        INSERT INTO free_proxies (
            id, ip_address, port, protocol, country, city, is_active, response_time_ms, 
            last_checked, created_at, updated_at, created_by, success_count, fail_count, uptime, notes
        )
        SELECT 
            nextval('proxy_sequence'), ip_address, port, protocol, country, city, is_active, response_time_ms, 
            last_checked, created_at, updated_at, 
            (SELECT postgres_id FROM mongo_postgres_id_mapping WHERE mongo_id = created_by AND entity_type = 'user')::text,
            success_count, fail_count, uptime, notes
        FROM 
            temp_mongo_free_proxies
        WHERE 
            id = mongo_proxy_id
        RETURNING id INTO postgres_proxy_id;
        
        -- Lưu mapping giữa MongoDB ID và PostgreSQL ID
        INSERT INTO mongo_postgres_id_mapping (mongo_id, postgres_id, entity_type)
        VALUES (mongo_proxy_id, postgres_proxy_id, 'proxy');
    END LOOP;
    
    -- Chuyển đổi access_logs
    INSERT INTO access_logs (
        id, user_id, username, ip_address, user_agent, endpoint, method, status_code, 
        timestamp, session_id, referrer, response_time_ms, country, city, browser, operating_system, device_type
    )
    SELECT 
        nextval('access_log_sequence'), 
        (SELECT postgres_id FROM mongo_postgres_id_mapping WHERE mongo_id = user_id AND entity_type = 'user')::text,
        username, ip_address, user_agent, endpoint, method, status_code, 
        timestamp, session_id, referrer, response_time_ms, country, city, browser, operating_system, device_type
    FROM 
        temp_mongo_access_logs;
    
    -- Chuyển đổi payment_methods
    FOR mongo_payment_method_id IN SELECT id FROM temp_mongo_payment_methods LOOP
        INSERT INTO payment_methods (
            id, name, display_name, description, provider_type, is_active, 
            icon_url, fee_percentage, fixed_fee, currency, created_at, updated_at
        )
        SELECT 
            nextval('payment_method_sequence'), name, display_name, description, provider_type, is_active, 
            icon_url, fee_percentage, fixed_fee, currency, created_at, updated_at
        FROM 
            temp_mongo_payment_methods
        WHERE 
            id = mongo_payment_method_id
        RETURNING id INTO postgres_payment_method_id;
        
        -- Lưu mapping giữa MongoDB ID và PostgreSQL ID
        INSERT INTO mongo_postgres_id_mapping (mongo_id, postgres_id, entity_type)
        VALUES (mongo_payment_method_id, postgres_payment_method_id, 'payment_method');
        
        -- Chuyển đổi config từ JSON object sang các bản ghi trong bảng payment_method_configs
        SELECT config::json INTO config_json FROM temp_mongo_payment_methods WHERE id = mongo_payment_method_id;
        
        IF config_json IS NOT NULL THEN
            FOR config_key, config_value IN SELECT * FROM json_each_text(config_json) LOOP
                INSERT INTO payment_method_configs (payment_method_id, config_key, config_value)
                VALUES (postgres_payment_method_id, config_key, config_value);
            END LOOP;
        END IF;
    END LOOP;
    
    -- Chuyển đổi password_reset_tokens
    INSERT INTO password_reset_tokens (
        id, token, user_id, expiry_date, used
    )
    SELECT 
        nextval('password_reset_token_sequence'), token, 
        (SELECT postgres_id FROM mongo_postgres_id_mapping WHERE mongo_id = user_id AND entity_type = 'user')::text,
        expiry_date, used
    FROM 
        temp_mongo_password_reset_tokens;
END;
$$ LANGUAGE plpgsql;

-- Tạo bảng để lưu trữ mapping giữa MongoDB ID và PostgreSQL ID
CREATE TABLE IF NOT EXISTS mongo_postgres_id_mapping (
    mongo_id VARCHAR(255) NOT NULL,
    postgres_id BIGINT NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    PRIMARY KEY (mongo_id, entity_type)
);

-- Tạo index cho bảng mapping
CREATE INDEX idx_mongo_postgres_id_mapping_mongo_id ON mongo_postgres_id_mapping(mongo_id);
CREATE INDEX idx_mongo_postgres_id_mapping_postgres_id ON mongo_postgres_id_mapping(postgres_id);
CREATE INDEX idx_mongo_postgres_id_mapping_entity_type ON mongo_postgres_id_mapping(entity_type);

-- Hướng dẫn sử dụng:
-- 1. Xuất dữ liệu từ MongoDB sang các bảng tạm trong PostgreSQL
-- 2. Chạy hàm migrate_mongo_data() để chuyển đổi dữ liệu
-- SELECT migrate_mongo_data();
-- 3. Xóa các bảng tạm sau khi hoàn thành
-- DROP TABLE temp_mongo_users;
-- DROP TABLE temp_mongo_roles;
-- DROP TABLE temp_mongo_permissions;
-- DROP TABLE temp_mongo_free_proxies;
-- DROP TABLE temp_mongo_access_logs;
-- DROP TABLE temp_mongo_payment_methods;
-- DROP TABLE temp_mongo_password_reset_tokens;
-- DROP TABLE mongo_postgres_id_mapping;
