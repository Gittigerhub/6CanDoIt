package com.sixcandoit.roomservice.repository.orders;

import com.sixcandoit.roomservice.entity.orders.MenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<MenuEntity, Integer> {

}
