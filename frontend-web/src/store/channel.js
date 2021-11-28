import * as chatApi from '@/api/chat'
import Vue from 'vue'
import router from '@/router'

export default {
  namespaced: true,
  state: {
    channel: {
      participants: []
    }
  },
  getters: {
    getChannel(state) {
      return state.channel
    },
    getParticipants(state) {
      return state.channel.participants
    }
  },
  mutations: {
    SET_CHANNEL(state, channel) {
      state.channel = channel
    },
    SET_MESSAGES(state, messages) {
      state.channel.messages = messages
    },
    ADD_MESSAGE(state, message) {
      state.channel.messages.push(message)
    }
  },
  actions: {
    async getChannelById({ commit, dispatch }, id) {
      try {
        const { data } = await chatApi.getChannelById(id)

        commit('SET_CHANNEL', data)
        dispatch('channels/getChannelsByUserId', null, { root: true })
      } catch (error) {
        console.error(error)
      }
    },
    async getMessagesByChannelId({ commit, state }) {
      try {
        const { data } = await chatApi.getMessagesByChannelId(state.channel.id)

        commit('SET_MESSAGES', data)
      } catch (error) {
        console.error(error)
      }
    },
    async send({ commit, state, dispatch }, content) {
      return chatApi.send({
        channelId: state.channel.id,
        senderId: Vue.prototype.$keycloak.subject,
        senderName: Vue.prototype.$keycloak.tokenParsed.name,
        content: content,
        unreaderIds: state.channel.recipients.map(recipient => recipient.id)
      })
      .then((response) => {
        commit('ADD_MESSAGE', response.data)
        dispatch('channels/getChannelsByUserId', null, { root: true })
      })
    },
    async read({ commit }, { channelId, messageId }) {
       try {
         const { data } = await chatApi.read(channelId, messageId)
        
         commit('ADD_MESSAGE', data)
       } catch (error) {
         console.error(error)
       }
    },
    async leave({ commit, state, rootState }) {
      try {
        await chatApi.leave(state.channel.id)

        rootState.channels.channels = rootState.channels.channels.filter((channel) => channel.id !== state.channel.id)

        commit('SET_CHANNEL', {})

        router.replace('/')
      } catch (error) {
        console.error(error)
      }
    }
  }
}