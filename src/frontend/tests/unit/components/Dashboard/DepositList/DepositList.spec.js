import { render, screen } from '@testing-library/vue'
import DepositList from '@/components/Dashboard/DepositList/DepositList.vue'
import PrimeVue from 'primevue/config'
import { createTestingPinia } from '@pinia/testing'
import { useDepositsStore } from '@/stores/deposits'
import userEvent from '@testing-library/user-event'
import { expect } from 'vitest'

vi.mock('primevue/usetoast', () => ({
  useToast: () => ({
    add: vi.fn() // Mock the 'add' function of useToast
  })
}))
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

describe('List of deposit', () => {
  const renderDepositList = () => {
    const pinia = createTestingPinia()
    const store = useDepositsStore()
    store.deposits = [
      {
        id: '01-DEP-2024',
        name: 'Raised fund for malaria',
        date: '10/08/2024',
        amount: 0,
        expirationDate: '17/08/2024',
        description: 'raised fund for malaria',
        status: 'Activated'
      },
      {
        id: '02-DEP-2024',
        name: 'Raised fund for malaria',
        date: '10/08/2024',
        amount: 0,
        expirationDate: '17/08/2024',
        description: 'raised fund for malaria',
        status: 'Requested Reopening'
      }
    ]
    render(DepositList, {
      global: {
        plugins: [PrimeVue, pinia]
      }
    })

    return { store }
  }

  it('fetches active deposit', () => {
    const { store } = renderDepositList()
    expect(store.GET_DEPOSITS).toHaveBeenCalled()
  })

  it('displays deposits', async () => {
    renderDepositList()

    //@ts-expect-error Getter is readonly
    const result = await screen.findAllByText(/status/i)
    expect(result.length).toBeGreaterThanOrEqual(1)
  })

  it('displays open button for deposits under status Activated', async () => {
    renderDepositList()
    const buttons = await screen.findAllByRole('button', { name: /open/i })
    expect(buttons.length).toBe(1)
  })

  describe('user clicks on the button Open', () => {
    it('shows deposit action events', async () => {
      renderDepositList()
      const buttons = await screen.findAllByRole('button', { name: /open/i })
      const openButton = buttons[0]
      await userEvent.click(openButton)
      const makeDepositButton = screen.getByRole('button', { name: /make deposit/i })
      expect(makeDepositButton).toBeInTheDocument()
    })

    describe('user makes a deposit', () => {
      it('opens deposit modal form', async () => {
        renderDepositList()
        const buttons = await screen.findAllByRole('button', { name: /open/i })
        const openButton = buttons[0]
        await userEvent.click(openButton)
        const makeDepositButton = screen.getByRole('button', { name: /make deposit/i })
        await userEvent.click(makeDepositButton)
        const makeDepositTitle = screen.getByText(/make deposit/i)
        const depositAmountInput = screen.getByPlaceholderText(/deposit amount/i)
        const saveButton = screen.getByRole('button', { name: /save/i })
        expect(makeDepositTitle).toBeInTheDocument()
        expect(depositAmountInput).toBeInTheDocument()
        expect(saveButton).toBeInTheDocument()
      })
    })

    describe('user updates deposit amount', () => {
      it('renders the amount in the DOM', async () => {
        renderDepositList()
        const buttons = await screen.findAllByRole('button', { name: /open/i })
        const openButton = buttons[0]
        await userEvent.click(openButton)
        const makeDepositButton = screen.getByRole('button', { name: /make deposit/i })
        await userEvent.click(makeDepositButton)
        const depositAmountInput = screen.getByPlaceholderText(/deposit amount/i)
        const saveButton = screen.getByRole('button', { name: /save/i })

        await userEvent.type(depositAmountInput, '1200')
        await userEvent.click(saveButton)
        const amountElement = screen.getByTestId('amount-01-DEP-2024')

        expect(amountElement).toHaveTextContent('Amount: 1,200.00')
      })
    })

    describe('user validates deposit amount', () => {
      it('renders the status VALIDATED in the DOM', async () => {
        renderDepositList()
        const buttons = await screen.findAllByRole('button', { name: /open/i })
        const openButton = buttons[0]
        await userEvent.click(openButton)

        const makeDepositButton = screen.getByRole('button', { name: /make deposit/i })
        await userEvent.click(makeDepositButton)
        const depositAmountInput = screen.getByPlaceholderText(/deposit amount/i)
        const saveButton = screen.getByRole('button', { name: /save/i })

        await userEvent.type(depositAmountInput, '1200')
        await userEvent.click(saveButton)

        await userEvent.click(openButton)

        const validateDepositButton = screen.getByRole('button', { name: /validate/i })
        await userEvent.click(validateDepositButton)
        //const result = await screen.findAllByText(/status/i)
        const statusElement = screen.getByTestId('status-01-DEP-2024')
        expect(statusElement).toHaveTextContent('Validated')
        console.log('result ', statusElement)
      })
    })
  })
})
