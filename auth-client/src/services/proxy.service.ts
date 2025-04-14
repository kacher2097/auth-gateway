import api from './api'
import type { Proxy, ProxyRequest, ProxyCheckResult, ImportResult } from '@/types/proxy'

export default {
  getAllProxies: async (): Promise<Proxy[]> => {
    const response = await api.get('/api/proxies')
    return response.data.data
  },
  
  getActiveProxies: async (): Promise<Proxy[]> => {
    const response = await api.get('/api/proxies/active')
    return response.data.data
  },
  
  getProxyById: async (id: string): Promise<Proxy> => {
    const response = await api.get(`/api/proxies/${id}`)
    return response.data.data
  },
  
  createProxy: async (proxy: ProxyRequest): Promise<Proxy> => {
    const response = await api.post('/api/proxies', proxy)
    return response.data.data
  },
  
  updateProxy: async (id: string, proxy: ProxyRequest): Promise<Proxy> => {
    const response = await api.put(`/api/proxies/${id}`, proxy)
    return response.data.data
  },
  
  deleteProxy: async (id: string): Promise<void> => {
    await api.delete(`/api/proxies/${id}`)
  },
  
  checkProxy: async (id: string): Promise<ProxyCheckResult> => {
    const response = await api.post(`/api/proxies/${id}/check`)
    return response.data.data
  },
  
  getProxiesByProtocol: async (protocol: string): Promise<Proxy[]> => {
    const response = await api.get(`/api/proxies/protocol/${protocol}`)
    return response.data.data
  },
  
  getProxiesByCountry: async (country: string): Promise<Proxy[]> => {
    const response = await api.get(`/api/proxies/country/${country}`)
    return response.data.data
  },
  
  getFastProxies: async (maxResponseTime: number = 1000): Promise<Proxy[]> => {
    const response = await api.get(`/api/proxies/fast?maxResponseTime=${maxResponseTime}`)
    return response.data.data
  },
  
  getReliableProxies: async (minUptime: number = 90): Promise<Proxy[]> => {
    const response = await api.get(`/api/proxies/reliable?minUptime=${minUptime}`)
    return response.data.data
  },
  
  importProxies: async (file: File, fileType: 'CSV' | 'EXCEL'): Promise<ImportResult> => {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('fileType', fileType)
    
    const response = await api.post('/api/proxies/import', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    
    return response.data.data
  }
}
