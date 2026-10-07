<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import PageHeader from '../components/PageHeader.vue'
import { listUsers, setUserEnabled } from '../api/learnlab'
import { useAsync } from '../composables/useAsync'
import { useAuthStore } from '../stores/auth'
import type { UserView } from '../types'

const auth = useAuthStore()
const { loading, run } = useAsync()
const users = ref<UserView[]>([])
const page = ref(1)
const total = ref(0)

async function load() {
  const result = await run(() => listUsers(page.value - 1, 20))
  if (!result) return
  users.value = result.items
  total.value = result.total
}

async function toggle(user: UserView, enabled: string | number | boolean) {
  const next = Boolean(enabled)
  const updated = await run(() => setUserEnabled(user.id, next))
  if (!updated) return
  user.enabled = updated.enabled
  ElMessage.success(updated.enabled ? '已启用' : '已停用')
}

onMounted(load)
</script>

<template>
  <PageHeader title="用户管理" description="只有管理员能打开。停用后，对方手里的 token 下一次请求就会失效。" />
  <el-table v-loading="loading" :data="users" border>
    <el-table-column prop="username" label="用户名" />
    <el-table-column prop="email" label="邮箱" />
    <el-table-column prop="role" label="角色" width="120" />
    <el-table-column label="启用" width="120">
      <template #default="{ row }">
        <el-switch
          :model-value="row.enabled"
          :disabled="row.id === auth.user?.id"
          @change="(value: string | number | boolean) => toggle(row, value)"
        />
      </template>
    </el-table-column>
  </el-table>
  <div class="pager">
    <el-pagination
      v-model:current-page="page"
      layout="prev, pager, next"
      :page-size="20"
      :total="total"
      hide-on-single-page
      @current-change="load"
    />
  </div>
</template>
