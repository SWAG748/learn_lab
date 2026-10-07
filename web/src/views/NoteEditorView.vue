<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { UploadRequestOptions } from 'element-plus'
import PageHeader from '../components/PageHeader.vue'
import {
  createComment,
  createNote,
  deleteAttachment,
  deleteComment,
  deleteNote,
  downloadAttachment,
  getNote,
  listComments,
  listCourses,
  listTags,
  updateNote,
  uploadAttachment
} from '../api/learnlab'
import { useAsync } from '../composables/useAsync'
import type { Attachment, Comment, Course } from '../types'
import { renderMarkdown } from '../utils/markdown'
import { formatTime } from '../utils/format'

const route = useRoute()
const router = useRouter()
const { loading, run } = useAsync()
const courses = ref<Course[]>([])
const tags = ref<string[]>([])
const comments = ref<Comment[]>([])
const attachments = ref<Attachment[]>([])
const commentText = ref('')
const noteId = computed(() => (route.params.id ? Number(route.params.id) : null))
const previewHtml = computed(() => renderMarkdown(form.content || ''))
const form = reactive({
  title: '',
  content: '',
  courseId: route.query.courseId ? Number(route.query.courseId) : undefined as number | undefined,
  pinned: false,
  tags: [] as string[]
})

async function loadCourses() {
  const page = await listCourses(0, 50)
  courses.value = page.items
  if (!form.courseId && courses.value[0]) form.courseId = courses.value[0].id
}

async function loadNote() {
  if (!noteId.value) return
  const note = await run(() => getNote(noteId.value!))
  if (!note) return
  form.title = note.title
  form.content = note.content
  form.courseId = note.courseId
  form.pinned = note.pinned
  form.tags = [...note.tags]
  attachments.value = note.attachments
  comments.value = (await run(() => listComments(note.id))) ?? []
}

async function save() {
  if (!form.courseId) {
    ElMessage.warning('先选一门课')
    return
  }
  const payload = {
    title: form.title,
    content: form.content,
    courseId: form.courseId,
    pinned: form.pinned,
    tags: form.tags
  }
  const saved = noteId.value
    ? await run(() => updateNote(noteId.value!, payload))
    : await run(() => createNote(payload))
  if (!saved) return
  ElMessage.success('笔记已保存')
  if (!noteId.value) router.replace(`/notes/${saved.id}`)
  else await loadNote()
}

async function remove() {
  if (!noteId.value) return
  try {
    await ElMessageBox.confirm('删除这篇笔记？评论和附件会一起删掉。', '删除笔记')
  } catch {
    return
  }
  const removed = await run(() => deleteNote(noteId.value!).then(() => true))
  if (removed) router.push(form.courseId ? `/courses/${form.courseId}` : '/courses')
}

async function sendComment() {
  if (!noteId.value || !commentText.value.trim()) return
  const created = await run(() => createComment(noteId.value!, commentText.value.trim()))
  if (!created) return
  commentText.value = ''
  comments.value = (await listComments(noteId.value)) ?? comments.value
}

async function removeComment(id: number) {
  await run(() => deleteComment(id))
  if (noteId.value) comments.value = (await listComments(noteId.value)) ?? []
}

async function uploadFile(options: UploadRequestOptions) {
  if (!noteId.value) {
    ElMessage.warning('先保存笔记，再上传附件')
    return
  }
  const saved = await run(() => uploadAttachment(noteId.value!, options.file))
  if (saved) await loadNote()
}

async function removeFile(id: number) {
  await run(() => deleteAttachment(id))
  await loadNote()
}

onMounted(async () => {
  tags.value = (await run(listTags)) ?? []
  await loadCourses()
  await loadNote()
})
</script>

<template>
  <PageHeader :title="noteId ? '编辑笔记' : '新笔记'" description="左边写，右边是一个很小的 Markdown 预览。">
    <el-button @click="router.back()">返回</el-button>
    <el-button v-if="noteId" type="danger" plain @click="remove">删除</el-button>
    <el-button type="primary" :loading="loading" @click="save">保存</el-button>
  </PageHeader>

  <div class="editor-grid">
    <section class="panel stack">
      <el-input v-model="form.title" placeholder="标题" maxlength="160" />
      <el-select v-model="form.courseId" placeholder="选择课程" style="width: 100%">
        <el-option v-for="course in courses" :key="course.id" :label="course.title" :value="course.id" />
      </el-select>
      <el-select v-model="form.tags" multiple filterable allow-create default-first-option placeholder="标签，回车新建" style="width: 100%">
        <el-option v-for="tag in tags" :key="tag" :label="tag" :value="tag" />
      </el-select>
      <el-checkbox v-model="form.pinned">置顶</el-checkbox>
      <el-input v-model="form.content" type="textarea" :rows="16" placeholder="支持 # 标题、- 列表、**加粗**、`代码`" />
    </section>
    <section class="panel">
      <h2>预览</h2>
      <div class="preview" v-html="previewHtml" />
    </section>
  </div>

  <section v-if="noteId" class="panel" style="margin-top: 16px">
    <h2>附件</h2>
    <el-upload :show-file-list="false" :http-request="uploadFile">
      <el-button>上传文件</el-button>
    </el-upload>
    <div class="note-list" style="margin-top: 12px">
      <div v-for="file in attachments" :key="file.id" class="note-row">
        <span>{{ file.originalName }}</span>
        <span>
          <el-button link @click="downloadAttachment(file.id, file.originalName)">下载</el-button>
          <el-button link type="danger" @click="removeFile(file.id)">删除</el-button>
        </span>
      </div>
    </div>
  </section>

  <section v-if="noteId" class="panel" style="margin-top: 16px">
    <h2>评论</h2>
    <div v-for="comment in comments" :key="comment.id" class="note-row">
      <div>
        <strong>{{ comment.authorName }}</strong>
        <p>{{ comment.content }}</p>
        <small>{{ formatTime(comment.createdAt) }}</small>
      </div>
      <el-button link type="danger" @click="removeComment(comment.id)">删除</el-button>
    </div>
    <div class="toolbar" style="margin-top: 12px">
      <el-input v-model="commentText" placeholder="写一条评论" @keyup.enter="sendComment" />
      <el-button type="primary" @click="sendComment">发送</el-button>
    </div>
  </section>
</template>
