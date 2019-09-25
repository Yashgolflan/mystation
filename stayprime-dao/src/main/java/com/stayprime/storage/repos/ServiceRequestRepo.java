package com.stayprime.storage.repos;

import com.stayprime.hibernate.entities.ServiceRequest;
import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface ServiceRequestRepo extends CrudRepository<ServiceRequest, Integer> {

    public List<ServiceRequest> findByStatusLessThanEqual(int maxStatus);
//    @Query("select sR from service_request sR where status < :maxStatus and time > -2")
    public List<ServiceRequest> findByStatusLessThanEqualAndTimeGreaterThan(int maxStatus,Date date);
}
