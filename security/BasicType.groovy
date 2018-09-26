package org.piercecountywa.pac.security

enum BasicType {
    BOOLEAN("Boolean", "Represents one of three values: true, false or null"),
    DATE("Date", "Used to represent dates."),
    DATETIME("DateTime", "Used to represent dates and times."),
    STRING("String", "A sequence of characters."),
    INTEGER("Integer", "A whole number."),
    LONG("Long", "A 64-bit signed two's complement integer."),
    DOUBLE("Double", "A double."),
    BYTE("Byte", "A byte.")

    final String name
    final String description

    BasicType(String name, String description) {
        this.name = name
        this.description = description
    }

    static BasicType getByName(String name) {
        for(BasicType type : values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return type
            }
        }
        return null
    }

    static boolean isBasicType(String name) {
        for(BasicType type : values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return true
            }
        }
        return false
    }
}
