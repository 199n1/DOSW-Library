package edu.eci.dosw.tdd.core.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ValidationUtil {
    public boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}