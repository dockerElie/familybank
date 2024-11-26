import {createRouter, createWebHistory} from 'vue-router'
import {generatePKCETokens, getAccessToken, getAuthorizationCode, oidcLogout} from '@/composables/useAuthenticate.js'
import {useUserStore} from "@/stores/user.js";

const routes = [
  { path: '/', component: () => import('@/views/HomeView.vue'), name: 'home' },
  {
    path: '/login',
    name: 'login',
    beforeEnter() {
      const userStore = useUserStore();
      if (userStore.user.identifier === '') {

        generatePKCETokens().then((res) => {
          userStore.SET_PKCE_TOKENS(res.code_challenge, res.code_verifier)
          let url = getAuthorizationCode(userStore.pkceChallenge)
          window.location.href = `${url}`
        })
      } else {
        return { path:  `/dashboard/users/${userStore.user.identifier}` }
      }
    }
  },
  {
    path: '/logout',
    name: 'logout',
    beforeEnter() {
      const userStore = useUserStore()
      const idToken = userStore.accessTokenInfo.idToken
      userStore.LOGOUT_USER()
      let url = oidcLogout(idToken)
      window.location.href = `${url}`
      return true;
    },
    component: () => import('@/views/HomeView.vue')
  },
  {
    path: '/callback',
    name: 'callback',
    async beforeEnter(to) {
      const userStore = useUserStore()
      if (!to.query.code?.toString()) {
        return {name: 'home'}
      }

      try {
        // Fetch the access token using the authorization code and PKCE verifier
        let code = to.query.code?.toString() || ''
        const tokenResponse = await getAccessToken(code, userStore.pkceVerifier);
        await handleUserAuthentication(tokenResponse, userStore);
      } catch (error) {
        if (error.response && error.response.status === 401) {
          try {
            // Attempt to refresh the token if the original request is unauthorized
            const refreshedTokenResponse = await userStore.REFRESH_TOKEN();
            await handleUserAuthentication(refreshedTokenResponse, userStore);
          } catch (refreshError) {
            return { name : 'home' }
          }
        }
      }
    }
  },
  {
    path: '/dashboard/users/:id',
    component: () => import('@/views/DashBoard/DashBoardView.vue'),
    children: [
      {
        path: '',
        name: 'depositList',
        beforeEnter() {
          const userStore = useUserStore()
          if (userStore.user.identifier === '') {
            return { name: 'login' }
          }
        },
        component: () => import('@/views/DashBoard/DepositList/DepositListView.vue'),
      }
    ]
  },
  { path: '/:pathMatch(.*)*', name: 'not-found', component: () => import('@/views/NotFound.vue') }
]

const handleUserAuthentication = async (tokenResponse, userStore) => {

  const { access_token, refresh_token, id_token, expires_in } = tokenResponse.data;
  try {
    const profile = await userStore.SET_USER_PROFILE(access_token, refresh_token, id_token, expires_in);
    userStore.user = profile.data;
    userStore.CLEAR_PKCE_TOKENS();
    if (userStore.accessTokenInfo.accessToken && profile.data.identifier) {
      window.location.href = `/dashboard/users/${profile.data.identifier}`
    }
  } catch (profileError) {
    throw profileError
  }
}

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
