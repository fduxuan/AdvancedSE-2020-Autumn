/*
 * Created on 2020/11/29 3:15 下午
 *
 * @Author: fduxuan
 *
 * Desc:
 */

import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export  default new Vuex.Store({
    state: {
        user: null,
        ws: null
    },
    mutations: {
        set_user (state, user) {
            state.user = user
        },
        set_ws (state, ws) {
            state.ws = ws
        }
    }
})
