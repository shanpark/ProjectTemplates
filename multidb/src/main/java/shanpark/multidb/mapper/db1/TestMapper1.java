package shanpark.multidb.mapper.db1;

import shanpark.multidb.vo.TestVo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TestMapper1 {
    TestVo getDate1();
}
