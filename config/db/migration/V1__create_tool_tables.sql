-- Create sequences
CREATE SEQUENCE blog_crawler_job_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE web_crawler_job_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE api_test_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE data_extractor_job_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE code_generation_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE chat_session_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE chat_message_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE affiliate_scraper_job_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE tracked_product_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE price_history_seq START WITH 1 INCREMENT BY 1;

-- Create blog_crawler_jobs table
CREATE TABLE blog_crawler_jobs (
    id BIGINT PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    url VARCHAR(1000) NOT NULL,
    category VARCHAR(255),
    search_keyword VARCHAR(255),
    date_from VARCHAR(50),
    date_to VARCHAR(50),
    author VARCHAR(255),
    sort_by VARCHAR(50),
    sort_order VARCHAR(10),
    created_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    error_message VARCHAR(1000),
    total_posts INTEGER,
    result_json TEXT
);

-- Create web_crawler_jobs table
CREATE TABLE web_crawler_jobs (
    id BIGINT PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    url VARCHAR(1000) NOT NULL,
    depth INTEGER,
    max_pages INTEGER,
    include_pattern VARCHAR(255),
    exclude_pattern VARCHAR(255),
    user_agent VARCHAR(500),
    timeout INTEGER,
    content_types TEXT,
    css_selector VARCHAR(500),
    xpath VARCHAR(500),
    respect_robots_txt BOOLEAN,
    schedule_type VARCHAR(20),
    schedule_time VARCHAR(50),
    created_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    next_run_at TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    error_message VARCHAR(1000),
    total_pages INTEGER,
    crawl_duration VARCHAR(50),
    result_json TEXT
);

-- Create api_tests table
CREATE TABLE api_tests (
    id BIGINT PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    method VARCHAR(20) NOT NULL,
    url VARCHAR(1000) NOT NULL,
    content_type VARCHAR(100),
    request_body TEXT,
    request_headers TEXT,
    timeout INTEGER,
    follow_redirects BOOLEAN,
    created_at TIMESTAMP NOT NULL,
    status_code INTEGER,
    status_text VARCHAR(100),
    response_headers TEXT,
    response_body TEXT,
    response_time VARCHAR(50),
    response_size BIGINT,
    response_content_type VARCHAR(100),
    error VARCHAR(1000),
    name VARCHAR(255),
    description VARCHAR(1000),
    favorite BOOLEAN
);

-- Create data_extractor_jobs table
CREATE TABLE data_extractor_jobs (
    id BIGINT PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    extraction_type VARCHAR(20) NOT NULL,
    url VARCHAR(1000),
    text TEXT,
    file_content TEXT,
    file_type VARCHAR(50),
    database_type VARCHAR(50),
    database_url VARCHAR(500),
    database_username VARCHAR(100),
    data_types TEXT,
    created_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    error_message VARCHAR(1000),
    total_items INTEGER,
    type_counts TEXT,
    extraction_time VARCHAR(50),
    result_json TEXT
);

-- Create code_generations table
CREATE TABLE code_generations (
    id BIGINT PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    code_type VARCHAR(20) NOT NULL,
    model_name VARCHAR(100),
    framework VARCHAR(50),
    database VARCHAR(50),
    fields TEXT,
    component_name VARCHAR(100),
    component_type VARCHAR(20),
    features TEXT,
    api_name VARCHAR(100),
    auth_required BOOLEAN,
    language VARCHAR(20),
    created_at TIMESTAMP NOT NULL,
    file_name VARCHAR(255),
    generated_code TEXT,
    name VARCHAR(255),
    description VARCHAR(1000),
    favorite BOOLEAN
);

-- Create chat_sessions table
CREATE TABLE chat_sessions (
    id BIGINT PRIMARY KEY,
    session_id VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    last_message_at TIMESTAMP,
    title VARCHAR(255),
    message_count INTEGER,
    token_count INTEGER,
    model VARCHAR(20)
);

-- Create chat_messages table
CREATE TABLE chat_messages (
    id BIGINT PRIMARY KEY,
    message_id VARCHAR(255) NOT NULL,
    session_id VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    sender VARCHAR(10) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    token_count INTEGER,
    model VARCHAR(20)
);

-- Create affiliate_scraper_jobs table
CREATE TABLE affiliate_scraper_jobs (
    id BIGINT PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    platform VARCHAR(20) NOT NULL,
    keyword VARCHAR(255),
    category VARCHAR(255),
    min_price DOUBLE PRECISION,
    max_price DOUBLE PRECISION,
    min_rating INTEGER,
    min_sold_count INTEGER,
    sort_by VARCHAR(20),
    limit INTEGER,
    affiliate_id VARCHAR(100),
    created_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    error_message VARCHAR(1000),
    total_products INTEGER,
    total_commission DOUBLE PRECISION,
    result_json TEXT
);

-- Create tracked_products table
CREATE TABLE tracked_products (
    id BIGINT PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    product_id VARCHAR(255) NOT NULL,
    platform VARCHAR(20) NOT NULL,
    name VARCHAR(500) NOT NULL,
    url VARCHAR(1000) NOT NULL,
    image_url VARCHAR(1000),
    original_price DOUBLE PRECISION NOT NULL,
    current_price DOUBLE PRECISION NOT NULL,
    lowest_price DOUBLE PRECISION,
    highest_price DOUBLE PRECISION,
    lowest_price_date TIMESTAMP,
    highest_price_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    last_checked_at TIMESTAMP,
    affiliate_url VARCHAR(1000),
    commission DOUBLE PRECISION,
    notify_on_price_change BOOLEAN,
    target_price DOUBLE PRECISION,
    notify_on_target_price BOOLEAN
);

-- Create price_history table
CREATE TABLE price_history (
    id BIGINT PRIMARY KEY,
    tracked_product_id BIGINT NOT NULL,
    price DOUBLE PRECISION NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    discount_percent DOUBLE PRECISION,
    is_lowest_price BOOLEAN,
    is_highest_price BOOLEAN,
    CONSTRAINT fk_price_history_tracked_product FOREIGN KEY (tracked_product_id) REFERENCES tracked_products(id)
);

-- Create indexes
CREATE INDEX idx_blog_crawler_jobs_user_id ON blog_crawler_jobs(user_id);
CREATE INDEX idx_web_crawler_jobs_user_id ON web_crawler_jobs(user_id);
CREATE INDEX idx_api_tests_user_id ON api_tests(user_id);
CREATE INDEX idx_data_extractor_jobs_user_id ON data_extractor_jobs(user_id);
CREATE INDEX idx_code_generations_user_id ON code_generations(user_id);
CREATE INDEX idx_chat_sessions_user_id ON chat_sessions(user_id);
CREATE INDEX idx_chat_sessions_session_id ON chat_sessions(session_id);
CREATE INDEX idx_chat_messages_session_id ON chat_messages(session_id);
CREATE INDEX idx_affiliate_scraper_jobs_user_id ON affiliate_scraper_jobs(user_id);
CREATE INDEX idx_tracked_products_user_id ON tracked_products(user_id);
CREATE INDEX idx_tracked_products_product_id ON tracked_products(product_id);
CREATE INDEX idx_price_history_tracked_product_id ON price_history(tracked_product_id);
