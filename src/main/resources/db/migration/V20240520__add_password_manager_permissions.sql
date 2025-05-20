-- Add password manager permissions
INSERT INTO permissions (name, display_name, description, category, created_at, updated_at)
VALUES
    ('password:read', 'Read Passwords', 'Permission to view password entries', 'Password Manager', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('password:create', 'Create Passwords', 'Permission to create new password entries', 'Password Manager', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('password:update', 'Update Passwords', 'Permission to update existing password entries', 'Password Manager', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('password:delete', 'Delete Passwords', 'Permission to delete password entries', 'Password Manager', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('password:export', 'Export Passwords', 'Permission to export password entries', 'Password Manager', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('password:import', 'Import Passwords', 'Permission to import password entries', 'Password Manager', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Add password manager permissions to admin role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ROLE_ADMIN' AND p.name IN (
    'password:read',
    'password:create',
    'password:update',
    'password:delete',
    'password:export',
    'password:import'
);

-- Add basic password manager permissions to user role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ROLE_USER' AND p.name IN (
    'password:read',
    'password:create',
    'password:update',
    'password:delete'
);
