<template>
    <el-scrollbar style="width: calc(100% - 10px); margin: 5px 0 0 5px;" max-height="calc(100vh - 65px)">
      <Contribution style="width: 100%; margin-top: 3px;" />
    </el-scrollbar>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import Contribution from '@/views/home/components/Contribution.vue';

export default defineComponent({
  name: 'Index',
  components: {
    Contribution
  },
  setup() {
    // 组件逻辑
  }
});
</script>