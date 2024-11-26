export function useDepositAction() {
  // Define the enum
  const action = Object.freeze({
    MAKE_DEPOSIT: 'make_deposit',
    VALIDATE_DEPOSIT: 'validate_deposit',
    REQUEST_REOPENING_DEPOSIT: 'request_reopening_deposit',
    CANCEL_DEPOSIT: 'cancel_deposit'
  })

  // Return the enum so it can be used in components
  return {
    action
  }
}
