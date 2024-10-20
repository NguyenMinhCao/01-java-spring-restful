package vn.hoidanit.jobhunter.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class LoginDTO {

    @NotBlank(message = "Không để trống username")
    private String username;

    @NotBlank(message = "Không để trống password")
    private String password;
}
