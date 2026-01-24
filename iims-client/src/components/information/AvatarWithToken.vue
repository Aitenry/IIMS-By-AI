<template>
  <el-avatar
      v-bind="$attrs"
      ref="avatarRef"
      :size="size"
      :shape="shape"
      :icon="icon"
      :src="displaySrc"
      :alt="alt"
      :fit="fit"
      :text-style="textStyle"
  >
    <!-- 透传插槽 -->
    <template v-for="(_, slotName) in $slots" #[slotName]="slotData">
      <slot :name="slotName" v-bind="slotData || {}" />
    </template>
  </el-avatar>
</template>

<script setup lang="ts">
import { ref, onUnmounted, watch } from 'vue';
import { ElAvatar } from 'element-plus';
import { getStorage } from '@/utils/auth.ts';

interface Props {
  src?: string;
  size?: 'large' | 'default' | 'small';
  shape?: 'circle' | 'square';
  icon?: any;
  alt?: string;
  fit?: 'fill' | 'contain' | 'cover' | 'none' | 'scale-down';
  textStyle?: Record<string, any>;
}

const props = withDefaults(defineProps<Props>(), {
  size: 'default',
  shape: 'circle',
  fit: 'cover',
});

const avatarRef = ref();
const displaySrc = ref('');
const objectUrls = ref<string[]>([]);

// 清理已创建的 object URLs
const revokeObjectUrls = () => {
  objectUrls.value.forEach(url => URL.revokeObjectURL(url));
  objectUrls.value = [];
};

// 加载带token的头像图片并返回URL
const loadAvatarWithToken = async (src: string) => {
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
      console.error(`Failed to fetch avatar: ${response.status} ${response.statusText}`);
      return '';
    }

    const blob = await response.blob();
    const objectUrl = URL.createObjectURL(blob);

    objectUrls.value.push(objectUrl);
    return objectUrl;
  } catch (error) {
    console.error('Failed to load avatar with token:', error);
    return src;
  }
};

// 当src变化时重新加载头像
watch(
    () => props.src,
    async (newSrc) => {
      if (displaySrc.value && props.src !== newSrc) {
        URL.revokeObjectURL(displaySrc.value);
        objectUrls.value = objectUrls.value.filter(url => url !== displaySrc.value);
      }

      if (newSrc) {
        displaySrc.value = await loadAvatarWithToken(newSrc);
      } else {
        displaySrc.value = '';
      }
    },
    { immediate: true }
);

onUnmounted(() => {
  revokeObjectUrls();
});

defineExpose({ avatarRef });
</script>