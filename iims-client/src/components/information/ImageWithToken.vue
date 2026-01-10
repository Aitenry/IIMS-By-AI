<template>
  <el-image
      v-bind="$attrs"
      ref="imageRef"
      :src="displaySrc"
      :preview-src-list="previewListWithToken"
      :initial-index="initialIndex"
      :fit="fit"
      :preview-teleported="true"
      :show-progress="true"
  >
    <!-- 透传插槽 -->
    <template v-for="(_, slotName) in $slots" #[slotName]="slotData">
      <slot :name="slotName" v-bind="slotData || {}" />
    </template>
  </el-image>
</template>

<script setup lang="ts">
import { ref, onUnmounted, watch } from 'vue';
import { ElImage } from 'element-plus';
import { getStorage } from '@/utils/auth.ts'; // 确保路径正确

interface Props {
  src?: string;
  previewSrcList?: string[]; // 接收原始的预览列表
  size?: number | string;
  initialIndex?: number;
  fit?: 'fill' | 'contain' | 'cover' | 'none' | 'scale-down';
}

const props = withDefaults(defineProps<Props>(), {
  size: 100,
  initialIndex: 0,
  fit: 'cover',
  previewSrcList: () => [],
});

const imageRef = ref();
const displaySrc = ref('');
const objectUrls = ref<string[]>([]);
const previewListWithToken = ref<string[]>([]); // 存储处理后的预览列表

// 清理已创建的 object URLs
const revokeObjectUrls = () => {
  objectUrls.value.forEach(url => URL.revokeObjectURL(url));
  objectUrls.value = [];
};

// 加载带token的图片并返回URL
const loadImageWithToken = async (src: string) => {
  try {
    const token = getStorage('token');
    if (!token) {
      console.warn('No token found, returning original src.');
      return src;
    }

    let urlToFetch: string;
    try {
      new URL(src);
      urlToFetch = src;
    } catch {
      console.error(`Invalid URL provided: ${src}`);
      return src;
    }

    const response = await fetch(urlToFetch, {
      headers: {
        'token': token
      }
    });

    if (!response.ok) {
      console.error(`Failed to fetch image: ${response.status} ${response.statusText}`);
      return '';
    }

    const blob = await response.blob();
    const objectUrl = URL.createObjectURL(blob);

    objectUrls.value.push(objectUrl);
    return objectUrl;
  } catch (error) {
    console.error('Failed to load image with token:', error);
    return src;
  }
};

// 批量处理预览列表
const loadPreviewListWithToken = async (list: string[]) => {
  const token = getStorage('token');
  if (!token) {
    console.warn('No token found, returning original preview list.');
    return list;
  }

  // 清理旧的预览列表 Object URLs
  const oldPreviewUrls = previewListWithToken.value.filter(url => !list.includes(url)); // 这个过滤逻辑不准确，但为了演示，先保留旧的清理逻辑
  oldPreviewUrls.forEach(url => {
    if (objectUrls.value.includes(url)) {
      URL.revokeObjectURL(url);
      objectUrls.value = objectUrls.value.filter(u => u !== url);
    }
  });

  const newPreviewList: string[] = [];
  for (const item of list) {
    try {
      const processedUrl = await loadImageWithToken(item);
      if (processedUrl) {
        newPreviewList.push(processedUrl);
      }
    } catch (e) {
      console.error(`Error processing preview item ${item}:`, e);
      // Optionally push the original URL if processing fails
      // newPreviewList.push(item);
    }
  }

  // 更新 previewListWithToken 并同步到 objectUrls 管理列表
  previewListWithToken.value = newPreviewList;
  // 注意：这里 loadImageWithToken 已经将 URL 添加到了 objectUrls.value
  // 如果需要精确管理哪些 URL 属于 previewList，可能需要额外的数据结构
};

// 当src变化时重新加载图片
watch(
    () => props.src,
    async (newSrc) => {
      if (displaySrc.value && props.src !== newSrc) {
        URL.revokeObjectURL(displaySrc.value);
        objectUrls.value = objectUrls.value.filter(url => url !== displaySrc.value);
      }

      if (newSrc) {
        displaySrc.value = await loadImageWithToken(newSrc);
      } else {
        displaySrc.value = '';
      }
    },
    { immediate: true }
);

// 当 previewSrcList 变化时重新处理预览列表
watch(
    () => props.previewSrcList,
    async (newList) => {
      if (newList && Array.isArray(newList)) {
        await loadPreviewListWithToken(newList);
      } else {
        // 如果新的列表无效，清空处理后的列表
        previewListWithToken.value = [];
      }
    },
    { immediate: true }
);

onUnmounted(() => {
  revokeObjectUrls();
});

// Expose internal properties/methods if needed by parent components
defineExpose({ imageRef });
</script>