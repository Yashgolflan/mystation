package com.stayprime.storage.repos;

import com.stayprime.hibernate.entities.PinLocation;
import com.stayprime.hibernate.entities.PinLocationId;
import org.springframework.data.repository.CrudRepository;

public interface PinLocationRepo extends CrudRepository<PinLocation, PinLocationId> {
}
