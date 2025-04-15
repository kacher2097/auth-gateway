// vite.config.ts
import { defineConfig, loadEnv } from "file:///H:/app/java/auth-gateway/auth-client-react/node_modules/vite/dist/node/index.js";
import react from "file:///H:/app/java/auth-gateway/auth-client-react/node_modules/@vitejs/plugin-react/dist/index.mjs";
import path from "path";
var __vite_injected_original_dirname = "H:\\app\\java\\auth-gateway\\auth-client-react";
var vite_config_default = defineConfig(({ command, mode }) => {
  const env = loadEnv(mode, process.cwd(), "");
  return {
    plugins: [react()],
    resolve: {
      alias: {
        "@": path.resolve(__vite_injected_original_dirname, "./src")
      }
    },
    server: {
      port: 5174,
      open: true,
      cors: true,
      // Cấu hình HMR
      hmr: {
        overlay: true,
        // Thời gian chờ kết nối lại (ms)
        timeout: 1e3
      },
      // Tự động reload khi file thay đổi
      watch: {
        usePolling: true,
        interval: 1e3
      },
      proxy: {
        // Proxy API requests to backend during development
        "/api": {
          target: env.VITE_API_URL || "http://localhost:8080",
          changeOrigin: true,
          rewrite: (path2) => path2.replace(/^\/api/, "")
        }
      }
    },
    build: {
      // Generate source maps for production build
      sourcemap: command === "serve",
      // Minify output
      minify: "terser",
      // Configure rollup options
      rollupOptions: {
        output: {
          manualChunks: {
            "react-vendor": ["react", "react-dom", "react-router-dom"],
            "antd": ["antd", "@ant-design/icons"],
            "axios": ["axios"]
          }
        }
      },
      // Reduce chunk size warnings limit
      chunkSizeWarningLimit: 1e3
    },
    css: {
      // Enable CSS source maps in development
      devSourcemap: true,
      preprocessorOptions: {
        scss: {
          additionalData: `@import "@/styles/variables.scss";`
        }
      }
    },
    // Optimize dependencies pre-bundling
    optimizeDeps: {
      include: ["react", "react-dom", "react-router-dom", "antd", "@ant-design/icons", "axios"],
      // Tối ưu force update khi dependencies thay đổi
      force: true
    }
  };
});
export {
  vite_config_default as default
};
//# sourceMappingURL=data:application/json;base64,ewogICJ2ZXJzaW9uIjogMywKICAic291cmNlcyI6IFsidml0ZS5jb25maWcudHMiXSwKICAic291cmNlc0NvbnRlbnQiOiBbImNvbnN0IF9fdml0ZV9pbmplY3RlZF9vcmlnaW5hbF9kaXJuYW1lID0gXCJIOlxcXFxhcHBcXFxcamF2YVxcXFxhdXRoLWdhdGV3YXlcXFxcYXV0aC1jbGllbnQtcmVhY3RcIjtjb25zdCBfX3ZpdGVfaW5qZWN0ZWRfb3JpZ2luYWxfZmlsZW5hbWUgPSBcIkg6XFxcXGFwcFxcXFxqYXZhXFxcXGF1dGgtZ2F0ZXdheVxcXFxhdXRoLWNsaWVudC1yZWFjdFxcXFx2aXRlLmNvbmZpZy50c1wiO2NvbnN0IF9fdml0ZV9pbmplY3RlZF9vcmlnaW5hbF9pbXBvcnRfbWV0YV91cmwgPSBcImZpbGU6Ly8vSDovYXBwL2phdmEvYXV0aC1nYXRld2F5L2F1dGgtY2xpZW50LXJlYWN0L3ZpdGUuY29uZmlnLnRzXCI7aW1wb3J0IHsgZGVmaW5lQ29uZmlnLCBsb2FkRW52IH0gZnJvbSAndml0ZSdcclxuaW1wb3J0IHJlYWN0IGZyb20gJ0B2aXRlanMvcGx1Z2luLXJlYWN0J1xyXG5pbXBvcnQgcGF0aCBmcm9tICdwYXRoJ1xyXG5cclxuLy8gaHR0cHM6Ly92aXRlanMuZGV2L2NvbmZpZy9cclxuZXhwb3J0IGRlZmF1bHQgZGVmaW5lQ29uZmlnKCh7IGNvbW1hbmQsIG1vZGUgfSkgPT4ge1xyXG4gIC8vIExvYWQgZW52IGZpbGUgYmFzZWQgb24gYG1vZGVgIGluIHRoZSBjdXJyZW50IHdvcmtpbmcgZGlyZWN0b3J5LlxyXG4gIGNvbnN0IGVudiA9IGxvYWRFbnYobW9kZSwgcHJvY2Vzcy5jd2QoKSwgJycpXHJcblxyXG4gIHJldHVybiB7XHJcbiAgICBwbHVnaW5zOiBbcmVhY3QoKV0sXHJcbiAgICByZXNvbHZlOiB7XHJcbiAgICAgIGFsaWFzOiB7XHJcbiAgICAgICAgJ0AnOiBwYXRoLnJlc29sdmUoX19kaXJuYW1lLCAnLi9zcmMnKSxcclxuICAgICAgfSxcclxuICAgIH0sXHJcbiAgICBzZXJ2ZXI6IHtcclxuICAgICAgcG9ydDogNTE3NCxcclxuICAgICAgb3BlbjogdHJ1ZSxcclxuICAgICAgY29yczogdHJ1ZSxcclxuICAgICAgLy8gQ1x1MUVBNXUgaFx1MDBFQ25oIEhNUlxyXG4gICAgICBobXI6IHtcclxuICAgICAgICBvdmVybGF5OiB0cnVlLFxyXG4gICAgICAgIC8vIFRoXHUxRUREaSBnaWFuIGNoXHUxRUREIGtcdTFFQkZ0IG5cdTFFRDFpIGxcdTFFQTFpIChtcylcclxuICAgICAgICB0aW1lb3V0OiAxMDAwLFxyXG4gICAgICB9LFxyXG4gICAgICAvLyBUXHUxRUYxIFx1MDExMVx1MUVEOW5nIHJlbG9hZCBraGkgZmlsZSB0aGF5IFx1MDExMVx1MUVENWlcclxuICAgICAgd2F0Y2g6IHtcclxuICAgICAgICB1c2VQb2xsaW5nOiB0cnVlLFxyXG4gICAgICAgIGludGVydmFsOiAxMDAwLFxyXG4gICAgICB9LFxyXG4gICAgICBwcm94eToge1xyXG4gICAgICAgIC8vIFByb3h5IEFQSSByZXF1ZXN0cyB0byBiYWNrZW5kIGR1cmluZyBkZXZlbG9wbWVudFxyXG4gICAgICAgICcvYXBpJzoge1xyXG4gICAgICAgICAgdGFyZ2V0OiBlbnYuVklURV9BUElfVVJMIHx8ICdodHRwOi8vbG9jYWxob3N0OjgwODAnLFxyXG4gICAgICAgICAgY2hhbmdlT3JpZ2luOiB0cnVlLFxyXG4gICAgICAgICAgcmV3cml0ZTogKHBhdGgpID0+IHBhdGgucmVwbGFjZSgvXlxcL2FwaS8sICcnKSxcclxuICAgICAgICB9LFxyXG4gICAgICB9LFxyXG4gICAgfSxcclxuICAgIGJ1aWxkOiB7XHJcbiAgICAgIC8vIEdlbmVyYXRlIHNvdXJjZSBtYXBzIGZvciBwcm9kdWN0aW9uIGJ1aWxkXHJcbiAgICAgIHNvdXJjZW1hcDogY29tbWFuZCA9PT0gJ3NlcnZlJyxcclxuICAgICAgLy8gTWluaWZ5IG91dHB1dFxyXG4gICAgICBtaW5pZnk6ICd0ZXJzZXInLFxyXG4gICAgICAvLyBDb25maWd1cmUgcm9sbHVwIG9wdGlvbnNcclxuICAgICAgcm9sbHVwT3B0aW9uczoge1xyXG4gICAgICAgIG91dHB1dDoge1xyXG4gICAgICAgICAgbWFudWFsQ2h1bmtzOiB7XHJcbiAgICAgICAgICAgICdyZWFjdC12ZW5kb3InOiBbJ3JlYWN0JywgJ3JlYWN0LWRvbScsICdyZWFjdC1yb3V0ZXItZG9tJ10sXHJcbiAgICAgICAgICAgICdhbnRkJzogWydhbnRkJywgJ0BhbnQtZGVzaWduL2ljb25zJ10sXHJcbiAgICAgICAgICAgICdheGlvcyc6IFsnYXhpb3MnXSxcclxuICAgICAgICAgIH0sXHJcbiAgICAgICAgfSxcclxuICAgICAgfSxcclxuICAgICAgLy8gUmVkdWNlIGNodW5rIHNpemUgd2FybmluZ3MgbGltaXRcclxuICAgICAgY2h1bmtTaXplV2FybmluZ0xpbWl0OiAxMDAwLFxyXG4gICAgfSxcclxuICAgIGNzczoge1xyXG4gICAgICAvLyBFbmFibGUgQ1NTIHNvdXJjZSBtYXBzIGluIGRldmVsb3BtZW50XHJcbiAgICAgIGRldlNvdXJjZW1hcDogdHJ1ZSxcclxuICAgICAgcHJlcHJvY2Vzc29yT3B0aW9uczoge1xyXG4gICAgICAgIHNjc3M6IHtcclxuICAgICAgICAgIGFkZGl0aW9uYWxEYXRhOiBgQGltcG9ydCBcIkAvc3R5bGVzL3ZhcmlhYmxlcy5zY3NzXCI7YCxcclxuICAgICAgICB9LFxyXG4gICAgICB9LFxyXG4gICAgfSxcclxuICAgIC8vIE9wdGltaXplIGRlcGVuZGVuY2llcyBwcmUtYnVuZGxpbmdcclxuICAgIG9wdGltaXplRGVwczoge1xyXG4gICAgICBpbmNsdWRlOiBbJ3JlYWN0JywgJ3JlYWN0LWRvbScsICdyZWFjdC1yb3V0ZXItZG9tJywgJ2FudGQnLCAnQGFudC1kZXNpZ24vaWNvbnMnLCAnYXhpb3MnXSxcclxuICAgICAgLy8gVFx1MUVEMWkgXHUwMUIwdSBmb3JjZSB1cGRhdGUga2hpIGRlcGVuZGVuY2llcyB0aGF5IFx1MDExMVx1MUVENWlcclxuICAgICAgZm9yY2U6IHRydWVcclxuICAgIH0sXHJcbiAgfVxyXG59KVxyXG4iXSwKICAibWFwcGluZ3MiOiAiO0FBQTBULFNBQVMsY0FBYyxlQUFlO0FBQ2hXLE9BQU8sV0FBVztBQUNsQixPQUFPLFVBQVU7QUFGakIsSUFBTSxtQ0FBbUM7QUFLekMsSUFBTyxzQkFBUSxhQUFhLENBQUMsRUFBRSxTQUFTLEtBQUssTUFBTTtBQUVqRCxRQUFNLE1BQU0sUUFBUSxNQUFNLFFBQVEsSUFBSSxHQUFHLEVBQUU7QUFFM0MsU0FBTztBQUFBLElBQ0wsU0FBUyxDQUFDLE1BQU0sQ0FBQztBQUFBLElBQ2pCLFNBQVM7QUFBQSxNQUNQLE9BQU87QUFBQSxRQUNMLEtBQUssS0FBSyxRQUFRLGtDQUFXLE9BQU87QUFBQSxNQUN0QztBQUFBLElBQ0Y7QUFBQSxJQUNBLFFBQVE7QUFBQSxNQUNOLE1BQU07QUFBQSxNQUNOLE1BQU07QUFBQSxNQUNOLE1BQU07QUFBQTtBQUFBLE1BRU4sS0FBSztBQUFBLFFBQ0gsU0FBUztBQUFBO0FBQUEsUUFFVCxTQUFTO0FBQUEsTUFDWDtBQUFBO0FBQUEsTUFFQSxPQUFPO0FBQUEsUUFDTCxZQUFZO0FBQUEsUUFDWixVQUFVO0FBQUEsTUFDWjtBQUFBLE1BQ0EsT0FBTztBQUFBO0FBQUEsUUFFTCxRQUFRO0FBQUEsVUFDTixRQUFRLElBQUksZ0JBQWdCO0FBQUEsVUFDNUIsY0FBYztBQUFBLFVBQ2QsU0FBUyxDQUFDQSxVQUFTQSxNQUFLLFFBQVEsVUFBVSxFQUFFO0FBQUEsUUFDOUM7QUFBQSxNQUNGO0FBQUEsSUFDRjtBQUFBLElBQ0EsT0FBTztBQUFBO0FBQUEsTUFFTCxXQUFXLFlBQVk7QUFBQTtBQUFBLE1BRXZCLFFBQVE7QUFBQTtBQUFBLE1BRVIsZUFBZTtBQUFBLFFBQ2IsUUFBUTtBQUFBLFVBQ04sY0FBYztBQUFBLFlBQ1osZ0JBQWdCLENBQUMsU0FBUyxhQUFhLGtCQUFrQjtBQUFBLFlBQ3pELFFBQVEsQ0FBQyxRQUFRLG1CQUFtQjtBQUFBLFlBQ3BDLFNBQVMsQ0FBQyxPQUFPO0FBQUEsVUFDbkI7QUFBQSxRQUNGO0FBQUEsTUFDRjtBQUFBO0FBQUEsTUFFQSx1QkFBdUI7QUFBQSxJQUN6QjtBQUFBLElBQ0EsS0FBSztBQUFBO0FBQUEsTUFFSCxjQUFjO0FBQUEsTUFDZCxxQkFBcUI7QUFBQSxRQUNuQixNQUFNO0FBQUEsVUFDSixnQkFBZ0I7QUFBQSxRQUNsQjtBQUFBLE1BQ0Y7QUFBQSxJQUNGO0FBQUE7QUFBQSxJQUVBLGNBQWM7QUFBQSxNQUNaLFNBQVMsQ0FBQyxTQUFTLGFBQWEsb0JBQW9CLFFBQVEscUJBQXFCLE9BQU87QUFBQTtBQUFBLE1BRXhGLE9BQU87QUFBQSxJQUNUO0FBQUEsRUFDRjtBQUNGLENBQUM7IiwKICAibmFtZXMiOiBbInBhdGgiXQp9Cg==
