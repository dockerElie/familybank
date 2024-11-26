<template>
  <div>
    <Card v-if="displayDeposits.length !== 0">
      <template #title>DEPOSIT LIST</template>
      <template #content
        ><DataView :value="displayDeposits">
          <template #list="slotProps">
            <div v-for="(item, index) in slotProps.items" :key="index">
              <deposit-item
                :deposit="item"
                :index="index"
                @deposit-event="showModal"
                @change-deposit-cover-period="setDepositCoveredPeriod"
              ></deposit-item>
            </div>
          </template>
        </DataView>
      </template>
    </Card>
    <Toast />
  </div>
  <div>
    <deposit-action-modal :show="displayModal" :title="modalTitle" @update:show="clear">
      <template v-if="modalTitle === 'MAKE DEPOSIT'" #makeDeposit>
        <make-deposit-form v-model="deposit" @save-deposit="makeDeposit"></make-deposit-form>
      </template>
      <template v-if="modalTitle === 'REQUEST REOPENING DEPOSIT'" #requestReopeningDeposit>
        <request-reopening-deposit-form
          v-model="deposit"
          @reopen-deposit="reopenDeposit"
        ></request-reopening-deposit-form>
      </template>
    </deposit-action-modal>
  </div>
</template>

<script setup lang="js">
import { onMounted, computed } from 'vue'
import { useDepositsStore } from '@/stores/deposits'
import Card from 'primevue/card'
import DataView from 'primevue/dataview'
import DepositItem from '@/components/Dashboard/Deposit/DepositItem.vue'
import DepositActionModal from '@/modal/DepositActionModal.vue'
import MakeDepositForm from '@/components/Dashboard/MakeDepositForm.vue'
import RequestReopeningDepositForm from '@/components/Dashboard/RequestReopeningDepositForm.vue'
import { useDepositAction } from '@/composables/useDepositAction'
import { useDepositStatus } from '@/composables/useDepositStatus'
import { ref } from 'vue'
import Toast from 'primevue/toast'
import { useToast } from 'primevue/usetoast'
const toast = useToast()
const storeDeposits = useDepositsStore()
const displayModal = ref(false)
const modalTitle = ref('')
const { action } = useDepositAction()
const deposit = ref({})
const { status } = useDepositStatus()

const displayDeposits = computed(() => {
  return storeDeposits.deposits
})

const showModal = (depositAction, item) => {
  deposit.value = item
  switch (depositAction) {
    case action.MAKE_DEPOSIT:
      displayModal.value = true
      modalTitle.value = 'MAKE DEPOSIT'
      break
    case action.VALIDATE_DEPOSIT:
      validate()
      break
    case action.REQUEST_REOPENING_DEPOSIT:
      displayModal.value = true
      modalTitle.value = 'REQUEST REOPENING DEPOSIT'
      break
    case action.CANCEL_DEPOSIT:
      cancelled()
      break
    default:
      modalTitle.value = ''
  }
}

const setDepositCoveredPeriod = (depositCoverPeriod, item) => {
  deposit.value = item
  deposit.value.depositCoverPeriod = depositCoverPeriod
}

const makeDeposit = () => {
  deposit.value.status = status.DONE
  displayModal.value = false
}
const reopenDeposit = () => {
  displayModal.value = false
  deposit.value.status = status.REQUESTED
}
const validate = () => {
  displayModal.value = false
  deposit.value.status = status.VALIDATED
  toast.add({ severity: 'success', detail: 'Deposit Validated', life: 3000 })
}

const cancelled = () => {
  displayModal.value = false
  deposit.value.status = status.CANCELLED
  deposit.value.amount = 0
  deposit.value.depositCoverPeriod = null
}

const clear = () => {
  displayModal.value = false
  if (isNaN(deposit.value.amount)) {
    deposit.value.amount = ''
  }
}

onMounted(storeDeposits.GET_DEPOSITS)
</script>
