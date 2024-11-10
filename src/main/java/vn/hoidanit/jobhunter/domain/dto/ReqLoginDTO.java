package vn.hoidanit.jobhunter.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqLoginDTO {

    @NotBlank(message = "Không để trống username")
    private String username;

    @NotBlank(message = "Không để trống password")
    private String password;
}
