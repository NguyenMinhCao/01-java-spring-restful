package vn.hoidanit.jobhunter.domain.response.email;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResEmailJob {
    private String name;
    private double salary;
    private CompanyEmail company;
    private List<SkillEmail> skills;

    @Setter
    @Getter
    @AllArgsConstructor
    public static class CompanyEmail {
        private String name;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    public static class SkillEmail {
        private String name;
    }
}
