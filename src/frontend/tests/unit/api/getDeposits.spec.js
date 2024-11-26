import axios from 'axios'
import getDeposits from '@/api/getDeposits'
import { beforeEach, describe } from 'vitest'

vi.mock('axios')

describe('getDeposits', () => {
  beforeEach(() => {
    axios.get.mockResolvedValue({ data: [] })
  })
  it('fetches active deposit', async () => {
    await getDeposits()
    expect(axios.get).toHaveBeenCalledWith('http://localhost:3000/deposits')
  })
})
