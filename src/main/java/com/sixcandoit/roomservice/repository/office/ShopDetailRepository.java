package com.sixcandoit.roomservice.repository.office;

import com.sixcandoit.roomservice.entity.office.ShopDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopDetailRepository extends JpaRepository<ShopDetailEntity, Integer> {



}
