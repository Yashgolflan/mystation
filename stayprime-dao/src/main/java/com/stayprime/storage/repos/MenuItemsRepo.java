package com.stayprime.storage.repos;

import com.stayprime.hibernate.entities.MenuItems;
import org.springframework.data.repository.CrudRepository;

public interface MenuItemsRepo extends CrudRepository<MenuItems, Integer> {
}
