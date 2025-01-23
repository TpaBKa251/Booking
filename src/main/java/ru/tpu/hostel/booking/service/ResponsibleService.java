package ru.tpu.hostel.booking.service;

import ru.tpu.hostel.booking.dto.request.ResponsibleSetDto;
import ru.tpu.hostel.booking.dto.response.ResponsibleResponseDto;
import ru.tpu.hostel.booking.dto.response.ResponsibleResponseWithNameDto;
import ru.tpu.hostel.booking.enums.BookingType;

import java.time.LocalDate;

public interface ResponsibleService {

    ResponsibleResponseDto setResponsible(ResponsibleSetDto responsibleSetDto);

    ResponsibleResponseWithNameDto getResponsible(LocalDate date, BookingType type);
}
