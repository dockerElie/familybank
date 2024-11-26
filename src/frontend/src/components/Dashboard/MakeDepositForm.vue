<template>
  <div class="formgroup-inline">
    <div class="field">
      <InputNumber id="deposit" v-model.lazy="model.amount" :min="0" placeholder="Deposit Amount" />
    </div>
    <action-button text="Save" severity="success" size="p-button" outlined="false" @click="save">
    </action-button>
  </div>
</template>

<script setup lang="js">
import ActionButton from '@/components/Shared/ActionButton.vue'
import InputNumber from 'primevue/inputnumber'

const emit = defineEmits(['saveDeposit'])

const model = defineModel({
  type: Object
})

const save = () => {
  emit('saveDeposit', model)
}
</script>
