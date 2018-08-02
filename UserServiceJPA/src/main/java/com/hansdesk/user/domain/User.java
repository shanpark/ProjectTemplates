package com.hansdesk.user.domain;

import com.hansdesk.user.constant.Role;
import com.hansdesk.user.constant.UserStatus;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class User {

    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long uid;

    @Column(unique = true, length = 512, nullable = false)
    private String email;

    @Column(length = 130, nullable = false)
    private String password;

    @Column(length = 128, nullable = false)
    private String firstName;

    @Column(length = 128, nullable = false)
    private String lastName;

    @Column(unique = true, length = 128, nullable = false)
    private String nickname;

    @Column(length = 20)
    private String hp;

    @Temporal(TemporalType.TIMESTAMP)
    private Date birth;

    @Column(length = 10, nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    /**
     * insertable이 false인 이유는 최초 생성 시에 modified 필드의 값을 DEFAULT 값인 DB의 현재 시간을 이용하기 위함이다.
     * 다만 update할 떄는 SQL을 실행할 때 처럼 NOW()같은 함수를 지정할 수 없으므로 modified에 DB의 시간을 기록하는 게 복잡하다.
     * 결국 다른 방법으로 DB의 시간을 가져와서 파라메터로 전달해 주던가 WAS서버의 로컬 시간을 전달하는 방법밖에는 없다.
     */
    @Column(columnDefinition = "DATE DEFAULT CURRENT_TIMESTAMP", nullable = false, insertable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    /**
     * insertable이 false인 이유는 최초 생성 시에 created 필드의 값을 DEFAULT 값인 DB의 현재 시간을 이용하기 위함이다.
     * update가 되어서도 안되므로 updatable도 false이어야 한다.
     */
    @Column(columnDefinition = "DATE DEFAULT CURRENT_TIMESTAMP", nullable = false, insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @ElementCollection
    @CollectionTable(name = "UserRole", joinColumns = @JoinColumn(name = "uid", nullable = false))
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<Role> authorities = new HashSet<>();

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHp() {
        return hp;
    }

    public void setHp(String hp) {
        this.hp = hp;
    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public Date getModified() {
        return modified;
    }

    public Date getCreated() {
        return created;
    }

    public Set<Role> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Role> authorities) {
        this.authorities = authorities;
    }
}
