### Vue CLI 설치
Vie CLI는 아래 명령으로 설치 가능하다. 
<pre>$ sudo npm install -g @vue/cli</pre>

### Project 생성
생성된 Spring 프로젝트의 root로 이동하여 Vue.JS 프로젝트 생성 명령을 실행하도록 한다.
<pre>$ cd &lt;project root&gt;
$ vue create vuejs</pre>
Spring 프로젝트의 src폴더와 같은 레벨에 생성해야 한다. (src 폴더 안에 생성하면 안된다.)

### Vue Project의 output 경로 설정.
위 명령으로 생성된 vuejs 폴더로 들어가서 `vue.config.js` 파일을 만들어서 아래 내용을 넣어준다.
<pre>// vue.config.js
const path = require('path');

module.exports = {
    outputDir: path.resolve(__dirname, "../src/main/resources/static")
}
</pre>
기본적으로 `/dist`가 기본 output 폴더지만 `Spring 프로젝트의 정적 리소스 파일 경로`로 바꿔주는 것이다.

### Vue.JS 빌드
아래 명령으로 vuejs 폴더에 들어가서 아래 명령을 실행하면 vuejs 프로젝트만 빌드를 수행하고 위에서 설정한 경로에 결과를 출력한다.
<pre>$ cd vuejs
$ npm run build
</pre> 