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
          nudge-bottom="8"
          rounded="lg"
          allow-overflow
          offset-y
          bottom
        >
          <template v-slot:activator="{ on, attrs }">
            <v-text-field
              v-bind="attrs"
              v-on="on"
              @input="search"
              :loading="isLoading"
              prepend-inner-icon="mdi-magnify"
              label="Search name or email"
              hide-details
              clearable
              dense
              solo
            />     
          </template>

          <v-list>
            <v-list-item
              v-for="user in users"
              :key="user.id"
            >
              <v-list-item-avatar tile>
                <user-avatar :user="user" />
              </v-list-item-avatar>
      
              <v-list-item-content>
                <v-list-item-title>{{ user.name }}</v-list-item-title>
              </v-list-item-content>

              <v-list-item-action>
                <v-btn 
                  v-if="isFriend(user)" 
                  icon
                  @click="getChannelBetweenUserIds(user.id)"
                >
                  <v-icon small>mdi-message</v-icon>
                </v-btn>
                <v-btn 
                  v-else 
                  icon
                  @click="addNewFriend(user.id)"
                >
                  <v-icon small>mdi-plus</v-icon>
                </v-btn>
              </v-list-item-action>
            </v-list-item>
          </v-list>
        </v-menu>
      </v-list-item>
  
      <v-list subheader>
        <v-subheader>
          Friends 
        </v-subheader>
        
        <v-list-item
          v-for="friend in friends"
          :key="friend.id"
          link
          @click="getChannelBetweenUserIds(friend.id)"
        >
          <v-list-item-avatar tile>
            <user-avatar :user="friend" />
          </v-list-item-avatar>
  
          <v-list-item-content>
            <v-list-item-title>{{ friend.name }}</v-list-item-title>
            <v-list-item-subtitle>{{ friend.statusMessage }}</v-list-item-subtitle>
          </v-list-item-content>
        </v-list-item>
      </v-list>
    </v-container>  
  </v-tab-item>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'
import { findUsersByQuery } from '@/api/user'
import { UserAvatar } from '@/components/avatar'
import Vue from 'vue'

export default {
  components: {
    UserAvatar
  },
  computed: {
    ...mapGetters({
      friends: 'friends/getFriends'
    }),
  },
  data: () => ({
    isLoading: false,
    isSearching: false,
    users: [],
  }),
  mounted() {
    this.getFriendsByUserId()
  },
  methods: {
    ...mapActions({
      getFriendsByUserId: 'friends/getFriendsByUserId',
      deleteFriendById: 'friends/deleteFriendById',
      getChannelBetweenUserIds: 'channels/getChannelBetweenUserIds'
    }),
    search(queryTerm) {
      this.isSearching = true
      this.isLoading = true

      findUsersByQuery(queryTerm)
        .then(response => this.users = response.data
          .filter((user) => user.id !== Vue.prototype.$keycloak.subject))
        .catch((error) => console.error(error))
        .finally(() => this.isLoading = false)
    },
    isFriend(user) {
      return this.friends.map(friend => friend.id).includes(user.id)
    },
    addNewFriend(id) {
      this.$store.dispatch('friends/addNewFriend', id)
      
      this.isSearching = false
    }
  }
}
</script>

<style>

</style>