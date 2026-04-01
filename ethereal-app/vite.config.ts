import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import Components from 'unplugin-vue-components/vite'
import { VantResolver } from '@vant/auto-import-resolver'
import { resolve } from 'path'

export default defineConfig({
  css: {
    preprocessorOptions: {
      scss: {
        // 与 Dart Sass 2.0 对齐；抑制 Vite 调用旧 JS API 等噪声（需 sass ≥ 1.79）
        silenceDeprecations: ['legacy-js-api', 'import', 'global-builtin', 'color-functions'],
      },
    },
  },
  plugins: [
    vue(),
    Components({
      resolvers: [VantResolver()]
    })
  ],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/socket.io': {
        target: 'http://localhost:9093',
        ws: true
      }
    }
  }
})
