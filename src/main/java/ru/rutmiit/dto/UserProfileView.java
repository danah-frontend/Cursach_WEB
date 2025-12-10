package ru.rutmiit.dto;

public class UserProfileView {
    private String email;
    private String name;
    private String phone;
    private String role;

    public UserProfileView() {}

    public UserProfileView(String email, String name, String phone, String role) {
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.role = role;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}