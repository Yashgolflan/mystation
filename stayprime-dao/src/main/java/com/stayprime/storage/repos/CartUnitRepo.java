package com.stayprime.storage.repos;

import com.stayprime.hibernate.entities.CartUnit;
import org.springframework.data.repository.CrudRepository;

public interface CartUnitRepo extends CrudRepository<CartUnit, String> {
}
