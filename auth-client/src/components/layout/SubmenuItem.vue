<template>
  <div
    class="submenu-item"
    :style="{
      animationDelay: `${index * 50}ms`,
      opacity: visible ? 1 : 0,
      transform: visible ? 'translateY(0)' : 'translateY(-10px)'
    }"
  >
    <slot></slot>
  </div>
</template>

<script setup lang="ts">
defineProps({
  index: {
    type: Number,
    required: true
  },
  visible: {
    type: Boolean,
    default: false
  }
})
</script>

<style scoped>
.submenu-item {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  animation: slideDown 0.3s ease-out forwards;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
