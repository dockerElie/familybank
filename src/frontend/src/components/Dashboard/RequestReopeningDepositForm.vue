<template>
  <div class="formgroup-inline">
    <div class="field">
      <Textarea
        v-model.lazy="model.reason"
        variant="filled"
        rows="5"
        cols="30"
        placeholder="Reason for reopening"
      />
    </div>
    <action-button text="Reopen" severity="warning" size="p-button" outlined="false" @click="save">
    </action-button>
    <action-button
      class="ml-2"
      text="Cancel"
      severity="secondary"
      size="p-button"
      outlined="false"
      @click="cancel"
    >
    </action-button>
  </div>
  <div class="mt-2">
    <span v-if="errors.reason !== null" class="text-red-700 h-8 mt-2 text-xs">{{
      errors.reason
    }}</span>
  </div>
</template>

<script setup lang="js">
import Textarea from 'primevue/textarea'
import ActionButton from '@/components/Shared/ActionButton.vue'
import { ref } from 'vue'
const emit = defineEmits(['reopenDeposit'])
const model = defineModel({
  type: Object
})
const errors = ref({
  reason: null
})
const validateReason = () => {
  let reason = model.value.reason
  if (reason === null || reason === '') {
    errors.value.reason = 'Please provide a reason.'
    return false
  }
  errors.value.reason = null
  return true
}
const save = () => {
  if (validateReason()) {
    emit('reopenDeposit', model)
  }
}

const cancel = () => {
  errors.value.reason = null
  model.value.reason = null
}
</script>
