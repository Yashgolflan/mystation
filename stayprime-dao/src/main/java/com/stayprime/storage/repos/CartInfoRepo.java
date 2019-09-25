package com.stayprime.storage.repos;

import com.stayprime.hibernate.entities.CartInfo;
import org.springframework.data.repository.CrudRepository;

public interface CartInfoRepo extends CrudRepository<CartInfo, Integer> {
}
