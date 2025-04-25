package ru.tpu.hostel.booking.service.access;

import ru.tpu.hostel.booking.service.access.role.RoleCheckStrategy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface CheckRoles {

    Action[] value() default {};

    @interface Action {

        /**
         * Метод, по которому можно получить необходимое поле
         */
        String method() default "";

        /**
         * Стратегия, по которой проверяются права (роль). По умолчанию {@link RoleCheckStrategy#EQUALS}
         */
        RoleCheckStrategy strategy() default RoleCheckStrategy.EQUALS;

        /**
         * Список ролей, которым по умолчанию разрешен доступ
         */
        Roles[] permissions() default {};

        String name();

    }

}
