package com.stayprime.storage.repos;

import com.stayprime.hibernate.entities.CartTracking;
import com.stayprime.hibernate.entities.CartTrackingId;
import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface CartTrackingRepo extends CrudRepository<CartTracking, CartTrackingId> {

    public List<CartTracking> findById_CartNumberAndId_TimestampBetween(Integer cart, Date start, Date end, Pageable p);
//    @Query("select sR from service_request sR where status < :maxStatus and time > -2")

    public List<CartTracking> findById_TimestampBetween(Date start, Date end, Pageable p);
//String queryString = query.unwrap(org.hibernate.Query.class).getQueryString();

}
