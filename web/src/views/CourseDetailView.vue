<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import PageHeader from '../components/PageHeader.vue'
import { getCourse, listNotes } from '../api/learnlab'
import { useAsync } from '../composables/useAsync'
import type { Course, NoteSummary } from '../types'
import { formatTime } from '../utils/format'

const route = useRoute()
const { loading, run } = useAsync()
const course = ref<Course | null>(null)
const notes = ref<NoteSummary[]>([])
const keyword = ref('')
const courseId = computed(() => Number(route.params.id))

async function load() {
  course.value = (await run(() => getCourse(courseId.value))) ?? null
  const page = await run(() => listNotes({ courseId: courseId.value, keyword: keyword.value, page: 0, size: 20 }))
  notes.value = page?.items ?? []
}

onMounted(load)
</script>

<template>
  <PageHeader
    :title="course?.title || '课程'"
    :description="course?.summary || '这门课里的笔记'"
  >
    <el-button @click="$router.push('/courses')">返回</el-button>
    <el-button type="primary" @click="$router.push({ path: '/notes/new', query: { courseId } })">写笔记</el-button>
  </PageHeader>

  <div class="toolbar">
    <el-input v-model="keyword" placeholder="搜索标题或正文" clearable style="max-width: 280px" @keyup.enter="load" />
    <el-button @click="load">搜索</el-button>
  </div>

  <el-skeleton v-if="loading && notes.length === 0" :rows="3" animated />
  <el-empty v-else-if="notes.length === 0" description="这门课还没有笔记" />
  <div v-else class="note-list">
    <RouterLink v-for="note in notes" :key="note.id" class="note-row" :to="`/notes/${note.id}`">
      <div>
        <strong>{{ note.pinned ? '置顶 · ' : '' }}{{ note.title }}</strong>
        <div>
          <span v-for="tag in note.tags" :key="tag" class="tag">{{ tag }}</span>
        </div>
      </div>
      <small>{{ formatTime(note.updatedAt) }}</small>
    </RouterLink>
  </div>
</template>
