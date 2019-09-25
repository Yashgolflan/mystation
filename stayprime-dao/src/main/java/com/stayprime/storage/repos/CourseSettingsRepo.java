package com.stayprime.storage.repos;

import com.stayprime.hibernate.entities.CourseSettings;
import org.springframework.data.repository.CrudRepository;

public interface CourseSettingsRepo extends CrudRepository<CourseSettings, String> {
}
