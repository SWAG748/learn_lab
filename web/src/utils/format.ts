export function formatTime(value: string | null | undefined) {
  if (!value) return ''
  return value.replace('T', ' ').slice(0, 16)
}

export const statusLabel = {
  TODO: '待办',
  DOING: '进行中',
  DONE: '完成'
} as const

export const priorityLabel = {
  LOW: '低',
  MEDIUM: '中',
  HIGH: '高'
} as const
