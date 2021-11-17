import Vue from 'vue'
import App from './App.vue'
import vuetify from './plugins/vuetify'
import router from './router'
import store from './store'
import Keycloak from '@dsb-norge/vue-keycloak-js'

Vue.config.productionTip = false

Vue.use(Keycloak, {
  init: {
    onLoad: 'login-required'
  },
  config: {
    url: 'http://localhost:8080/auth',
    realm: 'chat',
    clientId: 'frontend',
  },
  logout: {
    redirectUri: window.location.origin
  },
  onReady: () => {
    console.log("onReady")
    new Vue({
      vuetify,
      router,
      store,
      render: h => h(App)
    }).$mount('#app')   
  }
})