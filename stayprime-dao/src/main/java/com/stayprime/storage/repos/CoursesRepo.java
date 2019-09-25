package com.stayprime.storage.repos;

import com.stayprime.hibernate.entities.Courses;
import org.springframework.data.repository.CrudRepository;

public interface CoursesRepo extends CrudRepository<Courses, Integer> {
 
}
