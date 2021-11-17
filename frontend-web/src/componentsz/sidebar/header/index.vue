<template>
  <v-toolbar
    color="grey darken-4"
    short
    flat
    dark
  >
    <v-btn
      icon
      to="/profile"
    >
      <user-avatar :user="$keycloak.tokenParsed" always-active />
    </v-btn>

    <v-list-item-content class="pl-2">
      <v-list-item-title>{{ $keycloak.authenticated ? $keycloak.tokenParsed.name : 'anonymous' }}</v-list-item-title>
      <v-list-item-subtitle>{{ $keycloak.tokenParsed.statusMessage }}</v-list-item-subtitle>
    </v-list-item-content>
      
    <v-btn 
      icon
      @click="$keycloak.authenticated ? $keycloak.logoutFn() : $keycloak.loginFn()"
    >
      <v-icon>
        {{ $keycloak.authenticated ? "mdi-logout" : "mdi-login" }}
      </v-icon>
    </v-btn>
  </v-toolbar>
</template>

<script>
import { UserAvatar } from '@/components/avatar'

export default {
  components: {
    UserAvatar
  },
}
</script>

<style>

</style>