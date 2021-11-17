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
          mdi-account-circle
        </v-icon>
        Profile
      </v-toolbar-title>
    </v-app-bar>
  
    <v-row>
      <v-col>
        <v-card flat>
          <v-card-title>
            Edit Details
          </v-card-title>

          <v-card-text>
            <v-form>
              <v-row
                class="my-2"
                justify="center"
              >
                <v-badge
                  offset-x="32"
                  color="white"
                >
                  <v-btn 
                    slot="badge" 
                    icon 
                    @click="() => { newAvatarUrl = ''; newAvatar = null }"
                  >
                    <v-icon>
                      mdi-close-circle
                    </v-icon>
                  </v-btn>

                  <v-btn
                    style="height: 150px; width: 150px"
                    icon
                    x-large
                    outlined
                    @click="() => $refs.file.click()"
                  >
                    <v-avatar
                      size="150"
                    >
                      <v-img 
                        v-if="!!newAvatarUrl"
                        :src="newAvatarUrl" 
                      />

                      <strong 
                        v-else 
                        class="black--text"
                      >
                        {{ user.name[0] }}
                      </strong>
                    </v-avatar>
                  </v-btn>
                </v-badge>

                <input 
                  type="file"
                  ref="file"
                  accept="image/*"
                  @change="onFileChanged"
                  style="display: none" 
                />
              </v-row>

              <v-text-field 
                v-model="user.statusMessage" 
                placeholder="Status message"
                prepend-icon="mdi-pencil"
              />
              <v-text-field 
                v-model="user.given_name" 
                placeholder="First name"
                prepend-icon="mdi-account"
              />
              <v-text-field 
                v-model="user.family_name" 
                placeholder="Last name"
                prepend-icon="mdi-account"
              />
              <v-text-field 
                v-model="user.email"
                placeholder="Email"
                prepend-icon="mdi-email"
              />
              <v-btn
                color="grey darken-4"
                dark
                block
                @click="updateDetails"
              >
                CHANGE
              </v-btn>
            </v-form>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col>
        <v-card flat>
          <v-card-title>
            New Password
          </v-card-title>
          <v-card-text>
            <v-form>
              <v-text-field 
                v-model="newPassword" 
                label="New password"
                prepend-icon="mdi-lock"
                :append-icon="isPasswordSecureMode ? 'mdi-eye-off' : 'mdi-eye'"
                :type="isPasswordSecureMode ? 'password' : 'text'"
                @click:append="() => isPasswordSecureMode = !isPasswordSecureMode"
              />
              <v-text-field 
                v-model="passwordConfirmation"
                label="Password confirmation" 
                prepend-icon="mdi-lock"
                :append-icon="isPasswordSecureMode ? 'mdi-eye-off' : 'mdi-eye'"
                :type="isPasswordSecureMode ? 'password' : 'text'"
                @click:append="() => isPasswordSecureMode = !isPasswordSecureMode"
              />
              <v-btn
                color="grey darken-4"
                dark
                block
                @click="updatePassword"
              >
                CHANGE
              </v-btn>
            </v-form>
          </v-card-text>
      </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import Vue from 'vue'
import * as userApi from '@/api/user'
import * as imageApi from '@/api/image'

export default {
  data: () => ({
    user: { 
      ...Vue.prototype.$keycloak.tokenParsed,
    },
    newPassword: '',
    passwordConfirmation: '',
    isPasswordSecureMode: true,
    newAvatarUrl:  Vue.prototype.$keycloak.tokenParsed.imageUrl || '',
    newAvatar: null
  }),
  methods: {
    async updateDetails() {
      if (this.user.imageUrl !== this.newAvatarUrl) {
        if (this.newAvatar) {
          // upload
          const newImageRequest = new FormData()

          newImageRequest.append("avatar", this.newAvatar)
          console.log(await imageApi.upload(newImageRequest))
        } else {
          imageApi.deleteByImageUrl(this.user.imageUrl)
        }
      } 

      userApi.updateDetails({
        firstName: this.user.given_name,
        lastName: this.user.family_name,
        email: this.user.email,
        statusMessage: this.user.statusMessage
      })

      this.$router.go()
    },
    updatePassword() {
      userApi.updatePassword({
        newPassword: this.newPassword,
        passwordConfirmation: this.passwordConfirmation
      })

      this.$router.go()
    },
    onFileChanged(event) {
      const { files } = event.target
      
      if (0 < files.length < 2) {
        const newAvatar = files[0]
        if (newAvatar.type.match('image.*')) {
          this.newAvatarUrl = URL.createObjectURL(newAvatar)
          this.newAvatar = newAvatar
        }
      }
    }
  }
}
</script>

<style>

</style>