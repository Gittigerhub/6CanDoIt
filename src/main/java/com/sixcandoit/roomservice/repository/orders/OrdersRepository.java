package com.sixcandoit.roomservice.repository.orders;

import com.sixcandoit.roomservice.constant.OrdersStatus;
import com.sixcandoit.roomservice.entity.orders.OrdersEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrdersRepository extends JpaRepository<OrdersEntity, Integer> {

    // 구매이력
    // 회원 이메일과 최신생성기간 순으로 정렬하여 조회
    @Query("select o from OrdersEntity o where o.memberJoin.memberEmail = :memberEmail order by o.insDate DESC")
    Page<OrdersEntity> findOrdersEntity(String memberEmail, Pageable pageable);

    @Query("select count(o) from OrdersEntity o where o.memberJoin.memberEmail = :memberEmail")
    Integer totalcount(String memberEmail);

    Optional<OrdersEntity> findByIdx(Integer orderIdx);

    //public List<OrdersEntity> findByMemberEmailOrdersByOrdersDateDesc(String memberEmail, Pageable pageable);


    // 관리자용 추가 메서드들
    // 1. 주문번호로 검색
    @Query("SELECT o FROM OrdersEntity o WHERE o.idx = :keyword")
    Page<OrdersEntity> searchByOrderIdx(@Param("keyword") String keyword, Pageable pageable);

    // 2. 주문자명으로 검색
    @Query("SELECT o FROM OrdersEntity o WHERE o.memberJoin.memberName LIKE %:keyword%")
    Page<OrdersEntity> searchByMemberName(@Param("keyword") String keyword, Pageable pageable);

    // 3. 주문상태로 검색
    @Query("SELECT o FROM OrdersEntity o WHERE o.ordersStatus = :status")
    Page<OrdersEntity> searchByOrdersStatus(@Param("status") OrdersStatus status, Pageable pageable);

    // 4. 전체 검색 (주문번호, 주문자명, 주문상태)
    @Query("SELECT o FROM OrdersEntity o " +
            "WHERE o.idx = :orderIdx " +
            "OR o.memberJoin.memberName LIKE %:keyword% " +
            "OR o.ordersStatus = :status")
    Page<OrdersEntity> searchOrdersAll(@Param("orderIdx") Integer orderIdx,
                                 @Param("keyword") String keyword,
                                 @Param("status") OrdersStatus status,
                                 Pageable pageable);

    // 5. 특정 상태의 주문 목록 조회
    Page<OrdersEntity> findByOrdersStatus(OrdersStatus status, Pageable pageable);

    // 6. 특정 기간 내 주문 조회
//    @Query("SELECT o FROM OrdersEntity o WHERE o.ordersDate BETWEEN :startDate AND :endDate")
//    Page<OrdersEntity> findByOrdersDateBetween(
//            @Param("startDate") Date startDate,
//            @Param("endDate") Date endDate,
//            Pageable pageable);

    // 7. 특정 상태의 주문 수 조회
    @Query("SELECT COUNT(o) FROM OrdersEntity o WHERE o.ordersStatus = :status")
    Long countByOrdersStatus(@Param("status") OrdersStatus status);

    // 기관별 주문 조회
    @Query("SELECT o FROM OrdersEntity o JOIN o.memberJoin.reservationJoin r WHERE r.roomJoin.organizationJoin.idx = :organ_idx")
    Page<OrdersEntity> findByOrganIdx(@Param("organ_idx") Integer organ_idx, Pageable pageable);

    // 1. 주문번호와 기관으로 검색
    @Query("SELECT o FROM OrdersEntity o JOIN o.memberJoin.reservationJoin r WHERE o.idx = :keyword AND r.roomJoin.organizationJoin.idx = :organ_idx")
    Page<OrdersEntity> searchByOrderIdxAndOrganIdx(@Param("keyword") String keyword, @Param("organ_idx") Integer organ_idx, Pageable pageable);

    // 2. 주문자명과 기관으로 검색
    @Query("SELECT o FROM OrdersEntity o JOIN o.memberJoin.reservationJoin r WHERE o.memberJoin.memberName LIKE %:keyword% AND r.roomJoin.organizationJoin.idx = :organ_idx")
    Page<OrdersEntity> searchByMemberNameAndOrganIdx(@Param("keyword") String keyword, @Param("organ_idx") Integer organ_idx, Pageable pageable);

    // 3. 주문상태와 기관으로 검색
    @Query("SELECT o FROM OrdersEntity o JOIN o.memberJoin.reservationJoin r WHERE o.ordersStatus = :status AND r.roomJoin.organizationJoin.idx = :organ_idx")
    Page<OrdersEntity> searchByOrdersStatusAndOrganIdx(@Param("status") OrdersStatus status, @Param("organ_idx") Integer organ_idx, Pageable pageable);

}
