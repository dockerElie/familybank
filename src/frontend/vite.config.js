import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  build: {
    outDir: '../main/resources/static', // Spring Boot's static folder
    emptyOutDir: true, // Clear old files before building
  },
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  test: {
    globals: true,
    setupFiles: ['./tests/setup.js']
  },
  devServer: {
    proxy: {
      '/nesous/api': {
        target: 'http://localhost:9090',
        ws: true,
        changeOrigin: true,
      }
    }
  }
})
