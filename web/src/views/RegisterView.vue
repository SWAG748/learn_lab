<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '../api/learnlab'
import { ApiError } from '../api/http'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const auth = useAuthStore()
const loading = ref(false)
const fieldErrors = ref<Record<string, string>>({})
const form = reactive({ username: '', email: '', password: '' })

async function submit() {
  loading.value = true
  fieldErrors.value = {}
  try {
    const result = await register(form)
    auth.setSession(result.token, result.user)
    ElMessage.success('注册成功')
    router.push('/')
  } catch (error) {
    if (error instanceof ApiError) {
      fieldErrors.value = error.fields || {}
      ElMessage.error(error.message)
    }
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="auth-screen">
    <section class="auth-story">
      <div>
        <div class="eyebrow">新学员</div>
        <h1>注册后，课程和笔记只属于你。</h1>
        <p>用户名 3 到 40 位，只能用字母、数字和下划线。密码至少 6 位。这些规则在前端和后端各写了一遍，可以对照着看。</p>
      </div>
    </section>
    <section class="auth-panel">
      <div class="auth-card">
        <h2>注册</h2>
        <el-form label-position="top" @submit.prevent="submit">
          <el-form-item label="用户名" :error="fieldErrors.username">
            <el-input v-model="form.username" />
          </el-form-item>
          <el-form-item label="邮箱" :error="fieldErrors.email">
            <el-input v-model="form.email" />
          </el-form-item>
          <el-form-item label="密码" :error="fieldErrors.password">
            <el-input v-model="form.password" type="password" show-password />
          </el-form-item>
          <el-button type="primary" :loading="loading" style="width: 100%" @click="submit">创建账号</el-button>
        </el-form>
        <p class="muted">已有账号？<RouterLink to="/login">去登录</RouterLink></p>
      </div>
    </section>
  </div>
</template>
