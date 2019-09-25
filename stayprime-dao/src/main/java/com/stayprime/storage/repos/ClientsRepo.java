package com.stayprime.storage.repos;

import com.stayprime.hibernate.entities.Clients;
import org.springframework.data.repository.CrudRepository;

public interface ClientsRepo extends CrudRepository<Clients, Integer> {
}
