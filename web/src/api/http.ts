import axios from 'axios'
import type { ApiResponse } from '../types'
import { useAuthStore } from '../stores/auth'
import router from '../router'

export class ApiError extends Error {
  fields?: Record<string, string>

  constructor(message: string, fields?: Record<string, string>) {
    super(message)
    this.name = 'ApiError'
    this.fields = fields
  }
}

export const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || ''
})

http.interceptors.request.use((config) => {
  const auth = useAuthStore()
  if (auth.token) {
    config.headers.Authorization = `Bearer ${auth.token}`
  }
  return config
})

http.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status
    const data = error.response?.data as ApiResponse<unknown> | undefined
    const url = String(error.config?.url || '')
    const isAuthAttempt = url.includes('/api/auth/login') || url.includes('/api/auth/register')
    if (status === 401 && !isAuthAttempt) {
      useAuthStore().clear()
      if (router.currentRoute.value.path !== '/login') {
        router.push({ path: '/login', query: { redirect: router.currentRoute.value.fullPath } })
      }
    }
    const message = data?.message || '请求失败'
    return Promise.reject(new ApiError(message, data?.fields))
  }
)

export async function unwrap<T>(promise: Promise<{ data: ApiResponse<T> }>): Promise<T> {
  const response = await promise
  return response.data.data
}
