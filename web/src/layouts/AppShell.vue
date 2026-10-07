<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { changePassword, fetchMe } from '../api/learnlab'
import { useAuthStore } from '../stores/auth'
import { ApiError } from '../api/http'

const router = useRouter()
const auth = useAuthStore()
const passwordVisible = ref(false)
const saving = ref(false)
const form = reactive({ oldPassword: '', newPassword: '' })

onMounted(async () => {
  if (!auth.token) return
  try {
    const me = await fetchMe()
    auth.setSession(auth.token, me)
  } catch {
    auth.clear()
    router.push('/login')
  }
})

function logout() {
  auth.clear()
  router.push('/login')
}

async function savePassword() {
  saving.value = true
  try {
    await changePassword(form)
    passwordVisible.value = false
    form.oldPassword = ''
    form.newPassword = ''
    ElMessage.success('密码已更新')
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '修改失败')
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <div class="shell">
    <aside class="sidebar">
      <div class="brand">
        <strong>LearnLab</strong>
        <span>学习工坊</span>
      </div>
      <nav class="nav">
        <RouterLink to="/">总览</RouterLink>
        <RouterLink to="/courses">课程</RouterLink>
        <RouterLink to="/tasks">任务</RouterLink>
        <RouterLink v-if="auth.isAdmin" to="/admin">用户管理</RouterLink>
      </nav>
      <div class="sidebar-user">
        <div>{{ auth.user?.username }}</div>
        <small class="muted">{{ auth.isAdmin ? '管理员' : '学员' }}</small>
        <div>
          <button type="button" @click="passwordVisible = true">改密码</button>
          <button type="button" @click="logout">退出</button>
        </div>
      </div>
    </aside>
    <main class="main">
      <RouterView />
    </main>
  </div>

  <el-dialog v-model="passwordVisible" title="修改密码" width="420px">
    <el-form label-position="top">
      <el-form-item label="原密码">
        <el-input v-model="form.oldPassword" type="password" show-password />
      </el-form-item>
      <el-form-item label="新密码">
        <el-input v-model="form.newPassword" type="password" show-password />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="passwordVisible = false">取消</el-button>
      <el-button type="primary" :loading="saving" @click="savePassword">保存</el-button>
    </template>
  </el-dialog>
</template>
