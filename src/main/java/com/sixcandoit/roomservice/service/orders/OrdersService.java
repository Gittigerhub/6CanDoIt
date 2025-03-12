package com.sixcandoit.roomservice.service.orders;

import com.sixcandoit.roomservice.constant.OrderStatus;
import com.sixcandoit.roomservice.dto.orders.OrdersDTO;
import com.sixcandoit.roomservice.dto.orders.OrdersHistDTO;
import com.sixcandoit.roomservice.dto.orders.OrdersMenuDTO;
import com.sixcandoit.roomservice.entity.cart.CartMenuEntity;
import com.sixcandoit.roomservice.entity.member.MemberEntity;
import com.sixcandoit.roomservice.entity.menu.MenuEntity;
import com.sixcandoit.roomservice.entity.orders.OrdersEntity;
import com.sixcandoit.roomservice.entity.orders.OrdersMenuEntity;
import com.sixcandoit.roomservice.repository.cart.CartMenuRepository;
import com.sixcandoit.roomservice.repository.member.MemberRepository;
import com.sixcandoit.roomservice.repository.menu.MenuRepository;
import com.sixcandoit.roomservice.repository.orders.OrdersMenuRepository;
import com.sixcandoit.roomservice.repository.orders.OrdersRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional

public class OrdersService {

    private final OrdersRepository ordersRepository;
    private final MenuRepository menuRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;
    private final CartMenuRepository cartMenuRepository;
    private final OrdersMenuRepository ordersMenuRepository;

    //사용자 화면
    //본인의 주문이 맞는지 체크
    public boolean validateOrders(Integer orderIdx, String memberEmail) {
        //회원 정보 조회
        MemberEntity memberEntity = memberRepository.findByMemberEmail(memberEmail)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 회원이 없습니다."));

        //주문 정보 조회
        OrdersEntity ordersEntity = ordersRepository.findByIdx(orderIdx)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 주문정보가 없습니다."));

        // 주문 id로 찾은 주문 테이블의 회원 참조 email과 현재 로그인 한 사람을 비교
        if (!StringUtils.equals(memberEntity.getMemberEmail(),ordersEntity.getMemberJoin().getMemberEmail())){
            return false;
        }
        return true;
    }

    //주문 취소
    public void cancelOrders(Integer orderIdx) {
        //삭제할 번호를 받아서 삭제
        OrdersEntity ordersEntity = ordersRepository.findByIdx(orderIdx)
                .orElseThrow(EntityNotFoundException::new);
        //주문 -> 주문 취소로 상태 변경
        ordersEntity.setOrdersStatus(OrderStatus.CANCEL);

        //재고가 있는 상태가 아니기 때문에 주문 취소시 menu 재고수량에 다시 되돌려줄 이유가 없음.
    }


    // 주문하기
    public Integer createOrders(List<Integer> cartMenuIdxList, String memberEmail,
                                String ordersPhone, String ordersMemo) {

        // 주문 메뉴가 들어갈 주문 테이블 생성(주문 메뉴가 참조하는 주문)
        OrdersEntity ordersEntity = new OrdersEntity();

        // email을 통해 현재 로그인한 사용자 가져오기
        MemberEntity memberEntity = memberRepository.findByMemberEmail(memberEmail)
                .orElse(null);

        if (memberEntity != null) {
            System.out.println("memberEntity : " + memberEntity.getMemberEmail());
            // 사용자 정보 주문테이블에 추가
            ordersEntity.setMemberJoin(memberEntity);       // 회원 FK
        } else {
            System.out.println("조회되는 회원이 없습니다.");
            return null;
        }

        // 주문테이블 컬럼값 추가
        ordersEntity.setOrdersStatus(OrderStatus.NEW);      // 주문 상태
        ordersEntity.setOrdersPhone(ordersPhone);           // 연락받을 연락처
        ordersEntity.setOrdersMemo(ordersMemo);             // 주문 요청사항

        // 주문메뉴 생성
        OrdersMenuEntity ordersMenuEntity = new OrdersMenuEntity();

        // 리스트로 받아온 메뉴idx로 메뉴조회
        for (Integer cartMenuIdx : cartMenuIdxList) {
            // 장바구니 메뉴 찾아오기
            CartMenuEntity cartMenuEntity = cartMenuRepository.findById(cartMenuIdx)
                    .orElseThrow(EntityNotFoundException::new);

            // 메뉴 찾아오기
            MenuEntity menuEntity = menuRepository.findById(cartMenuEntity.getMenuEntity().getIdx())
                    .orElseThrow(EntityNotFoundException::new);


            ordersMenuEntity.setOrdersJoin(ordersEntity);           // FK 설정
            ordersMenuEntity.setMenuJoin(menuEntity);               // FK 설정
            ordersMenuEntity.setCount(cartMenuEntity.getCount());   // 카트메뉴 수량 설정

            // 주문메뉴 테이블 저장
            OrdersMenuEntity ordersMenu = ordersMenuRepository.save(ordersMenuEntity);

            // 주문 테이블에 주문메뉴 FK 설정
            ordersEntity.getOrdersMenuJoin().add(ordersMenu);

        }

        // 주문 테이블 저장
        ordersEntity = ordersRepository.save(ordersEntity);

        // 반환
        return ordersEntity.getIdx();

    }

    public Integer orders(List<OrdersDTO> ordersDTOList, String memberEmail) {

        //주문을 했다면 판매하고 있는 메뉴의 수량 변경

        Optional<MemberEntity> memberEntity = memberRepository.findByMemberEmail(memberEmail);
        List<OrdersMenuEntity> ordersMenuEntityList = new ArrayList<>();
        OrdersEntity ordersEntity = new OrdersEntity();

        for (OrdersDTO ordersDTO : ordersDTOList) {
            MenuEntity menuEntity
                    = menuRepository.findById(ordersDTO.getIdx())
                    .orElseThrow(EntityNotFoundException::new);
            OrdersMenuEntity ordersMenuEntity = new OrdersMenuEntity();
            ordersMenuEntity.setMenuJoin(menuEntity);
            ordersMenuEntity.setOrdersJoin(ordersEntity);

            ordersMenuEntityList.add(ordersMenuEntity);
        }
        ordersEntity.setMemberJoin(memberEntity.get());
        ordersEntity.setOrdersStatus(OrderStatus.NEW);
        ordersEntity.setOrdersMenuEntityList(ordersMenuEntityList);

        ordersRepository.save(ordersEntity);

        return ordersEntity.getIdx();
    }

    //주문 이력
//    public Page<OrdersHistDTO> getOrderList(String memberEmail, Pageable pageable) {
//
//        //주문 목록
//        List<OrdersEntity> ordersEntityList = ordersRepository.findOrdersEntity(memberEmail, pageable);
//
//        //페이징 처리를 위한 총 주문 목록의 수
//        Integer totalCount = ordersRepository.totalcount(memberEmail);
//
//        //주문 목록의 주문 메뉴들을 만들기 위한 List
//        List<OrdersHistDTO> ordersHistDTOList = new ArrayList<>();
//
//        // EntityToDTO //주문, 주문메뉴들, 주문메뉴들의 이미지
//        for (OrdersEntity ordersEntity : ordersEntityList) {
//            OrdersHistDTO ordersHistDTO = new OrdersHistDTO();
//            ordersHistDTO.setOrdersIdx(ordersEntity.getIdx());
//            ordersHistDTO.setOrderStatus(ordersEntity.getOrdersStatus());
//
//            List<OrdersMenuEntity> ordersMenuEntityList = ordersEntity.getOrdersMenuJoin();
//
//            for (OrdersMenuEntity ordersMenuEntity : ordersMenuEntityList) {
//                OrdersMenuDTO ordersMenuDTO = new OrdersMenuDTO();
//                ordersMenuDTO.setIdx(ordersEntity.getIdx());
//                ordersMenuDTO.setCount(ordersMenuEntity.getCount());
//
//                //메뉴 주문 이미지 추후 작성
//            }
//            ordersHistDTOList.add(ordersHistDTO);
//        }
//        return new PageImpl<OrdersHistDTO>(ordersHistDTOList, pageable, totalCount);
//    }


    //관리자 화면
    // 1. 관리자용 주문 목록 조회 (검색 기능 포함)
    public Page<OrdersHistDTO> getAdminOrderList(String type, String keyword, Pageable page) {
        log.info("관리자 주문 목록 조회");

        try {
            //1. 페이지 정보를 재가공
            int currentPage = page.getPageNumber() - 1;  //화면의 페이지 번호를 db 페이지 번호로
            int pageSize = 10;  //한 페이지를 구성하는 레코드 수

            //지정된 내용으로 페이지 정보를 재 생산, 정렬 내림차순 DESC
            Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.DESC, "idx"));

            //2. 조회
            //조회 결과를 저장할 변수 선언
            Page<OrdersEntity> ordersEntities;

            //여러개를 조회해야 할 땐 if문으로 분류에 따라 조회해야한다.
            //type : 주문번호(1), 주문자명(2), 주문상태(3), 전체(0)
            if (keyword != null && !keyword.isEmpty()) {
                if (type.equals("1")) { //type 분류 1, 주문번호로 검색할 때
                    log.info("주문번호로 검색 하는 중...");
                    ordersEntities = ordersRepository.searchByOrderIdx(keyword, pageable);
                } else if (type.equals("2")) { //type 분류 2, 주문자명으로 검색할 때
                    log.info("주문자명으로 검색 하는 중...");
                    ordersEntities = ordersRepository.searchByMemberName(keyword, pageable);
                } else if (type.equals("3")) { //type 분류 3, 주문상태로 검색할 때
                    log.info("주문상태로 검색 하는 중...");
                    OrderStatus status = OrderStatus.valueOf(keyword);
                    ordersEntities = ordersRepository.searchByOrderStatus(status, pageable);
                } else {    //type 분류 4, 전체로 검색할 때
                    log.info("전체 조회 검색중...");
                    ordersEntities = ordersRepository.findAll(pageable);
                }

            } else {  //검색어가 존재하지 않으면 모두 검색
                ordersEntities = ordersRepository.findAll(pageable);
            }

            //3. 조회한 결과를 HTML에서 사용할 DTO로 변환
            //Entity를 DTO로 변환 후 저장

            Page<OrdersHistDTO> ordersHistDTOS = ordersEntities.map(
                    data -> modelMapper.map(data, OrdersHistDTO.class));

            //4. 결과값을 전달
            return ordersHistDTOS;
        } catch (Exception e) { //오류 발생시 처리
            throw new RuntimeException("조회 오류");
        }
    }

    // 2. 관리자용 주문 상태 변경
    public void updateOrderStatus(Integer orderIdx, OrderStatus newStatus) {
        log.info("주문 상태 변경 시도");

        OrdersEntity ordersEntity = ordersRepository.findByIdx(orderIdx)
                .orElseThrow(() -> new EntityNotFoundException("해당 주문을 찾을 수 없습니다."));

        validateStatusChange(ordersEntity.getOrdersStatus(), newStatus);
        ordersEntity.setOrdersStatus(newStatus);
        ordersRepository.save(ordersEntity);

        log.info("주문 상태 변경 완료: {} -> {}", orderIdx, newStatus);
    }

    // 3. 관리자용 주문 상세 조회
    public OrdersHistDTO getAdminOrderDetail(Integer orderIdx) {
        log.info("주문 상세 정보 조회");

        OrdersEntity ordersEntity = ordersRepository.findByIdx(orderIdx)
                .orElseThrow(() -> new EntityNotFoundException("해당 주문을 찾을 수 없습니다."));

        OrdersHistDTO ordersHistDTO = new OrdersHistDTO();
        ordersHistDTO.setOrdersIdx(ordersEntity.getIdx());
        ordersHistDTO.setOrderStatus(ordersEntity.getOrdersStatus());

        List<OrdersMenuEntity> ordersMenuEntityList = ordersEntity.getOrdersMenuJoin();

        for (OrdersMenuEntity ordersMenuEntity : ordersMenuEntityList) {
            OrdersMenuDTO ordersMenuDTO = new OrdersMenuDTO();
            ordersMenuDTO.setIdx(ordersMenuEntity.getIdx());
            ordersMenuDTO.setCount(ordersMenuEntity.getCount());
            ordersHistDTO.getOrdersMenuDTOList().add(ordersMenuDTO);
        }

        return ordersHistDTO;
    }

    // 4. 관리자용 상태별 주문 목록 조회
    public Page<OrdersHistDTO> getOrdersByStatus(String status, Pageable pageable) {
        log.info("상태별 주문 목록 조회");

        List<OrdersEntity> ordersEntityList;
        int totalCount;

        if (status.equals("ALL")) {
            ordersEntityList = ordersRepository.findAll(pageable).getContent();
            totalCount = (int) ordersRepository.count();
        } else {
            OrderStatus orderStatus = OrderStatus.valueOf(status);
            ordersEntityList = ordersRepository.findByOrdersStatus(orderStatus, pageable).getContent();
            totalCount = ordersEntityList.size();
        }

        List<OrdersHistDTO> ordersHistDTOList = new ArrayList<>();

        for (OrdersEntity ordersEntity : ordersEntityList) {
            OrdersHistDTO ordersHistDTO = new OrdersHistDTO();
            ordersHistDTO.setOrdersIdx(ordersEntity.getIdx());
            ordersHistDTO.setOrderStatus(ordersEntity.getOrdersStatus());

            List<OrdersMenuEntity> ordersMenuEntityList = ordersEntity.getOrdersMenuJoin();

            for (OrdersMenuEntity ordersMenuEntity : ordersMenuEntityList) {
                OrdersMenuDTO ordersMenuDTO = new OrdersMenuDTO();
                ordersMenuDTO.setIdx(ordersMenuEntity.getIdx());
                ordersMenuDTO.setCount(ordersMenuEntity.getCount());
                ordersHistDTO.getOrdersMenuDTOList().add(ordersMenuDTO);
            }
            ordersHistDTOList.add(ordersHistDTO);
        }

        return new PageImpl<>(ordersHistDTOList, pageable, totalCount);
    }

    // 주문 상태 변경 유효성 검사
    private void validateStatusChange(OrderStatus currentStatus, OrderStatus newStatus) {
        if (newStatus == OrderStatus.CANCEL) {
            throw new IllegalStateException("관리자는 주문을 취소할 수 없습니다.");
        }

        switch (currentStatus) {
            case NEW:
                if (newStatus != OrderStatus.CHECK) {
                    throw new IllegalStateException("신규 주문은 접수 상태로만 변경할 수 있습니다.");
                }
                break;
            case CHECK:
                if (newStatus != OrderStatus.COOKING) {
                    throw new IllegalStateException("접수된 주문은 조리 중 상태로만 변경할 수 있습니다.");
                }
                break;
            case COOKING:
                if (newStatus != OrderStatus.CLOSE) {
                    throw new IllegalStateException("조리 중인 주문은 완료 상태로만 변경할 수 있습니다.");
                }
                break;
            case CLOSE:
                throw new IllegalStateException("완료된 주문의 상태는 변경할 수 없습니다.");
            case CANCEL:
                throw new IllegalStateException("취소된 주문의 상태는 변경할 수 없습니다.");
        }
    }

}



