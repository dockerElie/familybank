import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from '@/App.vue'
import router from '@/router'
import PrimeVue from 'primevue/config'
import ToastService from 'primevue/toastservice'
import 'primevue/resources/primevue.min.css'
import 'primevue/resources/themes/aura-light-blue/theme.css'
import 'primeflex/primeflex.min.css'
import 'primeicons/primeicons.css'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)
createApp(App)
  .use(PrimeVue, { ripple: true })
  .use(pinia)
  .use(router)
  .use(ToastService)
  .mount('#app')
