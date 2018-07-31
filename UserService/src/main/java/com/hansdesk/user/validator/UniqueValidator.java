package com.hansdesk.user.validator;

import com.hansdesk.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class UniqueValidator implements ConstraintValidator<Unique, String> {

    @Autowired
    UserMapper userMapper;

    private String table;
    private String column;

    @Override
    public void initialize(Unique constraintAnnotation) {
        table = constraintAnnotation.table();
        column = constraintAnnotation.column();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return (userMapper.getCount(table, column, value) <= 0);
    }
}
