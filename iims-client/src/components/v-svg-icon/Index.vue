<template>
  <i :class="iconClass"></i>
</template>

<script setup lang="ts">
const props = withDefaults(defineProps<{
  iconClass: string
  className?: string
}>(), {
  className: ''
})
</script>

<style scoped>
i {
  margin-right: 3px;
}
</style>