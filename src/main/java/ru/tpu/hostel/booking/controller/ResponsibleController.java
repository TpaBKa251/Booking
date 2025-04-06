package ru.tpu.hostel.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tpu.hostel.booking.dto.request.ResponsibleSetRequest;
import ru.tpu.hostel.booking.dto.response.ResponsibleResponse;
import ru.tpu.hostel.booking.dto.response.ResponsibleResponseWithName;
import ru.tpu.hostel.booking.entity.BookingType;
import ru.tpu.hostel.booking.service.old.ResponsibleService;

import java.time.LocalDate;

/**
 * Под списание
 */
@RestController
@RequestMapping("/responsibles")
@RequiredArgsConstructor
public class ResponsibleController {

    private final ResponsibleService responsibleService;

    @PostMapping
    public ResponsibleResponse setResponsible(@Valid @RequestBody ResponsibleSetRequest responsibleSetDto) {
        return responsibleService.setResponsible(responsibleSetDto);
    }

    @GetMapping("/get/{date}/{type}")
    public ResponsibleResponseWithName getResponsible(@PathVariable LocalDate date, @PathVariable BookingType type) {
        return responsibleService.getResponsible(date, type);
    }
}
