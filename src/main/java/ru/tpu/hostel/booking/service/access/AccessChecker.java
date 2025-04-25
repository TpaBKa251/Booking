package ru.tpu.hostel.booking.service.access;

public interface AccessChecker {

    <T, I> void check(String action, T entity, I userId, Roles[] userRoles, String reason);
}
