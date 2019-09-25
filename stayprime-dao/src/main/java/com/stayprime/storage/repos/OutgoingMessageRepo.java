package com.stayprime.storage.repos;

import com.stayprime.hibernate.entities.OutgoingMessage;

import org.springframework.data.repository.CrudRepository;

public interface OutgoingMessageRepo extends CrudRepository<OutgoingMessage, Integer> {
}
