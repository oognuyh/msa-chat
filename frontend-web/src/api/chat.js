import http from '@/api/http'

export const getChannelsByUserId = () => http.get('/chat-service/v1/channels')

export const getChannelBetweenUserIds = (userId) => http.get('/chat-service/v1/channels/between?userId=' + userId)

export const getChannelById = (channelId) => http.get('/chat-service/v1/channels/' + channelId)

export const getMessagesByChannelId = (channelId) => http.get('/chat-service/v1/channels/' + channelId + "/messages")

export const send = (newMessage) => http.post('/chat-service/v1/channels/' + newMessage.channelId + "/messages", newMessage)

export const read = (channelId, messageId) => http.get(`/chat-service/v1/channels/${channelId}/messages/${messageId}`)

export const leave = (channelId) => http.delete('/chat-service/v1/channels/' + channelId)

export const findChannelsByQuery = (queryTerm) => http.get(`/chat-service/v1/channels/search?queryTerm=${queryTerm}`)

export const createNewGroupChannel = (name) => http.post('/chat-service/v1/channels', {
    name: name
})

export const join = (channelId) => http.post(`/chat-service/v1/channels/${channelId}`)