import http from '@/api/http'
import Vue from 'vue'

export const updateDetails = (request) => http.put(`/users/${Vue.prototype.$keycloak.subject}`, request)

export const updatePassword = (request) => http.put(`/users/${Vue.prototype.$keycloak.subject}/password`, request)

export const findUsersByQuery = (queryTerm) => http.get('/users?queryTerm=' + queryTerm)

export const online = () => http.put(`/users/${Vue.prototype.$keycloak.subject}/status`, {
  status: "on"
})

export const offline = () => http.put(`/users/${Vue.prototype.$keycloak.subject}/status`, {
  status: "off"
})