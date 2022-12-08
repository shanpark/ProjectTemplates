package shanpark.multidb;

import shanpark.multidb.mapper.db1.TestMapper1;
import shanpark.multidb.mapper.db2.TestMapper2;
import shanpark.multidb.vo.TestVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Main implements CommandLineRunner {

    @Autowired
    TestMapper1 testMapper1;
    @Autowired
    TestMapper2 testMapper2;

    @Override
    public void run(String... args) {
        TestVo date1 = testMapper1.getDate1();
        TestVo date2 = testMapper2.getDate2();

        log.info("date1: {}, {}", date1.getDummyDate(), date1.getDummyData());
        log.info("date2: {}, {}", date2.getDummyDate(), date2.getDummyData());
    }
}
