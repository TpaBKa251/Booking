package ru.tpu.hostel.booking.service.old;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tpu.hostel.booking.dto.request.ResponsibleSetRequest;
import ru.tpu.hostel.booking.dto.response.ResponsibleResponse;
import ru.tpu.hostel.booking.dto.response.ResponsibleResponseWithName;
import ru.tpu.hostel.booking.entity.BookingType;
import ru.tpu.hostel.booking.entity.Responsible;
import ru.tpu.hostel.booking.external.rest.user.UserServiceClient;
import ru.tpu.hostel.booking.external.rest.user.dto.UserShortResponse;
import ru.tpu.hostel.booking.mapper.ResponsibleMapper;
import ru.tpu.hostel.booking.repository.ResponsibleRepository;
import ru.tpu.hostel.booking.repository.TimeSlotRepository;
import ru.tpu.hostel.internal.exception.ServiceException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Deprecated(forRemoval = true)
public class ResponsibleServiceImpl implements ResponsibleService {

    private final ResponsibleRepository responsibleRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final UserServiceClient userServiceClient;

    @Override
    public ResponsibleResponse setResponsible(ResponsibleSetRequest responsibleSetDto) {
        //Ищется чувак по типу и дате (он на какую-то дату назначен)
        Responsible responsible = responsibleRepository
                .findByTypeAndDate(responsibleSetDto.type(), responsibleSetDto.date())
                .orElse(null);

        if (responsible == null) {

            //Ищем любой слот в этом дне
            if (timeSlotRepository.findOneByTypeAndStartTimeOnSpecificDay(
                    responsibleSetDto.type(),
                    responsibleSetDto.date().atTime(0, 0),
                    responsibleSetDto.date().atTime(23, 59)
            ).isPresent()) {
                //Назначаем ответственного, если нет ответственного и если вообще есть расписание (слоты) на день
                responsible = new Responsible();
                responsible.setType(responsibleSetDto.type());
                responsible.setDate(responsibleSetDto.date());
            } else {
                throw new ServiceException.NotFound("Ответственный не найден");
            }
        }

        //Собирается все роли человека, которого ставим
        List<String> roles = userServiceClient.getAllRolesByUserId(responsibleSetDto.user());

        for (String role : roles) {
            //Если есть у человека роль ответственного за что-то, то ответственному ставим человека,
            // которому хотим записать(Если у человека есть роль на что-то, то его только на это и можно поставаить
            // (Если поставить человека не ответственного за зал в ответственного, то нельзя так))
            if (role.contains(responsibleSetDto.type().toString())) {
                responsible.setUser(responsibleSetDto.user());
                responsibleRepository.save(responsible);

                return ResponsibleMapper.mapToResponsibleResponseDto(responsible);
            }
        }

        throw new ServiceException.NotFound("Ответственный не найден");
    }

    //Для отображения человека с именем и ролью (Показывает, кто ответственный на день)
    @Override
    public ResponsibleResponseWithName getResponsible(LocalDate date, BookingType type) {
        //Здесь заглушка для кухни
        if (type == BookingType.KITCHEN) {
            return new ResponsibleResponseWithName(
                    "Валерий",
                    "Жмышенко",
                    "Альбертович"
            );
        }

        //Ищется ответственный по типу и дате
        Responsible responsible = responsibleRepository.findByTypeAndDate(type, date)
                .orElseThrow(() -> new ServiceException.NotFound("Ответственный не найден"));

        //Если пользователь не найден (Не назначен), то на мобилку идет пустота
        if (responsible.getUser() == null) {
            return new ResponsibleResponseWithName("", "", "");
        }

        UserShortResponse user = userServiceClient.getUserByIdShort(responsible.getUser());

        return new ResponsibleResponseWithName(user.firstName(), user.lastName(), user.middleName());
    }
}
