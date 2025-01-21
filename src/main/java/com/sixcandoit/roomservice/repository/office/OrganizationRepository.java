package com.sixcandoit.roomservice.repository.office;

import com.sixcandoit.roomservice.entity.office.OrganizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends JpaRepository<OrganizationEntity, Integer> {



}
