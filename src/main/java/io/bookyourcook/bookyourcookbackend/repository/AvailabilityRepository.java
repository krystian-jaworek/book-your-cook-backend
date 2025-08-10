package io.bookyourcook.bookyourcookbackend.repository;

import io.bookyourcook.bookyourcookbackend.model.Availability;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AvailabilityRepository extends MongoRepository<Availability, String> {

    @Query("{ 'cookId': ?0, 'date': { '$gte': ?1, '$lt': ?2 } }")
    List<Availability> findByCookIdAndDateBetween(String cookId, LocalDate start, LocalDate end);

    Optional<Availability> findByCookIdAndDate(String cookId, LocalDate date);
}
