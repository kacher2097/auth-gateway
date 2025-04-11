import api from './api.service';

interface StatisticsData {
  totalUsers: number;
  activeUsers: number;
  newUsers: number;
  loginAttempts: number;
  successfulLogins: number;
  failedLogins: number;
}

interface LoginActivityData {
  id: string;
  username: string;
  ip: string;
  status: 'success' | 'failed';
  reason?: string;
  timestamp: string;
  userAgent: string;
}

interface UserData {
  id: string;
  username: string;
  email: string;
  fullName: string;
  role: string;
  active: boolean;
  createdAt: string;
  lastLogin: string | null;
}

interface RoleData {
  id: string;
  name: string;
  description: string;
  permissions: string[];
}

interface InviteData {
  id: string;
  email: string;
  role: string;
  status: 'pending' | 'accepted' | 'expired';
  createdAt: string;
  expiresAt: string;
  createdBy: string;
}

interface TrafficData {
  date: string;
  visits: number;
  uniqueVisitors: number;
  pageViews: number;
}

interface UserActivityData {
  date: string;
  activeUsers: number;
  newUsers: number;
  returningUsers: number;
}

interface ReportData {
  id: string;
  name: string;
  description: string;
  type: string;
  createdAt: string;
  createdBy: string;
  lastRun: string | null;
  schedule: string | null;
}

const adminService = {
  // Dashboard statistics
  getStatistics: async (startDate: string, endDate: string): Promise<StatisticsData> => {
    const response = await api.get<StatisticsData>('/admin/statistics', {
      params: { startDate, endDate }
    });
    return response.data;
  },
  
  getLoginActivity: async (startDate: string, endDate: string): Promise<LoginActivityData[]> => {
    const response = await api.get<LoginActivityData[]>('/admin/login-activity', {
      params: { startDate, endDate }
    });
    return response.data;
  },
  
  // User management
  getUsers: async (page: number = 1, limit: number = 10, search?: string): Promise<{ users: UserData[], total: number }> => {
    const response = await api.get<{ users: UserData[], total: number }>('/admin/users', {
      params: { page, limit, search }
    });
    return response.data;
  },
  
  getUserById: async (id: string): Promise<UserData> => {
    const response = await api.get<UserData>(`/admin/users/${id}`);
    return response.data;
  },
  
  createUser: async (userData: Partial<UserData>): Promise<UserData> => {
    const response = await api.post<UserData>('/admin/users', userData);
    return response.data;
  },
  
  updateUser: async (id: string, userData: Partial<UserData>): Promise<UserData> => {
    const response = await api.put<UserData>(`/admin/users/${id}`, userData);
    return response.data;
  },
  
  deleteUser: async (id: string): Promise<void> => {
    await api.delete(`/admin/users/${id}`);
  },
  
  // Roles and permissions
  getRoles: async (): Promise<RoleData[]> => {
    const response = await api.get<RoleData[]>('/admin/roles');
    return response.data;
  },
  
  createRole: async (roleData: Partial<RoleData>): Promise<RoleData> => {
    const response = await api.post<RoleData>('/admin/roles', roleData);
    return response.data;
  },
  
  updateRole: async (id: string, roleData: Partial<RoleData>): Promise<RoleData> => {
    const response = await api.put<RoleData>(`/admin/roles/${id}`, roleData);
    return response.data;
  },
  
  deleteRole: async (id: string): Promise<void> => {
    await api.delete(`/admin/roles/${id}`);
  },
  
  // User invites
  getInvites: async (page: number = 1, limit: number = 10): Promise<{ invites: InviteData[], total: number }> => {
    const response = await api.get<{ invites: InviteData[], total: number }>('/admin/invites', {
      params: { page, limit }
    });
    return response.data;
  },
  
  createInvite: async (inviteData: { email: string, role: string }): Promise<InviteData> => {
    const response = await api.post<InviteData>('/admin/invites', inviteData);
    return response.data;
  },
  
  resendInvite: async (id: string): Promise<InviteData> => {
    const response = await api.post<InviteData>(`/admin/invites/${id}/resend`);
    return response.data;
  },
  
  deleteInvite: async (id: string): Promise<void> => {
    await api.delete(`/admin/invites/${id}`);
  },
  
  // Analytics
  getTrafficData: async (startDate: string, endDate: string): Promise<TrafficData[]> => {
    const response = await api.get<TrafficData[]>('/admin/analytics/traffic', {
      params: { startDate, endDate }
    });
    return response.data;
  },
  
  getUserActivityData: async (startDate: string, endDate: string): Promise<UserActivityData[]> => {
    const response = await api.get<UserActivityData[]>('/admin/analytics/user-activity', {
      params: { startDate, endDate }
    });
    return response.data;
  },
  
  // Reports
  getReports: async (page: number = 1, limit: number = 10): Promise<{ reports: ReportData[], total: number }> => {
    const response = await api.get<{ reports: ReportData[], total: number }>('/admin/reports', {
      params: { page, limit }
    });
    return response.data;
  },
  
  getReportById: async (id: string): Promise<ReportData> => {
    const response = await api.get<ReportData>(`/admin/reports/${id}`);
    return response.data;
  },
  
  createReport: async (reportData: Partial<ReportData>): Promise<ReportData> => {
    const response = await api.post<ReportData>('/admin/reports', reportData);
    return response.data;
  },
  
  updateReport: async (id: string, reportData: Partial<ReportData>): Promise<ReportData> => {
    const response = await api.put<ReportData>(`/admin/reports/${id}`, reportData);
    return response.data;
  },
  
  deleteReport: async (id: string): Promise<void> => {
    await api.delete(`/admin/reports/${id}`);
  },
  
  runReport: async (id: string): Promise<{ jobId: string }> => {
    const response = await api.post<{ jobId: string }>(`/admin/reports/${id}/run`);
    return response.data;
  },
  
  // Settings
  getSettings: async (category: string): Promise<Record<string, any>> => {
    const response = await api.get<Record<string, any>>(`/admin/settings/${category}`);
    return response.data;
  },
  
  updateSettings: async (category: string, settings: Record<string, any>): Promise<Record<string, any>> => {
    const response = await api.put<Record<string, any>>(`/admin/settings/${category}`, settings);
    return response.data;
  },
};

export default adminService;
