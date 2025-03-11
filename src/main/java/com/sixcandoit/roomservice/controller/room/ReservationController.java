package com.sixcandoit.roomservice.controller.room;

import com.sixcandoit.roomservice.dto.ImageFileDTO;
import com.sixcandoit.roomservice.dto.member.MemberDTO;
import com.sixcandoit.roomservice.dto.room.ReservationDTO;
import com.sixcandoit.roomservice.dto.room.RoomDTO;
import com.sixcandoit.roomservice.entity.room.ReservationEntity;
import com.sixcandoit.roomservice.entity.room.RoomEntity;
import com.sixcandoit.roomservice.repository.room.ReservationRepository;
import com.sixcandoit.roomservice.repository.room.RoomRepository;
import com.sixcandoit.roomservice.service.ImageFileService;
import com.sixcandoit.roomservice.service.member.MemberService;
import com.sixcandoit.roomservice.service.room.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/res")
public class ReservationController {

    private final ReservationService reservationService;
    private final RoomRepository roomRepository;
    private final ModelMapper modelMapper;
    private final ReservationRepository reservationRepository;
    private final ImageFileService imageFileService;
    private final MemberService memberService;

    //등록폼으로 이동
    @GetMapping("/create")
    public String createForm(Model model) {

        log.info("등록 페이지로 이동....");

        // 룸 데이터 조회
        List<RoomEntity> roomEntities  = roomRepository.findAll(); // RoomEntity 리스트
        List<RoomDTO> roomDTOList = roomEntities.stream()
                .map(room -> modelMapper.map(room, RoomDTO.class))  // ModelMapper를 이용해 변환
                .collect(Collectors.toList());

        // 조회된 룸 데이터와 멤버 데이터를 모델에 추가
        model.addAttribute("roomDTOList", roomDTOList);

        return "reserve/insert"; // Insert.html 뷰 템플릿을 찾아서 렌더링
    }

    @PostMapping("/updateStatus/{idx}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateResStatus(@PathVariable Integer idx, @RequestBody Map<String, String> requestBody) {
        String newStatus = requestBody.get("status");

        // roomidx에 해당하는 방을 roomRepository를 통해 찾아 상태 변경
        Optional<ReservationEntity> reservationEntityOpt = reservationRepository.findById(idx);
        if (!reservationEntityOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("success", false));
        }

        ReservationEntity res = reservationEntityOpt.get();

        // 상태 값 업데이트
        res.setResStatus(newStatus);
        reservationRepository.save(res); // 상태가 업데이트된 방을 저장

        // 상태 변경 성공 메시지 반환
        return ResponseEntity.ok(Collections.singletonMap("success", true));
    }

    // 예외 발생 시 에러 메시지 전달
    @ExceptionHandler(RuntimeException.class)
    public String handleException(RuntimeException e, RedirectAttributes redirectAttributes) {
        log.error("예외 처리 예외 발생: {}", e.getMessage());  // 예외 로그 추가
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:/res/create"; // 예약 등록 페이지로 리다이렉트
    }

    //등폭폼에서 입력한 내용을 저장
    @PostMapping("/create")
    public String createPage(@ModelAttribute ReservationDTO reservationDTO,
                             RedirectAttributes redirectAttributes,
                             Principal principal) {
        log.info("등록 처리 후 목록페이지로 이동하는 postmapping 시작....");

        try {
            // 현재 로그인한 사용자 정보 설정
            MemberDTO memberDTO = memberService.read(principal.getName());
            reservationDTO.setMemberDTO(memberDTO);
            reservationDTO.setMemberName(memberDTO.getMemberName());
            reservationDTO.setUsername(memberDTO.getMemberName());

            reservationService.reserveInsert(reservationDTO);
            redirectAttributes.addFlashAttribute("successMessage", "저장하였습니다.");
            log.info("등록 처리 후....");
        } catch (RuntimeException e) {
            log.error("등록 예외 발생: {}", e.getMessage());  // 예외 메시지 확인 로그 추가
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        log.info("목록페이지로 이동...");
        return "redirect:/res/list";
    }

    // AJAX 예약 요청을 처리하는 새로운 엔드포인트
    @PostMapping("/create/ajax")
    @ResponseBody
    public ResponseEntity<?> createReservationAjax(@RequestBody ReservationDTO reservationDTO,
                                                 Principal principal) {
        log.info("AJAX 예약 요청 처리 시작.... reservationDTO: {}", reservationDTO);

        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("message", "로그인이 필요합니다."));
            }

            // 현재 로그인한 사용자 정보 설정
            MemberDTO memberDTO = memberService.read(principal.getName());
            reservationDTO.setMemberDTO(memberDTO);
            reservationDTO.setMemberName(memberDTO.getMemberName());
            reservationDTO.setUsername(memberDTO.getMemberName());

            ReservationDTO savedReservation = reservationService.reserveInsert(reservationDTO);
            if (savedReservation != null) {
                return ResponseEntity.ok(Collections.singletonMap("message", "예약이 완료되었습니다."));
            } else {
                return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("message", "예약 처리 중 오류가 발생했습니다."));
            }
        } catch (RuntimeException e) {
            log.error("예약 처리 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    // 빈 객실 목록 조회
    @GetMapping("/available")
    @ResponseBody
    public ResponseEntity<?> getAvailableRooms(
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate,
            @RequestParam("organ_idx") Integer organ_idx) {
        
        log.info("빈 객실 목록 조회... organization: {}, startDate: {}, endDate: {}", organ_idx, startDate, endDate);

        try {
            // 빈 객실 목록을 조회 (organization_idx 기준)
            List<RoomEntity> availableRooms = reservationService.getAvailableRooms(organ_idx, startDate, endDate);
            
            // Entity를 DTO로 변환
            List<RoomDTO> roomDTOs = availableRooms.stream()
                    .map(room -> modelMapper.map(room, RoomDTO.class))
                    .collect(Collectors.toList());

            // 각 룸의 이미지 정보를 별도의 맵으로 관리
            Map<Integer, List<ImageFileDTO>> imageMap = new HashMap<>();
            for (RoomEntity room : availableRooms) {
                List<ImageFileDTO> images = imageFileService.readImage(room.getIdx(), "room");
                imageMap.put(room.getIdx(), images);
            }

            // 응답 데이터 구성
            Map<String, Object> response = new HashMap<>();
            response.put("rooms", roomDTOs);
            response.put("images", imageMap);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("빈 객실 조회 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "객실 조회 중 오류가 발생했습니다."));
        }
    }

    //목록페이지로 이동
    @GetMapping("/list")
    public String getAllPages(@RequestParam(name = "sdate", required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate sdate,
                              @RequestParam(name = "edate", required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate edate,
                              Model model) {
        log.info("데이터 조회 후 목록페이지로 이동....");

        // 리다이렉트 후 전달된 성공 또는 실패 메시지를 받아옵니다.
        if (model.containsAttribute("errorMessage")) {
            model.addAttribute("errorMessage", model.getAttribute("errorMessage"));
        }
        List<ReservationDTO> reserveDTOList = reservationService.reserveList(sdate, edate);

        model.addAttribute("List", reserveDTOList);
        model.addAttribute("sdate", sdate);
        model.addAttribute("edate", edate);

        return "reserve/list"; // page-list.html 뷰 템플릿을 찾아서 렌더링
    }

    @GetMapping("/detail")
    public String getPageById(@RequestParam("idx") Integer idx, Model model) {
        log.info("데이터 조회 후 상세페이지로 이동....");
        ReservationDTO reserveDTO = reservationService.reserveRead(idx);

        model.addAttribute("data", reserveDTO);
        return "reserve/detail"; // page-details.html 뷰 템플릿을 찾아서 렌더링
    }

    @GetMapping("/update/{idx}")
    public String updateForm(@PathVariable Integer idx, Model model) {
        log.info("데이터 조회 후 수정페이지로 이동....");
        ReservationDTO reserveDTO = reservationService.reserveRead(idx);

        // 룸 데이터 조회
        List<RoomEntity> roomEntities  = roomRepository.findAll(); // RoomEntity 리스트
        List<RoomDTO> roomDTOList = roomEntities.stream()
                .map(room -> modelMapper.map(room, RoomDTO.class))  // ModelMapper를 이용해 변환
                .collect(Collectors.toList());

        model.addAttribute("data", reserveDTO);
        model.addAttribute("roomDTOList", roomDTOList);
        return "reserve/update"; // update-form.html 뷰 템플릿을 찾아서 렌더링
    }

    // 예약 수정 처리
    @PostMapping("/update")
    public String updatePage(@ModelAttribute ReservationDTO updatedReserveDTO,
                             RedirectAttributes redirectAttributes) {
        log.info("수정 처리 후 목록페이지로 이동....");

        try {
            reservationService.reserveUpdate(updatedReserveDTO);
            redirectAttributes.addFlashAttribute("successMessage", "수정하였습니다.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/res/list";
    }

    // 삭제 처리 (AJAX 요청 처리)
    @PostMapping("/delete/{idx}")
    @ResponseBody
    public ResponseEntity<?> deleteReservation(@PathVariable Integer idx) {
        log.info("예약 삭제 처리 시작.... idx: {}", idx);
        try {
            reservationService.reserveDelete(idx);
            return ResponseEntity.ok().body(Collections.singletonMap("message", "예약이 취소되었습니다."));
        } catch (Exception e) {
            log.error("예약 삭제 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "예약 취소 중 오류가 발생했습니다."));
        }
    }

    @PostMapping("/update/{idx}")
    @ResponseBody
    public ResponseEntity<?> updateReservation(@PathVariable Integer idx, @RequestBody ReservationDTO reservationDTO) {
        log.info("예약 변경 처리 시작....");
        try {
            reservationDTO.setIdx(idx);
            ReservationDTO updatedReservation = reservationService.reserveUpdate(reservationDTO);
            if (updatedReservation != null) {
                return ResponseEntity.ok().body(Collections.singletonMap("message", "예약이 변경되었습니다."));
            } else {
                return ResponseEntity.badRequest().body(Collections.singletonMap("message", "예약을 찾을 수 없습니다."));
            }
        } catch (RuntimeException e) {
            log.error("예약 변경 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
        }
    }
}
