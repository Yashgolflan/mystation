package com.stayprime.storage.repos;

import com.stayprime.hibernate.entities.CartAssignment;
import org.springframework.data.repository.CrudRepository;

public interface CartAssignmentRepo extends CrudRepository<CartAssignment, Integer> {
}
