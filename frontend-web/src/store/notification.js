export default {
  namespaced: true,
  state: {
    notification: {
      active: false,
      message: ''
    }
  },
  getters: {
    getNotification(state) {
      return state.notification
    }
  },
  mutations: {
    SET_NOTIFICATION(state, notification) {
      state.notification = { ...notification }
    },
  },
  actions: {
    notify({ state, commit, dispatch, rootState }, event) {
      if (event.type === 'NEW_MESSAGE') {
        if (window.location.href.includes(event.channelId)) {
          dispatch('channel/read', {
            channelId: event.channelId, 
            messageId: event.messageId
          }, { root: true })
        } else {
          if (state.notification.active) state.notification.active = false
          
          commit('SET_NOTIFICATION', {
            active: true,
            message: event.message
          })
        }
        dispatch('channels/getChannelsByUserId', null, { root: true })
      } else if (event.type === 'USER_CHANGED_IN_FRIENDS') {
        dispatch('friends/getFriendsByUserId', null, { root: true })
      } else if (event.type === 'USER_CHANGED_IN_CHANNELS') {
        dispatch('channels/getChannelsByUserId', null, { root: true })
        if (rootState.channel.channel && 
            rootState.channel.channel.participants
              .find((participant) => participant.id === event.senderId)) {
          dispatch('channel/getChannelById', rootState.channel.channel.id, { root: true })
        }
      }
    } 
  }
}