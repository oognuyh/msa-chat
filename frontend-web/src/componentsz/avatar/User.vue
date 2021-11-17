<template>
  <v-badge
    :value="!!user"
    :color="user && user.isActive || alwaysActive ? 'green darken-3' : 'red darken-3'"
    offset-x="8"
    offset-y="8"
    bottom
    dot
  >
    <v-avatar color="white" size="34">
      <template v-if="!!user">
        <v-img 
          v-if="!!user.imageUrl"
          :src="user.imageUrl"
        />
        <strong 
          v-else 
          class="black--text"
        >
          {{ user.name[0] }}
        </strong>
      </template>
      <strong 
        v-else 
        class="black--text"
      >
        ?
      </strong>
    </v-avatar>
  </v-badge>
</template>

<script>
export default {
  props: {
    user: {
      type: Object,
      required: false
    },
    alwaysActive: {
      type: Boolean,
      default: false
    }
  }
}
</script>

<style>

</style>