package com.sixcandoit.roomservice.repository;

import com.sixcandoit.roomservice.entity.NoticeEntity;
import com.sixcandoit.roomservice.repository.member.MemberRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends JpaRepository<NoticeEntity, Integer> {

}
