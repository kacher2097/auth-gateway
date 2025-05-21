import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import './assets/css/main.css'
import permission from './directives/permission'

const app = createApp(App)

// Register global directives
app.directive('permission', permission)

app.use(createPinia())
app.use(router)

app.mount('#app')
