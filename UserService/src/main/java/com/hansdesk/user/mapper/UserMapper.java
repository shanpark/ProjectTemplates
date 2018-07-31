package com.hansdesk.user.mapper;

import com.hansdesk.user.constant.Role;
import com.hansdesk.user.dto.SignUpForm;
import com.hansdesk.user.vo.SignUpUserVO;
import com.hansdesk.user.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    int registerSignUpUser(SignUpForm signUpForm);
    SignUpUserVO getSignUpUser(String email);
    int deleteSignUpUser(String email);

    int registerUser(UserVO userVO);
    UserVO getUserByEmail(String email);
    List<String> getUserRoleByUid(Long uid);

    int addUserRole(Long uid, Role role);

    int getCount(String table, String column, String value);
}
