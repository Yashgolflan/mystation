package com.stayprime.storage.repos;

import com.stayprime.hibernate.entities.CourseInfo;
import org.springframework.data.repository.CrudRepository;

public interface CourseInfoRepo extends CrudRepository<CourseInfo, Integer> {
}
