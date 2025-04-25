package ru.tpu.hostel.booking.service.access.role;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RoleCheckStrategy {

    /**
     * Проверка на основе того, что одна строка содержит в себе другую
     */
    CONTAINS("RESPONSIBLE_"),

    /**
     * Проверка на основе равенства
     */
    EQUALS("");

    private final String prefix;
}
