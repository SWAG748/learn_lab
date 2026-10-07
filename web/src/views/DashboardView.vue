<script setup lang="ts">
import { onMounted, ref } from 'vue'
import PageHeader from '../components/PageHeader.vue'
import { fetchDashboard } from '../api/learnlab'
import { useAsync } from '../composables/useAsync'
import type { Dashboard } from '../types'
import { formatTime } from '../utils/format'

const { loading, run } = useAsync()
const data = ref<Dashboard | null>(null)

onMounted(async () => {
  data.value = (await run(fetchDashboard)) ?? null
})
</script>

<template>
  <PageHeader title="总览" description="你自己的课程、笔记和任务计数。数字来自 GET /api/dashboard。" />
  <el-skeleton v-if="loading && !data" :rows="4" animated />
  <template v-else-if="data">
    <section class="stat-grid">
      <article class="stat"><span>课程</span><strong>{{ data.courseCount }}</strong></article>
      <article class="stat"><span>笔记</span><strong>{{ data.noteCount }}</strong></article>
      <article class="stat"><span>进行中</span><strong>{{ data.doingCount }}</strong></article>
      <article class="stat warn"><span>已逾期</span><strong>{{ data.overdueCount }}</strong></article>
    </section>
    <section class="split">
      <div class="panel">
        <h2>最近笔记</h2>
        <div v-if="data.recentNotes.length === 0" class="muted">还没有笔记。</div>
        <RouterLink v-for="note in data.recentNotes" :key="note.id" class="note-row" :to="`/notes/${note.id}`">
          <div>
            <strong>{{ note.title }}</strong>
            <div><small>{{ note.courseTitle }} · {{ formatTime(note.updatedAt) }}</small></div>
          </div>
        </RouterLink>
      </div>
      <div class="panel">
        <h2>逾期任务</h2>
        <div v-if="data.overdueTasks.length === 0" class="muted">没有逾期任务。</div>
        <div v-for="task in data.overdueTasks" :key="task.id" class="task-card">
          <h3>{{ task.title }}</h3>
          <p class="overdue">截止 {{ task.dueDate }}</p>
        </div>
        <RouterLink to="/tasks">去任务板</RouterLink>
      </div>
    </section>
  </template>
</template>
