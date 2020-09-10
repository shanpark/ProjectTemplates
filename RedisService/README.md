# RedisService

### Sample for ...
- Spring Boot + Redis(lettuce)<br/>
  1. start.spring.io에서 프로젝트를 생성할 때 Redis dependency를 추가해주면 lettuce를 기본으로 사용한다.
  2. Jackson을 object serializer로 사용하기 위해서 jackson-core, jackson-databind를 dependency로 추가해 주었다. 

### Redis Docker 
참고로 Redis 서버는 docker를 사용하면 간단하게 시작할 수 있다. 작업 폴더에 redis 폴더를 생성하고 아래 명령으로 start, stop 시킬 수 있다.
- Redis start
<pre><code>$ sudo docker run -dp 6379:6379 --rm --name redis -v $PWD/redis:/data redis redis-server --appendonly yes
</code></pre>
- Redis stop
<pre><code>$ sudo docker stop redis
</code></pre>