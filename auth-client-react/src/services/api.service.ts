import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse, AxiosError } from 'axios';
import { errorHandler } from '../utils/errorHandler';

// API configuration from environment variables
const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8118';
const API_TIMEOUT = parseInt(import.meta.env.VITE_API_TIMEOUT || '30000', 10);

// Create a base API instance
const apiInstance: AxiosInstance = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  },
  timeout: API_TIMEOUT,
});

// Request interceptor for adding auth token
apiInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor for handling errors
apiInstance.interceptors.response.use(
  (response) => response,
  (error: AxiosError) => {
    return Promise.reject(errorHandler(error));
  }
);

// Enable request logging in development
if (import.meta.env.DEV && import.meta.env.VITE_ENABLE_LOGGER === 'true') {
  apiInstance.interceptors.request.use(request => {
    console.log('Starting Request', request);
    return request;
  });

  apiInstance.interceptors.response.use(response => {
    console.log('Response:', response);
    return response;
  }, error => {
    console.error('Response Error:', error);
    return Promise.reject(error);
  });
}

// Generic API methods
export const api = {
  get: <T>(url: string, config?: AxiosRequestConfig): Promise<AxiosResponse<T>> => {
    return apiInstance.get<T>(url, config);
  },

  post: <T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<AxiosResponse<T>> => {
    return apiInstance.post<T>(url, data, config);
  },

  put: <T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<AxiosResponse<T>> => {
    return apiInstance.put<T>(url, data, config);
  },

  delete: <T>(url: string, config?: AxiosRequestConfig): Promise<AxiosResponse<T>> => {
    return apiInstance.delete<T>(url, config);
  },
};

export default api;
