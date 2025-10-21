<template>
  <span v-if="checkPermission">
    <slot :permission="permission" />
  </span>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  props: {
    permission: {
      type: String,
      required: true
    }
  },
  computed: {
    ...mapGetters([
      'permissions'
    ]),
    checkPermission() {
      return this.permissions ? this.permissions.includes(this.permission) : false
    }
  }
}
</script>

<style scoped>
  span {
    display: inline-block;
    margin-left: 12px;
  }
</style>
