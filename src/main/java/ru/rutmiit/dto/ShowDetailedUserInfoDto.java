package ru.rutmiit.dto;

import java.util.List;

public class ShowDetailedUserInfoDto {
    private Long id;
    private String email;
    private String name;
    private String phone;
    private List<ShowOrderInfoDto> orders;


    public ShowDetailedUserInfoDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public List<ShowOrderInfoDto> getOrders() { return orders; }
    public void setOrders(List<ShowOrderInfoDto> orders) { this.orders = orders; }
}