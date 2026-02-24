<template>
  <div class="view-wrapper">
    <!-- 左侧侧栏，包含新增会话和历史列表 -->
    <div class="sidebar">
      <el-button type="primary" class="new-btn" @click="addConversation">
        新增对话
      </el-button>
      <div class="history-list">
        <div
          v-for="(item, idx) in history"
          :key="idx"
          class="history-item"
          :class="{ active: item.conversationId === currentConversationId }"
          @click="selectConversation(item)"
        >
          <el-input
            size="small"
            v-model="item.description"
            @blur="updateDescription(item)"
          />
        </div>
      </div>
    </div>

    <!-- 右侧聊天面板（原来的内容，略作容器调整） -->
    <div class="chat-container">
      <div class="message-list" ref="messageListRef">
          <div
            v-for="(m, idx) in messages"
            :key="m.mid || idx"
            :class="['message', m.from]"
          >
          <div class="text">{{ m.text }}</div>
          <audio
            v-if="m.audioUrl"
            :src="m.audioUrl"
            controls
          />
        </div>
      </div>

      <div class="input-area">
        <el-input
          v-model="userInput"
          placeholder="请输入消息"
          @keyup.enter="sendChat"
          class="user-input"
        />
        <el-button type="primary" @click="sendChat">发送文本</el-button>
        <el-button type="success" @click="sendAudio">发送并语音回复</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, watch } from 'vue'
import api from '@/services/api'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'

interface Message {
  from: 'user' | 'bot'
  text: string
  audioUrl?: string
  // 临时占位 id，用于替换“请稍等”文本
  tempId?: string | null
  // 标记是否已超时
  timedOut?: boolean
  // stable message id for v-for key
  mid?: string
}

// for history items returned from backend
interface HistoryItem {
  // backend messages include an id field, we keep it so that
  // detail/updateChat endpoints can reference the correct record
  id: number | null
  conversationId: string
  description: string
}

const userInput = ref('')
const messages = ref<Message[]>([])
const history = ref<HistoryItem[]>([])
const currentConversationId = ref<string>('')
const messageListRef = ref<HTMLElement | null>(null)
// track pending placeholders: tempId -> { timer, timedOut }
const pendingResponses = new Map<string, { timer: number | null; timedOut: boolean }>()

const auth = useAuthStore()
const router = useRouter()

// helper to extract userId from JWT token payload
function getUserIdFromToken(): string {
  const token = auth.token
  if (!token) return ''
  const parts = token.split('.')
  if (parts.length < 2) return ''
  try {
    const segment = parts[1] || ''
    const payload = JSON.parse(atob(segment))
    return payload.id || payload.userId || ''
  } catch {
    return ''
  }
}

onMounted(() => {
  if (!auth.isAuthenticated) {
    router.replace({ name: 'login' })
    return
  }
  loadHistory()
})

// 自动滚动到底部的辅助函数
function scrollToBottom() {
  const el = messageListRef.value
  if (!el) return
  // 平滑滚动会在某些场景被阻塞，使用瞬时滚动以保证可见性
  el.scrollTop = el.scrollHeight
}

// 当 messages 变化时自动滚动到最新消息
watch(messages, async () => {
  await nextTick()
  scrollToBottom()
}, { deep: true })

async function loadHistory() {
  console.debug('[chat] before loadHistory - auth.token:', auth.token, 'localStorage.token:', localStorage.getItem('token'))
  try {
    const resp = await api.get('/ai/history')
    const list: Array<{ id: number; userId: number; description: string }> = resp.data || []
    const uid = getUserIdFromToken()
    history.value = list.map(item => ({
      id: item.id,
      conversationId: uid + item.description,
      description: item.description,
    }))
  } catch (err: any) {
    console.error('failed to load history', err)
    if (err?.response?.status === 401) auth.logout()
  }
}

async function addConversation() {
  const desc = new Date().toISOString().replace('T', ' ').slice(0, 19)
  const uid = getUserIdFromToken()
  const cid = uid + desc
  console.log("[chat] addConversation - uid:", uid, "desc:", desc, "cid:", cid)
  try {
    await api.post(
      '/ai/addChat',
      JSON.stringify({ conversationId: cid, description: desc }),
      { headers: { 'Content-Type': 'application/json; charset=utf-8' } }
    )
    // refresh history and select new conversation
    await loadHistory()
    currentConversationId.value = cid
    messages.value = []
  } catch (err: any) {
    console.error('add conversation failed', err)
    if (err?.response?.status === 401) auth.logout()
  }
}

async function selectConversation(item: HistoryItem) {
  currentConversationId.value = item.conversationId
  try {
    const resp = await api.post(
      '/ai/detail',
      JSON.stringify({ 
        conversationId: item.conversationId,
        id:item.id
      }),
      { headers: { 'Content-Type': 'application/json; charset=utf-8' } }
    )
    const data: { sentences: Array<{ role: string; text: string }> } = resp.data || { sentences: [] }
    messages.value = data.sentences.map((s, i) => ({
      from: s.role === 'user' ? 'user' : 'bot',
      text: s.text,
      mid: `m-s-${i}-${Date.now()}`,
    }))
  } catch (err: any) {
    console.error('load conversation detail failed', err)
    if (err?.response?.status === 401) auth.logout()
  }
}

async function updateDescription(item: HistoryItem) {
  try {
    await api.put(
      '/ai/updateChat',
      // backend expects an id so it knows which record to update
      JSON.stringify({ id: item.id, conversationId: item.conversationId, description: item.description }),
      { headers: { 'Content-Type': 'application/json; charset=utf-8' } }
    )
    // refresh history just to ensure list is consistent
    await loadHistory()
  } catch (err: any) {
    console.error('update description failed', err)
    if (err?.response?.status === 401) auth.logout()
  }
}

async function sendChat() {
  const msg = userInput.value.trim()
  if (!msg) return
  const userMid = `m-u-${Date.now()}-${Math.floor(Math.random() * 10000)}`
  messages.value.push({ from: 'user', text: msg, mid: userMid })
  userInput.value = ''
  // push placeholder and start timeout
  const tempId = `t-${Date.now()}-${Math.floor(Math.random() * 10000)}`
  messages.value.push({ from: 'bot', text: '请稍等', tempId, mid: tempId })
  await nextTick()
  scrollToBottom()

  const timeoutMs = 60_000
  const timer = window.setTimeout(() => {
    const info = pendingResponses.get(tempId)
    if (!info) return
    info.timedOut = true
    // replace placeholder text with timeout notice
    const idx = messages.value.findIndex(m => m.tempId === tempId)
    if (idx >= 0) {
      const msg = messages.value[idx]
      if (msg) {
        msg.text = '对话超时'
        msg.timedOut = true
      }
    }
    pendingResponses.set(tempId, info)
    // ensure visible
    nextTick().then(scrollToBottom)
  }, timeoutMs)
  pendingResponses.set(tempId, { timer, timedOut: false })

  try {
    const body: any = { message: msg }
    if (currentConversationId.value) body.conversationId = currentConversationId.value
    const resp = await api.post('/ai/chat', JSON.stringify(body), {
      headers: { 'Content-Type': 'application/json; charset=utf-8' },
    })
    const botText = typeof resp.data === 'string' ? resp.data : resp.data?.content || ''

    const info = pendingResponses.get(tempId)
    if (info && !info.timedOut) {
      // replace placeholder with actual content
      const idx = messages.value.findIndex(m => m.tempId === tempId)
      if (idx >= 0) {
        const msg = messages.value[idx]
        if (msg) {
          msg.text = botText
          msg.tempId = null
        }
      } else {
        messages.value.push({ from: 'bot', text: botText, mid: `m-b-${Date.now()}-${Math.floor(Math.random() * 10000)}` })
      }
    } else {
      // already timed out: append as new message
      messages.value.push({ from: 'bot', text: botText, mid: `m-b-${Date.now()}-${Math.floor(Math.random() * 10000)}` })
    }
    // clear timer
    if (info && info.timer) clearTimeout(info.timer)
    pendingResponses.delete(tempId)
    await nextTick()
    scrollToBottom()
  } catch (err: any) {
    console.error('chat request failed', err)
    const info = pendingResponses.get(tempId)
    if (info) {
      if (!info.timedOut) {
        const idx = messages.value.findIndex(m => m.tempId === tempId)
        if (idx >= 0) {
          const msg = messages.value[idx]
          if (msg) msg.text = '对话超时'
        }
      }
      if (info.timer) clearTimeout(info.timer)
      pendingResponses.delete(tempId)
    }
    if (err.response && err.response.status === 401) {
      auth.logout()
    }
  }
}

async function sendAudio() {
  const msg = userInput.value.trim()
  if (!msg) return
  const userMid = `m-u-${Date.now()}-${Math.floor(Math.random() * 10000)}`
  messages.value.push({ from: 'user', text: msg, mid: userMid })
  userInput.value = ''

  try {
    const body: any = { message: msg }
    if (currentConversationId.value) body.conversationId = currentConversationId.value
    const resp = await api.post('/ai/audio', JSON.stringify(body), {
      responseType: 'blob',
      headers: { 'Content-Type': 'application/json; charset=utf-8' },
    })
    // For audio we also display a placeholder first
    const tempId = `t-${Date.now()}-${Math.floor(Math.random() * 10000)}`
    messages.value.push({ from: 'bot', text: '请稍等', tempId, mid: tempId })
    await nextTick()
    scrollToBottom()

    const timeoutMs = 60_000
    const timer = window.setTimeout(() => {
      const info = pendingResponses.get(tempId)
      if (!info) return
      info.timedOut = true
      const idx = messages.value.findIndex(m => m.tempId === tempId)
      if (idx >= 0) {
        const msg = messages.value[idx]
        if (msg) {
          msg.text = '对话超时'
          msg.timedOut = true
        }
      }
      pendingResponses.set(tempId, info)
      nextTick().then(scrollToBottom)
    }, timeoutMs)
    pendingResponses.set(tempId, { timer, timedOut: false })

    const encoded = resp.headers['responsetext'] || resp.headers['responseText']
    // 1. 解码 Base64 为二进制字符串
    const binaryString = atob(encoded)
    // 2. 将二进制字符串转为 Uint8Array
    const bytes = new Uint8Array(binaryString.length)
    for (let i = 0; i < binaryString.length; i++) {
      bytes[i] = binaryString.charCodeAt(i)
    }
    // 3. 用 UTF-8 解码得到原始文本
    const botText = new TextDecoder('utf-8').decode(bytes)
    const audioUrl = URL.createObjectURL(resp.data)

    const info = pendingResponses.get(tempId)
    if (info && !info.timedOut) {
      const idx = messages.value.findIndex(m => m.tempId === tempId)
      if (idx >= 0) {
        const msg = messages.value[idx]
        if (msg) {
          msg.text = botText
          msg.audioUrl = audioUrl
          msg.tempId = null
        }
      } else {
        messages.value.push({ from: 'bot', text: botText, audioUrl, mid: `m-b-${Date.now()}-${Math.floor(Math.random() * 10000)}` })
      }
    } else {
      // timed out earlier, append as separate message
      messages.value.push({ from: 'bot', text: botText, audioUrl, mid: `m-b-${Date.now()}-${Math.floor(Math.random() * 10000)}` })
    }
    if (info && info.timer) clearTimeout(info.timer)
    pendingResponses.delete(tempId)
    const audio = new Audio(audioUrl)
    audio.play().catch(() => {})
    await nextTick()
    scrollToBottom()
  } catch (err: any) {
    console.error('audio request failed', err)
    // mark pending placeholder as timeout if exists
    // find any pending placeholder without content
    for (const [tempId, info] of pendingResponses.entries()) {
      if (!info.timedOut) {
        const idx = messages.value.findIndex(m => m.tempId === tempId)
        if (idx >= 0) {
          const msg = messages.value[idx]
          if (msg) msg.text = '对话超时'
        }
        if (info.timer) clearTimeout(info.timer)
        pendingResponses.delete(tempId)
      }
    }
    if (err.response && err.response.status === 401) {
      auth.logout()
    }
  }
}
</script>

<style scoped>
.view-wrapper {
  display: flex;
  /* 页面总高度减少 200px */
  height: calc(100vh - 200px);
}

.sidebar {
  width: 250px;
  border-right: 1px solid #ddd;
  padding: 0.5rem;
  display: flex;
  flex-direction: column;
}

.new-btn {
  margin-bottom: 1rem;
}

.history-list {
  flex: 1;
  overflow-y: auto;
}

.history-item {
  margin-bottom: 0.5rem;
  cursor: pointer;
}

.history-item.active {
  background: #eef;
}

.chat-container {
  display: flex;
  flex-direction: column;
  /* 固定右侧聊天面板宽度（与左侧 250px 侧栏配合） */
  width: calc(100% - 250px);
  min-width: 480px;
  height: 100%;
  flex: 0 0 auto;
}

.message-list {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 1rem;
  overflow-y: auto;
  padding: 1rem;
  /* 给滚动区域底部预留空间，避免最新消息被输入区域遮挡 */
  padding-bottom: 5rem;
}

.message {
  margin-bottom: 0;
  max-width: 80%;
}

.message.user {
  /* 更可靠的右对齐：在 flex 列容器中使用自动外边距 */
  margin-left: auto;
  text-align: right;
}

.message.bot {
  /* 靠左对齐 */
  margin-right: auto;
  text-align: left;
}

.text {
  background: #f0f0f0;
  padding: 0.5rem 0.75rem;
  border-radius: 5px;
  display: inline-block;
}

.input-area {
  display: flex;
  gap: 0.5rem;
  padding: 0.5rem;
  border-top: 1px solid #eee;
  /* 固定输入区高度，便于计算预留空间 */
  height: 3.5rem;
}

.user-input {
  flex: 1;
}
</style>
