<template>
  <v-app fluid>
    <sidebar />
    
    <v-main>
      <router-view :key="$route.fullPath" />
    </v-main>

    <global-notification />
  </v-app>
</template>

<script>
import Sidebar from '@/components/Sidebar'
import GlobalNotification from '@/components/GlobalNotification'

export default {
  components: {
    Sidebar,
    GlobalNotification
  }
};
</script>

<style scoped>

</style>
