/**
 * 销售管理后端（sales-management）专用 HTTP 客户端。
 * 与 legacy 的 @/utils/request（VITE_BASE_URL）隔离，仅前端 B 模块使用。
 */
import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

function resolveSalesBaseUrl() {
  const u = import.meta.env.VITE_SALES_API_URL
  if (u && String(u).trim()) return String(u).replace(/\/$/, '')
  return 'http://localhost:8080'
}

function readBearerToken() {
  const raw = localStorage.getItem('system-user')
  if (!raw) return null
  try {
    const u = JSON.parse(raw)
    return u.accessToken || u.token || null
  } catch {
    return null
  }
}

const salesHttp = axios.create({
  baseURL: resolveSalesBaseUrl(),
  timeout: 30000,
})

salesHttp.interceptors.request.use((config) => {
  config.headers['Content-Type'] = 'application/json;charset=utf-8'
  const token = readBearerToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

salesHttp.interceptors.response.use(
  (response) => {
    const res = response.data
    if (typeof res === 'string') {
      try {
        return JSON.parse(res)
      } catch {
        return res
      }
    }
    return res
  },
  (error) => {
    const status = error.response?.status
    const body = error.response?.data
    const msg =
      (body && (body.message || body.msg)) ||
      error.message ||
      '请求失败'
    if (status === 401) {
      ElMessage.error(typeof msg === 'string' ? msg : '未登录或登录已过期')
      router.push('/login')
    } else {
      ElMessage.error(typeof msg === 'string' ? msg : '请求失败')
    }
    return Promise.reject(error)
  },
)

/**
 * @template T
 * @param {{ code: number; message?: string; data?: T }} res
 * @param {{ silent?: boolean }} [opt]
 * @returns {T | null}
 */
export function unwrapSalesResponse(res, opt = {}) {
  const silent = Boolean(opt.silent)
  if (!res || typeof res !== 'object') return null
  if (res.code !== 200) {
    if (!silent && res.message) {
      ElMessage.error(res.message)
    }
    return null
  }
  return res.data !== undefined ? res.data : null
}

export default salesHttp
