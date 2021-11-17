<template>
  <v-badge
    :value="!!user || !group"
    :color="color"
    offset-x="8"
    offset-y="8"
    bottom
    dot
  >
    <v-avatar color="white" size="34">
      <strong 
        v-if="group"
        class="black--text"
      >
        #
      </strong>

      <template v-else-if="!!user">
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
      default: null
    },
    alwaysActive: {
      type: Boolean,
      default: false
    },
    group: {
      type: Boolean,
      default: false
    }
  },
  computed: {
    color() {
      return this.user && this.user.isActive || this.alwaysActive ? 
        'green darken-3' : 
        'red darken-3'
    }
  }
}
</script>

<style>

</style>