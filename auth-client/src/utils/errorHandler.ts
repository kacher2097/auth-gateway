import { AxiosError } from 'axios'

export interface ApiErrorResponse {
  timestamp: string
  status: number
  error: string
  message: string
  errorCode: string
  path: string
  fieldErrors?: Record<string, string>
}

export class ApiError extends Error {
  status: number
  errorCode: string
  fieldErrors?: Record<string, string>

  constructor(message: string, status: number, errorCode: string, fieldErrors?: Record<string, string>) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.errorCode = errorCode
    this.fieldErrors = fieldErrors
  }

  static fromAxiosError(error: AxiosError): ApiError {
    if (error.response?.data) {
      const errorData = error.response.data as ApiErrorResponse
      return new ApiError(
        errorData.message || 'An error occurred',
        errorData.status || error.response.status,
        errorData.errorCode || 'UNKNOWN_ERROR',
        errorData.fieldErrors
      )
    }
    
    // Network error or other non-response error
    return new ApiError(
      error.message || 'Network error',
      error.response?.status || 0,
      'NETWORK_ERROR'
    )
  }

  // Helper method to check if this is a specific error type
  is(errorCode: string): boolean {
    return this.errorCode === errorCode
  }
}

// Function to handle API errors in a consistent way
export function handleApiError(error: unknown): ApiError {
  if (error instanceof ApiError) {
    return error
  }
  
  if (error instanceof AxiosError) {
    return ApiError.fromAxiosError(error)
  }
  
  // For any other type of error
  return new ApiError(
    error instanceof Error ? error.message : 'An unknown error occurred',
    500,
    'UNKNOWN_ERROR'
  )
}
