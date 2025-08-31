package dev.stanislavskyi.feedback_bot_vgr.telegram;

import lombok.Getter;

@Getter
public enum RoleTelegramUser {
    MECHANIC("Механік"),
    ELECTRICIAN("Електрик"),
    MANAGER("Менеджер");

    private final String displayName;

    RoleTelegramUser(String displayName) {
        this.displayName = displayName;
    }

    public static boolean isValid(String value) {
        for (RoleTelegramUser r : values()) {
            if (r.name().equalsIgnoreCase(value)) return true;
        }
        return false;
    }
    public static RoleTelegramUser fromString(String value) {
        if (value == null) return null;
        for (RoleTelegramUser r : values()) {
            if (r.name().equalsIgnoreCase(value)) {
                return r;
            }
        }
        return null;
    }
}
