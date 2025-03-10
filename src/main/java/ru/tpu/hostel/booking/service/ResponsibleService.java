package ru.tpu.hostel.booking.service;

import ru.tpu.hostel.booking.dto.request.ResponsibleSetRequest;
import ru.tpu.hostel.booking.dto.response.ResponsibleResponse;
import ru.tpu.hostel.booking.dto.response.ResponsibleResponseWithName;
import ru.tpu.hostel.booking.enums.BookingType;

import java.time.LocalDate;

public interface ResponsibleService {

    ResponsibleResponse setResponsible(ResponsibleSetRequest responsibleSetDto);

    ResponsibleResponseWithName getResponsible(LocalDate date, BookingType type);
}
