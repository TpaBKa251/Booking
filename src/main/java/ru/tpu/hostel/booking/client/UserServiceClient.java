package ru.tpu.hostel.booking.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.tpu.hostel.booking.dto.response.UserShortResponse;

import java.util.List;
import java.util.UUID;

@Component
@FeignClient(name = "user-userservice", url = "http://userservice:8080")
public interface UserServiceClient {

    @GetMapping("/users/get/by/id/{id}")
    ResponseEntity<?> getUserById(@PathVariable UUID id);

    @GetMapping("/roles/get/user/roles/all/{userId}")
    List<String> getAllRolesByUserId(@PathVariable UUID userId);

    @GetMapping("/users/get/by/id/short/{id}")
    UserShortResponse getUserByIdShort(@PathVariable UUID id);
}
