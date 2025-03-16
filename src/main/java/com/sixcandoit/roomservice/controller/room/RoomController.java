package com.sixcandoit.roomservice.controller.room;

import com.sixcandoit.roomservice.dto.ImageFileDTO;
import com.sixcandoit.roomservice.dto.office.OrganizationDTO;
import com.sixcandoit.roomservice.dto.room.ReservationDTO;
import com.sixcandoit.roomservice.dto.room.RoomDTO;
import com.sixcandoit.roomservice.entity.room.RoomEntity;
import com.sixcandoit.roomservice.repository.room.RoomRepository;
import com.sixcandoit.roomservice.service.ImageFileService;
import com.sixcandoit.roomservice.service.office.OrganizationService;
import com.sixcandoit.roomservice.service.room.ReservationService;
import com.sixcandoit.roomservice.service.room.RoomService;
import com.sixcandoit.roomservice.util.PageNationUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Log4j2
public class RoomController {

    // final 선언
    private final RoomService roomService;
    private final ReservationService reservationService;
    private final RoomRepository roomRepository;
    private final ImageFileService imageFileService;
    private final OrganizationService organizationService;

    /* -----------------------------------------------------------------------------
       경로 : /room/main
       인수 : Integer organ_idx, Model model
       출력 : room/main 페이지로 이동
       설명 : 룸 관리 메인 페이지를 반환
    ----------------------------------------------------------------------------- */
    // 룸 관리 메인 페이지
    @GetMapping("/room/ho/main")
    public String main(@RequestParam(required = false) Integer organ_idx, Model model) {
        if (organ_idx != null) {
            model.addAttribute("organ_idx", organ_idx);
        }
        return "room/ho/main";
    }

    /* -----------------------------------------------------------------------------
       경로 : /room/list
       인수 : Pageable page, String type, String keyword, String order, Integer organ_idx, String roomType, Model model
       출력 : room/list 페이지로 이동
       설명 : 룸 목록을 조회하고 페이지에 데이터를 전달하여 반환
    ----------------------------------------------------------------------------- */
    // 룸 전체 목록
    @GetMapping("/room/ho/list")
    private String list(@PageableDefault(page = 1) Pageable page,
                        @RequestParam(value = "type", defaultValue = "") String type,
                        @RequestParam(value = "keyword", defaultValue = "") String keyword,
                        @RequestParam(value = "order", defaultValue = "") String order,
                        @RequestParam(required = false) Integer organ_idx,
                        @RequestParam(value = "roomType", defaultValue = "") String roomType,
                        Model model) {
        log.info("룸 목록을 출력합니다.");

        // 조직별 룸 목록 조회
        Page<RoomDTO> roomDTOS = roomService.roomList(page, type, keyword, order, organ_idx, roomType);

        // 페이지 정보 생성
        Map<String, Integer> pageInfo = PageNationUtil.Pagination(roomDTOS);

        // 이미지 정보 가져오기
        Map<Integer, List<ImageFileDTO>> imageFileMap = new HashMap<>();
        Map<Integer, Boolean> repImageMap = new HashMap<>();

        for (RoomDTO roomDTO : roomDTOS.getContent()) {
            List<ImageFileDTO> imageList = imageFileService.readImage(roomDTO.getIdx(), "room");
            imageFileMap.put(roomDTO.getIdx(), imageList);

            // 대표이미지 존재 여부 확인
            boolean hasRepImage = imageList.stream()
                    .anyMatch(imageFileDTO -> "Y".equals(imageFileDTO.getRepimageYn()));
            repImageMap.put(roomDTO.getIdx(), hasRepImage);
        }

        // 페이지 정보를 모델에 추가
        model.addAttribute("roomDTOS", roomDTOS);
        model.addAttribute("currentPage", pageInfo.get("currentPage"));
        model.addAttribute("startPage", pageInfo.get("startPage"));
        model.addAttribute("endPage", pageInfo.get("endPage"));
        model.addAttribute("prevPage", pageInfo.get("prevPage"));
        model.addAttribute("nextPage", pageInfo.get("nextPage"));
        model.addAttribute("type", type);
        model.addAttribute("keyword", keyword);
        model.addAttribute("order", order);
        model.addAttribute("organ_idx", organ_idx);
        model.addAttribute("roomType", roomType);

        // 이미지 정보를 모델에 추가
        model.addAttribute("imageFileMap", imageFileMap);
        model.addAttribute("repImageMap", repImageMap);

        return "room/ho/list";
    }

    /* -----------------------------------------------------------------------------
       경로 : /room/reserve
       인수 : LocalDate sdate, LocalDate edate, Integer organ_idx, Model model
       출력 : room/reserve 페이지로 이동
       설명 : 예약 목록을 조회하여 페이지로 전달
    ----------------------------------------------------------------------------- */
    //관리자용 예약 목록 리스트
    @GetMapping("/room/ho/reserve")
    public String reslist(@RequestParam(name = "sdate", required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate sdate,
                          @RequestParam(name = "edate", required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate edate,
                          @RequestParam(required = false) Integer organ_idx,
                          Model model) {
        log.info("데이터 조회 후 목록페이지로 이동....");

        // Room 리스트 데이터 가져오기 (organ_idx에 따라 필터링)
        List<RoomDTO> roomDTOList;
        if (organ_idx != null) {
            // organ_idx가 있는 경우 해당 organization의 room만 조회
            roomDTOList = roomService.resList();  // 전체 목록 조회
            // organ_idx로 필터링
            roomDTOList = roomDTOList.stream()
                    .filter(room -> room.getOrgan_idx() != null && room.getOrgan_idx().equals(organ_idx))
                    .collect(Collectors.toList());
        } else {
            roomDTOList = roomService.resList();
        }
        model.addAttribute("roomDTOList", roomDTOList);

        // 리다이렉트 후 전달된 성공 또는 실패 메시지를 받아옵니다.
        if (model.containsAttribute("errorMessage")) {
            model.addAttribute("errorMessage", model.getAttribute("errorMessage"));
        }

        // 예약 리스트 데이터 가져오기 (organ_idx에 따라 필터링)
        List<ReservationDTO> reserveDTOList;
        if (organ_idx != null) {
            // organ_idx에 해당하는 room들의 예약만 조회
            reserveDTOList = reservationService.reserveListByOrganization(organ_idx, sdate, edate);
        } else {
            reserveDTOList = reservationService.reserveList(sdate, edate);
        }

        model.addAttribute("reserveDTOList", reserveDTOList);
        model.addAttribute("sdate", sdate);
        model.addAttribute("edate", edate);
        model.addAttribute("organ_idx", organ_idx);

        return "room/ho/reserve";
    }

    /* -----------------------------------------------------------------------------
       경로 : /room/updateStatus/{idx}
       인수 : Integer idx, Map<String, String> requestBody
       출력 : JSON 응답
       설명 : 특정 방의 상태를 업데이트
    ----------------------------------------------------------------------------- */
    @PostMapping("/room/ho/updateStatus/{idx}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateRoomStatus(@PathVariable Integer idx, @RequestBody Map<String, String> requestBody) {
        String newStatus = requestBody.get("status");

        // roomidx에 해당하는 방을 roomRepository를 통해 찾아 상태 변경
        Optional<RoomEntity> roomEntityOpt = roomRepository.findById(idx);
        if (!roomEntityOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("success", false));
        }

        RoomEntity room = roomEntityOpt.get();

        // 상태 값 업데이트
        room.setResStatus(newStatus);
        roomRepository.save(room); // 상태가 업데이트된 방을 저장

        // 상태 변경 성공 메시지 반환
        return ResponseEntity.ok(Collections.singletonMap("success", true));
    }

    /* -----------------------------------------------------------------------------
       경로 : /room/register
       인수 : Integer organ_idx, Model model
       출력 : room/register 페이지로 이동
       설명 : 룸 등록 페이지로 이동하며 초기 룸 DTO를 모델에 추가
    ----------------------------------------------------------------------------- */
    // 룸 등록
    @GetMapping("/room/ho/register")
    public String register(@RequestParam(required = false) Integer organ_idx, Model model){
        log.info("룸 등록 페이지로 이동합니다.");

        RoomDTO roomDTO = new RoomDTO();
        // 기본값 설정
        roomDTO.setRoomNum(2);                // 투숙객 수 기본값
        roomDTO.setRoomBed(0);                // 침대 타입 기본값
        roomDTO.setRoomPrice(0);              // 가격 기본값
        roomDTO.setRoomSize(0);               // 평수 기본값
        roomDTO.setRoomWifi("N");             // 와이파이 기본값
        roomDTO.setRoomTv("N");               // TV 기본값
        roomDTO.setRoomAir("N");              // 에어컨 기본값
        roomDTO.setRoomBath("N");             // 전용욕실 기본값
        roomDTO.setRoomBreakfast("N");        // 조식 기본값
        roomDTO.setRoomSmokingYn("N");        // 흡연 기본값
        roomDTO.setRoomCheckIn(LocalTime.of(14, 0));    // 체크인 시간 기본값
        roomDTO.setRoomCheckOut(LocalTime.of(11, 0));   // 체크아웃 시간 기본값

        if (organ_idx != null) {
            roomDTO.setOrgan_idx(organ_idx);
        }

        model.addAttribute("roomDTO", roomDTO);
        model.addAttribute("organ_idx", organ_idx);

        return "room/ho/register";
    }

    /* -----------------------------------------------------------------------------
       경로 : /room/register
       인수 : RoomDTO roomDTO, BindingResult bindingResult, List<MultipartFile> imageFiles
       출력 : room/list 페이지로 리다이렉트
       설명 : 새로운 룸을 등록 처리하고, 등록이 완료된 후 룸 목록으로 리다이렉트
    ----------------------------------------------------------------------------- */
    // 룸 등록 저장 처리
    @PostMapping("/room/ho/register")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> registerProc(@Valid @ModelAttribute RoomDTO roomDTO,
                               BindingResult bindingResult, List<MultipartFile> imageFiles) {
        log.info("새로운 룸을 등록합니다.");

        Map<String, Object> response = new HashMap<>();

        if (bindingResult.hasErrors()) {
            log.info("유효성 검사 오류 발생");
            bindingResult.getAllErrors().forEach(error -> log.error("Validation error: " + error.getDefaultMessage()));
            response.put("success", false);
            response.put("message", "입력값을 확인해주세요.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // roomWifi, roomTv, roomAir, roomBath가 null일 경우 N으로 설정
            if (roomDTO.getRoomWifi() == null) roomDTO.setRoomWifi("N");
            if (roomDTO.getRoomTv() == null) roomDTO.setRoomTv("N");
            if (roomDTO.getRoomAir() == null) roomDTO.setRoomAir("N");
            if (roomDTO.getRoomBath() == null) roomDTO.setRoomBath("N");
            if (roomDTO.getRoomBreakfast() == null) roomDTO.setRoomBreakfast("N");
            if (roomDTO.getRoomSmokingYn() == null) roomDTO.setRoomSmokingYn("N");

            // 체크인, 체크아웃 값이 없으면 기본값을 설정
            if (roomDTO.getRoomCheckIn() == null) roomDTO.setRoomCheckIn(LocalTime.of(14, 0));
            if (roomDTO.getRoomCheckOut() == null) roomDTO.setRoomCheckOut(LocalTime.of(11, 0));

            // 등록 처리
            roomService.roomRegister(roomDTO, imageFiles);

            response.put("success", true);
            response.put("message", "객실이 성공적으로 등록되었습니다.");
            response.put("redirectUrl", "/room/ho/list" + (roomDTO.getOrgan_idx() != null ? "?organ_idx=" + roomDTO.getOrgan_idx() : ""));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("객실 등록 중 오류 발생: " + e.getMessage());
            response.put("success", false);
            response.put("message", "객실 등록 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /* -----------------------------------------------------------------------------
       경로 : /room/detail
       인수 : Integer idx, Model model
       출력 : room/detail 페이지로 이동
       설명 : 특정 룸의 상세 정보를 조회하여 페이지에 전달
    ----------------------------------------------------------------------------- */
    // 룸 상세보기
    @GetMapping("/room/ho/detail")
    public String detail(@RequestParam Integer idx, Model model){

        // join값 생성
        String join = "room";

        log.info("룸 개별 데이터를 읽는 중입니다.");
        RoomDTO roomDTO = roomService.roomDetail(idx);

        log.info("룸 이미지 데이터를 읽는 중입니다.");
        List<ImageFileDTO> imageFileDTOS =
                imageFileService.readImage(idx, join);

        // 대표이미지 존재여부 확인
        boolean hasRepImage = imageFileDTOS.stream()
                .anyMatch(imageFileDTO -> "Y".equals(imageFileDTO.getRepimageYn()));

        log.info("룸 개별 데이터를 페이지에 전달하는 중입니다.");
        model.addAttribute("roomDTO", roomDTO);
        model.addAttribute("imageFileDTOS", imageFileDTOS);
        model.addAttribute("hasRepImage", hasRepImage);

        return "room/ho/detail";
    }

    /* -----------------------------------------------------------------------------
       경로 : /room/update
       인수 : Integer idx, Model model
       출력 : room/update 페이지로 이동
       설명 : 특정 룸의 데이터를 수정하기 위해 해당 데이터를 페이지로 전달
    ----------------------------------------------------------------------------- */
    // 룸 수정 불러오기
    @GetMapping("/room/ho/update")
    public String update(@RequestParam Integer idx, Model model){
        log.info("수정할 데이터를 읽는 중입니다.");
        RoomDTO roomDTO = roomService.roomDetail(idx);

        log.info("개별 데이터를 페이지로 전달합니다.");
        model.addAttribute("roomDTO", roomDTO);

        return "room/ho/update";
    }

    /* -----------------------------------------------------------------------------
       경로 : /room/update
       인수 : RoomDTO roomDTO, BindingResult bindingResult, List<MultipartFile> imageFiles
       출력 : room/detail 페이지로 리다이렉트
       설명 : 수정된 룸 데이터를 저장하고, 저장 후 상세 페이지로 리다이렉트
    ----------------------------------------------------------------------------- */
    // 룸 수정 수정하기
    @PostMapping("/room/ho/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateProc(@Valid @ModelAttribute RoomDTO roomDTO,
                             BindingResult bindingResult,
                             List<MultipartFile> imageFiles) {
        Map<String, Object> response = new HashMap<>();

        if (bindingResult.hasErrors()) {
            log.info("유효성 검사 오류 발생");
            bindingResult.getAllErrors().forEach(error -> log.error("Validation error: " + error.getDefaultMessage()));
            response.put("success", false);
            response.put("message", "입력값을 확인해주세요.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            String join = "room";
            // roomWifi, roomTv, roomAir, roomBath가 null일 경우 N으로 설정
            if (roomDTO.getRoomWifi() == null) roomDTO.setRoomWifi("N");
            if (roomDTO.getRoomTv() == null) roomDTO.setRoomTv("N");
            if (roomDTO.getRoomAir() == null) roomDTO.setRoomAir("N");
            if (roomDTO.getRoomBath() == null) roomDTO.setRoomBath("N");
            if (roomDTO.getRoomBreakfast() == null) roomDTO.setRoomBreakfast("N");
            if (roomDTO.getRoomSmokingYn() == null) roomDTO.setRoomSmokingYn("N");

            // 체크인, 체크아웃 값이 없으면 기본값을 설정
            if (roomDTO.getRoomCheckIn() == null) roomDTO.setRoomCheckIn(LocalTime.of(14, 0));
            if (roomDTO.getRoomCheckOut() == null) roomDTO.setRoomCheckOut(LocalTime.of(11, 0));

            roomService.roomUpdate(roomDTO, join, imageFiles);

            response.put("success", true);
            response.put("message", "객실이 성공적으로 수정되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("객실 수정 중 오류 발생: " + e.getMessage());
            response.put("success", false);
            response.put("message", "객실 수정 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /* -----------------------------------------------------------------------------
       경로 : /room/delete
       인수 : Integer idx
       출력 : room/list 페이지로 리다이렉트
       설명 : 특정 룸 데이터를 삭제하고, 룸 목록 페이지로 리다이렉트
    ----------------------------------------------------------------------------- */
    // 룸 삭제
    @GetMapping("/room/ho/delete")
    public String delete(@RequestParam Integer idx){

        // join값 생성
        String join = "room";

        log.info("데이터를 삭제합니다.");
        roomService.roomDelete(idx, join);

        return "redirect:/room/ho/list";
    }

    /* -----------------------------------------------------------------------------
       경로 : /room/roomSeason/update
       인수 : Integer idx, String roomSeason
       출력 : room/detail 페이지로 리다이렉트
       설명 : 룸의 성수기를 등록하거나 해제하며, 완료 후 룸 상세 페이지로 리다이렉트
    ----------------------------------------------------------------------------- */
    // 성수기 등록 및 해제
    @GetMapping("/room/ho/roomSeason/update")
    public String updateSeason(@RequestParam Integer idx, @RequestParam String roomSeason){
        roomService.updateSeason(idx, roomSeason);
        return "redirect:/room/ho/detail?idx=" + idx; // 상세 페이지로 리다이렉트
    }

    /* -----------------------------------------------------------------------------
       경로 : /room/member/list
       인수 : Integer organ_idx, LocalDate startDate, LocalDate endDate, Principal principal, Model model
       출력 : room/member/list 페이지로 이동
       설명 : 특정 조직의 룸 목록과 예약을 조회하여 회원 페이지에 전달
    ----------------------------------------------------------------------------- */
    // 호텔 상세 + 룸 조회 + 룸 예약
    @GetMapping("/room/member/list")
    public String memberRoomList(@RequestParam(required = false) Integer organ_idx,
                                 @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                 @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                 Principal principal,
                                 Model model) {
        log.info("memberRoomList called with organ_idx: {}, principal: {}", organ_idx, principal);

        // 객실 목록 조회
        List<RoomDTO> roomDTOS = roomService.getRoomsByOrganizationForMember(organ_idx);
        model.addAttribute("roomDTOS", roomDTOS);

        // 이미지 맵 생성
        Map<Integer, List<ImageFileDTO>> imageFileMap = new HashMap<>();
        Map<Integer, Boolean> repImageMap = new HashMap<>();

        // 각 룸의 이미지 정보 조회
        for (RoomDTO roomDTO : roomDTOS) {
            List<ImageFileDTO> imageFileDTOS = imageFileService.readImage(roomDTO.getIdx(), "room");
            imageFileMap.put(roomDTO.getIdx(), imageFileDTOS);
            repImageMap.put(roomDTO.getIdx(), imageFileDTOS.stream().anyMatch(img -> "Y".equals(img.getRepimageYn())));
        }

        model.addAttribute("imageFileMap", imageFileMap);
        model.addAttribute("repImageMap", repImageMap);

        // 조직 정보 조회 (organ_idx가 있는 경우 해당 조직 정보도 조회)
        if (organ_idx != null) {
            organizationService.findById(organ_idx).ifPresent(organization -> {
                model.addAttribute("organization", organization);
                log.info("Found organization: {}", organization.getOrganName());

                // 조직 상세 정보 (organDetail)도 가져와서 모델에 추가
                OrganizationDTO organDTO = organizationService.organRead(organ_idx);
                model.addAttribute("organDTO", organDTO);

                // 조직 이미지 조회
                List<ImageFileDTO> organImageFileDTOS = imageFileService.readImage(organ_idx, "organ");
                model.addAttribute("imageFileDTOS", organImageFileDTOS);

                // 대표 이미지 여부 확인
                boolean hasRepImage = organImageFileDTOS.stream()
                        .anyMatch(imageFileDTO -> "Y".equals(imageFileDTO.getRepimageYn()));
                model.addAttribute("hasRepImage", hasRepImage);
            });
        }

        // 로그인한 사용자의 예약 목록 조회
        if (principal != null) {
            try {
                List<ReservationDTO> reservations;
                if (organ_idx != null) {
                    // 특정 숙소의 예약만 조회
                    reservations = reservationService.getUserReservationsByOrganization(principal.getName(), organ_idx);
                    log.info("Fetching reservations for user {} and organization {}", principal.getName(), organ_idx);
                } else {
                    // 전체 예약 조회
                    reservations = reservationService.getUserReservations(principal.getName());
                    log.info("Fetching all reservations for user {}", principal.getName());
                }
                model.addAttribute("reservations", reservations);
            } catch (Exception e) {
                log.error("Error fetching reservations for user: {}", principal.getName(), e);
                // 에러가 발생해도 페이지는 계속 로드되도록 함
                model.addAttribute("reservations", new ArrayList<>());
            }
        } else {
            log.info("No principal found - user is not logged in");
        }

        return "room/member/list"; // 해당 템플릿에 room과 organ 정보가 모두 포함되어 렌더링됨
    }
}
