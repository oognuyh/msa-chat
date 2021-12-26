<template>
  <v-container fluid ref="container">
    <v-app-bar
      app
      dark
      flat
      short
    >
      <v-toolbar-title>
        <template>
            <avatar v-if="channel.type === 'DIRECT'" :user="channel.recipients[0]" />
            <avatar v-else-if="channel.type === 'GROUP'" group />
        </template>
        <span class="ml-4">{{ channel.name }}</span>
        <v-chip
          v-if="channel.type === 'GROUP'"
          class="ml-2"
          outlined
          small
        >
          <v-icon small left>
            mdi-account-outline
          </v-icon>
          {{ channel.participants.length || 0 }}
        </v-chip>
      </v-toolbar-title>

      <v-spacer />
      <v-btn 
        icon
        @click="leave"
      >
        <v-icon>mdi-trash-can</v-icon>
      </v-btn>
    </v-app-bar>

    <message 
      v-for="message in channel.messages" 
      :key="message.id" 
      :message="message" 
    />

     <v-footer
      color="grey darken-4"
      app
      inset
      dark
    >
      <v-text-field 
        v-model="newMessage"
        label="Message"
        single-line
        clearable
        dense
        hide-details
        prepend-icon="mdi-message"
        append-outer-icon="mdi-send"
        @keyup.enter.prevent="send"
        @click:append-outer="send"
      />
    </v-footer>

    <v-row ref="end" />
  </v-container>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'
import Avatar from '@/components/Avatar'
import Message from '@/components/Message'

export default {
  components: {
    Avatar,
    Message
  },
  data: () => ({
    newMessage: ""
  }),
  computed: {
    ...mapGetters({
      channel: 'channel/getChannel'
    })
  },
  created() {
    this.getChannelById(this.$route.params.id)
  },
  updated() {
    this.$nextTick(() => {
      this.$refs.end.scrollIntoView({
        behavior: 'smooth'
      })
    })
  },
  methods: {
    ...mapActions({
      getChannelById: 'channel/getChannelById',
      leave: 'channel/leave'
    }),
    send() {
      this.$store.dispatch('channel/send', this.newMessage)
        .then(() => this.newMessage = '')
        .catch((e) => console.error(e))
    }
  }
}
</script>

<style>

</style>