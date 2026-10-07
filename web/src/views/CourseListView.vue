<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import PageHeader from '../components/PageHeader.vue'
import { createCourse, deleteCourse, listCourses, updateCourse } from '../api/learnlab'
import { useAsync } from '../composables/useAsync'
import type { Course } from '../types'

const { loading, run } = useAsync()
const courses = ref<Course[]>([])
const page = ref(1)
const total = ref(0)
const dialog = ref(false)
const editingId = ref<number | null>(null)
const form = reactive({ title: '', summary: '' })

async function load() {
  const result = await run(() => listCourses(page.value - 1, 9))
  if (!result) return
  courses.value = result.items
  total.value = result.total
}

function openCreate() {
  editingId.value = null
  form.title = ''
  form.summary = ''
  dialog.value = true
}

function openEdit(course: Course) {
  editingId.value = course.id
  form.title = course.title
  form.summary = course.summary || ''
  dialog.value = true
}

async function save() {
  const payload = { title: form.title.trim(), summary: form.summary.trim() }
  const saved = editingId.value
    ? await run(() => updateCourse(editingId.value!, payload))
    : await run(() => createCourse(payload))
  if (!saved) return
  ElMessage.success(editingId.value ? '课程已更新' : '课程已创建')
  dialog.value = false
  await load()
}

async function remove(course: Course) {
  try {
    await ElMessageBox.confirm(`删除「${course.title}」？课里如果还有笔记或任务，后端会拒绝。`, '删除课程')
  } catch {
    return
  }
  const removed = await run(() => deleteCourse(course.id).then(() => true))
  if (removed) await load()
}

onMounted(load)
</script>

<template>
  <PageHeader title="课程" description="一门课下面挂笔记和任务。分页从后端的 0 开始，这里显示成第 1 页。">
    <el-button type="primary" @click="openCreate">新建课程</el-button>
  </PageHeader>

  <el-skeleton v-if="loading && courses.length === 0" :rows="4" animated />
  <el-empty v-else-if="courses.length === 0" description="还没有课程" />
  <section v-else class="card-grid">
    <article v-for="course in courses" :key="course.id" class="course-card">
      <h2>{{ course.title }}</h2>
      <p>{{ course.summary || '还没有简介' }}</p>
      <div class="meta">
        <span>{{ course.noteCount }} 篇笔记</span>
        <span>{{ course.taskCount }} 个任务</span>
      </div>
      <div class="card-actions">
        <el-button type="primary" link @click="$router.push(`/courses/${course.id}`)">打开</el-button>
        <el-button link @click="openEdit(course)">编辑</el-button>
        <el-button link type="danger" @click="remove(course)">删除</el-button>
      </div>
    </article>
  </section>
  <div class="pager">
    <el-pagination
      v-model:current-page="page"
      layout="prev, pager, next"
      :page-size="9"
      :total="total"
      hide-on-single-page
      @current-change="load"
    />
  </div>

  <el-dialog v-model="dialog" :title="editingId ? '编辑课程' : '新建课程'" width="480px">
    <el-form label-position="top">
      <el-form-item label="标题">
        <el-input v-model="form.title" maxlength="120" />
      </el-form-item>
      <el-form-item label="简介">
        <el-input v-model="form.summary" type="textarea" :rows="3" maxlength="500" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialog = false">取消</el-button>
      <el-button type="primary" :loading="loading" @click="save">保存</el-button>
    </template>
  </el-dialog>
</template>
