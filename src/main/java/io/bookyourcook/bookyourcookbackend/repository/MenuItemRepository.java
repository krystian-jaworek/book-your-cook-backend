package io.bookyourcook.bookyourcookbackend.repository;

import io.bookyourcook.bookyourcookbackend.model.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends MongoRepository<MenuItem, String> {

    List<MenuItem> findByCookId(String cookId);

    @Query("{$or: [ {'nameNormalized': { $regex: ?0, $options: 'i' }}, {'descriptionNormalized': { $regex: ?0, $options: 'i' }}, {'ingredientsNormalized': { $regex: ?0, $options: 'i' }} ] }")
    Page<MenuItem> findByPartialMatch(String normalizedQuery, Pageable pageable);
}
