import api from './api.service';
import { UserInfo } from '../types/auth.types';

interface ProfileUpdateRequest {
  fullName: string;
  email: string;
}

interface PasswordChangeRequest {
  currentPassword: string;
  newPassword: string;
}

const userService = {
  getProfile: async (): Promise<UserInfo> => {
    const response = await api.get<UserInfo>('/users/profile');
    return response.data;
  },
  
  updateProfile: async (data: ProfileUpdateRequest): Promise<UserInfo> => {
    const response = await api.put<UserInfo>('/users/profile', data);
    return response.data;
  },
  
  changePassword: async (data: PasswordChangeRequest): Promise<{ message: string }> => {
    const response = await api.post<{ message: string }>('/users/change-password', data);
    return response.data;
  },
  
  uploadAvatar: async (file: File): Promise<{ avatarUrl: string }> => {
    const formData = new FormData();
    formData.append('avatar', file);
    
    const response = await api.post<{ avatarUrl: string }>('/users/avatar', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    
    return response.data;
  },
};

export default userService;
