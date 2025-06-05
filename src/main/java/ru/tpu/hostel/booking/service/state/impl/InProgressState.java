package ru.tpu.hostel.booking.service.state.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tpu.hostel.booking.config.cache.RedissonListCacheManager;
import ru.tpu.hostel.booking.dto.response.BookingResponse;
import ru.tpu.hostel.booking.entity.Booking;
import ru.tpu.hostel.booking.entity.BookingStatus;
import ru.tpu.hostel.booking.service.state.BookingState;
import ru.tpu.hostel.internal.exception.ServiceException;
import ru.tpu.hostel.internal.utils.TimeUtil;

import java.util.UUID;

import static ru.tpu.hostel.booking.config.cache.RedissonCacheConfig.KEY_PATTERN;
import static ru.tpu.hostel.booking.entity.BookingStatus.IN_PROGRESS;

/**
 * Реализация интерфейса {@link BookingState} для состояния "В процессе"
 */
@Service
@RequiredArgsConstructor
public class InProgressState implements BookingState {

    private final RedissonListCacheManager<String, BookingResponse> cacheManager;

    @Override
    public void updateStatus(Booking booking) {
        if (booking.getEndTime().isBefore(TimeUtil.now())) {
            booking.setStatus(BookingStatus.COMPLETED);
            updateCache(booking.getId(), booking.getUser());
        }
    }

    private void updateCache(UUID id, UUID userId) {
        try {
            cacheManager.removeCache(KEY_PATTERN.formatted(userId, IN_PROGRESS), id);
        } catch (Exception ignored) {
            // Ничего не делаем
        }
    }

    @Override
    public void cancelBooking(Booking booking) {
        throw new ServiceException.UnprocessableEntity("Вы не можете отменить уже начатую бронь");
    }

    @Override
    public BookingStatus getStatus() {
        return IN_PROGRESS;
    }
}
