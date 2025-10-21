<template>
    <el-scrollbar style="width: calc(100% - 10px); margin: 5px 0 0 5px;" max-height="calc(100vh - 65px)">
      <Indicator style="width: 100%;" />
      <Contribution style="width: 100%; margin-top: 3px;" />
    </el-scrollbar>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import Contribution from '@/views/home/components/Contribution.vue'; // 根据实际路径调整
import Indicator from '@/views/home/components/Indicator.vue'; // 根据实际路径调整

export default defineComponent({
  name: 'Index',
  components: {
    Contribution,
    Indicator
  },
  setup() {
    // 组件逻辑
  }
});
</script>