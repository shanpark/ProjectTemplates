package com.hansdesk.user.repository;

import com.hansdesk.user.domain.SignUpUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SignUpUserRepository extends JpaRepository<SignUpUser, String> {
}
