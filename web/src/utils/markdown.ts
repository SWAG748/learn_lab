function escapeHtml(value: string) {
  return value
    .replaceAll('&', '&amp;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;')
    .replaceAll('"', '&quot;')
}

function inline(value: string) {
  return value
    .replace(/`([^`]+)`/g, '<code>$1</code>')
    .replace(/\*\*([^*]+)\*\*/g, '<strong>$1</strong>')
}

/** 只支持标题、列表、行内代码和加粗。先转义 HTML，避免笔记内容被当成脚本执行。 */
export function renderMarkdown(source: string) {
  const lines = escapeHtml(source).replaceAll('\r\n', '\n').split('\n')
  const html: string[] = []
  let inList = false

  const closeList = () => {
    if (!inList) return
    html.push('</ul>')
    inList = false
  }

  for (const line of lines) {
    const item = line.match(/^- (.+)$/)
    if (item) {
      if (!inList) {
        html.push('<ul>')
        inList = true
      }
      html.push(`<li>${inline(item[1])}</li>`)
      continue
    }
    closeList()
    if (line.startsWith('### ')) html.push(`<h3>${inline(line.slice(4))}</h3>`)
    else if (line.startsWith('## ')) html.push(`<h2>${inline(line.slice(3))}</h2>`)
    else if (line.startsWith('# ')) html.push(`<h1>${inline(line.slice(2))}</h1>`)
    else if (line.trim() !== '') html.push(`<p>${inline(line)}</p>`)
  }
  closeList()
  return html.join('\n')
}
