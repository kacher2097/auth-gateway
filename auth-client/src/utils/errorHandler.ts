import {AxiosError} from 'axios'

export class ApiError extends Error {
  code: string
  status: number
  fieldErrors?: Record<string, string>

  constructor(message: string, code: string, status: number = 400, fieldErrors?: Record<string, string>) {
    super(message)
    this.name = 'ApiError'
    this.code = code
    this.status = status
    this.fieldErrors = fieldErrors
  }

  static fromAxiosError(error: AxiosError): ApiError {
    if (error.response?.data) {
      // Check if the response has the expected structure
      const responseData = error.response.data as any

      // Handle the ApiResponse structure from the Java backend
      if (responseData.code && responseData.message) {
        return new ApiError(
          responseData.message,
          responseData.code,
          error.response.status,
          responseData.data?.fieldErrors
        )
      }

      // Handle legacy error format if present
      if (responseData.errorCode || responseData.error) {
        return new ApiError(
          responseData.message || 'An error occurred',
          responseData.errorCode || 'UNKNOWN_ERROR',
          error.response.status,
          responseData.fieldErrors
        )
      }
    }

    // Network error or other non-response error
    return new ApiError(
      error.message || 'Network error',
      'NETWORK_ERROR',
      error.response?.status || 0
    )
  }

  // Helper method to check if this is a specific error type
  is(errorCode: string): boolean {
    return this.code === errorCode
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
    "500",
    200
  )
}
