package ru.tpu.hostel.booking.service.access.role.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;
import ru.tpu.hostel.booking.service.access.Roles;
import ru.tpu.hostel.booking.service.access.role.RoleCheckStrategy;
import ru.tpu.hostel.booking.service.access.role.RoleChecker;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ContainsRoleChecker implements RoleChecker {

    @Override
    public boolean isRolePermitted(Roles[] userRoles, Roles neededRole, Roles ... permittedRoles) {
        userRoles = (Roles[]) ArrayUtils.nullToEmpty(userRoles);
        permittedRoles = (Roles[]) ArrayUtils.nullToEmpty(permittedRoles);

        Set<Roles> permittedRolesSet = new HashSet<>(Arrays.asList(permittedRoles));

        return Arrays.stream(userRoles)
                .anyMatch(userRole -> userRole.name().contains(neededRole.name()) || permittedRolesSet.contains(userRole));
    }

    @Override
    public boolean isApplicable(RoleCheckStrategy strategy) {
        return strategy == RoleCheckStrategy.CONTAINS;
    }

}
