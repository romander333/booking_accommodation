package com.romander.bookingapp.repository;

import com.romander.bookingapp.model.Accommodation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {
    @Query(value = "SELECT a from Accommodation a left join fetch a.amenities")
    Page<Accommodation> findAllWithAmenities(Pageable pageable);

    @Query(value = "SELECT a from Accommodation a left join fetch a.amenities where a.id = ?1")
    Optional<Accommodation> findById(Long id);
}
