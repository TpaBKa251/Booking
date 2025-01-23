package ru.tpu.hostel.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.tpu.hostel.booking.dto.request.ResponsibleSetDto;
import ru.tpu.hostel.booking.dto.response.ResponsibleResponseDto;
import ru.tpu.hostel.booking.dto.response.ResponsibleResponseWithNameDto;
import ru.tpu.hostel.booking.enums.BookingType;
import ru.tpu.hostel.booking.service.ResponsibleService;

import java.time.LocalDate;

@RestController
@RequestMapping("/responsibles")
@RequiredArgsConstructor
public class ResponsibleController {

    private final ResponsibleService responsibleService;

    @PostMapping
    public ResponsibleResponseDto setResponsible(@Valid @RequestBody ResponsibleSetDto responsibleSetDto) {
        return responsibleService.setResponsible(responsibleSetDto);
    }

    @GetMapping("/get/{date}/{type}")
    public ResponsibleResponseWithNameDto getResponsible(@PathVariable LocalDate date, @PathVariable BookingType type) {
        return responsibleService.getResponsible(date, type);
    }
}
