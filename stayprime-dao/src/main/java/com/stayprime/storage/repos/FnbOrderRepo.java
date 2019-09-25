/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stayprime.storage.repos;

import com.stayprime.hibernate.entities.FnbOrder;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author sarthak
 */
@Repository
public interface FnbOrderRepo extends CrudRepository<FnbOrder, Integer> {
    
}
