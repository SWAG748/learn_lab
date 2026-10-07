<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import PageHeader from '../components/PageHeader.vue'
import { createTask, deleteTask, listCourses, listTasks, updateTaskStatus } from '../api/learnlab'
import { useAsync } from '../composables/useAsync'
import type { Course, Priority, Task, TaskStatus } from '../types'
import { priorityLabel, statusLabel } from '../utils/format'

const columns: TaskStatus[] = ['TODO', 'DOING', 'DONE']
const { loading, run } = useAsync()
const tasks = ref<Task[]>([])
const courses = ref<Course[]>([])
const courseFilter = ref<number | undefined>()
const dialog = ref(false)
const form = reactive({
  title: '',
  detail: '',
  status: 'TODO' as TaskStatus,
  priority: 'MEDIUM' as Priority,
  dueDate: '' as string | '',
  courseId: undefined as number | undefined
})

const grouped = computed(() =>
  Object.fromEntries(columns.map((status) => [status, tasks.value.filter((task) => task.status === status)])) as Record<TaskStatus, Task[]>
)

async function load() {
  const result = await run(() => listTasks({ courseId: courseFilter.value }))
  tasks.value = result ?? []
}

async function save() {
  const created = await run(() => createTask({
    title: form.title,
    detail: form.detail,
    status: form.status,
    priority: form.priority,
    dueDate: form.dueDate || null,
    courseId: form.courseId ?? null
  }))
  if (!created) return
  dialog.value = false
  form.title = ''
  form.detail = ''
  form.dueDate = ''
  ElMessage.success('任务已添加')
  await load()
}

async function move(task: Task, status: TaskStatus) {
  const updated = await run(() => updateTaskStatus(task.id, status))
  if (updated) await load()
}

async function remove(task: Task) {
  try {
    await ElMessageBox.confirm(`删除「${task.title}」？`, '删除任务')
  } catch {
    return
  }
  const removed = await run(() => deleteTask(task.id).then(() => true))
  if (removed) await load()
}

onMounted(async () => {
  const page = await listCourses(0, 50)
  courses.value = page.items
  await load()
})
</script>

<template>
  <PageHeader title="任务板" description="状态用 PATCH 单独修改，不必把整张任务再提交一遍。">
    <el-select v-model="courseFilter" clearable placeholder="全部课程" style="width: 180px" @change="load">
      <el-option v-for="course in courses" :key="course.id" :label="course.title" :value="course.id" />
    </el-select>
    <el-button type="primary" @click="dialog = true">新建任务</el-button>
  </PageHeader>

  <section class="board">
    <div v-for="status in columns" :key="status" class="column">
      <h2>{{ statusLabel[status] }}</h2>
      <article v-for="task in grouped[status]" :key="task.id" class="task-card">
        <h3>{{ task.title }}</h3>
        <p>{{ task.detail }}</p>
        <div class="meta">
          <span>{{ priorityLabel[task.priority] }}优先级</span>
          <span :class="{ overdue: task.overdue }">{{ task.dueDate || '无截止日期' }}</span>
          <span v-if="task.courseTitle">{{ task.courseTitle }}</span>
        </div>
        <div class="card-actions">
          <el-button v-if="status !== 'TODO'" link @click="move(task, 'TODO')">待办</el-button>
          <el-button v-if="status !== 'DOING'" link @click="move(task, 'DOING')">进行中</el-button>
          <el-button v-if="status !== 'DONE'" link @click="move(task, 'DONE')">完成</el-button>
          <el-button link type="danger" @click="remove(task)">删除</el-button>
        </div>
      </article>
      <el-empty v-if="grouped[status].length === 0" description="空" :image-size="60" />
    </div>
  </section>

  <el-dialog v-model="dialog" title="新建任务" width="480px">
    <el-form label-position="top">
      <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
      <el-form-item label="说明"><el-input v-model="form.detail" type="textarea" :rows="3" /></el-form-item>
      <el-form-item label="课程">
        <el-select v-model="form.courseId" clearable placeholder="可以先不归类" style="width: 100%">
          <el-option v-for="course in courses" :key="course.id" :label="course.title" :value="course.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="优先级">
        <el-select v-model="form.priority" style="width: 100%">
          <el-option label="低" value="LOW" />
          <el-option label="中" value="MEDIUM" />
          <el-option label="高" value="HIGH" />
        </el-select>
      </el-form-item>
      <el-form-item label="截止日期">
        <el-date-picker v-model="form.dueDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialog = false">取消</el-button>
      <el-button type="primary" :loading="loading" @click="save">保存</el-button>
    </template>
  </el-dialog>
</template>
