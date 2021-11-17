import * as friendApi from '@/api/friend'

export default {
  namespaced: true,
  state: {  
    friends: [],
  },
  getters: {
    getFriends(state) {
      return state.friends
    }
  },
  mutations: {
    SET_FRIENDS(state, friends) {
      state.friends = friends
    },
    ADD_NEW_FRIEND(state, newFriend) {
      state.friends = [
        ...state.friends,
        newFriend
      ]
    },
    DELETE_FRIEND(state, friend) {
      state.friends = state.friends
        .filter(existingFriend => existingFriend !== friend)
    }
  },
  actions: {
    async getFriendsByUserId({ commit }) {
      try {
        const { data } = await friendApi.getFriendsByUserId()
        
        console.log(data)
        
        commit('SET_FRIENDS', data)
      } catch (error) {
        console.error(error)
      }
    },
    async addNewFriend({ commit }, id) {
      try {
        const { data } = await friendApi.addNewFriend(id)
        
        commit('ADD_NEW_FRIEND', data)
      } catch (error) {
        console.error(error)
      }
    },
    async deleteFriend({ commit }, friend) {
      try {
        await friendApi.deleteFriendById(friend)

        commit('DELETE_FRIEND', friend)
      } catch (error) {
        console.error(error)
      }
    }
  }
}