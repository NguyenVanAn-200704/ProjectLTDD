package com.example.project.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserRequest {
    @NotBlank(message = "Email không được để trống !")
    @Email(message = "Email không hợp lệ !")
    private String email;

    @NotBlank(message = "Password không được để trống !")
    @Size(min = 5, max = 25, message = "Password từ 5-25 ký tự !")
    private String password;

    @NotBlank(message = "Name không được để trống !")
    private String name;

    private String avatar;
}
