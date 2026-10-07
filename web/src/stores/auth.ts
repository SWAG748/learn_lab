import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import type { UserView } from '../types'

const TOKEN_KEY = 'learnlab.token'
const USER_KEY = 'learnlab.user'

function readUser(): UserView | null {
  const raw = localStorage.getItem(USER_KEY)
  if (!raw) return null
  try {
    return JSON.parse(raw) as UserView
  } catch {
    return null
  }
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem(TOKEN_KEY) || '')
  const user = ref<UserView | null>(readUser())
  const isAdmin = computed(() => user.value?.role === 'ADMIN')

  function setSession(nextToken: string, nextUser: UserView) {
    token.value = nextToken
    user.value = nextUser
    localStorage.setItem(TOKEN_KEY, nextToken)
    localStorage.setItem(USER_KEY, JSON.stringify(nextUser))
  }

  function clear() {
    token.value = ''
    user.value = null
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
  }

  return { token, user, isAdmin, setSession, clear }
})
