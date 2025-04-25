package ru.tpu.hostel.booking.service.access.role;

import ru.tpu.hostel.booking.service.access.Roles;

public interface RoleChecker {

    boolean isRolePermitted(Roles[] userRoles, Roles neededRole, Roles ... permittedRoles);

    boolean isApplicable(RoleCheckStrategy strategy);

}
