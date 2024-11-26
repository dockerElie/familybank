import { defineStore } from 'pinia'
import { ref } from 'vue'
import fetchWithAuth from "@/api/fetchWithAuth.js";
import { getRefreshToken } from '@/composables/useAuthenticate.js'

// use of Pinia persisted state plugin to solve the issue of lost state during OAuth redirects
const USER_PROFILE_ENDPOINT = "nesous/api/user-profile.json"
const emptyUser = {identifier: '', lastName: '', firstName: '', userName: '', email: '', status: '', roles: []}


export const useUserStore = defineStore('user', () => {
  const user = ref({ ...emptyUser }); // Initialize with a shallow copy
  const accessTokenInfo = ref({accessToken: '', refreshToken: '', idToken: '', expiresIn: -1})
  const pkceChallenge = ref('')
  const pkceVerifier = ref('')

  const LOGOUT_USER = () => {
    accessTokenInfo.value.accessToken = ''
    accessTokenInfo.value.refreshToken  = ''
    accessTokenInfo.value.idToken  = ''
    accessTokenInfo.value.expiresIn = -1
    user.value = { ...emptyUser }
    CLEAR_PKCE_TOKENS()
  }

  const REFRESH_TOKEN = async () => {
    return await getRefreshToken(accessTokenInfo.value.refreshToken, accessTokenInfo.value.expiresIn)
  }

  const SET_USER_PROFILE = async (accessToken, refreshToken, idToken, expiresIn) => {
    accessTokenInfo.value.accessToken = accessToken
    accessTokenInfo.value.refreshToken = refreshToken
    accessTokenInfo.value.expiresIn = Date.now() + expiresIn * 1000
    accessTokenInfo.value.idToken = idToken
    return await fetchWithAuth(USER_PROFILE_ENDPOINT, accessToken)
  }

  const SET_PKCE_TOKENS = (challenge, verifier) => {
    pkceChallenge.value = challenge
    pkceVerifier.value = verifier
  }

  const CLEAR_PKCE_TOKENS = () => {
    pkceChallenge.value = ''
    pkceVerifier.value = ''
  }

  return {
    user,
    pkceChallenge,
    pkceVerifier,
    accessTokenInfo,
    REFRESH_TOKEN,
    SET_PKCE_TOKENS,
    CLEAR_PKCE_TOKENS,
    SET_USER_PROFILE,
    LOGOUT_USER
  }
}, { persist: true })
