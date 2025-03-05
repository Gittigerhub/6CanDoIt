package com.sixcandoit.roomservice.controller.room;

import com.sixcandoit.roomservice.dto.ImageFileDTO;
import com.sixcandoit.roomservice.dto.room.ReservationDTO;
import com.sixcandoit.roomservice.dto.room.RoomDTO;
import com.sixcandoit.roomservice.entity.room.RoomEntity;
import com.sixcandoit.roomservice.repository.room.RoomRepository;
import com.sixcandoit.roomservice.service.ImageFileService;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Log4j2
public class RoomController {

    // final 선언
    private final RoomService roomService;
    private final ReservationService reservationService;
    private final RoomRepository roomRepository;
    private final ImageFileService imageFileService;

    // 룸 관리 메인 페이지
    @GetMapping("/room/main")
    public String main() {
        return "room/main";
    }

    // 룸 전체 목록
    @GetMapping("/room/list")
    private String list(@PageableDefault(page = 1) Pageable page, // 페이지 정보
                        @RequestParam(value = "type", defaultValue = "") String type, // 검색대상
                        @RequestParam(value = "keyword", defaultValue = "") String keyword, // 키워드
                        @RequestParam(value = "order", defaultValue = "") String order, // 가격순
                        Model model){
        // 해당페이지의 내용을 서비스를 통해 데이터베이스로부터 조회
        Page<RoomDTO> roomDTOList = roomService.roomList(page, type, keyword, order);
        // html에 필요한 페이지 정보를 받는다.
        Map<String, Integer> pageInfo = PageNationUtil.Pagination(roomDTOList);

        model.addAttribute("roomDTOList", roomDTOList); // 데이터 전달
        model.addAllAttributes(pageInfo); // 페이지 정보
        model.addAttribute("type", type); //검색분류
        model.addAttribute("keyword", keyword); // 키워드
        model.addAttribute("order", order); // 가격순

        return "room/list";
    }

    //관리자용 예약 목록 리스트
    @GetMapping("/room/reserve")
    public String reslist(@RequestParam(name = "sdate", required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate sdate,
                          @RequestParam(name = "edate", required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate edate,
                          Model model) {
        log.info("데이터 조회 후 목록페이지로 이동....");

        // Room 리스트 데이터 가져오기
        List<RoomDTO> roomDTOList = roomService.resList();
        model.addAttribute("roomDTOList", roomDTOList);  // roomList를 모델에 추가

        // 리다이렉트 후 전달된 성공 또는 실패 메시지를 받아옵니다.
        if (model.containsAttribute("errorMessage")) {
            // 에러 메시지가 있으면 모달을 띄워주도록 합니다.
            model.addAttribute("errorMessage", model.getAttribute("errorMessage"));
        }

        List<ReservationDTO> reserveDTOList = reservationService.reserveList(sdate, edate);

        model.addAttribute("reserveDTOList", reserveDTOList);
        model.addAttribute("sdate", sdate);
        model.addAttribute("edate", edate);

        return "room/reserve";
    }

    @PostMapping("/room/updateStatus/{idx}")
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


    // 룸 등록
    @GetMapping("/room/register")
    public String register(Model model){
        log.info("룸 등록 페이지로 이동합니다.");

        model.addAttribute("roomDTO", new RoomDTO()); // 빈 RoomDTO 객체 전달

        return "room/register";
    }

    // 룸 등록 저장 처리
    @PostMapping("/room/register")
    public String registerProc(@Valid @ModelAttribute RoomDTO roomDTO,
                               BindingResult bindingResult, List<MultipartFile> imageFiles){
        log.info("새로운 룸을 등록합니다.");

        // roomWifi, roomTv, roomAir, roomBath가 null일 경우 N으로 설정
        if (roomDTO.getRoomWifi() == null) {
            roomDTO.setRoomWifi("N");
        }
        if (roomDTO.getRoomTv() == null) {
            roomDTO.setRoomTv("N");
        }
        if (roomDTO.getRoomAir() == null) {
            roomDTO.setRoomAir("N");
        }
        if (roomDTO.getRoomBath() == null) {
            roomDTO.setRoomBath("N");
        }
        if (roomDTO.getRoomBreakfast() == null) {
            roomDTO.setRoomBreakfast("N");
        }
        if (roomDTO.getRoomSmokingYn() == null) {
            roomDTO.setRoomSmokingYn("N");
        }
        // 체크인, 체크아웃 값이 없으면 기본값을 설정
        if (roomDTO.getRoomCheckIn() == null) {
            roomDTO.setRoomCheckIn(LocalTime.of(14, 0)); // 기본 체크인 시간 (14:00)
        }
        if (roomDTO.getRoomCheckOut() == null) {
            roomDTO.setRoomCheckOut(LocalTime.of(11, 0)); // 기본 체크아웃 시간 (11:00)
        }

        if (bindingResult.hasErrors()){ // 유효성 검사에 실패 시
            log.info("유효성 검사 오류 발생");
            return "room/register"; // register로 돌아간다
        }

        // 등록 처리
        roomService.roomRegister(roomDTO, imageFiles);

        return "redirect:/room/list";
    }

    // 룸 상세보기
    @GetMapping("/room/detail")
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

        return "room/detail";
    }

    // 룸 수정 불러오기
    @GetMapping("/room/update")
    public String update(@RequestParam Integer idx, Model model){
        log.info("수정할 데이터를 읽는 중입니다.");
        RoomDTO roomDTO = roomService.roomDetail(idx);

        log.info("개별 데이터를 페이지로 전달합니다.");
        model.addAttribute("roomDTO", roomDTO);

        return "room/update";
    }

    // 룸 수정 수정하기
    @PostMapping("/room/update")
    public String updateProc(@Valid @ModelAttribute RoomDTO roomDTO,
                             BindingResult bindingResult,
                             List<MultipartFile> imageFiles){

        log.info("수정된 데이터를 저장합니다.");
        try {

            String join = "room";
            // roomWifi, roomTv, roomAir, roomBath가 null일 경우 N으로 설정
            if (roomDTO.getRoomWifi() == null) {
                roomDTO.setRoomWifi("N");
            }
            if (roomDTO.getRoomTv() == null) {
                roomDTO.setRoomTv("N");
            }
            if (roomDTO.getRoomAir() == null) {
                roomDTO.setRoomAir("N");
            }
            if (roomDTO.getRoomBath() == null) {
                roomDTO.setRoomBath("N");
            }
            if (roomDTO.getRoomBreakfast() == null) {
                roomDTO.setRoomBreakfast("N");
            }
            if (roomDTO.getRoomSmokingYn() == null) {
                roomDTO.setRoomSmokingYn("N");
            }
            // 체크인, 체크아웃 값이 없으면 기본값을 설정
            if (roomDTO.getRoomCheckIn() == null) {
                roomDTO.setRoomCheckIn(LocalTime.of(14, 0)); // 기본 체크인 시간 (14:00)
            }
            if (roomDTO.getRoomCheckOut() == null) {
                roomDTO.setRoomCheckOut(LocalTime.of(11, 0)); // 기본 체크아웃 시간 (11:00)
            }

            if (bindingResult.hasErrors()){ // 유효성 검사에 실패 시
                log.info("유효성 검사 오류 발생");
                return "room/update"; // update로 돌아간다
            }
            // 유효성 검사 성공 시 수정 처리
            roomService.roomUpdate(roomDTO, join, imageFiles);

            return "redirect:/room/detail?idx=" + roomDTO.getIdx();

        } catch (Exception e) {
            // 예외가 발생했을 경우 사용자에게 오류 메시지 전달
            log.error(e.getMessage());
            return "room/update"; // 수정 페이지로 돌아가면서 오류 메시지 전달
        }
    }

    // 룸 삭제
    @GetMapping("/room/delete")
    public String delete(@RequestParam Integer idx){

        // join값 생성
        String join = "room";

        log.info("데이터를 삭제합니다.");
        roomService.roomDelete(idx, join);

        return "redirect:/room/list";
    }

    // 성수기 등록 및 해제
    @GetMapping("/room/roomSeason/update")
    public String updateSeason(@RequestParam Integer idx, @RequestParam String roomSeason){
        roomService.updateSeason(idx, roomSeason);
        return "redirect:/room/detail?idx=" + idx; // 상세 페이지로 리다이렉트
    }
}
