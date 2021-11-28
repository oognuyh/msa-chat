<template>
  <v-tab-item>
    <v-container fluid>
      <v-list-item
        class="mt-4"
      >
        <v-menu
          v-model="isSearching"
          :close-on-content-click="false"
          :open-on-click="false"
          max-height="300"
          max-width="244"
          nudge-bottom="8"
          rounded="lg"
          allow-overflow
          offset-y
          bottom
        >
          <template v-slot:activator="{ on, attrs }">
            <v-text-field
              v-model="queryTerm"
              v-bind="attrs"
              v-on="on"
              @input="search"
              :loading="isLoading"
              prepend-inner-icon="mdi-magnify"
              label="Search or create"
              hide-details
              clearable
              dense
              solo
            />     
          </template>

          <v-list>
            <v-list-item>
              <v-btn
                color="grey darken-3"
                class="white--text"
                block
                small
                :disabled="!queryTerm"
                @click="createNewGroupChannel(queryTerm)"
              >
                Create
              </v-btn>
            </v-list-item>

            <v-list-item
              v-for="channel in searchedChannels"
              :key="channel.id"
            >
              <v-list-item-avatar tile>
                <avatar group />
              </v-list-item-avatar>
      
              <v-list-item-content>
                <v-list-item-title>
                  {{ channel.name }}
                </v-list-item-title>
                <v-list-item-subtitle>
                  <v-chip
                    outlined
                    x-small
                  >
                    <v-icon x-small left>
                      mdi-account-outline
                    </v-icon>
                    {{ channel.participants.length }}
                  </v-chip>
                </v-list-item-subtitle>
              </v-list-item-content>

              <v-list-item-action>
                <v-btn 
                  v-if="isAlreadyIn(channel.id)"
                  icon
                  small
                  @click="() => $router.push(`/channels/${channel.id}`).catch(() => {})"
                >
                  <v-icon small>mdi-message</v-icon>
                </v-btn>
                <v-btn 
                  v-else 
                  icon
                  small
                  @click="join(channel.id)"
                >
                  <v-icon small>mdi-plus</v-icon>
                </v-btn>
              </v-list-item-action>
            </v-list-item>
          </v-list>
        </v-menu>
      </v-list-item>

      <v-list subheader>
        <v-subheader>Channels</v-subheader>
  
        <v-list-item
          v-for="channel in channels"
          :key="channel.id"
          :to="`/channels/${channel.id}`"
          link
        >
          <v-list-item-avatar 
            :tile="channel.type === 'DIRECT'"
          >
            <avatar v-if="channel.type === 'DIRECT'" :user="channel.recipients[0]" />
            <avatar v-else-if="channel.type === 'GROUP'" group />
          </v-list-item-avatar>
  
          <v-list-item-content>
            <v-list-item-title>
              {{ channel.name }}
            </v-list-item-title>
            <v-list-item-subtitle v-text="channel.lastMessageContent || 'No messages'" />
          </v-list-item-content>

          <v-list-item-action>
            <v-badge
              :value="channel.numOfUnreadMessages"
              :content="channel.numOfUnreadMessages"
              color="yellow darken-3"
              overlap
            >
              <v-chip
                v-if="channel.type === 'GROUP'"
                outlined
                small
              >
                <v-icon small left>
                  mdi-account-outline
                </v-icon>
                {{ channel.participants.length }}
              </v-chip>
            </v-badge>
          </v-list-item-action>
        </v-list-item>
      </v-list>
    </v-container>  
  </v-tab-item>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'
import { findChannelsByQuery } from '@/api/chat'
import Avatar from '@/components/Avatar'

export default {
  components: {
    Avatar
  },
  data: () => ({
    isLoading: false,
    isSearching: false,
    searchedChannels: [],
    queryTerm: '',
  }),
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
    }),
    isAlreadyIn(channelId) {
      return this.channels.some((channel) => channel.id === channelId)
    },
    search(queryTerm) {
      this.isSearching = true
      this.isLoading = true

      findChannelsByQuery(queryTerm || '')
        .then(response => this.searchedChannels = response.data)
        .catch(error => console.error(error))
        .finally(() => this.isLoading = false)
    },
    join(channelId) {
      this.isSearching = false

      this.$store.dispatch('channels/join', channelId)
      this.queryTerm = ''
    },
    createNewGroupChannel() {
      this.isSearching = false

      this.queryTerm && this.$store.dispatch('channels/createNewGroupChannel', this.queryTerm)
      this.queryTerm = ''
    }
  }
}
</script>

<style>

</style>