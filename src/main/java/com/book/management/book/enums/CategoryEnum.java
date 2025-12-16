package com.db.ms.book.enums;

import java.util.Locale;

/**
 * Fixed categories with canonical IDs.
 */
public enum CategoryEnum {
    FICTION("CAT-FIC"),
    NON_FICTION("CAT-NF"),
    TECHNOLOGY("CAT-TCH"),
    SCIENCE("CAT-SCI"),
    HISTORY("CAT-HIS"),
    FANTASY("CAT-FAN"),
    BIOGRAPHY("CAT-BIO"),
    BUSINESS("CAT-BUS"),
    OTHER("CAT-OTH");

    private final String id;

    CategoryEnum(String id) { this.id = id; }
    public String getId() { return id; }

    public static CategoryEnum fromId(String id) {
        if (id == null || id.isBlank()) return OTHER;
        String normalized = id.trim().toUpperCase(Locale.ROOT);
        for (CategoryEnum c : values()) if (c.id.equalsIgnoreCase(normalized)) return c;
        return OTHER;
    }

    public static CategoryEnum fromName(String name) {
        if (name == null || name.isBlank()) return OTHER;
        String normalized = name.trim().toUpperCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
        try { return CategoryEnum.valueOf(normalized); } catch (IllegalArgumentException ex) { return OTHER; }
    }
}

