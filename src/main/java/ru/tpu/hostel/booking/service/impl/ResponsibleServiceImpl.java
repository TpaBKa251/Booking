package ru.tpu.hostel.booking.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tpu.hostel.booking.client.UserServiceClient;
import ru.tpu.hostel.booking.dto.request.ResponsibleSetDto;
import ru.tpu.hostel.booking.dto.response.ResponsibleResponseDto;
import ru.tpu.hostel.booking.dto.response.ResponsibleResponseWithNameDto;
import ru.tpu.hostel.booking.dto.response.UserShortResponseDto2;
import ru.tpu.hostel.booking.entity.Responsible;
import ru.tpu.hostel.booking.enums.BookingType;
import ru.tpu.hostel.booking.exception.ResponsibleNotFoundException;
import ru.tpu.hostel.booking.mapper.ResponsibleMapper;
import ru.tpu.hostel.booking.repository.ResponsibleRepository;
import ru.tpu.hostel.booking.repository.TimeSlotRepository;
import ru.tpu.hostel.booking.service.ResponsibleService;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResponsibleServiceImpl implements ResponsibleService {

    private final ResponsibleRepository responsibleRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final UserServiceClient userServiceClient;

    @Override
    public ResponsibleResponseDto setResponsible(ResponsibleSetDto responsibleSetDto) {
        Responsible responsible = responsibleRepository
                .findByTypeAndDate(responsibleSetDto.type(), responsibleSetDto.date())
                .orElse(null);

        if (responsible == null) {

            if (timeSlotRepository.findOneByTypeAndStartTimeOnSpecificDay(
                    responsibleSetDto.type(),
                    responsibleSetDto.date().atTime(0, 0),
                    responsibleSetDto.date().atTime(23, 59)
            ).isPresent()) {
                responsible = new Responsible();
                responsible.setType(responsibleSetDto.type());
                responsible.setDate(responsibleSetDto.date());
            } else {
                throw new ResponsibleNotFoundException();
            }
        }

        List<String> roles = userServiceClient.getAllRolesByUserId(responsibleSetDto.user());

        for (String role : roles) {
            if (role.contains(responsibleSetDto.type().toString())) {
                responsible.setUser(responsibleSetDto.user());
                responsibleRepository.save(responsible);

                return ResponsibleMapper.mapToResponsibleResponseDto(responsible);
            }
        }

        throw new ResponsibleNotFoundException();
    }

    @Override
    public ResponsibleResponseWithNameDto getResponsible(LocalDate date, BookingType type) {
        if (type == BookingType.KITCHEN) {
            return new ResponsibleResponseWithNameDto(
                    "Валерий",
                    "Жмышенко",
                    "Альбертович"
            );
        }
        Responsible responsible = responsibleRepository.findByTypeAndDate(type, date).orElseThrow(
                ResponsibleNotFoundException::new
        );

        if (responsible.getUser() == null) {
            return new ResponsibleResponseWithNameDto("", "", "");
        }

        UserShortResponseDto2 user = userServiceClient.getUserByIdShort(responsible.getUser());

        return new ResponsibleResponseWithNameDto(user.firstName(), user.lastName(), user.middleName());
    }
}