package ru.tpu.hostel.booking.service.access.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tpu.hostel.booking.common.exception.ServiceException;
import ru.tpu.hostel.booking.service.access.AccessChecker;
import ru.tpu.hostel.booking.service.access.CheckOwner;
import ru.tpu.hostel.booking.service.access.CheckRoles;
import ru.tpu.hostel.booking.service.access.Roles;
import ru.tpu.hostel.booking.service.access.role.RoleChecker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

/**
 * Реализация {@link AccessChecker} для полной проверки прав юзера
 */
@Service
@RequiredArgsConstructor
public class FullAccessChecker implements AccessChecker {

    private final Set<RoleChecker> roleCheckers;

    @Override
    public void check(String action, Object entity, Object sendingUserId, Roles[] userRoles, String reason) {
        Class<?> entityClass = entity.getClass();

        CheckOwner checkOwner = entityClass.getAnnotation(CheckOwner.class);
        CheckRoles checkRoles = entityClass.getAnnotation(CheckRoles.class);
        if (checkOwner == null && checkRoles == null) {
            return;
        }

        if (checkOwner != null && isOwnerEqualSendingUser(entity, sendingUserId, checkOwner)) {
            return;
        }

        if (checkRoles != null) {
            CheckRoles.Action checkRolesAction = Arrays.stream(checkRoles.value())
                    .filter(a -> a.name().equals(action))
                    .findFirst()
                    .orElse(null);

            if (checkRolesAction != null && isRolesPermitted(entity, userRoles, checkRolesAction)) {
                return;
            } else if (checkRolesAction == null) {
                throw new ServiceException.InternalServerError("Разраб мудак, где действие");
            }
        }

        throw new ServiceException.Forbidden(reason);
    }

    private boolean isOwnerEqualSendingUser(Object entity, Object sendingUserId, CheckOwner checkOwner) {
        try {
            Object ownerId = getFieldValue(entity, checkOwner.method());
            return sendingUserId.equals(ownerId);
        } catch (Exception e) {
            throw new ServiceException.InternalServerError("Ошибка проверки юзера", e);
        }
    }

    private boolean isRolesPermitted(Object entity, Roles[] userRoles, CheckRoles.Action checkRolesAction) {
        RoleChecker roleChecker = roleCheckers.stream()
                .filter(checker -> checker.isApplicable(checkRolesAction.strategy()))
                .findFirst()
                .orElseThrow(() -> new ServiceException.InternalServerError(
                        "Не найдена стратегия проверки роли для " + checkRolesAction.strategy()
                ));

        try {
            Roles role = Roles.valueOf(checkRolesAction.strategy().getPrefix() + ("".equals(checkRolesAction.method())
                    ? ""
                    : getFieldValue(entity, checkRolesAction.method()).toString()));
            return roleChecker.isRolePermitted(userRoles, role, checkRolesAction.permissions());
        } catch (Exception e) {
            throw new ServiceException.InternalServerError("Ошибка проверки роли", e);
        }
    }

    private Object getFieldValue(Object entity, String methodName)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method getter = entity.getClass().getMethod(methodName);
        return getter.invoke(entity);
    }
}
