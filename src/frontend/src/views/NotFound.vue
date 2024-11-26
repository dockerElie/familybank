<template>
  <div class="flex flex-wrap justify-content-center">Page not found</div>
  <div class="flex flex-wrap justify-content-center mt-2">
    <action-button
      text="Go back"
      severity="info"
      size="p-button-lg"
      outlined="false"
      @click="goBack"
    ></action-button>
  </div>
</template>

<script setup lang="js">
import { useRouter } from 'vue-router'
import ActionButton from '@/components/Shared/ActionButton.vue'

const router = useRouter()

const goBack = () => {
  router.back()
}
</script>
