package vn.hoidanit.jobhunter.domain.dto;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestCreateUserDTO {
    private Long id;
    private String name;
    private String email;
    private int age;
    private String gender;
    private String address;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant createdAt;
}
