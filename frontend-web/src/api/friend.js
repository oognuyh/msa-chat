import http from '@/api/http'

export const getFriendsByUserId = () => http.get('/friends')

export const addNewFriend = (id) => http.post('/friends', {
    friendId: id
})

export const deleteFriendById = ({ id }) => http.delete('/friends/' + id)