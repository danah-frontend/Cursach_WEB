package ru.rutmiit.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class UserRegistrationDto {

    @NotEmpty(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    private String email;

    @NotEmpty(message = "Пароль не может быть пустым")
    @Size(min = 6, message = "Пароль должен содержать минимум 6 символов")
    private String password;

    @NotEmpty(message = "Подтверждение пароля не может быть пустым")
    private String confirmPassword;

    @NotEmpty(message = "Имя не может быть пустым")
    private String name;

    private String phone;

    public UserRegistrationDto() {}

    public UserRegistrationDto(String email, String password, String confirmPassword, String name, String phone) {
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.name = name;
        this.phone = phone;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}