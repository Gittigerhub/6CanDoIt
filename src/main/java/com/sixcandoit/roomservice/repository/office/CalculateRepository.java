package com.sixcandoit.roomservice.repository.office;


import com.sixcandoit.roomservice.entity.office.CalculateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("calculateRepository")

public interface CalculateRepository extends JpaRepository<CalculateEntity, Integer> {
}
