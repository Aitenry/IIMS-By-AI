package com.toryu.iims.common.markdown.renderer;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.commonmark.ext.image.attributes.ImageAttributes;
import org.commonmark.node.Image;
import org.commonmark.node.Node;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlWriter;
import org.springframework.util.CollectionUtils;

import java.util.Objects;
import java.util.Set;

/**
 * @author: Aiox
 * @url: www.aiox.com
 * @date: 2023/11/1 14:41
 * @description: 自定义图片节点渲染器
 **/
public class ImageNodeRenderer implements NodeRenderer {

    private final HtmlWriter html;

    /**
     * 图片宽
     */
    private final static String KEY_WIDTH = "width";
    /**
     * 图片高
     */
    private final static String KEY_HEIGHT = "height";


    public ImageNodeRenderer(HtmlNodeRendererContext context) {
        this.html = context.getWriter();
    }

    @Override
    public Set<Class<? extends Node>> getNodeTypes() {
        // 指定想要自定义渲染的节点，这里指定为图片 Image
        // return Sets.newHashSet(Image.class, Link.class);
        return Sets.newHashSet(Image.class);
    }

    @Override
    public void render(Node node) {
        if (node instanceof Image) {
            // We only handle one type as per getNodeTypes, so we can just cast it here.
            Image img = (Image) node;
            html.line();
            // 图片链接
            String imgUrl = img.getDestination();
            // 图片描述
            String imgTitle = img.getTitle();

            // 拼接 HTML 结构
            StringBuilder sb = new StringBuilder("<img src=\"");
            sb.append(imgUrl);
            sb.append("\"");

            // 添加 title="图片标题" alt="图片标题" 属性
            if (StringUtils.isNotBlank(imgTitle)) {
                sb.append(String.format(" title=\"%s\" alt=\"%s\"", imgTitle, imgTitle));
            }

            // 图片宽高
            Node lastChild = node.getLastChild();
            if (Objects.nonNull(lastChild) && lastChild instanceof ImageAttributes) {
                ImageAttributes imageAttributes = (ImageAttributes) lastChild;
                if (Objects.nonNull(imageAttributes) && !CollectionUtils.isEmpty(imageAttributes.getAttributes())) {
                    String width = imageAttributes.getAttributes().get(KEY_WIDTH);
                    String height = imageAttributes.getAttributes().get(KEY_HEIGHT);
                    sb.append(StringUtils.isBlank(width) ? "" : (" " + KEY_WIDTH + "=" + width + "\""));
                    sb.append(StringUtils.isBlank(height) ? "" : (" " + KEY_HEIGHT + "=" + height + "\""));
                }
            }
            sb.append(">");

            if (StringUtils.isNotBlank(imgTitle)) {
                // 图文下方文字
                sb.append(String.format("<span class=\"image-caption\">%s</span>", imgTitle));
            }

            // 设置 HTML 内容
            html.raw(sb.toString());
            html.line();
        }
        // else if (node instanceof Link) {
        //     Link link = (Link) node;
        //     System.out.println(link);
        //     html.line();
        //     html.tag("a");
        //     String title = link.getTitle();
        //
        //     if (StringUtils.isNotBlank(title)) {
        //
        //     }
        //
        //     String url = link.getDestination();
        //     html.text(url);
        //     html.tag("/a");
        //     html.line();
        // }
    }
}
