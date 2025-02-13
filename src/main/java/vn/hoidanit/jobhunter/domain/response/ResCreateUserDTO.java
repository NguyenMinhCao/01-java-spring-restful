package vn.hoidanit.jobhunter.domain.response;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class ResCreateUserDTO {
    private Long id;
    private String name;
    private String email;
    private int age;
    private String gender;
    private String address;
    // @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant createdAt;
    private CompanyUser company;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompanyUser {
        private Long id;
        private String name;
    }
}
