import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../views/Home.vue'
import store from '../store'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'home',
    component: Home
  },
  {
    path: '/login',
    name: 'login',
    component: () => import(/* webpackChunkName: "login" */ '../views/Login.vue'),
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

const permitAll = [ "login" ];

router.beforeEach((to, from, next) => {
  if (store.state.authInfo !== null) { // 이미 로그인 상태이면
    if (to.name == "login") // 로그인 페이지로 이동하려고 하면
      next({name: "home"}); // home 페이지로 리다이렉트 시킨다.
    else
      next();
  } else {
    // JWT가 쿠키로 내려오기 때문에 새로고침이나 새로 탭을 열었을 때 store.state.authInfo는 null로 초기화될 것이다.
    // 따라서 아래 요청을 새로 보내서 성공적이라면 정상적인 JWT 쿠키가 이미 있는 것이고 다시 사용자 정보를 저장해 놓으면 된다.
    // 만약 실패한다면 JWT 쿠키가 없는 것이므로 아직 인증받지 못했다는 것이므로 login 페이지로 이동시켜주면 된다.
    if (permitAll.includes(to.name)) { // 인증이 필요없는 페이지이면
      next(); // 그냥 이동시킨다.
    } else {
      window.axios.get('/reauth')
      .then(res => {
        store.state.authInfo = res.data; // ReauthResDto 객체가 내려올 것이다.
        next(); // 성공하면 계속.
      })
      .catch(err => {
        console.log(err);
        router.push({name: 'login'}); // 실패 시 login 페이지로 이동.
      });
    }
  }
})

export default router
