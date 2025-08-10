package io.bookyourcook.bookyourcookbackend.repository;

import io.bookyourcook.bookyourcookbackend.model.Role;
import io.bookyourcook.bookyourcookbackend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByUsername(String username);

    Page<User> findAllByRole(Role role, Pageable pageable);

    @Query("{'role': ?0, $or: [ {'firstNameNormalized': { $regex: ?1, $options: 'i' }}, {'lastNameNormalized': { $regex: ?1, $options: 'i' }}, {'bioNormalized': { $regex: ?1, $options: 'i' }} ] }")
    Page<User> findCooksByRoleAndPartialMatch(Role role, String normalizedQuery, Pageable pageable);
}
