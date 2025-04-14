export interface Proxy {
  id: string;
  ipAddress: string;
  port: number;
  protocol: string;
  country?: string;
  city?: string;
  isActive: boolean;
  responseTimeMs: number;
  lastChecked: string;
  createdAt: string;
  updatedAt: string;
  createdBy: string;
  successCount: number;
  failCount: number;
  uptime: number;
  notes?: string;
}

export interface ProxyRequest {
  ipAddress: string;
  port: number;
  protocol: string;
  country?: string;
  city?: string;
  notes?: string;
}

export interface ProxyCheckResult {
  id: string;
  isWorking: boolean;
  responseTimeMs: number;
  checkedAt: string;
}

export type ProxyProtocol = 'HTTP' | 'HTTPS' | 'SOCKS4' | 'SOCKS5';

export const PROXY_PROTOCOLS: ProxyProtocol[] = ['HTTP', 'HTTPS', 'SOCKS4', 'SOCKS5'];

export interface ImportError {
  rowNumber: number;
  errorMessage: string;
  rawData: string;
}

export interface ImportResult {
  totalProcessed: number;
  successCount: number;
  failCount: number;
  errors: ImportError[];
  importedProxies: Proxy[];
}
