/**
 * Types for API responses that match the Java backend structure
 */

/**
 * Generic API response structure that matches the Java backend
 */
export interface ApiResponse<T> {
  code: string;
  message: string;
  data: T | null;
  metadata?: Record<string, any>;
}

/**
 * Pagination response structure that matches the Java backend
 */
export interface PagedResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

/**
 * Error response structure
 */
export interface ApiErrorData {
  code: string;
  message: string;
  fieldErrors?: Record<string, string>;
}
