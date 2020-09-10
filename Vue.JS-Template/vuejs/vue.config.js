module.exports = {
  transpileDependencies: [
    "vuetify"
  ],
  devServer: { // 개발 서버 설정
    port: 8081,
    proxy: { // 프록시 설정
      '^/': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  },
  configureWebpack: {
    devtool: 'source-map'
  },
  outputDir: path.resolve(__dirname, "../src/main/resources/static")
}