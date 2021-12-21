import http from '@/api/http'
import Vue from 'vue'

export const updateDetails = (request) => http.put(`/user-service/v1/users/${Vue.prototype.$keycloak.subject}`, request)

export const updatePassword = (request) => http.put(`/user-service/v1/users/${Vue.prototype.$keycloak.subject}/password`, request)

export const findUsersByQuery = (queryTerm) => http.get('/user-service/v1/users?queryTerm=' + queryTerm)

export const online = async () => http.put(`/user-service/v1/users/${Vue.prototype.$keycloak.subject}/status`, {
  status: "on"
})

export const offline = async () => http.put(`/user-service/v1/users/${Vue.prototype.$keycloak.subject}/status`, {
  status: "off"
})