export interface UserView {
  id: number
  username: string
  email: string
  role: 'USER' | 'ADMIN'
  enabled: boolean
}

export interface AuthResponse {
  token: string
  user: UserView
}

export interface PageResult<T> {
  items: T[]
  page: number
  size: number
  total: number
  totalPages: number
}

export interface Course {
  id: number
  title: string
  summary: string | null
  ownerId: number
  ownerName: string
  noteCount: number
  taskCount: number
  createdAt: string
}

export interface CourseInput {
  title: string
  summary: string
}

export interface NoteSummary {
  id: number
  title: string
  pinned: boolean
  courseId: number
  courseTitle: string
  authorName: string
  tags: string[]
  createdAt: string
  updatedAt: string
}

export interface Attachment {
  id: number
  originalName: string
  contentType: string | null
  sizeBytes: number
  createdAt: string
}

export interface NoteDetail extends NoteSummary {
  content: string
  attachments: Attachment[]
}

export interface NoteInput {
  title: string
  content: string
  courseId: number
  pinned: boolean
  tags: string[]
}

export interface Comment {
  id: number
  content: string
  authorName: string
  createdAt: string
}

export type TaskStatus = 'TODO' | 'DOING' | 'DONE'
export type Priority = 'LOW' | 'MEDIUM' | 'HIGH'

export interface Task {
  id: number
  title: string
  detail: string | null
  status: TaskStatus
  priority: Priority
  dueDate: string | null
  courseId: number | null
  courseTitle: string | null
  assigneeName: string
  overdue: boolean
  createdAt: string
}

export interface TaskInput {
  title: string
  detail: string
  status: TaskStatus
  priority: Priority
  dueDate: string | null
  courseId: number | null
}

export interface Dashboard {
  courseCount: number
  noteCount: number
  todoCount: number
  doingCount: number
  doneCount: number
  overdueCount: number
  recentNotes: NoteSummary[]
  overdueTasks: Task[]
}

export interface ApiResponse<T> {
  success: boolean
  code: string
  message: string | null
  data: T
  fields?: Record<string, string>
}
