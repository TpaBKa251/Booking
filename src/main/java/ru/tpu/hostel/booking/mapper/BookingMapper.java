package ru.tpu.hostel.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.tpu.hostel.booking.dto.response.BookingResponse;
import ru.tpu.hostel.booking.dto.response.BookingResponseWithUser;
import ru.tpu.hostel.booking.entity.Booking;

/**
 * Маппер для броней
 */
@Mapper(componentModel = "spring")
public interface BookingMapper {

    /**
     * Маппит сущность брони в ДТО для ответа
     *
     * @param booking бронь
     * @return ДТО для ответа
     */
    @Mapping(target = "type", expression = "java(booking.getType().getBookingTypeName())")
    BookingResponse mapToBookingResponse(Booking booking);

    /**
     * Маппит сущность брони в ДТО с ID юзера
     *
     * @param booking бронь
     * @return ДТО с ID юзера
     */
    @Mapping(target = "type", expression = "java(booking.getType().getBookingTypeName())")
    @Mapping(target = "userId", source = "user")
    BookingResponseWithUser mapToBookingResponseWithUser(Booking booking);

}
