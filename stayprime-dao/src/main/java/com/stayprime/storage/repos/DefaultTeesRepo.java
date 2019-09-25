package com.stayprime.storage.repos;

import com.stayprime.hibernate.entities.DefaultTees;
import com.stayprime.hibernate.entities.DefaultTeesId;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface DefaultTeesRepo extends CrudRepository<DefaultTees, DefaultTeesId> {
    public List<DefaultTees> findById_Course(int courseNumber);
}
