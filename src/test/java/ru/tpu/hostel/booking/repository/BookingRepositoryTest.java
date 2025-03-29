package ru.tpu.hostel.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import ru.tpu.hostel.booking.repository.util.RepositoryTest;
/**
 *
 */
@RepositoryTest
@DisplayName("Тесты репозитория броней")
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();

    }
}