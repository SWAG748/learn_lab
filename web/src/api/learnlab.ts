import { http, unwrap } from './http'
import type {
  AuthResponse,
  Comment,
  Course,
  CourseInput,
  Dashboard,
  NoteDetail,
  NoteInput,
  NoteSummary,
  PageResult,
  Task,
  TaskInput,
  TaskStatus,
  UserView
} from '../types'

export function register(payload: { username: string; email: string; password: string }) {
  return unwrap<AuthResponse>(http.post('/api/auth/register', payload))
}

export function login(payload: { username: string; password: string }) {
  return unwrap<AuthResponse>(http.post('/api/auth/login', payload))
}

export function fetchMe() {
  return unwrap<UserView>(http.get('/api/auth/me'))
}

export function changePassword(payload: { oldPassword: string; newPassword: string }) {
  return unwrap<void>(http.put('/api/auth/password', payload))
}

export function listCourses(page = 0, size = 9) {
  return unwrap<PageResult<Course>>(http.get('/api/courses', { params: { page, size } }))
}

export function getCourse(id: number) {
  return unwrap<Course>(http.get(`/api/courses/${id}`))
}

export function createCourse(payload: CourseInput) {
  return unwrap<Course>(http.post('/api/courses', payload))
}

export function updateCourse(id: number, payload: CourseInput) {
  return unwrap<Course>(http.put(`/api/courses/${id}`, payload))
}

export function deleteCourse(id: number) {
  return unwrap<void>(http.delete(`/api/courses/${id}`))
}

export function listNotes(params: { page?: number; size?: number; courseId?: number; keyword?: string; tag?: string }) {
  return unwrap<PageResult<NoteSummary>>(http.get('/api/notes', { params }))
}

export function getNote(id: number) {
  return unwrap<NoteDetail>(http.get(`/api/notes/${id}`))
}

export function createNote(payload: NoteInput) {
  return unwrap<NoteDetail>(http.post('/api/notes', payload))
}

export function updateNote(id: number, payload: NoteInput) {
  return unwrap<NoteDetail>(http.put(`/api/notes/${id}`, payload))
}

export function deleteNote(id: number) {
  return unwrap<void>(http.delete(`/api/notes/${id}`))
}

export function listComments(noteId: number) {
  return unwrap<Comment[]>(http.get(`/api/notes/${noteId}/comments`))
}

export function createComment(noteId: number, content: string) {
  return unwrap<Comment>(http.post(`/api/notes/${noteId}/comments`, { content }))
}

export function deleteComment(id: number) {
  return unwrap<void>(http.delete(`/api/comments/${id}`))
}

export function listTags() {
  return unwrap<string[]>(http.get('/api/tags'))
}

export function uploadAttachment(noteId: number, file: File) {
  const form = new FormData()
  form.append('file', file)
  return unwrap(http.post(`/api/notes/${noteId}/attachments`, form))
}

export async function downloadAttachment(id: number, filename: string) {
  const response = await http.get(`/api/attachments/${id}/download`, { responseType: 'blob' })
  const url = URL.createObjectURL(response.data)
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  link.click()
  URL.revokeObjectURL(url)
}

export function deleteAttachment(id: number) {
  return unwrap<void>(http.delete(`/api/attachments/${id}`))
}

export function listTasks(params: { courseId?: number; status?: TaskStatus } = {}) {
  return unwrap<Task[]>(http.get('/api/tasks', { params }))
}

export function createTask(payload: TaskInput) {
  return unwrap<Task>(http.post('/api/tasks', payload))
}

export function updateTaskStatus(id: number, status: TaskStatus) {
  return unwrap<Task>(http.patch(`/api/tasks/${id}/status`, { status }))
}

export function deleteTask(id: number) {
  return unwrap<void>(http.delete(`/api/tasks/${id}`))
}

export function fetchDashboard() {
  return unwrap<Dashboard>(http.get('/api/dashboard'))
}

export function listUsers(page = 0, size = 20) {
  return unwrap<PageResult<UserView>>(http.get('/api/admin/users', { params: { page, size } }))
}

export function setUserEnabled(id: number, enabled: boolean) {
  return unwrap<UserView>(http.patch(`/api/admin/users/${id}/enabled`, { enabled }))
}
