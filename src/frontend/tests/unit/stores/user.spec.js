import { useUserStore } from '@/stores/user'
import { createPinia, setActivePinia } from 'pinia'

describe('user store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('keeps track of if user is connected', () => {
    const user = localStorage.getItem('user')
    expect(user).toBeNull()
  })

  it('logs the user in', () => {
    const store = useUserStore()
    store.LOGIN_USER()
    const user = JSON.parse(localStorage.getItem('user'))
    expect(user).not.toBeNull()
  })
})
