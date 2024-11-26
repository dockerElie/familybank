<template>
  <div>
    <Button
      :severity="buttonSeverity"
      class="border-round"
      :class="buttonSize"
      :outlined="buttonOutlined"
      >{{ text }}</Button
    >
  </div>
</template>

<script setup lang="js">
import { computed, toRefs } from 'vue'
import Button from 'primevue/button'
const props = defineProps({
  severity: {
    type: String,
    required: false,
    default: 'secondary',
    validator(value) {
      return ['secondary', 'success', 'info', 'warning', 'help', 'danger', 'contrast'].includes(
        value
      )
    }
  },
  size: {
    type: String,
    required: false,
    default: '',
    validator(value) {
      return ['p-button', 'p-button-sm', 'p-button-lg'].includes(value)
    }
  },
  text: {
    type: String,
    required: true,
    default: ''
  },
  outlined: {
    type: String,
    required: true,
    default: 'false'
  }
})
const { severity } = toRefs(props)
const { size } = toRefs(props)
const { outlined } = toRefs(props)
const buttonSize = computed(() => {
  return { [size.value]: true }
})
const buttonSeverity = computed(() => {
  return severity.value
})

const buttonOutlined = computed(() => {
  return outlined.value === 'false' ? false : true
})
</script>
