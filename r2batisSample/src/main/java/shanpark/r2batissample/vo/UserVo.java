package shanpark.r2batissample.vo;

import lombok.Data;

import java.util.Date;

@Data
public class UserVo {
    private Long id;
    private String name;
    private String username;
    private String email;
    private String password;
    private String address;
    private String phone;
    private String website;
    private String company;
    private Date inserted1;
    private Date inserted2;
    private Date inserted3;
    private Date inserted4;
    private String twoWord;
}
