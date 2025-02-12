package com.sixcandoit.roomservice.repository.orders;

import com.sixcandoit.roomservice.entity.orders.MenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<MenuEntity, Integer> {

    public Optional<MenuEntity> findByIdx(Integer idx);

}
