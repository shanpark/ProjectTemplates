package shanpark.multidb.mapper.db2;

import shanpark.multidb.vo.TestVo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TestMapper2 {
    TestVo getDate2();
}
