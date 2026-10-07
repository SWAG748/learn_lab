import { describe, expect, it } from 'vitest'
import { renderMarkdown } from './markdown'

describe('renderMarkdown', () => {
  it('renders headings, lists and bold text', () => {
    const html = renderMarkdown('# 标题\n\n- 一项\n\n这是 **重点**')
    expect(html).toContain('<h1>标题</h1>')
    expect(html).toContain('<li>一项</li>')
    expect(html).toContain('<strong>重点</strong>')
  })

  it('escapes raw html', () => {
    const html = renderMarkdown('<script>alert(1)</script>')
    expect(html).not.toContain('<script>')
    expect(html).toContain('&lt;script&gt;')
  })
})
