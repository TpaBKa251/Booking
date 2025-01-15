package ru.tpu.hostel.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tpu.hostel.booking.dto.request.ResponsibleSetDto;
import ru.tpu.hostel.booking.dto.response.ResponsibleResponseDto;
import ru.tpu.hostel.booking.service.ResponsibleService;

@RestController
@RequestMapping("/responsibles")
@RequiredArgsConstructor
public class ResponsibleController {

    private final ResponsibleService responsibleService;

    @PostMapping
    public ResponsibleResponseDto setResponsible(@Valid @RequestBody ResponsibleSetDto responsibleSetDto) {
        return responsibleService.setResponsible(responsibleSetDto);
    }
}
