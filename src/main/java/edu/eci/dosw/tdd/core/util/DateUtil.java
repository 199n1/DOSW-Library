package edu.eci.dosw.tdd.core.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;

@UtilityClass
public class DateUtil {
    public LocalDate getCurrentDate() {
        return LocalDate.now();
    }
}