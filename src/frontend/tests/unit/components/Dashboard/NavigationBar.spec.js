import { render, screen } from '@testing-library/vue'
import NavigationBar from '@/components/Dashboard/NavigationBar.vue'
import PrimeVue from 'primevue/config' // Import PrimeVue
import Menubar from 'primevue/menubar' // Import specific PrimeVue components
import userEvent from '@testing-library/user-event'
import { useRouter } from 'vue-router'
import { createTestingPinia } from '@pinia/testing'
import { useUserStore } from '@/stores/user'
vi.mock('vue-router')

/*Mocking window.matchMedia. Fix issue Match media is not a function.
This is a common issue when testing components that rely on responsive behavior
as matchmedia is a browser-specific API that isn't available in most test environments*/
beforeAll(() => {
  window.matchMedia = vi.fn().mockImplementation((query) => {
    return {
      matches: false, // Change this to true if you want to simulate a smaller screen
      media: query,
      onchange: null,
      addListener: vi.fn(), // Support for deprecated methods
      removeListener: vi.fn(),
      addEventListener: vi.fn(),
      removeEventListener: vi.fn(),
      dispatchEvent: vi.fn()
    }
  })
})

describe('Dashboard page', () => {
  useRouter.mockReturnValue({
    push: vi.fn()
  })

  beforeEach(() => {
    useRouter().push.mockReset()
  })
  const pinia = createTestingPinia()
  const storeUser = useUserStore()

  it('should render the menu bar with the "Deposits" label', async () => {
    localStorage.setItem(
      'user',
      JSON.stringify({ userId: '1234', userName: 'John Doe', userStatus: 'Activated' })
    )
    storeUser.isLoggedIn = true
    render(NavigationBar, {
      global: {
        plugins: [PrimeVue, pinia],
        components: { Menubar }
      }
    })
    expect(screen.getByText(/deposits/i)).toBeInTheDocument()
  })

  describe('when clicking on the Deposits menu item', () => {
    it('should display list of deposits', async () => {
      storeUser.isLoggedIn = true
      const router = useRouter()
      render(NavigationBar, {
        global: {
          plugins: [PrimeVue, pinia],
          components: { Menubar }
        }
      })
      const depositLink = screen.getByText(/deposits/i)
      await userEvent.click(depositLink)
      expect(router.push).toHaveBeenCalledWith({ name: 'depositList', params: { id: '' } })
    })
  })

  describe('when clicking on user menu item', () => {
    it('should display user profile', async () => {
      storeUser.isLoggedIn = true
      render(NavigationBar, {
        global: {
          plugins: [PrimeVue, pinia],
          components: { Menubar }
        }
      })
      const button = screen.getByRole('button', { name: '' })
      await userEvent.click(button)

      const profileMenuItems = screen.queryAllByRole('menuitem')
      expect(profileMenuItems).toHaveLength(5)
    })
  })

  describe('when clicking on user menu logout item', () => {
    it('should logout the user', async () => {
      const router = useRouter()
      render(NavigationBar, {
        global: {
          plugins: [PrimeVue, pinia],
          components: { Menubar }
        }
      })
      const button = screen.getByRole('button', { name: '' })
      await userEvent.click(button)

      const logoutLink = screen.getByText(/logout/i)
      await userEvent.click(logoutLink)

      expect(router.push).toHaveBeenCalledWith({ name: 'home' })
    })
  })
})
