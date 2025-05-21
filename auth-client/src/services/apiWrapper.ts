import api from './api'
import { ApiResponse, PagedResponse } from '@/types/api'
import { handleApiError } from '@/utils/errorHandler'
import { AxiosRequestConfig } from 'axios'

/**
 * Wrapper for API calls that handles the ApiResponse structure
 */
export default {
  /**
   * Make a GET request and extract the data from the ApiResponse
   */
  async get<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
    try {
      const response = await api.get<ApiResponse<T>>(url, config)
      return this.extractData(response.data)
    } catch (error) {
      throw handleApiError(error)
    }
  },

  /**
   * Make a POST request and extract the data from the ApiResponse
   */
  async post<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    try {
      const response = await api.post<ApiResponse<T>>(url, data, config)
      return this.extractData(response.data)
    } catch (error) {
      throw handleApiError(error)
    }
  },

  /**
   * Make a PUT request and extract the data from the ApiResponse
   */
  async put<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    try {
      const response = await api.put<ApiResponse<T>>(url, data, config)
      return this.extractData(response.data)
    } catch (error) {
      throw handleApiError(error)
    }
  },

  /**
   * Make a DELETE request and extract the data from the ApiResponse
   */
  async delete<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
    try {
      const response = await api.delete<ApiResponse<T>>(url, config)
      return this.extractData(response.data)
    } catch (error) {
      throw handleApiError(error)
    }
  },

  /**
   * Extract data from an ApiResponse
   */
  extractData<T>(apiResponse: ApiResponse<T>): T {
    // Check if the response is successful
    if (apiResponse.code === '00') {
      return apiResponse.data as T
    }
    
    // If the response is not successful, throw an error
    throw new Error(`API error: ${apiResponse.message} (code: ${apiResponse.code})`)
  },

  /**
   * Check if a response is a PagedResponse
   */
  isPagedResponse<T>(data: any): data is PagedResponse<T> {
    return (
      data &&
      typeof data === 'object' &&
      'content' in data &&
      'page' in data &&
      'size' in data &&
      'totalElements' in data &&
      'totalPages' in data
    )
  }
}
