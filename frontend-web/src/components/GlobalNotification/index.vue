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
import { online, offline } from '@/api/user'
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
    document.addEventListener('beforeunload', function(e) {
      e.preventDefault()

      offline()
    })
  },
  methods: {
    ...mapActions('notification', ['notify']),
    connect() {
      const socket = new SockJS('/ws')
      const options = {
        debug: false, 
        protocols: Stomp.VERSIONS.supportedProtocols()
      }

      this.stompClient = Stomp.over(socket, options)

      const headers = {
        Authorization: `Bearer ${Vue.prototype.$keycloak.token}`
      }

      this.stompClient.connect(headers, this.subscribe, (error) => {
          offline()
          console.error(error)
      })
    },
    subscribe() {
      online()

      this.stompClient.subscribe(`/notifications/${Vue.prototype.$keycloak.subject}`, (response) => {
        this.notify(JSON.parse(response.body))
      })
    }
  },
}
</script>

<style scoped>

</style>