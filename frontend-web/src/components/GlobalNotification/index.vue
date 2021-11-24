<template>
  <v-snackbar
    v-model="notification.active"
    timeout="1000"
    left
    top
  >
    {{ notification.message }}
  </v-snackbar>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'
import { offline } from '@/api/user'
import Stomp from 'webstomp-client'
import SockJS from 'sockjs-client'
import Vue from 'vue'

export default {
  computed: {
    ...mapGetters({
      notification: 'notification/getNotification'
    })
  },
  mounted() {
    this.connect()
    window.onbeforeunload = () => {
      offline()
    }
  },
  methods: {
    ...mapActions('notification', ['notify']),
    connect() {
      const socket = new SockJS(`/ws`)
      const options = {
        debug: false, 
        protocols: Stomp.VERSIONS.supportedProtocols()
      }
      this.stompClient = Stomp.over(socket, options)

      this.stompClient.connect({
        Authorization: `Bearer ${Vue.prototype.$keycloak.token}`
      }, this.subscribe, (error) => {
          console.error(error)
          offline()
      })
    },
    subscribe() {
      this.stompClient.subscribe(`/notifications/${Vue.prototype.$keycloak.subject}`, (response) => {
        this.notify(JSON.parse(response.body))
      })
    }
  },
}
</script>

<style scoped>

</style>