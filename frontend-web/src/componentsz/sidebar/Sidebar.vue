<template>
  <v-navigation-drawer
    width="300"
    permanent
    app
  >
    <sidebar-header />
    <sidebar-tabs />

    <v-btn
      bottom
      fixed
      href="https://github.com/oognuyh/msa-chat"
      target="blank"
      left
      dark
      fab
    >
      <v-icon x-large>
        mdi-github
      </v-icon>
    </v-btn>
  </v-navigation-drawer>
</template>

<script>
import SidebarHeader from './SidebarHeader'
import SidebarTabs from './tabs'

export default {
  components: {
    SidebarHeader,
    SidebarTabs
  }
}
</script>

<style>

</style>