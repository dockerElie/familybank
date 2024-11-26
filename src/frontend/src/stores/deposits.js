import { defineStore } from 'pinia'
import { ref } from 'vue'
import getDeposits from '@/api/getDeposits'
export const useDepositsStore = defineStore('deposits', () => {
  const deposits = ref([])

  const GET_DEPOSITS = async () => {
    let result = await getDeposits()
    deposits.value = result
  }

  return {
    deposits,
    GET_DEPOSITS
  }
})
