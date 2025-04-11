import React from 'react'
import ReactDOM from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import App from './App'
import './styles/index.css'

// Enable performance tracking in development
if (import.meta.env.DEV) {
  if (performance && 'mark' in performance) {
    performance.mark('app-start')
  }
}

const rootElement = document.getElementById('root')
if (!rootElement) {
  throw new Error('Failed to find root element')
}

ReactDOM.createRoot(rootElement).render(
  <React.StrictMode>
    <BrowserRouter>
      <App />
    </BrowserRouter>
  </React.StrictMode>,
)

// Performance measurement end
if (import.meta.env.DEV) {
  if (performance && 'mark' in performance && 'measure' in performance) {
    performance.mark('app-end')
    performance.measure('app-initialization', 'app-start', 'app-end')
  }
}

// Log environment info in development
if (import.meta.env.DEV && import.meta.env.VITE_ENABLE_DEBUG === 'true') {
  console.log('App initialized with environment:', import.meta.env.MODE)
  console.log('App version:', import.meta.env.VITE_APP_VERSION || 'development')
}
