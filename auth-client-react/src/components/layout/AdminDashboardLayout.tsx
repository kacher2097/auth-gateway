import React from 'react';
import { Layout } from 'antd';
import AdminLayout from './AdminLayout';

interface AdminDashboardLayoutProps {
    children: React.ReactNode;
    title?: string;
}

const AdminDashboardLayout: React.FC<AdminDashboardLayoutProps> = ({ children, title = 'Dashboard' }) => {
    return (
        <AdminLayout title={title}>
            <div style={{ padding: '16px' }}>
                {children}
            </div>
        </AdminLayout>
    );
};

export default AdminDashboardLayout;