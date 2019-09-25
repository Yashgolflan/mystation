/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stayprime.storage.util;

import com.stayprime.hibernate.entities.CourseInfo;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author stayprime
 */
public class DomainUtil {

    public static <T> List<T> toList(Iterable<T> iterable) {
        ArrayList<T> list = new ArrayList<>();
        for (T t : iterable) {
            list.add(t);
        }
        return list;
    }

    /**
     * Validate only one courseInfo record is in the database and return it.
     * @param records all the CourseInfo records from the database
     * @return the only CourseInfo record or null if records is empty
     * @throws IllegalStateException if there are more than one records
     */
    public static CourseInfo getValidCourseInfo(Iterable<CourseInfo> records) {
        List<CourseInfo> list = DomainUtil.toList(records);
        if (list.isEmpty()) {
            return null;
        }
        if (list.size() > 1) {
            throw new IllegalStateException("Only one CourseInfo record should be in the database, courseId should be 1");
        }
        return list.get(0);
    }

    /**
     * Try to get a valid courseInfo record from CourseInfo.
     * @param records all the CourseInfo records from the database
     * @return the record with courseId == 1 or the first record
     */
    public static CourseInfo getAnyCourseInfo(Iterable<CourseInfo> records) {
        List<CourseInfo> list = DomainUtil.toList(records);
        if (list.isEmpty()) {
            return null;
        }
        if (list.size() > 1 && list.get(1).getCourseId() == 1) {
            return list.get(1);
        }
        return list.get(0);
    }

}
