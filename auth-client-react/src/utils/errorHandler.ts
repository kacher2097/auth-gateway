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
      const message = data.message || 'An error occurred';
      const fieldErrors = data.fieldErrors || data.errors || null;
      const errorCode = data.errorCode || data.code || null;

      // Provide more specific messages based on status codes
      let userFriendlyMessage = message;
      if (status === 500) {
        userFriendlyMessage = 'The server encountered an error. Please try again later.';
      } else if (status === 404) {
        userFriendlyMessage = 'The requested resource was not found.';
      } else if (status === 403) {
        userFriendlyMessage = 'You do not have permission to access this resource.';
      } else if (status === 401) {
        userFriendlyMessage = 'Your session has expired. Please log in again.';
      } else if (status === 400 && errorCode === 'DATABASE_ERROR') {
        userFriendlyMessage = 'There was a problem retrieving data. Please try again later.';
      } else if (status === 400 && errorCode === 'NULL_POINTER_ERROR') {
        userFriendlyMessage = 'There was a problem processing your request. Please try again later.';
      }

      return new ApiError(userFriendlyMessage, status, data, fieldErrors, errorCode);
    }

    return new ApiError(
      'An error occurred while processing your request',
      status,
      data
    );
  } else if (error.request) {
    // The request was made but no response was received
    return new ApiError(
      'No response received from the server. Please check your internet connection and try again.',
      0
    );
  } else {
    // Something happened in setting up the request that triggered an Error
    return new ApiError(
      error.message || 'An error occurred while setting up the request',
      0
    );
  }
};

// For backward compatibility
export const errorHandler = handleApiError;
