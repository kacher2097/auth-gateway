-- Create sequences
CREATE SEQUENCE inventory_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE sales_history_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE replenishment_suggestion_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE forecast_data_seq START WITH 1 INCREMENT BY 1;

-- Create inventory table
CREATE TABLE inventory (
    id BIGINT PRIMARY KEY,
    sku VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL,
    low_stock_threshold INTEGER,
    category VARCHAR(100),
    description TEXT,
    cost_price DOUBLE PRECISION,
    selling_price DOUBLE PRECISION,
    supplier VARCHAR(255),
    location VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Create sales_history table
CREATE TABLE sales_history (
    id BIGINT PRIMARY KEY,
    sku VARCHAR(50) NOT NULL,
    quantity INTEGER NOT NULL,
    sale_date TIMESTAMP NOT NULL,
    sale_price DOUBLE PRECISION,
    channel VARCHAR(100),
    order_id VARCHAR(100),
    customer_id VARCHAR(100),
    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255)
);

-- Create replenishment_suggestions table
CREATE TABLE replenishment_suggestions (
    id BIGINT PRIMARY KEY,
    sku VARCHAR(50) NOT NULL,
    suggested_quantity INTEGER NOT NULL,
    reason TEXT NOT NULL,
    status VARCHAR(20) NOT NULL,
    approved_quantity INTEGER,
    approved_by VARCHAR(255),
    approved_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Create forecast_data table
CREATE TABLE forecast_data (
    id BIGINT PRIMARY KEY,
    sku VARCHAR(50) NOT NULL,
    forecast_date TIMESTAMP NOT NULL,
    predicted_quantity INTEGER NOT NULL,
    confidence_level DOUBLE PRECISION,
    forecast_model VARCHAR(50),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Create indexes
CREATE INDEX idx_inventory_sku ON inventory(sku);
CREATE INDEX idx_inventory_category ON inventory(category);
CREATE INDEX idx_inventory_low_stock ON inventory(quantity, low_stock_threshold);

CREATE INDEX idx_sales_history_sku ON sales_history(sku);
CREATE INDEX idx_sales_history_sale_date ON sales_history(sale_date);
CREATE INDEX idx_sales_history_sku_date ON sales_history(sku, sale_date);

CREATE INDEX idx_replenishment_suggestions_sku ON replenishment_suggestions(sku);
CREATE INDEX idx_replenishment_suggestions_status ON replenishment_suggestions(status);

CREATE INDEX idx_forecast_data_sku ON forecast_data(sku);
CREATE INDEX idx_forecast_data_date ON forecast_data(forecast_date);
CREATE INDEX idx_forecast_data_sku_date ON forecast_data(sku, forecast_date);

-- Insert sample data for testing
INSERT INTO inventory (id, sku, name, quantity, low_stock_threshold, category, description, cost_price, selling_price, supplier, location, created_at, created_by)
VALUES
    (nextval('inventory_seq'), 'PROD-001', 'Smartphone X', 25, 10, 'Electronics', 'Latest smartphone model', 400.0, 599.99, 'TechSupplier Inc.', 'Warehouse A', NOW(), 'system'),
    (nextval('inventory_seq'), 'PROD-002', 'Laptop Pro', 15, 5, 'Electronics', 'Professional laptop', 800.0, 1299.99, 'TechSupplier Inc.', 'Warehouse A', NOW(), 'system'),
    (nextval('inventory_seq'), 'PROD-003', 'Wireless Earbuds', 50, 20, 'Electronics', 'Bluetooth earbuds', 30.0, 79.99, 'AudioTech', 'Warehouse B', NOW(), 'system'),
    (nextval('inventory_seq'), 'PROD-004', 'Smart Watch', 20, 8, 'Electronics', 'Fitness tracking watch', 120.0, 199.99, 'WearableTech', 'Warehouse A', NOW(), 'system'),
    (nextval('inventory_seq'), 'PROD-005', 'USB-C Cable', 100, 30, 'Accessories', '1m USB-C charging cable', 2.0, 12.99, 'CableSupplier', 'Warehouse C', NOW(), 'system'),
    (nextval('inventory_seq'), 'PROD-006', 'Wireless Mouse', 35, 15, 'Accessories', 'Ergonomic wireless mouse', 15.0, 29.99, 'PeripheralTech', 'Warehouse B', NOW(), 'system'),
    (nextval('inventory_seq'), 'PROD-007', 'External SSD 1TB', 10, 5, 'Storage', '1TB external solid state drive', 80.0, 149.99, 'StorageSolutions', 'Warehouse A', NOW(), 'system'),
    (nextval('inventory_seq'), 'PROD-008', 'Bluetooth Speaker', 18, 8, 'Audio', 'Portable Bluetooth speaker', 25.0, 59.99, 'AudioTech', 'Warehouse B', NOW(), 'system'),
    (nextval('inventory_seq'), 'PROD-009', 'Tablet Pro', 12, 5, 'Electronics', '10-inch tablet', 200.0, 349.99, 'TechSupplier Inc.', 'Warehouse A', NOW(), 'system'),
    (nextval('inventory_seq'), 'PROD-010', 'HDMI Cable', 80, 25, 'Accessories', '2m HDMI cable', 3.0, 14.99, 'CableSupplier', 'Warehouse C', NOW(), 'system');

-- Insert sample sales history (last 30 days)
INSERT INTO sales_history (id, sku, quantity, sale_date, sale_price, channel, order_id, created_at, created_by)
VALUES
    -- PROD-001 (Smartphone X) sales
    (nextval('sales_history_seq'), 'PROD-001', 2, NOW() - INTERVAL '2 days', 599.99, 'Online', 'ORD-001', NOW(), 'system'),
    (nextval('sales_history_seq'), 'PROD-001', 1, NOW() - INTERVAL '5 days', 599.99, 'Store', 'ORD-002', NOW(), 'system'),
    (nextval('sales_history_seq'), 'PROD-001', 3, NOW() - INTERVAL '10 days', 599.99, 'Online', 'ORD-003', NOW(), 'system'),
    (nextval('sales_history_seq'), 'PROD-001', 2, NOW() - INTERVAL '15 days', 599.99, 'Store', 'ORD-004', NOW(), 'system'),
    (nextval('sales_history_seq'), 'PROD-001', 1, NOW() - INTERVAL '20 days', 599.99, 'Online', 'ORD-005', NOW(), 'system'),
    (nextval('sales_history_seq'), 'PROD-001', 2, NOW() - INTERVAL '25 days', 599.99, 'Store', 'ORD-006', NOW(), 'system'),
    
    -- PROD-002 (Laptop Pro) sales
    (nextval('sales_history_seq'), 'PROD-002', 1, NOW() - INTERVAL '3 days', 1299.99, 'Online', 'ORD-007', NOW(), 'system'),
    (nextval('sales_history_seq'), 'PROD-002', 1, NOW() - INTERVAL '8 days', 1299.99, 'Store', 'ORD-008', NOW(), 'system'),
    (nextval('sales_history_seq'), 'PROD-002', 2, NOW() - INTERVAL '14 days', 1299.99, 'Online', 'ORD-009', NOW(), 'system'),
    (nextval('sales_history_seq'), 'PROD-002', 1, NOW() - INTERVAL '22 days', 1299.99, 'Store', 'ORD-010', NOW(), 'system'),
    
    -- PROD-003 (Wireless Earbuds) sales
    (nextval('sales_history_seq'), 'PROD-003', 3, NOW() - INTERVAL '1 day', 79.99, 'Online', 'ORD-011', NOW(), 'system'),
    (nextval('sales_history_seq'), 'PROD-003', 2, NOW() - INTERVAL '4 days', 79.99, 'Store', 'ORD-012', NOW(), 'system'),
    (nextval('sales_history_seq'), 'PROD-003', 4, NOW() - INTERVAL '9 days', 79.99, 'Online', 'ORD-013', NOW(), 'system'),
    (nextval('sales_history_seq'), 'PROD-003', 3, NOW() - INTERVAL '12 days', 79.99, 'Store', 'ORD-014', NOW(), 'system'),
    (nextval('sales_history_seq'), 'PROD-003', 5, NOW() - INTERVAL '18 days', 79.99, 'Online', 'ORD-015', NOW(), 'system'),
    (nextval('sales_history_seq'), 'PROD-003', 3, NOW() - INTERVAL '24 days', 79.99, 'Store', 'ORD-016', NOW(), 'system'),
    
    -- PROD-004 (Smart Watch) sales
    (nextval('sales_history_seq'), 'PROD-004', 2, NOW() - INTERVAL '2 days', 199.99, 'Online', 'ORD-017', NOW(), 'system'),
    (nextval('sales_history_seq'), 'PROD-004', 1, NOW() - INTERVAL '7 days', 199.99, 'Store', 'ORD-018', NOW(), 'system'),
    (nextval('sales_history_seq'), 'PROD-004', 2, NOW() - INTERVAL '13 days', 199.99, 'Online', 'ORD-019', NOW(), 'system'),
    (nextval('sales_history_seq'), 'PROD-004', 3, NOW() - INTERVAL '19 days', 199.99, 'Store', 'ORD-020', NOW(), 'system'),
    
    -- PROD-005 (USB-C Cable) sales
    (nextval('sales_history_seq'), 'PROD-005', 5, NOW() - INTERVAL '1 day', 12.99, 'Online', 'ORD-021', NOW(), 'system'),
    (nextval('sales_history_seq'), 'PROD-005', 3, NOW() - INTERVAL '6 days', 12.99, 'Store', 'ORD-022', NOW(), 'system'),
    (nextval('sales_history_seq'), 'PROD-005', 7, NOW() - INTERVAL '11 days', 12.99, 'Online', 'ORD-023', NOW(), 'system'),
    (nextval('sales_history_seq'), 'PROD-005', 4, NOW() - INTERVAL '16 days', 12.99, 'Store', 'ORD-024', NOW(), 'system'),
    (nextval('sales_history_seq'), 'PROD-005', 6, NOW() - INTERVAL '21 days', 12.99, 'Online', 'ORD-025', NOW(), 'system'),
    (nextval('sales_history_seq'), 'PROD-005', 5, NOW() - INTERVAL '26 days', 12.99, 'Store', 'ORD-026', NOW(), 'system');

-- Insert sample replenishment suggestions
INSERT INTO replenishment_suggestions (id, sku, suggested_quantity, reason, status, created_at, created_by)
VALUES
    (nextval('replenishment_suggestion_seq'), 'PROD-001', 10, 'Based on average daily sales of 0.37 units', 'PENDING', NOW(), 'system'),
    (nextval('replenishment_suggestion_seq'), 'PROD-003', 15, 'Based on average daily sales of 0.67 units', 'PENDING', NOW(), 'system'),
    (nextval('replenishment_suggestion_seq'), 'PROD-005', 20, 'Based on average daily sales of 1.00 units', 'PENDING', NOW(), 'system');

-- Insert sample forecast data
INSERT INTO forecast_data (id, sku, forecast_date, predicted_quantity, confidence_level, forecast_model, created_at, created_by)
VALUES
    -- PROD-001 (Smartphone X) forecast
    (nextval('forecast_data_seq'), 'PROD-001', NOW() + INTERVAL '1 day', 2, 0.8, 'MOVING_AVERAGE', NOW(), 'system'),
    (nextval('forecast_data_seq'), 'PROD-001', NOW() + INTERVAL '2 days', 1, 0.8, 'MOVING_AVERAGE', NOW(), 'system'),
    (nextval('forecast_data_seq'), 'PROD-001', NOW() + INTERVAL '3 days', 2, 0.8, 'MOVING_AVERAGE', NOW(), 'system'),
    (nextval('forecast_data_seq'), 'PROD-001', NOW() + INTERVAL '4 days', 1, 0.8, 'MOVING_AVERAGE', NOW(), 'system'),
    (nextval('forecast_data_seq'), 'PROD-001', NOW() + INTERVAL '5 days', 2, 0.8, 'MOVING_AVERAGE', NOW(), 'system'),
    (nextval('forecast_data_seq'), 'PROD-001', NOW() + INTERVAL '6 days', 1, 0.8, 'MOVING_AVERAGE', NOW(), 'system'),
    (nextval('forecast_data_seq'), 'PROD-001', NOW() + INTERVAL '7 days', 3, 0.8, 'MOVING_AVERAGE', NOW(), 'system'),
    
    -- PROD-003 (Wireless Earbuds) forecast
    (nextval('forecast_data_seq'), 'PROD-003', NOW() + INTERVAL '1 day', 3, 0.8, 'MOVING_AVERAGE', NOW(), 'system'),
    (nextval('forecast_data_seq'), 'PROD-003', NOW() + INTERVAL '2 days', 2, 0.8, 'MOVING_AVERAGE', NOW(), 'system'),
    (nextval('forecast_data_seq'), 'PROD-003', NOW() + INTERVAL '3 days', 4, 0.8, 'MOVING_AVERAGE', NOW(), 'system'),
    (nextval('forecast_data_seq'), 'PROD-003', NOW() + INTERVAL '4 days', 3, 0.8, 'MOVING_AVERAGE', NOW(), 'system'),
    (nextval('forecast_data_seq'), 'PROD-003', NOW() + INTERVAL '5 days', 3, 0.8, 'MOVING_AVERAGE', NOW(), 'system'),
    (nextval('forecast_data_seq'), 'PROD-003', NOW() + INTERVAL '6 days', 4, 0.8, 'MOVING_AVERAGE', NOW(), 'system'),
    (nextval('forecast_data_seq'), 'PROD-003', NOW() + INTERVAL '7 days', 5, 0.8, 'MOVING_AVERAGE', NOW(), 'system'),
    
    -- PROD-005 (USB-C Cable) forecast
    (nextval('forecast_data_seq'), 'PROD-005', NOW() + INTERVAL '1 day', 5, 0.8, 'MOVING_AVERAGE', NOW(), 'system'),
    (nextval('forecast_data_seq'), 'PROD-005', NOW() + INTERVAL '2 days', 4, 0.8, 'MOVING_AVERAGE', NOW(), 'system'),
    (nextval('forecast_data_seq'), 'PROD-005', NOW() + INTERVAL '3 days', 6, 0.8, 'MOVING_AVERAGE', NOW(), 'system'),
    (nextval('forecast_data_seq'), 'PROD-005', NOW() + INTERVAL '4 days', 5, 0.8, 'MOVING_AVERAGE', NOW(), 'system'),
    (nextval('forecast_data_seq'), 'PROD-005', NOW() + INTERVAL '5 days', 7, 0.8, 'MOVING_AVERAGE', NOW(), 'system'),
    (nextval('forecast_data_seq'), 'PROD-005', NOW() + INTERVAL '6 days', 5, 0.8, 'MOVING_AVERAGE', NOW(), 'system'),
    (nextval('forecast_data_seq'), 'PROD-005', NOW() + INTERVAL '7 days', 6, 0.8, 'MOVING_AVERAGE', NOW(), 'system');
