module.exports = {
    lintOnSave: false,
    publicPath: '/',
    devServer: {
    proxy: {
        '^/api/test': {
            target: 'http://localhost:5000/',
            changeOrigin: true,
            ws: true,
            pathRewrite: {
                '^/api/test': '/api/test'
            }
        },
        '^/api/user': {
            target: 'http://106.14.244.24:5001/',
            changeOrigin: true,
            ws: true,
            pathRewrite: {
                '^/api/user': '/api/user'
            }
        },
        '^/api/conference': {
            target: 'http://106.14.244.24:5002/',
            changeOrigin: true,
            ws: true,
            pathRewrite: {
                '^/api/conference': '/api/conference'
            }
        },

        '^/api/invitation': {
            target: 'http://106.14.244.24:5004/',
            changeOrigin: true,
            ws: true,
            pathRewrite: {
                '^/api/invitation': '/api/invitation'
            }
        },

        '^/api/discuss': {
            target: 'http://106.14.244.24:5005/',
            changeOrigin: true,
            ws: true
        },

        '^/api/reviewProcess': {
            target: 'http://106.14.244.24:5006/',
            changeOrigin: true,
            ws: true,
            pathRewrite: {
                '^/api/reviewProcess': '/api/reviewProcess'
            }
        },

        '^/api/notification': {
            target: 'http://106.14.244.24:5007/',
            changeOrigin: true,
            ws: true
        },

        '^/api/draft': {
            target: 'http://106.14.244.24:5003/',
            changeOrigin: true,
            ws: true,
            pathRewrite: {
                '^/api/draft': '/api/draft'
            }
        },
        '^/ws': {
            target: 'ws://106.14.244.24:5007/',
            changeOrigin: true,
            ws: true
        },

    },

    disableHostCheck: true
    },
    css: {
        loaderOptions: {
            less: {
                lessOptions: {
                    modifyVars: {
                        'primary-color': '#494949',
                        'menu-dark-submenu-bg': '#dad7cc',
                        'menu-dark-bg': "#868173",
                        'menu-dark-color':'#232222a6',
                    },
                    javascriptEnabled: true,
                },
            },
        },
      },

}
