import http from '@/api/http'

export const getChannelsByUserId = () => http.get('/chat/channels')

export const getChannelBetweenUserIds = (userId) => http.get('/chat/channels/between?userId=' + userId)

export const getChannelById = (channelId) => http.get('/chat/channels/' + channelId)

export const getMessagesByChannelId = (channelId) => http.get('/chat/channels/' + channelId + "/messages")

export const send = (newMessage) => http.post('/chat/channels/' + newMessage.channelId + "/messages", newMessage)

export const read = (channelId, messageId) => http.get(`/chat/channels/${channelId}/messages/${messageId}`)

export const leave = (channelId) => http.delete('/chat/channels/' + channelId)

export const findChannelsByQuery = (queryTerm) => http.get(`/chat/channels/search?queryTerm=${queryTerm}`)

export const createNewGroupChannel = (name) => http.post('/chat/channels', {
    name: name
})

export const join = (channelId) => http.post(`/chat/channels/${channelId}`)