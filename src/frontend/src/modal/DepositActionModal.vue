<template>
  <div>
    <Dialog v-model:visible="internalShow" modal :header="props.title">
      <div v-if="$slots.makeDeposit">
        <slot name="makeDeposit"></slot>
      </div>
      <div v-if="$slots.requestReopeningDeposit">
        <slot name="requestReopeningDeposit"></slot>
      </div>

      <div v-if="$slots.default">
        <slot></slot>
      </div>
    </Dialog>
  </div>
</template>

<script setup lang="js">
import Dialog from 'primevue/dialog'
import { computed } from 'vue'
const props = defineProps({
  show: { type: Boolean, default: false },
  title: { type: String, default: '' }
})
const emit = defineEmits(['update:show'])
const internalShow = computed({
  get: () => props.show, // Return the current prop value
  set: (value) => emit('update:show', value) // Emit event to parent when changed
})
</script>
