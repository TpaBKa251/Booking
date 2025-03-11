package ru.tpu.hostel.booking.mapper;

import org.springframework.stereotype.Component;
import ru.tpu.hostel.booking.dto.response.ResponsibleResponse;
import ru.tpu.hostel.booking.dto.response.ResponsibleResponseWithName;
import ru.tpu.hostel.booking.entity.Responsible;

@Component
public class ResponsibleMapper {

    public static ResponsibleResponse mapToResponsibleResponseDto(Responsible responsible) {
        return new ResponsibleResponse(
                responsible.getDate(),
                responsible.getType(),
                responsible.getUser()
        );
    }

    public static ResponsibleResponseWithName mapToResponsibleResponseWithNameDto(
            String firstName,
            String lastName,
            String middleName
    ) {
        return new ResponsibleResponseWithName(firstName, lastName, middleName);
    }
}
