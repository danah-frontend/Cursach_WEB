package ru.rutmiit.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class CheckoutDto {

    @NotEmpty(message = "Адрес доставки не может быть пустым")
    private String address;

    @NotEmpty(message = "Телефон не может быть пустым")
    private String phone;

    private String comment;

    public CheckoutDto() {}

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
