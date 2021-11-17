<template>
  <v-row
    class="d-flex justify-start"
    :class="{ 'flex-row-reverse': isCurrentUser}"
  >
    <v-col 
      v-if="!isCurrentUser"
      align-self="start"
      style="max-width: 45px"
    >
      <avatar :user="user" />
    </v-col>
    
    <v-col
      style="max-width: 300px"
    >
      <p 
        v-if="!isCurrentUser"
        class="overline mb-0"
      >
        {{ user && user.name || 'Anonymous' }}
      </p>

      <v-textarea 
        :value="message.content" 
        style="border-radius: 10px"
        background-color="grey darken-4"
        color="white"
        rows="1"
        hide-details
        readonly
        dense
        auto-grow
        outlined
        dark
      />
      <p 
        class="overline text-left mb-0 pl-1" 
        :class="{ 'text-right': isCurrentUser }"
      >
        {{ message.createdAt }}
      </p>
    </v-col>
  </v-row>
</template>

<script>
import Vue from 'vue'
import Avatar from '@/components/Avatar'
import { mapGetters } from 'vuex'

export default {
  components: {
    Avatar
  },
  props: {
    message: Object
  },
  data: () => ({

  }),
  computed: {
    isCurrentUser() {
      return Vue.prototype.$keycloak.subject === this.message.senderId
    },
    ...mapGetters({
      participants: 'channel/getParticipants'
    }),
    user() {
      return this.participants.find(participant => participant.id === this.message.senderId)
    }
  }
}
</script>

<style>

</style>