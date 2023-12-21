package shanpark.r2batissample;

import io.github.shanpark.r2batis.R2batisAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;
import shanpark.r2batissample.mapper.UserMapper;
import shanpark.r2batissample.service.UserService;
import shanpark.r2batissample.vo.UserVo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SpringBootTest
class R2batisSampleApplicationTests {

	private final Logger log = LoggerFactory.getLogger("ApplicationTests");

	// 이 Test 클래스를 클래스 이름 옆의 삼각형 버튼(run)으로 그냥 실행할 때 R2batis가 정상 동작하려면 아래 설정이 필요하다.
	// Gradle의 bootTestRun으로는 R2batisMapper가 주입되지 않는다. 따라서 이 Test 클래스를 직접 실행한다.
	static {
		R2batisAutoConfiguration.isTesting = true;
	}

	@Autowired
	private UserMapper userMapper;
	@Autowired
	private UserService userService;

	@Test
	void selectTest() {
		Long totalUserCount = userMapper.getUserCount()
				.block();
		Assert.isTrue((totalUserCount != null), "totalUserCount는 not null 이어야 한다.");
		log.info("getUserCount: OK");

		Long userCount = userMapper.getUserCountWithNameStarting(null)
				.block();
		Assert.isTrue((userCount != null) && (userCount.equals(totalUserCount)), "prefix가 null이면 전체 사용자 수를 반환해야 한다.");
		log.info("getUserCountWithNameStarting(null): OK");

		UserVo userVo = userMapper.getUser(21L)
				.block();
		Assert.isTrue((userVo != null) && (userVo.getId() == 21L) && (userVo.getName().equals("박성한")), "'id: 21' 인 사용자는 '박성한'이어야 함.");
		log.info("getUser(21L): OK");

		List<Long> userIds = List.of(21L, 22L, 23L, 24L, 25L, 26L, 27L, 28L, 29L, 30L);
		List<UserVo> userList = userMapper.getUserListByIds(userIds)
				.collectList()
				.block();
		Assert.isTrue((userList != null) && (userList.size() == 6) && (userList.get(0).getId() == 21L) && (userList.get(0).getName().equals("박성한")), "6명 조회되어야 하고 첫 번째 사용자는 '박성한'이어야 함.");
		log.info("getUserListByIds(21L, 22L, 23L, 24L, 25L, 26L, 27L, 28L, 29L, 30L): OK");

		userList = userMapper.getAllUserList()
				.collectList()
				.block();
		Assert.isTrue((userList != null) && (userList.size() == totalUserCount), "모든 사용자를 조회한 결과가 총 사용자 숫자만큼 나오지 않음.");
		log.info("getAllUserList: OK");

		userVo = userMapper.getUserById(21)
				.block();
		Assert.isTrue((userVo != null) && userVo.getName().equals("박성한"), "'id: 21' 인 사용자는 '박성한'이어야 함.");
		userVo = userMapper.getUserById(50)
				.block();
		Assert.isTrue((userVo != null) && userVo.getId().equals(30L), "'id: 50'으로 조회해도 30이 나와야 함.");
		log.info("getUserById: OK");
	}

	@Test
	void modifyTest() {
		Long deleted;

		UserVo userVo1 = new UserVo();
		userVo1.setName("Uniq_Name 1");
		userVo1.setUsername("Uniq_Username_1" + System.currentTimeMillis());
		userVo1.setEmail("Uniq_Email_1@iotree.co.kr." + System.currentTimeMillis());
		UserVo userVo2 = new UserVo();
		userVo2.setName("Uniq_Name 2");
		userVo2.setUsername("Uniq_Username_2" + System.currentTimeMillis());
		userVo2.setEmail("Uniq_Email_2@iotree.co.kr." + System.currentTimeMillis());

		// 최초 초기화 삭제
		deleted = userMapper.deleteUserWithNameStarting("Uniq_Name")
				.block();
		Assert.isTrue(deleted != null, "삭제가 되었든 안되었든 값은 null이면 안됨.");
		log.info("deleteUserWithNameStarting #1: OK");

		// 키값 가져오고 selectKey 테스트.
		Long userId = userMapper.insertUserAndGetKeyAndInsertTime(userVo1)
				.block();
		Assert.isTrue((userId != null) && (userId > 1), "생성된 id(키)는 2 이상이어야 함.");
		Assert.isTrue((userVo1.getInserted1() != null) && (userVo1.getInserted2() != null) && (userVo1.getInserted3() != null) && (userVo1.getInserted4() != null) , "inserted 필드에 값이 채워져야 함.");
		Assert.isTrue((userVo1.getInserted1().getTime() + 1000L) == userVo1.getInserted2().getTime(), "inserted1과 inserted2는 1초 차이어야 함.");
		Assert.isTrue((userVo1.getInserted2().getTime() + 1000L) == userVo1.getInserted3().getTime(), "inserted2과 inserted3는 1초 차이어야 함.");
		Assert.isTrue((userVo1.getInserted3().getTime() + 1000L) == userVo1.getInserted4().getTime(), "inserted3과 inserted4는 1초 차이어야 함.");

		log.info("insertUserAndGetKeyAndInsertTime: OK");

		deleted = userMapper.deleteUserWithNameStarting("Uniq_Name")
				.block();
		Assert.isTrue((deleted != null) && (deleted == 1), "반드시 1건 삭제되어야 함.");
		log.info("deleteUserWithNameStarting #2: OK");

		List<UserVo> userList = new ArrayList<>();
		userList.add(userVo1);
		userList.add(userVo2);

		Long inserted = userMapper.insertUserList(userList)
				.block();
		Assert.isTrue((inserted != null) && (inserted == 2), "반드시 2건 등록되어야 함.");
		log.info("insertUserList: OK");

		Long updated = userMapper.updateUserAddressWithNameStarting("Uniq_Name", "some address", userVo2)
				.block();
		Assert.isTrue((updated != null) && (updated == 2), "반드시 2건 업데이트되어야 함.");
		Assert.isTrue((userVo2.getInserted1() != null) && (userVo2.getInserted2() != null), "inserted 필드에 값이 채워져야 함.");
		Assert.isTrue((userVo2.getInserted1().getTime() + 1000L) == userVo2.getInserted2().getTime() , "inserted1과 inserted2는 1초 차이어야 함.");

		userList = userMapper.getUserWithNameStarting("Uniq_Name")
				.collectList()
				.block();
		Assert.isTrue(
				(userList != null) && (userList.size() == 2) && (userList.get(0).getAddress().equals("some address"))&& (userList.get(1).getAddress().equals("some address")),
				"수정된 주소는 'some address' 이어야 함."
		);
		log.info("updateUserAddressWithNameStarting: OK");

		deleted = userMapper.deleteUserWithNameStarting("Uniq_Name")
				.block();
		Assert.isTrue((deleted != null) && (deleted == 2), "반드시 2건 삭제되어야 함.");
		log.info("deleteUserWithNameStarting #3: OK");
	}

	@Test
	void specialCaseTest() {
		// 최초 초기화 삭제
		Long deleted = userMapper.deleteUserWithNameStarting("Uniq_Name")
				.block();
		Assert.isTrue(deleted != null, "삭제가 되었든 안되었든 값은 null이면 안됨.");
		log.info("deleteUserWithNameStarting #4: OK");

		List<String> passwordList = List.of("Password #1", "Password #2");
		List<String> addressList = List.of("Address #1", "Address #2");
		Long inserted = userMapper.insertUserForSpecialCase("Uniq_Name", "Uniq_Username", "Uniq_Email", passwordList, addressList)
				.block();
		Assert.isTrue((inserted != null) && (inserted == 4), "반드시 4건 등록되어야 함.");

		List<UserVo> userList = userMapper.getUserWithNameStarting("Uniq_Name_0_0")
				.collectList()
				.block();
		Assert.isTrue(
				(userList != null) &&
						userList.get(0).getPassword().equals("Password #1") &&
						userList.get(0).getAddress().equals("Address #1"), "'Uniq_Name_0_0' 사용자는 'Password #1', 'Address #1' 이어야 함."
		);
		userList = userMapper.getUserWithNameStarting("Uniq_Name_0_1")
				.collectList()
				.block();
		Assert.isTrue(
				(userList != null) &&
						userList.get(0).getPassword().equals("Password #1") &&
						userList.get(0).getAddress().equals("Address #2"), "'Uniq_Name_0_0' 사용자는 'Password #1', 'Address #2' 이어야 함."
		);
		userList = userMapper.getUserWithNameStarting("Uniq_Name_1_0")
				.collectList()
				.block();
		Assert.isTrue(
				(userList != null) &&
						userList.get(0).getPassword().equals("Password #2") &&
						userList.get(0).getAddress().equals("Address #1"), "'Uniq_Name_0_0' 사용자는 'Password #2', 'Address #1' 이어야 함."
		);
		userList = userMapper.getUserWithNameStarting("Uniq_Name_1_1")
				.collectList()
				.block();
		Assert.isTrue(
				(userList != null) &&
						userList.get(0).getPassword().equals("Password #2") &&
						userList.get(0).getAddress().equals("Address #2"), "'Uniq_Name_0_0' 사용자는 'Password #2', 'Address #2' 이어야 함."
		);
		log.info("insertUserForSpecialCase: OK");

		deleted = userMapper.deleteUserWithNameStarting("Uniq_Name")
				.block();
		Assert.isTrue((deleted != null) && (deleted == 4), "반드시 4건 삭제되어야 함.");
		log.info("deleteUserWithNameStarting #5: OK");
	}

	@Test
	void specialCaseTest2() {
		List<Long> oneList = List.of(21L);
		List<Long> manyList = List.of(22L, 23L, 24L, 25L, 26L, 27L, 28L, 29L, 30L);
		List<UserVo> userList = userMapper.getUserListForSpecialCase(oneList, manyList)
				.collectList()
				.block();
		Assert.isTrue((userList != null) && (userList.size() == 6) && (userList.get(0).getId() == 21L) && (userList.get(0).getName().equals("박성한")), "6명 조회되어야 하고 첫 번째 사용자는 '박성한'이어야 함.");
		log.info("getUserListForSpecialCase([21L], [22L, 23L, 24L, 25L, 26L, 27L, 28L, 29L, 30L]): OK");
	}

	@Test
	void transactionTest() {
		Long totalUserCount = userMapper.getUserCount()
				.block();

		userService.insertUniqUsers()
				.onErrorResume(err -> Mono.empty())
				.block();

		Long newUserCount = userMapper.getUserCount()
				.block();
		Assert.isTrue(Objects.equals(totalUserCount, newUserCount), "insertUniqUsers() 전 후의 반환값이 같아야 한다.");
		log.info("Transaction Test: OK");
	}
}
