package ru.tpu.hostel.booking.external.rest.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.tpu.hostel.booking.external.rest.user.dto.UserShortResponse;

import java.util.List;
import java.util.UUID;

@Component
@FeignClient(name = "user-service", url = "${rest.base-url.user-service}")
public interface UserServiceClient {

    @GetMapping("/users/get/by/id/{id}")
    ResponseEntity<?> getUserById(@PathVariable UUID id);

    @GetMapping("/roles/get/user/roles/all/{userId}")
    List<String> getAllRolesByUserId(@PathVariable UUID userId);

    @GetMapping("/users/get/by/id/short/{id}")
    UserShortResponse getUserByIdShort(@PathVariable UUID id);
}
