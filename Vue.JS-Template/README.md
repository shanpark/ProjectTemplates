### Vue CLI 설치
Vie CLI는 아래 명령으로 설치 가능하다. 
<pre>$ sudo npm install -g @vue/cli</pre>

### Project 생성
생성된 Spring 프로젝트의 root로 이동하여 Vue.JS 프로젝트 생성 명령을 실행하도록 한다.
<pre>$ cd &lt;project root&gt;
$ vue create vuejs</pre>
Spring 프로젝트의 src폴더와 같은 레벨에 생성해야 한다. (src 폴더 안에 생성하면 안된다.)

### Project에 사용할 Plugin 추가
많이 사용되는 Vuetify, Axios 플러그인의 경우 아래 명령으로 프로젝트에 추가한다.
참고로 src/plugins 밑에 플러그인 사용을 위한 js파일들이 생성된다.
<pre><code># vuetify 설치
vue add vuetify

# axios 설치 및 추가
npm install axios --save
vue add axios</code></pre>

### Vue Project의 output 경로 설정.
위 명령으로 생성된 vuejs 폴더로 들어가서 `vue.config.js` 파일을 만들어서 아래 내용을 넣어준다.
<pre>// vue.config.js
const path = require('path');

module.exports = {
    outputDir: path.resolve(__dirname, "../src/main/resources/static")
}
</pre>
기본적으로 `/dist`가 기본 output 폴더지만 `Spring 프로젝트의 정적 리소스 파일 경로`로 바꿔주는 것이다.

### Vue.js 빌드
- 아래 명령으로 vuejs 폴더에 들어가서 아래 명령을 실행하면 vue.js 프로젝트만 빌드를 수행하고 위에서 설정한 경로에 결과를 출력한다.
<pre>$ cd vuejs
$ npm run build
</pre> 
- Spring을 통해서 테스트를 하려면 vue.js를 build해 주어야 Spring 프로젝트의 /static 폴더에 최종 결과물이 생성되기 때문에
vue.js 코드를 수정 후에는 반드시 한 번 build를 해줘야 한다.
- vue.js를 빌드하면 resouces/static 폴더를 완전히 삭제 후 다시 생성하므로 vue.js 파일들 외에 다른 파일을 넣어놓아도 vue.js를 
build한 후에는 삭제될 것이다. 따라서 추가로 필요한 정적 파일은 vuejs 폴더에 적절히 넣어야 build 후에 복사되어 static에 생성될 것이다. 

### Spring Security 설정 참고 사항
1. static 경로에 Vue.js의 최종 산출물을 생성하지만 브라우저에서 접근하려고 하면 Spring Security가 접근을 막기 때문에 Vue.js가 생성한 산출물에 
대해서는 인증과 상관없이 접근이 가능하도록 해제해주어야 한다. 아래 경로들을 해제해 주면 된다.
   <pre>/
   /favicon.ico
   /css/**
   /img/**
   /js/**</pre>
2. static의 root에 생성된 index.html 파일에 대해서는 Contoller에서 GET mapping을 추가하여 "/" 요청에 대해서 "index.html"이 맵핑이 되도록 해준다.

# Vue.js 보안 인증을 위한 선택 사항
1. HTTPS 사용
2. JWT(http only cookie) 사용.
3. JSON을 이용한 REST API 통신.

### 설명 
1. JWT이 만료되기 전 세션이 살아있는 동안 상태유지를 위해서는 JWT을 Cookie에 저장하는 것이 적당하다. 
   - localStorage는 데이터가 영구적으로 남기 때문에 안되고 sessionStorage는 브라우저의 탭간에 데이터 공유가 되지 않기 때문에 사용할 수 없다.
   - Session Cookie의 경우에는 모든 탭이 데이터를 공유하고 브라우저를 종료하면 사라진다. 
   - httpOnly 옵션이 주어지면 JavaScript로 접근이 안되기 때문에 XSS 공격에도 안전하다.
2. 일반 Cookie는 XSS 공격에 노출되기 쉬으므로 httpOnly Cookie로 해야한다.
3. httpOnly Cookie라고 하더라도 CSRF 공격이 취약하므로 모든 통신을 form data가 아닌 json 통신으로 하는 것이 좋다.
   - JSON 통신을 하면 간단한 link를 통해서 위조 요청을 만들어내는 걸 막을 수 있다. 
4. Cookie 값이나 Protocol 내용을 분석할 수 없도록 반드시 https 통신으로 해야한다.
5. 그럼에도 불구하고 공격을 수행하려면...
   - 프로토콜을 잘 알고 있는 공격자가 
   - 위조 요청을 수행하는(ajax 등으로) 악성 스크립트를 만들고
   - 공격대상 사용자가 자신의 PC에서 브라우저를 실행하여 인증을 받은 상태에서 
   - JWT가 만료되기 전에 공격대상 사용자가 악성 스크립트를 실행하도록 유도하면 공격 가능함. 
6. 실제로는 공격자가 프로토콜을 알 수도 없고 악성 스크립트를 "사용자가 인증 받은 상태에서 실행하도록 유도"하는 것도 어려운 일이다.
