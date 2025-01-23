package ru.tpu.hostel.booking.mapper;

import org.springframework.stereotype.Component;
import ru.tpu.hostel.booking.dto.response.ResponsibleResponseDto;
import ru.tpu.hostel.booking.dto.response.ResponsibleResponseWithNameDto;
import ru.tpu.hostel.booking.entity.Responsible;

@Component
public class ResponsibleMapper {

    public static ResponsibleResponseDto mapToResponsibleResponseDto(Responsible responsible) {
        return new ResponsibleResponseDto(
                responsible.getDate(),
                responsible.getType(),
                responsible.getUser()
        );
    }

    public static ResponsibleResponseWithNameDto mapToResponsibleResponseWithNameDto(
            String firstName,
            String lastName,
            String middleName
    ) {
        return new ResponsibleResponseWithNameDto(firstName, lastName, middleName);
    }
}
