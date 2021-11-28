<template>
  <v-container fluid>
    <v-app-bar
      app
      dark
      flat
      short
    >
      <v-toolbar-title>
        <v-icon
          class="mr-2"
          large
        >
          mdi-text-box
        </v-icon>
        README.md
      </v-toolbar-title>  
    </v-app-bar>

    <v-row>
      <v-card>
        <v-card-text>
          <div v-html="getDocs" />
        </v-card-text>  
      </v-card>
    </v-row>
  </v-container>
</template>

<script>
import marked from 'marked'

export default {
  data: () => ({
    docs: ''
  }),
  computed: {
    getDocs() {
        return marked(this.docs)
    }
  },
  mounted() {
    fetch('https://raw.githubusercontent.com/oognuyh/msa-chat/master/README.md')
      .then(response => response.text())
      .then(docs => this.docs = docs)
  }
}
</script>

<style>

</style>