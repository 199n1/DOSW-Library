package edu.eci.dosw.tdd.core.util;

import lombok.experimental.UtilityClass;
import java.util.UUID;

@UtilityClass
public class IdGeneratorUtil {
    public String generateId() {
        return UUID.randomUUID().toString();
    }
}