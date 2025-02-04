package com.sixcandoit.roomservice.repository.notice;

import com.sixcandoit.roomservice.entity.notice.NoticeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends JpaRepository<NoticeEntity, Integer> {

}
