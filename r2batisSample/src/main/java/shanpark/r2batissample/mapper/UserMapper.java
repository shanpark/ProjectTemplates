package shanpark.r2batissample.mapper;

import io.github.shanpark.r2batis.annotation.R2batisMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import shanpark.r2batissample.vo.UserVo;

import java.util.List;

@R2batisMapper(connectionFactory = "oracle") // ConnectionFactory가 2개 이상이라면 이 인터페이스에서 사용할 ConnectionFactory bean의 name을 지정한다.
//@R2batisMapper(connectionFactory = "mysql")
//@R2batisMapper(connectionFactory = "mariadb")
//@R2batisMapper // connectionFactory가 1개라면 지정하지 않아도 된다.
public interface UserMapper {
    Mono<Long> getUserCount();
    Mono<Long> getUserCountWithNameStarting(String prefix);
    Mono<UserVo> getUser(long userId);
    Mono<UserVo> getUserById(long userId);
    Flux<UserVo> getUserListByIds(List<Long> userIds);
    Flux<UserVo> getAllUserList();
    Flux<UserVo> getUserWithNameStarting(String prefix);
    Mono<Long> insertUserAndGetKeyAndInsertTime(UserVo userVo);
    Mono<Long> insertUserList(List<UserVo> userList);
    Mono<Long> updateUserAddressWithNameStarting(String prefix, String address, UserVo userVo);
    Mono<Long> insertUserForSpecialCase(String name, String username, String email, List<String> passwordList, List<String> addressList);
    Flux<UserVo> getUserListForSpecialCase(List<Long> oneList, List<Long> manyList);
    Mono<Long> deleteUserWithNameStarting(String prefix);
}
