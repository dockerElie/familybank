<template>
  <div v-if="isDisplayDepositCoverPeriod" class="flex flex-column md:flex-row">
    <span class="ml-2">Cover period Calendar</span>
  </div>
  <div class="mt-1">
    <Calendar
      v-if="isDisplayDepositCoverPeriod"
      v-model="depositCoverPeriodDate"
      input-id="deposit-cover-period"
      show-button-bar
      date-format="dd/mm/yy"
      class="ml-2"
      touch-u-i
      @date-select="showCalendar = false"
    />
  </div>
  <div
    class="flex flex-column md:flex-row p-3 gap-4 border-round shawdow-2"
    :class="surfaceBorderClass"
  >
    <!-- Left side (Deposit Details)-->

    <div class="flex flex-column h-full justify-content-between">
      <span :data-testid="`status-${deposit.id}`" class="text-sm"
        ><strong>Status: </strong
        ><Tag :value="deposit.status" :severity="getSeverity(deposit)"></Tag
      ></span>

      <span :data-testid="`id-${deposit.id}`" class="text-sm mt-5"
        ><strong>ID: </strong>{{ deposit.id }}</span
      >
      <span :data-testid="`name-${deposit.id}`" class="text-sm mt-1"
        ><strong>Name: </strong>{{ deposit.name }}</span
      >
      <span :data-testid="`date-${deposit.id}`" class="text-sm mt-1"
        ><strong>Date: </strong>{{ deposit.date }}</span
      >
      <span
        v-if="isValidNumber(deposit.amount)"
        :data-testid="`amount-${deposit.id}`"
        class="text-sm mt-1"
        ><strong>Amount: </strong>{{ formatAmount(deposit.amount) }}</span
      >
      <span v-else :data-testid="`invalidamount-${deposit.id}`" class="text-sm mt-1"
        ><strong>Amount: </strong>N/A</span
      >
      <span :data-testid="`expirationdate-${deposit.id}`" class="text-sm mt-1"
        ><strong>Expiration Date: </strong>{{ deposit.expirationDate }}</span
      >

      <span
        v-if="deposit.depositCoverPeriod != null"
        :data-testid="`deposit-cover-period-${deposit.id}`"
        class="text-sm mt-1"
        ><strong>Deposit Cover period Date: </strong>{{ formattedCoverDate }}</span
      >

      <span :data-testid="`description-${deposit.id}`" class="text-sm mt-1"
        ><strong>Description: </strong>{{ deposit.description }}</span
      >
      <span v-if="displayReason" :data-testid="`reason-${deposit.reason}`" class="text-sm mt-1"
        ><strong>Reason: </strong>{{ deposit.reason }}</span
      >
    </div>
  </div>

  <div v-if="isDisplayButton" class="mt-4 flex flex-column md:align-items-end">
    <div class="flex flex-row-reverse md:flex-row" :class="applyMarginBottomToButton">
      <action-button
        text="OPEN"
        size="p-button-lg"
        severity="info"
        outlined="true"
        class="white-space-nowrap"
        @click="toggle"
      ></action-button>

      <OverlayPanel ref="displayActionButtons">
        <div class="flex flex-column gap-3">
          <div class="text-base font-medium underline sm:text-overflow-clip">Manage Deposit</div>
          <div
            v-if="
              shouldDisplayButtonsForStatusActivated ||
              shouldDisplayButtonsForStatusDone ||
              shouldDisplayButtonsForStatusCancelled
            "
            class="flex flex-row justify-content-center"
          >
            <action-button
              text="MAKE DEPOSIT"
              size="p-button-sm"
              severity="info"
              outlined="false"
              @click="openModal('depositEvent', 'make_deposit')"
            ></action-button>
          </div>

          <div
            v-if="shouldDisplayButtonsForStatusDone"
            class="flex flex-row justify-content-center"
          >
            <action-button
              text="VALIDATE DEPOSIT"
              size="p-button-sm"
              severity="success"
              outlined="false"
              @click="openModal('depositEvent', 'validate_deposit')"
            >
            </action-button>
          </div>

          <div
            v-if="shouldDisplayButtonsForStatusDone || shouldDisplayButtonsForStatusValidated"
            class="flex flex-row justify-content-center"
          >
            <action-button
              text="REQUEST DEPOSIT"
              size="p-button-sm"
              severity="warning"
              outlined="false"
              @click="openModal('depositEvent', 'request_reopening_deposit')"
            >
            </action-button>
          </div>

          <div
            v-if="shouldDisplayButtonsForStatusDone || shouldDisplayButtonsForStatusValidated"
            class="flex flex-row justify-content-center"
          >
            <action-button
              text="CANCELLED DEPOSIT"
              size="p-button-sm"
              severity="secondary"
              outlined="false"
              @click="openModal('depositEvent', 'cancel_deposit')"
            >
            </action-button>
          </div>
        </div>
      </OverlayPanel>
    </div>
  </div>
</template>

<script setup lang="js">
import { useDepositsStore } from '@/stores/deposits'
import ActionButton from '@/components/Shared/ActionButton.vue'
import Calendar from 'primevue/calendar'
import OverlayPanel from 'primevue/overlaypanel'
import { ref, computed, watch } from 'vue'
import { useDepositStatus } from '@/composables/useDepositStatus'
import Tag from 'primevue/tag'
import { useDateFormatter } from '@/composables/useDateFormatter'
const displayActionButtons = ref()
const depositCoverPeriodDate = ref()
const showCalendar = ref(false)
const emit = defineEmits(['depositEvent', 'changeDepositCoverPeriod'])
const storeDeposits = useDepositsStore()
const { status } = useDepositStatus()
const { formatDate } = useDateFormatter()
const props = defineProps({
  deposit: {
    type: Object,
    required: true
  },
  index: {
    type: Number,
    required: true
  }
})
const surfaceBorderClass = computed(() => {
  return props.index !== 0 ? 'border-top-1 surface-border' : ''
})

const applyMarginBottomToButton = computed(() => {
  return props.index === storeDeposits.deposits.length - 1 ? '' : 'mb-3'
})

const toggle = (event) => {
  displayActionButtons.value.toggle(event)
}

const openModal = (depositActionEvent, depositAction) => {
  displayActionButtons.value.toggle(false)
  emit(depositActionEvent, depositAction, props.deposit)
}

const isValidNumber = () => {
  return props.deposit.amount !== '' && !isNaN(props.deposit.amount)
}

const isDisplayButton = computed(() => {
  switch (props.deposit.status) {
    case status.ACTIVATED:
    case status.CLOSED:
    case status.CANCELLED:
    case status.DONE:
    case status.REACTIVATED:
    case status.VALIDATED:
      return true
    default:
      return false
  }
})

const isDisplayDepositCoverPeriod = computed(() => {
  return props.deposit.status === status.DONE
})

const shouldDisplayButtonsForStatusDone = computed(() => {
  return props.deposit.status === status.DONE
})

const shouldDisplayButtonsForStatusCancelled = computed(() => {
  return props.deposit.status === status.CANCELLED
})

const shouldDisplayButtonsForStatusActivated = computed(() => {
  return props.deposit.status === status.ACTIVATED
})

const shouldDisplayButtonsForStatusValidated = computed(() => {
  return props.deposit.status === status.VALIDATED
})

const getSeverity = (deposit) => {
  switch (deposit.status) {
    case status.ACTIVATED:
    case status.DONE:
    case status.VALIDATED:
    case status.REACTIVATED:
      return 'success'

    case status.REQUESTED:
      return 'warning'

    case status.DENIED:
      return 'danger'

    case status.CANCELLED:
      return 'secondary'

    default:
      return null
  }
}

const formatAmount = (value) => {
  if (value == null) {
    return ''
  }

  return new Intl.NumberFormat('en-US', {
    useGrouping: true,
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  }).format(value)
}

const formattedCoverDate = computed(() => formatDate(props.deposit.depositCoverPeriod))

const displayReason = computed(() => {
  return props.deposit.reason !== null && props.deposit.reason !== ''
})

watch(
  depositCoverPeriodDate,
  () => {
    emit('changeDepositCoverPeriod', depositCoverPeriodDate, props.deposit)
  },
  { immediate: true }
)
</script>
<style scoped></style>
