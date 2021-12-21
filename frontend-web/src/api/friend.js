import http from '@/api/http'

export const getFriendsByUserId = () => http.get('/friend-service/v1/friends')

export const addNewFriend = (id) => http.post('/friend-service/v1/friends', {
    friendId: id
})

export const deleteFriendById = ({ id }) => http.delete('/friend-service/v1/friends/' + id)