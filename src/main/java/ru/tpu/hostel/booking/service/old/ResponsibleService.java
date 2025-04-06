package ru.tpu.hostel.booking.service.old;

import ru.tpu.hostel.booking.dto.request.ResponsibleSetRequest;
import ru.tpu.hostel.booking.dto.response.ResponsibleResponse;
import ru.tpu.hostel.booking.dto.response.ResponsibleResponseWithName;
import ru.tpu.hostel.booking.entity.BookingType;

import java.time.LocalDate;

@Deprecated(forRemoval = true)
public interface ResponsibleService {

    ResponsibleResponse setResponsible(ResponsibleSetRequest responsibleSetDto);

    ResponsibleResponseWithName getResponsible(LocalDate date, BookingType type);
}
