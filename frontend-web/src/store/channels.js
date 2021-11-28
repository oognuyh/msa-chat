import * as chatApi from '@/api/chat'
import router from '@/router'

export default {
  namespaced: true,
  state: {
    channels: [],
  },
  getters: {
    getChannels(state) {
      return state.channels
    },
    getNumOfUnreadMessages(state) {
      return state.channels
        .map((channel) => channel.numOfUnreadMessages)
        .reduce((total, num) => total + num, 0)
    }
  },
  mutations: {
    SET_CHANNELS(state, channels) {
      state.channels = channels
    },
    ADD_NEW_CHANNEL(state, channel) {
      if (state.channels.map(channel => channel.id).includes(channel.id)) return

      state.channels = [
          ...state.channels,
          channel
      ]
    },
    DELETE_CHANNEL(state, channel) {
      state.channels = state.channels
        .filter(existingChannel => existingChannel !== channel)
    }
  },
  actions: {
    async getChannelsByUserId({ commit }) {
      try {
        const { data } = await chatApi.getChannelsByUserId()
        
        commit('SET_CHANNELS', data)
      } catch (error) {
        console.error(error)
      }
    },
    async getChannelBetweenUserIds({ commit }, userId) {
      try {
        const { data } = await chatApi.getChannelBetweenUserIds(userId)

        commit('ADD_NEW_CHANNEL', data)

        router.push('/channels/' + data.id).catch(() => {})
      } catch (error) {
        console.error(error)
      }
    },
    async createNewGroupChannel({ commit }, name) {
      try {
        const { data } = await chatApi.createNewGroupChannel(name) 

        commit('ADD_NEW_CHANNEL', data)

        router.push('/channels/' + data.id).catch(() => {})
      } catch (error) {
        console.error(error)
      }
    },
    async join({ commit }, channelId) {
      try {
        const { data } = await chatApi.join(channelId) 

        commit('ADD_NEW_CHANEL', data)

        router.push('/channels/' + data.id).catch(() => {})
      } catch (error) {
        console.error(error)
      }
    }
  }
}