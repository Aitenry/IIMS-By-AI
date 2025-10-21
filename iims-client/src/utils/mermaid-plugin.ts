// mermaid-plugin.ts
import mermaid from "mermaid";
import MarkdownIt from 'markdown-it';

// Mermaid插件函数
function markdownItMermaid(md: MarkdownIt): MarkdownIt {
    // 存储已渲染的图表缓存
    const chartCache = new Map<string, string>();

    // 初始化Mermaid（延迟初始化，避免重复初始化）
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

    // 生成稳定的ID
    function generateStableId(content: string): string {
        // 简单的哈希函数
        let hash = 0;
        for (let i = 0; i < content.length; i++) {
            const char = content.charCodeAt(i);
            hash = ((hash << 5) - hash) + char;
            hash = hash & hash; // 转换为32位整数
        }
        return `mermaid-${Math.abs(hash)}`;
    }

    // 匹配Mermaid代码块的正则表达式
    const mermaidRegex = /^mermaid\s*$/i;

    // 修改fence规则来处理Mermaid代码块
    const defaultFenceRenderer = md.renderer.rules.fence || function(tokens, idx, options, self) {
        return self.renderToken(tokens, idx, options);
    };

    md.renderer.rules.fence = function (tokens, idx, options, env, self) {
        const token = tokens[idx];
        const code = token.content.trim();
        const info = token.info ? md.utils.unescapeAll(token.info).trim() : '';

        // 检查是否是Mermaid代码块
        if (mermaidRegex.test(info)) {
            initMermaid();
            const id = generateStableId(code);

            // 返回占位符，后续通过DOM操作渲染
            return `<div class="mermaid-placeholder" data-mermaid-content="${encodeURIComponent(code)}" data-mermaid-id="${id}" data-mermaid-raw="${encodeURIComponent(code)}">
                        <div class="mermaid-loading">Loading diagram...</div>
                    </div>`;

        }

        // 如果不是Mermaid代码块，使用默认渲染
        return defaultFenceRenderer(tokens, idx, options, env, self);
    };

    // 自动渲染Mermaid图表的函数
    async function autoRenderMermaidCharts(): Promise<void> {
        if (typeof document === 'undefined') return;

        // 只处理未渲染的占位符
        const placeholders = document.querySelectorAll('.mermaid-placeholder:not(.mermaid-rendered)');

        for (const placeholder of Array.from(placeholders)) {
            try {
                // 立即标记为已处理，避免重复处理
                placeholder.classList.add('mermaid-rendered');

                const content = decodeURIComponent(placeholder.getAttribute('data-mermaid-content') || '');
                const rawContent = decodeURIComponent(placeholder.getAttribute('data-mermaid-raw') || '');
                const id = placeholder.getAttribute('data-mermaid-id') || '';

                // 检查缓存
                let svg: string | null = null;
                let renderSuccess = true;

                if (chartCache.has(content)) {
                    svg = chartCache.get(content) || null;
                } else {
                    // 渲染Mermaid图表
                    try {
                        if (await mermaid.parse(content, { suppressErrors: false })) {
                            const renderResult = await mermaid.render(id, content);
                            svg = renderResult.svg;
                            // 缓存结果
                            if (svg) {
                                chartCache.set(content, svg);
                            }
                        }
                    } catch (renderError) {
                        renderSuccess = false;
                        svg = null;
                    }
                }

                // 创建容器
                let containerHtml: string;
                if (renderSuccess && svg) {
                    containerHtml = createCanvasContainer(svg, id, rawContent);
                } else {
                    // 渲染失败时显示原始内容
                    containerHtml = createErrorContainer(rawContent, id);
                }

                // 替换占位符
                placeholder.outerHTML = containerHtml;

            } catch (error) {
                console.error('Failed to process Mermaid placeholder:', error);
                // 移除处理标记，允许重试
                placeholder.classList.remove('mermaid-rendered');
            }
        }

        // 初始化画布功能（只对新创建的画布）
        if (placeholders.length > 0) {
            initCanvasFeatures();
        }
    }

    // 创建画布容器
    function createCanvasContainer(svg: string, id: string, rawContent: string): string {
        return `
          <div class="mermaid-canvas-wrapper mermaid-rendered" data-mermaid-id="${id}">
            <div class="mermaid-canvas-header">
              <div class="mermaid-canvas-title">图表预览</div>
              <div class="mermaid-canvas-controls">
                <button class="mermaid-btn mermaid-reset-view" title="重置视图"><i class="ri-restart-line"></i></button>
                <button class="mermaid-btn mermaid-show-code" title="显示源码"><i class="ri-book-read-line"></i></button>
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

    // 创建错误容器（显示原始内容）
    function createErrorContainer(rawContent: string, id: string): string {
        return `
          <div class="mermaid-canvas-wrapper mermaid-error-wrapper mermaid-rendered" data-mermaid-id="${id}">
            <div class="mermaid-canvas-header">
              <div class="mermaid-error-title">图表渲染失败 - 显示原始内容</div>
              <div class="mermaid-canvas-controls">
                <button class="mermaid-btn mermaid-show-code active" title="隐藏源码"><i class="ri-book-read-line"></i></button>
              </div>
            </div>
            <div class="mermaid-code-panel" style="display: block;">
              <pre><code class="language-mermaid">${escapeHtml(rawContent)}</code></pre>
            </div>
          </div>
        `;
    }

    // HTML转义
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

    // 初始化画布功能
    function initCanvasFeatures(): void {
        if (typeof document === 'undefined') return;

        // 只为未初始化的画布初始化
        const canvases = document.querySelectorAll('.mermaid-canvas:not(.mermaid-canvas-initialized)');
        canvases.forEach(canvas => {
            canvas.classList.add('mermaid-canvas-initialized');
            initializeCanvas(canvas as HTMLElement);
        });
    }

    // 初始化单个画布
    function initializeCanvas(canvas: HTMLElement): void {
        let isPanning = false;
        let startX = 0, startY = 0;
        let startOffsetX = 0, startOffsetY = 0;
        let scale = 0.3;
        const svgWrapper = canvas.querySelector('.mermaid-svg-wrapper') as HTMLElement | null;

        if (!svgWrapper) return;

        // 初始化居中显示
        setTimeout(() => {
            centerSvgInCanvas(canvas, svgWrapper);
        }, 50);

        // 鼠标按下 - 开始拖拽
        canvas.addEventListener('mousedown', function(e: MouseEvent) {
            if (e.button === 0) { // 左键
                isPanning = true;
                startX = e.clientX;
                startY = e.clientY;
                startOffsetX = parseFloat(svgWrapper.style.left) || 0;
                startOffsetY = parseFloat(svgWrapper.style.top) || 0;
                canvas.style.cursor = 'grabbing';
                e.preventDefault();
            }
        });

        // 鼠标移动 - 拖拽中
        document.addEventListener('mousemove', function(e: MouseEvent) {
            if (isPanning) {
                const deltaX = e.clientX - startX;
                const deltaY = e.clientY - startY;
                svgWrapper.style.left = (startOffsetX + deltaX) + 'px';
                svgWrapper.style.top = (startOffsetY + deltaY) + 'px';
            }
        });

        // 鼠标释放 - 结束拖拽
        document.addEventListener('mouseup', function() {
            if (isPanning) {
                isPanning = false;
                canvas.style.cursor = 'grab';
            }
        });

        // 鼠标进入画布
        canvas.addEventListener('mouseenter', function() {
            canvas.style.cursor = 'grab';
        });

        // 鼠标离开画布
        canvas.addEventListener('mouseleave', function() {
            if (!isPanning) {
                canvas.style.cursor = 'default';
            }
        });

        // 滚轮缩放
        canvas.addEventListener('wheel', function(e: WheelEvent) {
            e.preventDefault();

            const rect = canvas.getBoundingClientRect();
            const mouseX = e.clientX - rect.left;
            const mouseY = e.clientY - rect.top;

            // 获取当前SVG wrapper的位置和缩放
            const currentLeft = parseFloat(svgWrapper.style.left) || 0;
            const currentTop = parseFloat(svgWrapper.style.top) || 0;
            const currentScale = scale;

            // 计算鼠标相对于SVG内容的位置（考虑缩放）
            const relativeX = (mouseX - currentLeft) / currentScale;
            const relativeY = (mouseY - currentTop) / currentScale;

            // 计算缩放因子
            const zoomIntensity = 0.1;
            const wheel = e.deltaY < 0 ? 1 : -1;
            const zoom = Math.exp(wheel * zoomIntensity);

            // 限制缩放范围
            const newScale = Math.max(0.1, Math.min(5, currentScale * zoom));

            if (newScale !== currentScale) {
                scale = newScale;

                // 应用缩放
                svgWrapper.style.transform = `scale(${scale})`;

                // 重新计算位置以保持鼠标位置不变
                const newLeft = mouseX - relativeX * scale;
                const newTop = mouseY - relativeY * scale;

                svgWrapper.style.left = newLeft + 'px';
                svgWrapper.style.top = newTop + 'px';
            }
        });

        // 双击重置
        canvas.addEventListener('dblclick', function() {
            resetCanvasView(canvas, svgWrapper);
        });

        // 重置视图按钮
        const wrapper = canvas.closest('.mermaid-canvas-wrapper');
        const resetBtn = wrapper?.querySelector('.mermaid-reset-view');
        if (resetBtn) {
            resetBtn.addEventListener('click', function() {
                resetCanvasView(canvas, svgWrapper);
            });
        }

        // 显示/隐藏源码
        const showCodeBtn = wrapper?.querySelector('.mermaid-show-code');
        if (showCodeBtn) {
            showCodeBtn.addEventListener('click', function() {
                const codePanel = wrapper?.querySelector('.mermaid-code-panel') as HTMLElement | null;
                if (codePanel) {
                    const isVisible = codePanel.style.display !== 'none';
                    codePanel.style.display = isVisible ? 'none' : 'block';
                    showCodeBtn.classList.toggle('active', !isVisible);
                }
            });
        }

        // 下载SVG按钮
        const downloadSvgBtn = wrapper?.querySelector('.mermaid-download-svg');
        if (downloadSvgBtn) {
            downloadSvgBtn.addEventListener('click', async function() {
                await downloadSvg(canvas, wrapper as HTMLElement);
            });
        }
    }

    // 居中显示SVG
    function centerSvgInCanvas(canvas: HTMLElement, svgWrapper: HTMLElement): void {
        if (!canvas || !svgWrapper) return;

        // 获取画布尺寸
        const canvasRect = canvas.getBoundingClientRect();

        // 强制重新计算SVG尺寸
        svgWrapper.style.width = 'auto';
        svgWrapper.style.height = 'auto';

        // 获取SVG的实际尺寸
        const svgRect = svgWrapper.getBoundingClientRect();

        // 计算居中位置
        const centerX = (canvasRect.width - svgRect.width) / 2;
        const centerY = (canvasRect.height - svgRect.height) / 2;

        svgWrapper.style.left = Math.max(0, centerX) + 'px';
        svgWrapper.style.top = Math.max(0, centerY) + 'px';
    }

    // 重置画布视图
    function resetCanvasView(canvas: HTMLElement, svgWrapper: HTMLElement): void {
        if (svgWrapper) {
            svgWrapper.style.transform = 'scale(0.3)';
            svgWrapper.style.left = '0px';
            svgWrapper.style.top = '0px';
            // 重置宽度
            svgWrapper.style.width = 'auto';
            svgWrapper.style.height = 'auto';
        }
        // 重新居中
        setTimeout(() => {
            centerSvgInCanvas(canvas, svgWrapper);
        }, 10);
    }

    // 下载SVG
    async function downloadSvg(canvas: HTMLElement, wrapper: HTMLElement): Promise<void> {
        const svgWrapper = canvas.querySelector('.mermaid-svg-wrapper') as HTMLElement | null;
        if (!svgWrapper) return;

        const svgElement = svgWrapper.querySelector('svg') as SVGElement;
        if (!svgElement) return;

        // 获取原始内容以生成文件名
        const rawContent = wrapper.querySelector('.mermaid-code-panel code')?.textContent || 'diagram';
        const filename = generateFilename(rawContent, 'svg');

        // 获取SVG的XML字符串
        const serializer = new XMLSerializer();
        let svgString = serializer.serializeToString(svgElement);

        // 添加XML声明
        svgString = '<?xml version="1.0" standalone="no"?>\n' + svgString;

        // 创建Blob并下载
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

    // 生成文件名
    function generateFilename(content: string, extension: string): string {
        // 简单的文件名生成逻辑
        const prefix = 'mermaid-diagram';
        const timestamp = new Date().toISOString().slice(0, 19).replace(/[:\-]/g, '');

        // 从内容中提取一些有意义的字符作为标识
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

    // 设置自动渲染
    if (typeof window !== 'undefined') {
        // 页面加载完成后渲染
        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', () => autoRenderMermaidCharts());
        } else {
            // 延迟执行，确保DOM完全加载
            setTimeout(() => autoRenderMermaidCharts(), 0);
        }

        // 定期检查新内容（适用于打字机效果）
        setInterval(() => autoRenderMermaidCharts(), 100);
    }

    // 将渲染函数挂载到markdown-it实例上（供手动调用）
    (md as any).renderMermaidCharts = autoRenderMermaidCharts;

    return md;
}

// 添加必要的CSS
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
            overflow: hidden; /* 隐藏滚动条 */
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
            will-change: transform, left, top; /* 优化性能 */
            width: auto !important;
            height: auto !important;
          }
          
          .mermaid-svg-wrapper svg {
            display: block;
            background: #ffffff00;
            border-radius: 4px;
            width: 1920px !important;
            max-width: none !important; /* 防止SVG被压缩 */
            height: auto !important;
          }
          
          .mermaid-code-panel {
            border-top: 1px solid #eee;
            background: #f8f9fa;
            height: 400px;
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

// 确保样式被添加
if (typeof window !== 'undefined') {
    addMermaidStyles();
}

export default markdownItMermaid;