<template>
  <v-tab-item>
    <v-container fluid>
      <v-list-item
        class="mt-4"
      >
        <v-text-field
          prepend-inner-icon="mdi-magnify"
          label="Search name"
          solo
          dense
          hide-details
        />
      </v-list-item>
  
      <v-list subheader>
        <v-subheader>Channels </v-subheader>
  
        <v-list-item
          v-for="channel in channels"
          :key="channel.id"
          :to="`/channels/${channel.id}`"
          link
        >
          <v-list-item-avatar 
            :tile="channel.type === 'DIRECT'"
          >
            <user-avatar v-if="channel.type === 'DIRECT'" :user="channel.recipients[0]" />
            <v-icon v-else>mdi-group</v-icon>
          </v-list-item-avatar>
  
          <v-list-item-content>
            <v-list-item-title v-text="channel.name" />
            <v-list-item-subtitle v-text="channel.lastMessageContent || ''" />
          </v-list-item-content>

          <v-list-item-action>
            <v-chip
              v-show="channel.numOfUnreadMessages > 0"
              color="yellow darken-3"
              small
              dark
            >
              {{ channel.numOfUnreadMessages }}
            </v-chip>
          </v-list-item-action>
        </v-list-item>
      </v-list>
    </v-container>  
  </v-tab-item>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'
import { UserAvatar } from '@/components/avatar'

export default {
  components: {
    UserAvatar
  },
  computed: {
    ...mapGetters({
      channels: 'channels/getChannels'
    })
  },
  created() {
    this.getChannelsByUserId()
  },
  methods: {
    ...mapActions({
      getChannelsByUserId: 'channels/getChannelsByUserId'
    })
  }
}
</script>

<style>

</style>