package shanpark.r2batissample.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import shanpark.r2batissample.mapper.UserMapper;
import shanpark.r2batissample.vo.UserVo;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Transactional(transactionManager = "oracleTM") // TransactionManager가 2개 이상이면 반드시 지정해줘야 한다.
//    @Transactional(transactionManager = "mysqlTM")
//    @Transactional(transactionManager = "mariadbTM")
//    @Transactional // TransactionManager가 1개라면 지정하지 않아도 된다.
    public Mono<Void> insertUniqUsers() {
        UserVo userVo1 = new UserVo();
        userVo1.setName("Uniq_Name 1");
        userVo1.setUsername("Uniq_Username_1" + System.currentTimeMillis());
        userVo1.setEmail("Uniq_Email_1@iotree.co.kr." + System.currentTimeMillis());
        UserVo userVo2 = new UserVo();
        userVo2.setName("Uniq_Name 2");
        userVo2.setUsername("Uniq_Username_2" + System.currentTimeMillis());
        userVo2.setEmail("Uniq_Email_2@iotree.co.kr." + System.currentTimeMillis());

        List<UserVo> userList = new ArrayList<>();
        userList.add(userVo1);
        userList.add(userVo2);

        return userMapper.insertUserList(userList)
                .then(Mono.error(new RuntimeException("Just error"))); // Transaction 테스트를 위해 에러발생
    }
}
