package ru.tpu.hostel.booking.service.access;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface CheckOwner {

    /**
     * Метод, по которому можно получить владельца
     */
    String method();
}
