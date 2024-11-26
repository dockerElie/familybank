import axios from 'axios'


const fetchWithAuth = async (endpoint, accessToken) => {
    const API_URL = import.meta.env.VITE_APP_FB_BACKEND_URL
    let url = `${API_URL}/${endpoint}`
    return await axios.get(url, {headers: {'Authorization': `Bearer ${accessToken}`}})
}

export default fetchWithAuth