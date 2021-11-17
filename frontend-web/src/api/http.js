import axios from 'axios'
import Vue from 'vue'

const instance = axios.create({
    headers: {
        'Content-Type': 'application/json; charset=utf-8'
    }    ,
    baseURL: '/api'
})

instance.interceptors.request.use(function(config) {
    if (Vue.prototype.$keycloak.authenticated) {
        config.headers.Authorization = `Bearer ${Vue.prototype.$keycloak.token}`
    }

    return config
})

export default instance