import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { ApiError } from '../api/http'

export function useAsync() {
  const loading = ref(false)

  async function run<T>(action: () => Promise<T>, fallback = '操作失败'): Promise<T | undefined> {
    loading.value = true
    try {
      return await action()
    } catch (error) {
      const message = error instanceof ApiError || error instanceof Error ? error.message : fallback
      ElMessage.error(message || fallback)
      return undefined
    } finally {
      loading.value = false
    }
  }

  return { loading, run }
}
