import { AxiosResponse } from 'axios';
import { ApiResponse } from '@/types/auth.types';

/**
 * Safely extracts data from an API response, providing a fallback if data is missing
 * @param response The API response
 * @param fallback Default value to use if data is missing
 * @returns The response data or fallback value
 */
export function extractApiData<T>(response: AxiosResponse<ApiResponse<T>>, fallback: T): T {
  if (response?.data?.data) {
    return response.data.data;
  }
  return fallback;
}

/**
 * Checks if an API response contains valid data
 * @param response The API response
 * @returns True if the response contains valid data
 */
export function hasValidData<T>(response: AxiosResponse<ApiResponse<T>>): boolean {
  return !!(response?.data?.data);
}

/**
 * Creates a default empty state for common data types
 */
export const defaultEmptyStates = {
  array: [] as any[],
  object: {} as Record<string, any>,
  number: 0,
  string: '',
  boolean: false
};
