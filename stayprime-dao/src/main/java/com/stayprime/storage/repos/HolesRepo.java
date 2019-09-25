package com.stayprime.storage.repos;

import com.stayprime.hibernate.entities.Holes;
import com.stayprime.hibernate.entities.HolesId;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface HolesRepo extends CrudRepository<Holes, HolesId> {

    public List<Holes> findById_Course(int courseNumber);
}
