import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

export default new Router({
  mode: 'history',
  base: process.env.BASE_URL,
  routes: [
    {
      path: '/',
      name: 'HomePage',
      component: () => import('./view/HomePage.vue')
    },

    {
      path: '/conference',
      name: 'Conference',
      component: () => import('./view/Conference.vue')
    },

    {
      path: '/notification',
      name: 'notification',
      component: () => import('./view/Notification.vue')
    },

    {
      path: '/admin',
      name: 'admin',
      component: () => import('./view/Admin.vue')
    },

    {
      path: '/conference/:cid',
      name: 'conference_detail',
      component: () => import('./view/ConferenceDetail.vue')
    },

    {
      path: '/discussion/:draftId',
      name: 'Discussion',
      component: () => import('./view/Discussion.vue')
    },

    {
      path: '/pdfpreview/:fid',
      name: 'PDFPreview',
      component: () => import('./view/PDFPreview.vue')
    }
  ]
})
