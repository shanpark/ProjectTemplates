package com.hansdesk.rest.mapper;

import com.hansdesk.rest.vo.UserVO;
//import org.apache.ibatis.annotations.Mapper;

import java.util.List;

//@Mapper
public interface UserMapper {
    UserVO getUserByEmail(String email);
    List<String> getUserRoleByUid(Long uid);
}
