import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', component: () => import('../views/LoginView.vue'), meta: { guest: true } },
    { path: '/register', component: () => import('../views/RegisterView.vue'), meta: { guest: true } },
    {
      path: '/',
      component: () => import('../layouts/AppShell.vue'),
      meta: { requiresAuth: true },
      children: [
        { path: '', component: () => import('../views/DashboardView.vue') },
        { path: 'courses', component: () => import('../views/CourseListView.vue') },
        { path: 'courses/:id', component: () => import('../views/CourseDetailView.vue') },
        { path: 'notes/new', component: () => import('../views/NoteEditorView.vue') },
        { path: 'notes/:id', component: () => import('../views/NoteEditorView.vue') },
        { path: 'tasks', component: () => import('../views/TaskBoardView.vue') },
        { path: 'admin', component: () => import('../views/AdminView.vue'), meta: { requiresAdmin: true } }
      ]
    }
  ]
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  const requiresAuth = to.matched.some((record) => record.meta.requiresAuth)
  const requiresAdmin = to.matched.some((record) => record.meta.requiresAdmin)
  if (requiresAuth && !auth.token) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }
  if (to.meta.guest && auth.token) return '/'
  if (requiresAdmin && auth.user?.role !== 'ADMIN') return '/'
  return true
})

export default router
