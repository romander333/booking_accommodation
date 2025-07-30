package com.romander.bookingapp.repository;

import static com.romander.bookingapp.util.AccommodationDataTest.getAccommodation;
import static org.junit.Assert.assertEquals;

import com.romander.bookingapp.model.Accommodation;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.datasource.init.ScriptUtils;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AccommodationRepositoryTest {

    @Autowired
    private AccommodationRepository accommodationRepository;

    @BeforeAll
    static void beforeAll(@Autowired DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/accommodation/add-accommodation.sql"));
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/accommodation_amenities/add-accommodation_amenities.sql")
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/accommodation_amenities/remove-accommodation_amenities.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/accommodation/remove-accommodation.sql")
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void findAllWithAmenities_WithGivenValidCatalog_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Accommodation expected = getAccommodation();
        Page<Accommodation> allWithAmenities =
                accommodationRepository.findAllWithAmenities(pageable);
        assertEquals(expected, allWithAmenities.getContent().get(0));
    }

    @Test
    void findById_WithValidId_ShouldReturnAccommodation() {
        Long id = 1L;
        Accommodation expected = getAccommodation();
        Optional<Accommodation> actual = accommodationRepository.findById(id);
        assertEquals(expected, actual.get());
    }
}
