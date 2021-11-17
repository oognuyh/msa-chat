import Vue from 'vue'
import VueRouter from 'vue-router'
import { Home, Channel, Profile } from '../views'

Vue.use(VueRouter)

const routes = [
    {
        path: '/',
        component: Home,
        name: 'Home'
    },
    {
        path: '/channels/:id',
        component: Channel,
        name: 'Channel'
    },
    {
        path: '/profile',
        component: Profile,
        name: 'Profile'
    }
]

export default new VueRouter({
    mode: 'history',
    routes
})