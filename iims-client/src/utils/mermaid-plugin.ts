// mermaid-plugin.ts
import mermaid from "mermaid";
import MarkdownIt from 'markdown-it';

function markdownItMermaid(md: MarkdownIt): MarkdownIt {
    // 存储已渲染的图表缓存
    const chartCache = new Map<string, string>();

    // 跟踪正在渲染的节点，防止重复渲染
    const renderingMap = new WeakMap<Element, boolean>();

    // 跟踪已渲染的节点，防止重复处理
    const renderedNodes = new WeakSet<Element>();

    let mermaidInitialized = false;

    function initMermaid(): void {
        if (!mermaidInitialized) {
            try {
                mermaid.initialize({
                    startOnLoad: false,
                    theme: 'default',
                    securityLevel: 'loose',
                    fontFamily: 'inherit'
                });
                mermaidInitialized = true;
            } catch (error) {
                console.warn('Mermaid initialization failed:', error);
            }
        }
    }

    // 生成唯一ID：添加时间戳和随机数
    function generateStableId(content: string): string {
        let hash = 0;
        for (let i = 0; i < content.length; i++) {
            const char = content.charCodeAt(i);
            hash = ((hash << 5) - hash) + char;
            hash = hash & hash;
        }
        // 添加时间戳和随机数确保全局唯一
        const timestamp = Date.now();
        const random = Math.random().toString(36).substring(2, 8);
        return `mermaid-${Math.abs(hash)}-${timestamp}-${random}`;
    }

    const mermaidRegex = /^mermaid\s*$/i;

    const defaultFenceRenderer = md.renderer.rules.fence || function(tokens, idx, options, self) {
        return self.renderToken(tokens, idx, options);
    };

    md.renderer.rules.fence = function (tokens, idx, options, env, self) {
        const token = tokens[idx];
        const code = token.content.trim();
        const info = token.info ? md.utils.unescapeAll(token.info).trim() : '';

        if (mermaidRegex.test(info)) {
            initMermaid();
            const id = generateStableId(code);

            return `<div class="mermaid-placeholder" data-mermaid-content="${encodeURIComponent(code)}" data-mermaid-id="${id}" data-mermaid-raw="${encodeURIComponent(code)}">
                        <div class="mermaid-loading">Loading diagram...</div>
                    </div>`;
        }

        return defaultFenceRenderer(tokens, idx, options, env, self);
    };

    // 防抖函数
    let renderDebounceTimer: number | null = null;
    function debounceRender(delay: number = 50): void {
        if (renderDebounceTimer) {
            clearTimeout(renderDebounceTimer);
        }
        renderDebounceTimer = window.setTimeout(() => {
            autoRenderMermaidCharts().then();
        }, delay);
    }

    // 【重构】自动渲染函数
    async function autoRenderMermaidCharts(): Promise<void> {
        if (typeof document === 'undefined') return;

        const placeholders = document.querySelectorAll('.mermaid-placeholder:not(.mermaid-rendered)');
        if (placeholders.length === 0) return;

        async function bindCopyButtonEvents(wrapper: HTMLElement): Promise<void> {
            const copyBtn = wrapper.querySelector<HTMLElement>('.mermaid-copy-code');
            if (!copyBtn || (copyBtn as any).__copyBound) return; // 防重复绑定

            (copyBtn as any).__copyBound = true;
            copyBtn.addEventListener('click', async () => {
                const codePanel = wrapper.querySelector('.mermaid-code-panel');
                const codeEl = codePanel?.querySelector('code');
                const codeText = codeEl?.textContent?.trim();

                if (!codeText) return;

                try {
                    await navigator.clipboard.writeText(codeText);
                    const original = copyBtn.innerHTML;
                    copyBtn.innerHTML = '<i class="ri-check-line"></i>';
                    setTimeout(() => { copyBtn.innerHTML = original; }, 2000);
                } catch {
                    // 降级方案
                    const ta = document.createElement('textarea');
                    ta.value = codeText;
                    document.body.appendChild(ta);
                    ta.select();
                    document.execCommand('copy');
                    document.body.removeChild(ta);

                    const original = copyBtn.innerHTML;
                    copyBtn.innerHTML = '<i class="ri-check-line"></i>';
                    setTimeout(() => { copyBtn.innerHTML = original; }, 2000);
                }
            });
        }

        for (const placeholder of Array.from(placeholders)) {
            // 检查是否已在渲染中
            if (renderingMap.has(placeholder)) {
                continue;
            }

            // 检查是否已被替换（防止重复处理）
            if (renderedNodes.has(placeholder)) {
                placeholder.classList.add('mermaid-rendered');
                continue;
            }

            try {
                // 标记为正在渲染
                renderingMap.set(placeholder, true);

                if (!placeholder.isConnected || !placeholder.parentNode) {
                    console.warn('[Mermaid] Placeholder removed before render, skipping:', placeholder);
                    renderingMap.delete(placeholder);
                    continue;
                }

                placeholder.classList.add('mermaid-rendered');

                const content = decodeURIComponent(placeholder.getAttribute('data-mermaid-content') || '');
                const rawContent = decodeURIComponent(placeholder.getAttribute('data-mermaid-raw') || '');
                const id = placeholder.getAttribute('data-mermaid-id') || '';

                // 使用ID作为缓存键，而非content
                let svg: string | null = null;
                let renderSuccess = true;

                if (chartCache.has(id)) {
                    svg = chartCache.get(id) || null;
                } else {
                    try {
                        if (await mermaid.parse(content, { suppressErrors: false })) {
                            const renderResult = await mermaid.render(id, content);
                            svg = renderResult.svg;
                            if (svg) {
                                chartCache.set(id, svg);
                            }
                        }
                    } catch (renderError) {
                        renderSuccess = false;
                        svg = null;
                    }
                }

                //二次校验
                if (!placeholder.isConnected || !placeholder.parentNode) {
                    placeholder.classList.remove('mermaid-rendered');
                    renderingMap.delete(placeholder);
                    continue;
                }

                //检查是否已被其他任务替换
                if (!placeholder.classList.contains('mermaid-placeholder')) {
                    renderingMap.delete(placeholder);
                    continue;
                }

                const containerHtml = renderSuccess && svg
                    ? createCanvasContainer(svg, id, rawContent)
                    : createErrorContainer(rawContent, id);

                const tempDiv = document.createElement('div');
                tempDiv.innerHTML = containerHtml.trim();
                const newNode = tempDiv.firstElementChild;

                if (newNode) {
                    // 替换前再次确认
                    if (placeholder.isConnected && placeholder.parentNode) {
                        placeholder.replaceWith(newNode);
                        renderedNodes.add(placeholder); // 标记为已处理
                        await bindCopyButtonEvents(newNode as HTMLElement);
                    }
                } else {
                    console.error('[Mermaid] Failed to create replacement node');
                    placeholder.classList.remove('mermaid-rendered');
                }

            } catch (error) {
                console.error('[Mermaid] Render failed for placeholder:', placeholder, error);
                if (placeholder.classList.contains('mermaid-rendered')) {
                    placeholder.classList.remove('mermaid-rendered');
                }
            } finally {
                // 清除渲染标记
                renderingMap.delete(placeholder);
            }
        }

        // 初始化画布功能
        if (document.querySelector('.mermaid-canvas:not(.mermaid-canvas-initialized)')) {
            initCanvasFeatures();
        }
    }

    // 创建画布容器（修改按钮文本和图标）
    function createCanvasContainer(svg: string, id: string, rawContent: string): string {
        return `
          <div class="mermaid-canvas-wrapper mermaid-rendered" data-mermaid-id="${id}">
            <div class="mermaid-canvas-header">
              <div class="mermaid-canvas-title">图表预览</div>
              <div class="mermaid-canvas-controls">
                <button class="mermaid-btn mermaid-reset-view" title="重置视图"><i class="ri-restart-line"></i></button>
                <button class="mermaid-btn mermaid-copy-code" title="复制源码"><i class="ri-file-copy-2-line"></i></button>
                <button class="mermaid-btn mermaid-download-svg" title="下载SVG"><i class="ri-download-line"></i></button>
              </div>
            </div>
            <div class="mermaid-canvas-container">
              <div class="mermaid-canvas" data-mermaid-id="${id}">
                <div class="mermaid-svg-wrapper">
                  ${svg}
                </div>
              </div>
            </div>
            <div class="mermaid-code-panel" style="display: none;">
              <pre><code class="language-mermaid">${escapeHtml(rawContent)}</code></pre>
            </div>
          </div>
        `;
    }

    // 创建错误容器（修改按钮文本和图标）
    function createErrorContainer(rawContent: string, id: string): string {
        return `
          <div class="mermaid-canvas-wrapper mermaid-error-wrapper mermaid-rendered" data-mermaid-id="${id}">
            <div class="mermaid-canvas-header">
              <div class="mermaid-error-title">mermaid</div>
              <div class="mermaid-canvas-controls">
                <button class="mermaid-btn mermaid-copy-code" title="复制源码"><i class="ri-file-copy-2-line"></i></button>
              </div>
            </div>
            <div class="mermaid-code-panel" style="display: block;">
              <pre><code class="language-mermaid">${escapeHtml(rawContent)}</code></pre>
            </div>
          </div>
        `;
    }

    function escapeHtml(text: string): string {
        const map: Record<string, string> = {
            '&': '&amp;',
            '<': '<',
            '>': '>',
            '"': '&quot;',
            "'": '&#039;'
        };
        return text.replace(/[&<>"']/g, function(m) { return map[m]; });
    }

    function initCanvasFeatures(): void {
        if (typeof document === 'undefined') return;

        const canvases = document.querySelectorAll('.mermaid-canvas:not(.mermaid-canvas-initialized)');
        canvases.forEach(canvas => {
            canvas.classList.add('mermaid-canvas-initialized');
            initializeCanvas(canvas as HTMLElement);
        });
    }

    function initializeCanvas(canvas: HTMLElement): void {
        let isPanning = false;
        let startX = 0, startY = 0;
        let startOffsetX = 0, startOffsetY = 0;
        let scale = 0.3;
        const svgWrapper = canvas.querySelector('.mermaid-svg-wrapper') as HTMLElement | null;

        if (!svgWrapper) return;

        setTimeout(() => {
            centerSvgInCanvas(canvas, svgWrapper);
        }, 50);

        canvas.addEventListener('mousedown', function(e: MouseEvent) {
            if (e.button === 0) {
                isPanning = true;
                startX = e.clientX;
                startY = e.clientY;
                startOffsetX = parseFloat(svgWrapper.style.left) || 0;
                startOffsetY = parseFloat(svgWrapper.style.top) || 0;
                canvas.style.cursor = 'grabbing';
                e.preventDefault();
            }
        });

        document.addEventListener('mousemove', function(e: MouseEvent) {
            if (isPanning) {
                const deltaX = e.clientX - startX;
                const deltaY = e.clientY - startY;
                svgWrapper.style.left = (startOffsetX + deltaX) + 'px';
                svgWrapper.style.top = (startOffsetY + deltaY) + 'px';
            }
        });

        document.addEventListener('mouseup', function() {
            if (isPanning) {
                isPanning = false;
                canvas.style.cursor = 'grab';
            }
        });

        canvas.addEventListener('mouseenter', function() {
            canvas.style.cursor = 'grab';
        });

        canvas.addEventListener('mouseleave', function() {
            if (!isPanning) {
                canvas.style.cursor = 'default';
            }
        });

        canvas.addEventListener('wheel', function(e: WheelEvent) {
            e.preventDefault();

            const rect = canvas.getBoundingClientRect();
            const mouseX = e.clientX - rect.left;
            const mouseY = e.clientY - rect.top;

            const currentLeft = parseFloat(svgWrapper.style.left) || 0;
            const currentTop = parseFloat(svgWrapper.style.top) || 0;
            const currentScale = scale;

            const relativeX = (mouseX - currentLeft) / currentScale;
            const relativeY = (mouseY - currentTop) / currentScale;

            const zoomIntensity = 0.1;
            const wheel = e.deltaY < 0 ? 1 : -1;
            const zoom = Math.exp(wheel * zoomIntensity);

            const newScale = Math.max(0.1, Math.min(5, currentScale * zoom));

            if (newScale !== currentScale) {
                scale = newScale;
                svgWrapper.style.transform = `scale(${scale})`;

                const newLeft = mouseX - relativeX * scale;
                const newTop = mouseY - relativeY * scale;

                svgWrapper.style.left = newLeft + 'px';
                svgWrapper.style.top = newTop + 'px';
            }
        });

        canvas.addEventListener('dblclick', function() {
            resetCanvasView(canvas, svgWrapper);
        });

        const wrapper = canvas.closest('.mermaid-canvas-wrapper');
        const resetBtn = wrapper?.querySelector('.mermaid-reset-view');
        if (resetBtn) {
            resetBtn.addEventListener('click', function() {
                resetCanvasView(canvas, svgWrapper);
            });
        }

        // 复制源码功能
        const copyCodeBtn = wrapper?.querySelector('.mermaid-copy-code');
        if (copyCodeBtn) {
            copyCodeBtn.addEventListener('click', async function() {
                const codePanel = wrapper?.querySelector('.mermaid-code-panel') as HTMLElement | null;
                const codeElement = codePanel?.querySelector('code');

                if (codeElement && codeElement.textContent) {
                    try {
                        await navigator.clipboard.writeText(codeElement.textContent);

                        // 临时改变按钮样式提示复制成功
                        const originalIcon = copyCodeBtn.innerHTML;
                        copyCodeBtn.innerHTML = '<i class="ri-check-line"></i>';

                        setTimeout(() => {
                            copyCodeBtn.innerHTML = originalIcon;
                        }, 2000);
                    } catch (err) {
                        console.error('Failed to copy code: ', err);

                        // 降级方案：创建临时textarea复制
                        const textarea = document.createElement('textarea');
                        textarea.value = codeElement.textContent;
                        document.body.appendChild(textarea);
                        textarea.select();
                        document.execCommand('copy');
                        document.body.removeChild(textarea);

                        // 同样的视觉反馈
                        const originalIcon = copyCodeBtn.innerHTML;
                        copyCodeBtn.innerHTML = '<i class="ri-check-line"></i>';

                        setTimeout(() => {
                            copyCodeBtn.innerHTML = originalIcon;
                        }, 2000);
                    }
                }
            });
        }

        const downloadSvgBtn = wrapper?.querySelector('.mermaid-download-svg');
        if (downloadSvgBtn) {
            downloadSvgBtn.addEventListener('click', async function() {
                await downloadSvg(canvas, wrapper as HTMLElement);
            });
        }
    }

    function centerSvgInCanvas(canvas: HTMLElement, svgWrapper: HTMLElement): void {
        if (!canvas || !svgWrapper) return;

        const canvasRect = canvas.getBoundingClientRect();
        svgWrapper.style.width = 'auto';
        svgWrapper.style.height = 'auto';

        const svgRect = svgWrapper.getBoundingClientRect();

        const centerX = (canvasRect.width - svgRect.width) / 2;
        const centerY = (canvasRect.height - svgRect.height) / 2;

        svgWrapper.style.left = Math.max(0, centerX) + 'px';
        svgWrapper.style.top = Math.max(0, centerY) + 'px';
    }

    function resetCanvasView(canvas: HTMLElement, svgWrapper: HTMLElement): void {
        if (svgWrapper) {
            svgWrapper.style.transform = 'scale(0.3)';
            svgWrapper.style.left = '0px';
            svgWrapper.style.top = '0px';
            svgWrapper.style.width = 'auto';
            svgWrapper.style.height = 'auto';
        }
        setTimeout(() => {
            centerSvgInCanvas(canvas, svgWrapper);
        }, 10);
    }

    async function downloadSvg(canvas: HTMLElement, wrapper: HTMLElement): Promise<void> {
        const svgWrapper = canvas.querySelector('.mermaid-svg-wrapper') as HTMLElement | null;
        if (!svgWrapper) return;

        const svgElement = svgWrapper.querySelector('svg') as SVGElement;
        if (!svgElement) return;

        const rawContent = wrapper.querySelector('.mermaid-code-panel code')?.textContent || 'diagram';
        const filename = generateFilename(rawContent, 'svg');

        const serializer = new XMLSerializer();
        let svgString = serializer.serializeToString(svgElement);
        svgString = '<?xml version="1.0" standalone="no"?>\n' + svgString;

        const blob = new Blob([svgString], { type: 'image/svg+xml' });
        const url = URL.createObjectURL(blob);

        const a = document.createElement('a');
        a.href = url;
        a.download = filename;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        URL.revokeObjectURL(url);
    }

    function generateFilename(content: string, extension: string): string {
        const prefix = 'mermaid-diagram';
        const timestamp = new Date().toISOString().slice(0, 19).replace(/[:\-]/g, '');

        const contentPreview = content
            .substring(0, 20)
            .replace(/[^a-zA-Z0-9]/g, '-')
            .replace(/-+/g, '-')
            .replace(/^-|-$/g, '')
            .toLowerCase()
            .substring(0, 20);

        const suffix = contentPreview ? `-${contentPreview}` : '';
        return `${prefix}${suffix}-${timestamp}.${extension}`;
    }

    // 【关键修复】移除高频轮询，改用智能触发
    if (typeof window !== 'undefined') {
        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', () => autoRenderMermaidCharts());
        } else {
            setTimeout(() => autoRenderMermaidCharts(), 0);
        }

        // 【新增】监听 DOM 变化，智能触发渲染
        const observer = new MutationObserver((mutations) => {
            // 检查是否有新的 mermaid-placeholder
            for (const mutation of mutations) {
                if (mutation.addedNodes.length > 0) {
                    for (const node of mutation.addedNodes) {
                        if (node.nodeType === Node.ELEMENT_NODE) {
                            const element = node as Element;
                            if (element.classList?.contains('mermaid-placeholder') ||
                                element.querySelector('.mermaid-placeholder')) {
                                debounceRender();
                                break;
                            }
                        }
                    }
                }
            }
        });

        observer.observe(document.body, {
            childList: true,
            subtree: true
        });

        // 【新增】暴露手动触发方法
        window.addEventListener('mermaid:refresh', () => {
            autoRenderMermaidCharts().then();
        });
    }

    (md as any).renderMermaidCharts = autoRenderMermaidCharts;
    (md as any).refreshMermaidCharts = () => {
        // 清除所有渲染标记，重新渲染
        document.querySelectorAll('.mermaid-rendered').forEach(el => {
            el.classList.remove('mermaid-rendered');
        });
        chartCache.clear();
        autoRenderMermaidCharts().then();
    };

    return md;
}

// CSS 保持不变
function addMermaidStyles(): void {
    if (typeof document !== 'undefined' && !document.getElementById('mermaid-plugin-styles')) {
        const style = document.createElement('style');
        style.id = 'mermaid-plugin-styles';
        style.textContent = `
          .mermaid-placeholder {
            background: #f8f9fa;
            border: 1px dashed #dee2e6;
            border-radius: 4px;
            padding: 20px;
            margin: 10px 0;
            min-height: 444.667px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: inherit;
          }
          
          .mermaid-loading {
            color: #6c757d;
            font-style: italic;
          }
          
          .mermaid-canvas-wrapper {
            margin: 10px 0;
            border: 1px solid #ddd;
            border-radius: 6px;
            background: white;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            overflow: hidden;
          }
          
          .mermaid-canvas-header {
            background: #f8f9fa;
            border-bottom: 1px solid #ddd;
            padding: 8px 12px;
            display: flex;
            justify-content: space-between;
            align-items: center;
          }
          
          .mermaid-canvas-title {
            font-size: 14px;
            font-weight: 500;
            color: #495057;
          }
          
          .mermaid-error-title {
            font-size: 14px;
            color: #dc3545;
            font-weight: 500;
          }
          
          .mermaid-canvas-controls {
            display: flex;
            gap: 4px;
          }
          
          .mermaid-btn {
            background: white;
            border: 1px solid #ccc;
            border-radius: 4px;
            padding: 4px 8px;
            cursor: pointer;
            font-size: 12px;
            transition: all 0.2s;
            min-width: 28px;
            height: 28px;
            display: flex;
            align-items: center;
            justify-content: center;
          }
          
          .mermaid-btn:hover {
            background: #e9ecef;
            border-color: #999;
          }
          
          .mermaid-btn.active {
            background: #007bff;
            color: white;
            border-color: #007bff;
          }
          
          .mermaid-canvas-container {
            position: relative;
            height: 400px;
            background: #f8f9fa;
          }
          
          .mermaid-canvas {
            width: 100%;
            height: 100%;
            overflow: hidden;
            cursor: grab;
            position: relative;
            background: 
              linear-gradient(45deg, #f0f0f0 25%, transparent 25%),
              linear-gradient(-45deg, #f0f0f0 25%, transparent 25%),
              linear-gradient(45deg, transparent 75%, #f0f0f0 75%),
              linear-gradient(-45deg, transparent 75%, #f0f0f0 75%);
            background-size: 20px 20px;
            background-position: 0 0, 0 10px, 10px -10px, -10px 0px;
          }
          
          .mermaid-canvas:active {
            cursor: grabbing;
          }
          
          .mermaid-svg-wrapper {
            position: absolute;
            transform-origin: 0 0;
            padding: 20px;
            transform: scale(0.3);
            left: 0;
            top: 0;
            will-change: transform, left, top;
            width: auto !important;
            height: auto !important;
          }
          
          .mermaid-svg-wrapper svg {
            display: block;
            background: #ffffff00;
            border-radius: 4px;
            width: 1920px !important;
            max-width: none !important;
            height: auto !important;
          }
          
          .mermaid-code-panel {
            border-top: 1px solid #eee;
            background: #f8f9fa;
            max-height: 400px;
            overflow: auto;
          }
          
          .mermaid-code-panel::-webkit-scrollbar {
            width: 6px;
          }
        
           .mermaid-code-panel::-webkit-scrollbar-track {
             background: #f1f1f1;
             border-radius: 4px;
           }
        
           .mermaid-code-panel::-webkit-scrollbar-thumb {
             background: #999;
             border-radius: 3px;
           }
        
           .mermaid-code-panel::-webkit-scrollbar-thumb:hover {
             background: #666;
           }
          
          .mermaid-code-panel pre {
            margin: 0;
            background: #fff;
            padding: 10px;
            border-radius: 4px;
            overflow: auto;
          }
          
          .mermaid-code-panel code {
            font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
            font-size: 12px;
            line-height: 1.4;
          }
          
          .mermaid-error-wrapper .mermaid-canvas-header {
            background: #fff5f5;
            border-bottom-color: #f5c6cb;
          }
        `;
        document.head.appendChild(style);
    }
}

if (typeof window !== 'undefined') {
    addMermaidStyles();
}

export default markdownItMermaid;