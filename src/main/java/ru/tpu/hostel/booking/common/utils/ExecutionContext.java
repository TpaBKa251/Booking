package ru.tpu.hostel.booking.common.utils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.tpu.hostel.booking.service.access.Roles;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExecutionContext {

    private static final ThreadLocal<ExecutionContext> CONTEXT_HOLDER = new ThreadLocal<>();

    @Getter
    private UUID userID;

    private Set<Roles> userRoles;

    @Getter
    private String traceId;

    @Getter
    private String spanId;

    public static ExecutionContext create() {
        if (CONTEXT_HOLDER.get() != null) {
            return CONTEXT_HOLDER.get();
        }
        ExecutionContext context = new ExecutionContext();
        CONTEXT_HOLDER.set(context);
        return context;
    }

    public static ExecutionContext create(UUID userID, Set<Roles> userRoles, String traceId, String spanId) {
        if (CONTEXT_HOLDER.get() != null) {
            return CONTEXT_HOLDER.get();
        }
        ExecutionContext context = new ExecutionContext(userID, userRoles, traceId, spanId);
        CONTEXT_HOLDER.set(context);
        return context;
    }

    public static ExecutionContext get() {
        return CONTEXT_HOLDER.get();
    }

    public static void clear() {
        CONTEXT_HOLDER.remove();
    }

    public Set<Roles> getUserRoles() {
        return this.userRoles == null
                ? Collections.emptySet()
                : Collections.unmodifiableSet(this.userRoles);
    }

}
