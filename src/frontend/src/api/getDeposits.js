import axios from 'axios'

const getDeposits = async () => {
  const API_URL = import.meta.env.VITE_APP_API_URL
  const url = `${API_URL}/deposits`
  const response = await axios.get(url)
  return response.data
}

export default getDeposits
