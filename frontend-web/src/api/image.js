import http from '@/api/http'

export const upload = (request) => http.post('/images/avatars', request, {
  headers: {
    'Content-Type': 'multipart/form-data'
  }
})

export const deleteAvatarByImageUrl = (imageUrl) => http.delete(imageUrl)