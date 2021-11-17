<template>
  <v-tabs
    v-model="selectedTab"
    color="grey darken-4"
    centered
    grow
  >
    <v-tab>
      <v-icon>mdi-account-multiple</v-icon>
    </v-tab>

    <v-tab>
      <v-badge
        :value="numOfUnreadMessages"
        :content="numOfUnreadMessages"
        color="yellow darken-3"
        overlap
      >
        <v-icon>mdi-chat</v-icon>
      </v-badge>
    </v-tab>
    

    <v-tabs-items
      v-model="selectedTab"
    >
      <sidebar-tab-item-friends />
      <sidebar-tab-item-channels />
    </v-tabs-items>
  </v-tabs>
</template>

<script>
import SidebarTabItemFriends from './items/friends.vue'
import SidebarTabItemChannels from './items/channels.vue'
import { mapGetters } from 'vuex'

export default {
  components: {
    SidebarTabItemFriends,
    SidebarTabItemChannels
  },
  computed: {
    ...mapGetters({
      numOfUnreadMessages: 'channels/getNumOfUnreadMessages'
    })
  },
  data: () => ({
    selectedTab: null,
    tabs: [
      {
        name: "Friends",
        icon: "mdi-account-multiple"
      },
      {
        name: "Channels",
        icon: "mdi-chat"  
      }
    ]
  })
}
</script>

<style>

</style>