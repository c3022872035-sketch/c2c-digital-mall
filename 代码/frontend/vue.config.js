const webpack = require('webpack')
const os = require('os')

module.exports = {
    publicPath: './',
    assetsDir: 'static',
    productionSourceMap: false,
    lintOnSave: false,
    parallel: os.cpus().length > 1,
    configureWebpack: {
        devtool: process.env.NODE_ENV === 'development' ? 'eval' : false,

        plugins: [
            new webpack.ProvidePlugin({
                $: "jquery",
                jQuery: "jquery",
                "windows.jQuery": "jquery"
            })
        ],
        cache: true
    },

    devServer: {
        port: 8080,
        hot: true,
        compress: true,
    },
    // devServer: {
    //     proxy: {
    //         '/api':{
    //             target:'http://jsonplaceholder.typicode.com',
    //             changeOrigin:true,
    //             pathRewrite:{
    //                 '/api':''
    //             }
    //         }
    //     }
    // }
};