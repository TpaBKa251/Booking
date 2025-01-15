package ru.tpu.hostel.booking.service;

import ru.tpu.hostel.booking.dto.request.ResponsibleSetDto;
import ru.tpu.hostel.booking.dto.response.ResponsibleResponseDto;

public interface ResponsibleService {

    ResponsibleResponseDto setResponsible(ResponsibleSetDto responsibleSetDto);
}
