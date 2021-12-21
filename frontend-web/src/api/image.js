import http from '@/api/http'

export const upload = (request) => http.post('/image-service/v1/images/avatars', request, {
  headers: {
    'Content-Type': 'multipart/form-data'
  }
})

export const deleteAvatarByAvatarId = (avatarId) => http.delete(`/image-service/v1/images/avatars/${avatarId}`)