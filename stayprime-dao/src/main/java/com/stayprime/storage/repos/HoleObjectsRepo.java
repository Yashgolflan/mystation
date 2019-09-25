package com.stayprime.storage.repos;

import com.stayprime.hibernate.entities.HoleObjects;
import com.stayprime.hibernate.entities.HoleObjectsId;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface HoleObjectsRepo extends CrudRepository<HoleObjects, HoleObjectsId> {
    public List<HoleObjects> findById_CourseAndId_Hole(int course, int hole);
}
