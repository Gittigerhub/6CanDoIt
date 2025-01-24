package com.sixcandoit.roomservice.repository.qna;

import com.sixcandoit.roomservice.entity.qna.QnaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QnaRepository extends JpaRepository<QnaEntity, Integer> {

}
