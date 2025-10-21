package com.toryu.iims.common.markdown.renderer;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlWriter;

import java.util.Objects;
import java.util.Set;

/**
 * @author: Aiox
 * @url: www.aiox.com
 * @date: 2023/11/1 14:41
 * @description: 自定义链接节点渲染器
 **/
public class LinkNodeRenderer implements NodeRenderer {

    private final HtmlWriter html;

    /**
     * 网站域名（上线后需要改成自己的域名）
     */
    private final static String DOMAIN = "www.aiox.com";


    public LinkNodeRenderer(HtmlNodeRendererContext context) {
        this.html = context.getWriter();
    }

    @Override
    public Set<Class<? extends Node>> getNodeTypes() {
        // 指定想要自定义渲染的节点，这里指定为超链接 Link
        return Sets.newHashSet(Link.class);
    }

    @Override
    public void render(Node node) {
        if (node instanceof Link) {
            Link link = (Link) node;

            // 链接的内容
            String linkDescription = null;
            if (Objects.nonNull(link.getFirstChild()) && link.getFirstChild() instanceof Text) {
                Text text = (Text) link.getFirstChild();
                linkDescription = text.getLiteral();
            }

            // 链接 url
            String linkUrl = link.getDestination();
            // 链接标题
            String linkTitle = link.getTitle();

            // 拼接 HTML 结构
            StringBuilder sb = new StringBuilder("<a href=\"");
            sb.append(linkUrl);
            sb.append("\"");

            // 添加 title="链接标题" 属性
            if (StringUtils.isNotBlank(linkTitle)) {
                sb.append(String.format(" title=\"%s\"", linkTitle));
            }

            // 如果链接不是自己域名，则添加 rel="nofollow" 属性
            if (!linkUrl.contains(DOMAIN)) {
                sb.append(" ref=\"nofollow\"");
            }

            // 添加 target="_blank" 属性
            sb.append(" target=\"_blank\">");
            // 超链接展示内容
            sb.append(StringUtils.isNotBlank(linkDescription) ? linkDescription : linkUrl);
            sb.append("</a>");

            // 添加 svg 图标
            String svg = "<span><svg xmlns=\"http://www.w3.org/2000/svg\" class=\"inline ml-1\" style=\"color: #aaa;\" aria-hidden=\"true\" focusable=\"false\" x=\"0px\" y=\"0px\" viewBox=\"0 0 100 100\" width=\"15\" height=\"15\" class=\"icon outbound\"><path fill=\"currentColor\" d=\"M18.8,85.1h56l0,0c2.2,0,4-1.8,4-4v-32h-8v28h-48v-48h28v-8h-32l0,0c-2.2,0-4,1.8-4,4v56C14.8,83.3,16.6,85.1,18.8,85.1z\"></path> <polygon fill=\"currentColor\" points=\"45.7,48.7 51.3,54.3 77.2,28.5 77.2,37.2 85.2,37.2 85.2,14.9 62.8,14.9 62.8,22.9 71.5,22.9\"></polygon></svg> <span class=\"sr-only\"></span></span>";
            sb.append(svg);

            // 设置 HTML 内容
            html.raw(sb.toString());
        }
    }
}
