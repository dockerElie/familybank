import { render, screen } from '@testing-library/vue'
import HomeLogin from '@/components/Login/HomeLogin.vue'
import userEvent from '@testing-library/user-event'
import { useRouter } from 'vue-router'
import { createTestingPinia } from '@pinia/testing'
vi.mock('vue-router')

describe('HomeLogin', () => {
  it('display Login button', () => {
    render(HomeLogin)
    const signButton = screen.getByRole('button', { name: /sign in/i })
    expect(signButton).toBeInTheDocument()
  })
})

describe('when user logs in', () => {
  const pinia = createTestingPinia()
  useRouter.mockReturnValue({
    push: vi.fn()
  })

  beforeEach(() => {
    useRouter().push.mockReset()
  })

  it('redirects user to his dashboard page', async () => {
    localStorage.setItem(
      'user',
      JSON.stringify({ userId: '1234', userName: 'John Doe', status: 'Activated' })
    )
    const router = useRouter()
    render(HomeLogin, {
      global: {
        plugins: [pinia]
      }
    })
    const signButton = screen.getByRole('button', { name: /sign in/i })
    await userEvent.click(signButton)
    expect(router.push).toHaveBeenCalledWith({ name: 'depositList', params: { id: '1234' } })
  })
})
