package ru.rutmiit.models.enums;

public enum UserRoles {
    USER("User"),
    ADMIN("Admin"),
    MODERATOR("Moderator");

    private final String displayName;

    UserRoles(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}