import apiWrapper from './apiWrapper'
import { handleApiError } from '@/utils/errorHandler'
import { PagedResponse } from '@/types/api'

// Password interfaces
export interface Password {
  id: number
  siteUrl: string
  username: string
  password?: string
  iconUrl?: string
  createdAt: string
  updatedAt: string
}

export interface PasswordCreateRequest {
  siteUrl: string
  username: string
  password: string
  iconUrl?: string
}

export interface PasswordUpdateRequest {
  siteUrl?: string
  username?: string
  password?: string
  iconUrl?: string
}

export interface PasswordSearchRequest {
  keyword?: string
  provider?: string
  username?: string
  page?: number
  size?: number
  sortBy?: string
  sortDirection?: 'ASC' | 'DESC'
}

export interface GeneratePasswordRequest {
  length?: number
  includeLowercase?: boolean
  includeUppercase?: boolean
  includeNumbers?: boolean
  includeSpecial?: boolean
}

export interface CheckPasswordStrengthRequest {
  password: string
}

export interface CheckPasswordStrengthResponse {
  score: number
  feedback: string
}

export interface ImportPasswordsRequest {
  format: 'JSON' | 'CSV'
  importData?: string
  passwords?: PasswordCreateRequest[]
}

export interface ImportPasswordsResponse {
  totalImported: number
  failedImports: number
}

export interface ExportPasswordsResponse {
  exportData: string
}

class PasswordManagerService {
  async getPassword(id: number, includePassword: boolean = false): Promise<Password> {
    try {
      return await apiWrapper.post<Password>(`/api/passwords/get?id=${id}&includePassword=${includePassword}`)
    } catch (error) {
      throw handleApiError(error)
    }
  }

  async searchPasswords(request: PasswordSearchRequest): Promise<PagedResponse<Password>> {
    try {
      return await apiWrapper.post<PagedResponse<Password>>('/api/passwords/search', request)
    } catch (error) {
      throw handleApiError(error)
    }
  }

  async createPassword(request: PasswordCreateRequest): Promise<Password> {
    try {
      return await apiWrapper.post<Password>('/api/passwords/create', request)
    } catch (error) {
      throw handleApiError(error)
    }
  }

  async updatePassword(id: number, request: PasswordUpdateRequest): Promise<Password> {
    try {
      return await apiWrapper.post<Password>(`/api/passwords/update?id=${id}`, request)
    } catch (error) {
      throw handleApiError(error)
    }
  }

  async deletePassword(id: number): Promise<void> {
    try {
      await apiWrapper.post<void>(`/api/passwords/delete?id=${id}`)
    } catch (error) {
      throw handleApiError(error)
    }
  }

  async generatePassword(request: GeneratePasswordRequest): Promise<string> {
    try {
      const response = await apiWrapper.post<GeneratePasswordResponse>('/api/passwords/generate', request)
      return response.password
    } catch (error) {
      throw handleApiError(error)
    }
  }

  async checkPasswordStrength(password: string): Promise<CheckPasswordStrengthResponse> {
    try {
      return await apiWrapper.post<CheckPasswordStrengthResponse>('/api/passwords/check-strength', { password })
    } catch (error) {
      throw handleApiError(error)
    }
  }

  async exportPasswords(format: 'JSON' | 'CSV', includePasswords: boolean = false): Promise<string> {
    try {
      const response = await apiWrapper.post<ExportPasswordsResponse>(
        `/api/passwords/export?format=${format}&includePasswords=${includePasswords}`
      )
      return response.exportData
    } catch (error) {
      throw handleApiError(error)
    }
  }

  async importPasswords(request: ImportPasswordsRequest): Promise<ImportPasswordsResponse> {
    try {
      return await apiWrapper.post<ImportPasswordsResponse>('/api/passwords/import', request)
    } catch (error) {
      throw handleApiError(error)
    }
  }
}

// Add missing interface
interface GeneratePasswordResponse {
  password: string
}

export default new PasswordManagerService()
