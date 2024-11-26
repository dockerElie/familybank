export function useDepositStatus() {
  // Define the enum
  const status = Object.freeze({
    ACTIVATED: 'Activated',
    CANCELLED: 'Cancelled',
    CLOSED: 'Closed',
    DONE: 'Done',
    DENIED: 'Denied',
    REQUESTED: 'Requested Reopening',
    REACTIVATED: 'Reactivated',
    VALIDATED: 'Validated'
  })

  // Return the enum so it can be used in components
  return {
    status
  }
}
