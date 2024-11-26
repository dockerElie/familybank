import { useDepositsStore } from '@/stores/deposits'
import { createPinia, setActivePinia } from 'pinia'
import axios from 'axios'

vi.mock('axios')

describe('deposits store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('should fectch active deposit', async () => {
    axios.get.mockResolvedValue({
      data: [
        {
          id: '01-DEP-2024',
          name: 'Raised fund for malaria',
          date: '10/08/2024',
          amount: 'N/A',
          expirationDate: '17/08/2024',
          description: 'raised fund for malaria'
        }
      ]
    })
    const store = useDepositsStore()
    await store.GET_DEPOSITS()

    expect(store.deposits.length).toBe(1)
  })
})
