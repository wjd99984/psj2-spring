package com.prj2spring.domain.member;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class Member {
    private Integer id;
    private String email;
    private String password;
    private String nickName;
    private LocalDateTime inserted;

    public String getSignupDateAndTime() {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy년MM월dd일 HH시mm분ss초");
        return inserted.format(formatter);
    }
}
