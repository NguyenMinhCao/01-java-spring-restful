package vn.hoidanit.jobhunter.domain.response.resume;

import java.time.Instant;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;
import vn.hoidanit.jobhunter.util.constant.StatusResumeEnum;

@Getter
@Setter
public class ResResumeDTO {
    private Long id;

    private String email;

    private String url;

    @Enumerated(EnumType.STRING)
    private StatusResumeEnum status;

    private Instant createdAt;
    private Instant updatedAt;

    private String createBy;
    private String updateBy;

    private String companyName; 
    private UserResume user;
    private JobResume job;

    @Getter
    @Setter
    public static class UserResume {
        private Long id;
        private String name;
    }

    @Getter
    @Setter
    public static class JobResume {
        private Long id;
        private String name;
    }
}
