package io.bookyourcook.bookyourcookbackend.repository;

import io.bookyourcook.bookyourcookbackend.model.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends MongoRepository<Booking, String> {

    @Query("{ 'cookId': ?0, 'date': { '$gte': ?1, '$lt': ?2 } }")
    List<Booking> findByCookIdAndDateBetween(String cookId, LocalDate start, LocalDate end);

    @Query("{ 'customerId': ?0, 'date': { '$gte': ?1, '$lt': ?2 } }")
    List<Booking> findByCustomerIdAndDateBetween(String customerId, LocalDate start, LocalDate end);
}
