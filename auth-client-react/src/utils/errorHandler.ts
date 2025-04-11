import { AxiosError } from 'axios';

export class ApiError extends Error {
  status: number;
  data: any;
  fieldErrors: Record<string, string> | null;
  errorCode: string | null;

  constructor(message: string, status: number, data?: any, fieldErrors?: Record<string, string>, errorCode?: string) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.data = data;
    this.fieldErrors = fieldErrors || null;
    this.errorCode = errorCode || null;
  }

  is(code: string): boolean {
    return this.errorCode === code;
  }
}

export const handleApiError = (error: any): ApiError => {
  if (error.response) {
    // The request was made and the server responded with a status code
    // that falls out of the range of 2xx
    const { data, status } = error.response;

    // Handle structured error responses
    if (data && typeof data === 'object') {
      const message = data.message || 'Đã xảy ra lỗi';
      const fieldErrors = data.fieldErrors || data.errors || null;
      const errorCode = data.errorCode || data.code || null;

      return new ApiError(message, status, data, fieldErrors, errorCode);
    }

    return new ApiError(
      'Đã xảy ra lỗi khi gửi yêu cầu',
      status,
      data
    );
  } else if (error.request) {
    // The request was made but no response was received
    return new ApiError(
      'Không nhận được phản hồi từ máy chủ',
      0
    );
  } else {
    // Something happened in setting up the request that triggered an Error
    return new ApiError(
      error.message || 'Đã xảy ra lỗi khi thiết lập yêu cầu',
      0
    );
  }
};

// For backward compatibility
export const errorHandler = handleApiError;
