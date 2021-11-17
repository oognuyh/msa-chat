import Vue from 'vue'
import Vuex from 'vuex'

import channel from '@/store/channel'
import channels from '@/store/channels'
import friends from '@/store/friends'
import notification from '@/store/notification'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {  

  },
  mutations: {  

  },
  actions: {  

  },
  modules: {
    channel,
    channels,
    friends,
    notification
  }
})