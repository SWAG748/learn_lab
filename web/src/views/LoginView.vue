<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '../api/learnlab'
import { ApiError } from '../api/http'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()
const loading = ref(false)
const form = reactive({ username: 'ada', password: 'learn123' })

async function submit() {
  loading.value = true
  try {
    const result = await login(form)
    auth.setSession(result.token, result.user)
    const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : '/'
    router.push(redirect)
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="auth-screen">
    <section class="auth-story">
      <div>
        <div class="eyebrow">Vue · Spring Boot · SQL</div>
        <h1>把一门课、一篇笔记、一件任务串起来。</h1>
        <p>这是一个用来学全栈的本地项目。页面在 Vue，接口在 Spring Boot，表结构由 Flyway 管。先用演示账号走进去，再顺着文档改代码。</p>
      </div>
      <p>演示学员 ada / learn123<br />管理员 admin / admin123</p>
    </section>
    <section class="auth-panel">
      <div class="auth-card">
        <h2>登录</h2>
        <el-form label-position="top" @submit.prevent="submit">
          <el-form-item label="用户名">
            <el-input v-model="form.username" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="form.password" type="password" show-password @keyup.enter="submit" />
          </el-form-item>
          <el-button type="primary" :loading="loading" style="width: 100%" @click="submit">进入工坊</el-button>
        </el-form>
        <p class="muted">没有账号？<RouterLink to="/register">注册一个</RouterLink></p>
      </div>
    </section>
  </div>
</template>
