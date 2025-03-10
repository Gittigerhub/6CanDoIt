package com.sixcandoit.roomservice.service.orders;

import com.sixcandoit.roomservice.constant.OrderStatus;
import com.sixcandoit.roomservice.dto.orders.OrdersDTO;
import com.sixcandoit.roomservice.dto.orders.OrdersHistDTO;
import com.sixcandoit.roomservice.dto.orders.OrdersMenuDTO;
import com.sixcandoit.roomservice.entity.member.MemberEntity;
import com.sixcandoit.roomservice.entity.menu.MenuEntity;
import com.sixcandoit.roomservice.entity.orders.OrdersEntity;
import com.sixcandoit.roomservice.entity.orders.OrdersMenuEntity;
import com.sixcandoit.roomservice.repository.member.MemberRepository;
import com.sixcandoit.roomservice.repository.menu.MenuRepository;
import com.sixcandoit.roomservice.repository.orders.OrdersRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
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


    //주문
    public Integer createOrders(OrdersDTO ordersDTO, String memberEmail) {

        MenuEntity menuEntity = menuRepository.findByIdx(ordersDTO.getMenuIdx())
                .orElseThrow(EntityNotFoundException::new);
        //email을 통해 현재 로그인한 사용자 가져오기
        MemberEntity memberEntity = memberRepository.findByMemberEmail(memberEmail)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 회원이 없습니다."));

        //ordersmenu 생성
        OrdersMenuEntity ordersMenuEntity = new OrdersMenuEntity();
        ordersMenuEntity.setMenuEntity(menuEntity); //주문 메뉴
        ordersMenuEntity.setCount(ordersDTO.getCount());    //수량
        ordersMenuEntity.setOrderPrice(menuEntity.getMenuPrice());  //메뉴 금액

        //주문 메뉴가 들어갈 주문 테이블 생성(주문 메뉴가 참조하는 주문)
        OrdersEntity ordersEntity = new OrdersEntity();
        ordersEntity.setMemberJoin(memberEntity);   //주문자 email로 찾아온 entity 객체

        ordersEntity.setOrdersMenuEntityList(ordersMenuEntity); //주문목록 (OrdersEntity 객체 참조)

        ordersEntity.setOrdersStatus(OrderStatus.NEW);      //주문 상태
        ordersEntity.setOrdersDate(LocalDateTime.now());    //주문 시간

        // 이렇게 만들어진 order 주문 객체를 저장하기 전에
        // orderItem에서 private Order order;를 set해줌으로써
        // 양방향이기에 같이 등록되며 같이 등록될때 pk값도 같이 참조해준다.
        ordersMenuEntity.setOrdersEntity(ordersEntity);

        // 실제 저장은 order만 하지만 order에
        // @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,
        //         orphanRemoval = true, fetch = FetchType.LAZY)
        // private List<OrderItem> orderItemList = new ArrayList<>();
        // 이부분을 set해줬기에 둘다 저장된다.
        ordersEntity = ordersRepository.save(ordersEntity);

        return ordersEntity.getIdx();
    }

    public Integer orders(List<OrdersDTO> ordersDTOList, String memberEmail) {

        //주문을 했다면 판매하고 있는 메뉴의 수량 변경

        Optional<MemberEntity> memberEntity = memberRepository.findByMemberEmail(memberEmail);
        List<OrdersMenuEntity> ordersMenuEntityList = new ArrayList<>();
        OrdersEntity ordersEntity = new OrdersEntity();

        for (OrdersDTO ordersDTO : ordersDTOList) {
            MenuEntity menuEntity
                    = menuRepository.findById(ordersDTO.getMenuIdx())
                    .orElseThrow(EntityNotFoundException::new);
            OrdersMenuEntity ordersMenuEntity = new OrdersMenuEntity();
            ordersMenuEntity.setMenuEntity(menuEntity);
            ordersMenuEntity.setOrderPrice(menuEntity.getMenuPrice());
            ordersMenuEntity.setCount(ordersDTO.getCount());
            ordersMenuEntity.setOrdersEntity(ordersEntity);

            ordersMenuEntityList.add(ordersMenuEntity);
        }
        ordersEntity.setMemberJoin(memberEntity.get());
        ordersEntity.setOrdersStatus(OrderStatus.NEW);
        ordersEntity.setOrdersDate(LocalDateTime.now());
        ordersEntity.setOrdersMenuEntityList(ordersMenuEntityList);

        ordersRepository.save(ordersEntity);

        return ordersEntity.getIdx();
    }

    //주문 이력
    public Page<OrdersHistDTO> getOrderList(String memberEmail, Pageable pageable) {

        //주문 목록
        List<OrdersEntity> ordersEntityList = ordersRepository.findOrdersEntity(memberEmail, pageable);

        //페이징 처리를 위한 총 주문 목록의 수
        Integer totalCount = ordersRepository.totalcount(memberEmail);

        //주문 목록의 주문 메뉴들을 만들기 위한 List
        List<OrdersHistDTO> ordersHistDTOList = new ArrayList<>();

        // EntityToDTO //주문, 주문메뉴들, 주문메뉴들의 이미지
        for (OrdersEntity ordersEntity : ordersEntityList) {
            OrdersHistDTO ordersHistDTO = new OrdersHistDTO();
            ordersHistDTO.setOrdersIdx(ordersEntity.getIdx());
            ordersHistDTO.setOrdersDate(ordersEntity.getOrdersDate());
            ordersHistDTO.setOrderStatus(ordersEntity.getOrdersStatus());

            List<OrdersMenuEntity> ordersMenuEntityList = ordersEntity.getOrdersMenuEntityList();

            for (OrdersMenuEntity ordersMenuEntity : ordersMenuEntityList) {
                OrdersMenuDTO ordersMenuDTO = new OrdersMenuDTO();
                ordersMenuDTO.setIdx(ordersEntity.getIdx());
                ordersMenuDTO.setMenuName(ordersMenuEntity.getMenuEntity().getMenuName());
                ordersMenuDTO.setOrderPrice(ordersMenuEntity.getOrderPrice());
                ordersMenuDTO.setCount(ordersMenuEntity.getCount());

                //메뉴 주문 이미지 추후 작성
            }
            ordersHistDTOList.add(ordersHistDTO);
        }
        return new PageImpl<OrdersHistDTO>(ordersHistDTOList, pageable, totalCount);
    }


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

        List<OrdersMenuEntity> ordersMenuEntityList = ordersEntity.getOrdersMenuEntityList();

        for (OrdersMenuEntity ordersMenuEntity : ordersMenuEntityList) {
            OrdersMenuDTO ordersMenuDTO = new OrdersMenuDTO();
            ordersMenuDTO.setIdx(ordersMenuEntity.getIdx());
            ordersMenuDTO.setMenuName(ordersMenuEntity.getMenuEntity().getMenuName());
            ordersMenuDTO.setOrderPrice(ordersMenuEntity.getOrderPrice());
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

            List<OrdersMenuEntity> ordersMenuEntityList = ordersEntity.getOrdersMenuEntityList();

            for (OrdersMenuEntity ordersMenuEntity : ordersMenuEntityList) {
                OrdersMenuDTO ordersMenuDTO = new OrdersMenuDTO();
                ordersMenuDTO.setIdx(ordersMenuEntity.getIdx());
                ordersMenuDTO.setMenuName(ordersMenuEntity.getMenuEntity().getMenuName());
                ordersMenuDTO.setOrderPrice(ordersMenuEntity.getOrderPrice());
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



