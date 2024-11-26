export function useDateFormatter() {
  let formatDate = (date, locale = 'en-GB') => {
    if (!date) {
      return ''
    }

    let dateObj = typeof date === 'string' ? new Date(date) : date
    return dateObj.toLocaleDateString(locale)
  }
  return { formatDate }
}
