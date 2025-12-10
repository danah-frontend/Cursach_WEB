package ru.rutmiit.models.entities;

import jakarta.persistence.*;
import ru.rutmiit.models.enums.UserRoles;

@Entity
@Table(name = "roles")
public class Role extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false, length = 20)
    private UserRoles name;

    public Role() {}

    public Role(UserRoles name) {
        this.name = name;
    }

    public UserRoles getName() { return name; }
    public void setName(UserRoles name) { this.name = name; }
}