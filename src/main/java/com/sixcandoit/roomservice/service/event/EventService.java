package com.sixcandoit.roomservice.service.event;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.sixcandoit.roomservice.dto.ImageFileDTO;
import com.sixcandoit.roomservice.dto.event.EventDTO;
import com.sixcandoit.roomservice.entity.ImageFileEntity;
import com.sixcandoit.roomservice.entity.event.EventEntity;
import com.sixcandoit.roomservice.repository.event.EventRepository;
import com.sixcandoit.roomservice.repository.member.MemberRepository;
import com.sixcandoit.roomservice.service.ImageFileService;
import com.sixcandoit.roomservice.service.S3Uploader;
import com.sixcandoit.roomservice.util.FileUpload;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log

public class EventService {
//    @Value("${dataUploadPath}")
//    private  String imgLocation;

    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;
    private final FileUpload fileUpload;
    private final EventRepository eventRepository;
    private final ImageFileService imageFileService;
    private final S3Uploader s3Uploader;


    /*-------------------------------------------------
    함수명 : register(EventDTO eventDTO)
    인수 : eventDTO
    출력 : 없음
    설명 : 이벤트를 등록할때 사용
    ---------------------------------------------------*/
    public void register(EventDTO eventDTO) {


        try {

            //변환
            EventEntity eventEntity = modelMapper.map(eventDTO, EventEntity.class);

            // 이미지 등록
            List<ImageFileEntity> images = imageFileService.saveImages(eventDTO.getFiles());
            System.out.println("S3 이미지 등록 완료");

            System.out.println("for문 들어오기전 검사:" + images);

            // 이미지 정보 추가
            // 양방향 연관관계 편의 메서드 사용
            for (ImageFileEntity image : images) {


                System.out.println("이미지 주소:" + image.getUrl());
                eventEntity.addImage(image);  // FK 자동 설정
            }

            System.out.println("FK 자동 등록");

            // 데이터 저장
            eventRepository.save(eventEntity);
            System.out.println("저장 최최최종");

        } catch (Exception e) {
            throw new RuntimeException("이미지 저장 실패 : " + e.getMessage());
        }
    }

    /*-------------------------------------------------
    함수명 : read(int idx)
    인수 : 조회할 이벤트 idx
    출력 : EventDTO
    설명 : 포인트를 조회할 때 사용
  ---------------------------------------------------*/
    public EventDTO read(Integer idx) {
        try {
            Optional<EventEntity> read = eventRepository.findById(idx);
            //정보가 있냐? 없냐? 판단
            if (!read.isPresent()) {//정보가 없으면
                throw new RuntimeException("이벤트 개별 조회 실패");

            } else { //정보가 있으면
                //System.out.println("체크1");
                EventDTO eventDTO = modelMapper.map(read, EventDTO.class);
                //System.out.println("체크2:" + eventDTO.getIdx());
                //System.out.println("체크3:" + imageFileService.readImage(eventDTO.getIdx(), "event"));
                List<ImageFileDTO> imageFileDTO = imageFileService.readImage(eventDTO.getIdx(), "event");

                //이미지 돌리기 판단
                for (ImageFileDTO image : imageFileDTO) {

                    if (image.getIdx() == eventDTO.getImageFileJoin().getFirst().getIdx() && image.getRepimageYn().equals("Y")) {
                        //log.info("체크4:" + image.getUrl());
                        eventDTO.setEventTitleImg(image.getUrl());
                        //log.info("체크5: 끝");
                    } else if (image.getIdx() == eventDTO.getImageFileJoin().getFirst().getIdx() && image.getRepimageYn().equals("N")) {
                        //log.info("체크6:" + image.getUrl());
                        eventDTO.setEventImg(image.getUrl());
                        //log.info("체크7:끝");

                    }
                    if (image.getIdx() == eventDTO.getImageFileJoin().getLast().getIdx() && image.getRepimageYn().equals("Y")) {
                        //log.info("체크4:" + image.getUrl());
                        eventDTO.setEventTitleImg(image.getUrl());
                        //log.info("체크5: 끝");
                    } else if (image.getIdx() == eventDTO.getImageFileJoin().getLast().getIdx() && image.getRepimageYn().equals("N")) {
                        //log.info("체크6:" + image.getUrl());
                        eventDTO.setEventImg(image.getUrl());
                        //log.info("체크7:끝");

                    }
                }


                return eventDTO;
            }
        } catch (Exception e) {
            throw new RuntimeException("이벤트 개별 조회 실패: " + e.getMessage());
        }
    }

    /*-------------------------------------------------
    함수명 : update(EventDTO eventDTO, MultipartFile multipartFile, MemberDTO memberDTO)
    인수 : event
    출력 : 없음
    설명 : 이벤트 정보를 수정할때 사용;
  ---------------------------------------------------*/
    public void update(EventDTO eventDTO, List<MultipartFile> imageFiles) {


        try {
            Optional<EventEntity> read = eventRepository.findById(eventDTO.getIdx());
            List<MultipartFile> imgFile = eventDTO.getFiles();

            // 빈 파일 제거 후 유효한 파일 리스트만 남김
            // 자꾸 빈파일이 넘겨져 파일을 넘길때와 같이 길이가 1이되어 오류가 생김으로 유효리스트 생성
            List<MultipartFile> validImageFiles = imgFile.stream()
                    .filter(file -> file != null && !file.isEmpty()) // 비어 있지 않은 파일만 필터링
                    .collect(Collectors.toList());

            System.out.println("유효한 이미지 파일 개수: " + validImageFiles.size());


            if (read.isEmpty()) {
                throw new RuntimeException("이벤트 조회 실패");
            } else {

                EventEntity eventEntity = modelMapper.map(eventDTO, EventEntity.class);
                System.out.println("변환까지");



                /*
                if (!eventDTO.getFiles().getFirst().isEmpty() && !eventDTO.getFiles().getLast().isEmpty()) {
                    if (imageFileDTOList.isEmpty()) {
                        System.out.println("둘다 없을때");
                        List<ImageFileEntity> images = imageFileService.saveImages(eventDTO.getFiles());
                        for (ImageFileEntity image : images) {
                            eventEntity.addImage(image);
                        }
                    }else {
                        System.out.println("그냥 저장됨");
                        imageFileService.deleteImage(imageFileDTOList.getFirst().getIdx());
                        imageFileService.deleteImage(imageFileDTOList.getLast().getIdx());
                        List<ImageFileEntity> images = imageFileService.updateImage(eventDTO.getFiles(), "event", eventDTO.getIdx());
                        eventEntity.updateImages(images);
                    }

                } else if (!eventDTO.getFiles().getFirst().isEmpty() && eventDTO.getFiles().getLast().isEmpty()) {
                    System.out.println("메인 이미지 보존");
                    if(imageFileDTOList.getFirst().getRepimageYn().equals("Y")) {
                        imageFileService.deleteImage(imageFileDTOList.getFirst().getIdx());
                    }
                    else {
                        imageFileService.deleteImage(imageFileDTOList.getLast().getIdx());
                    }
                    System.out.println("이미지 삭제까지 완료");
                    List<ImageFileEntity> images = imageFileService.updateImage(validImageFiles, "event", eventDTO.getIdx());
                    for (ImageFileEntity image : images) {
                        eventEntity.addImage(image);
                    }
                    eventEntity.updateImages(images);

                } else if (!eventDTO.getFiles().getLast().isEmpty() && eventDTO.getFiles().getFirst().isEmpty()) {
                    System.out.println("타이틀 이미지 보존");
                    if(imageFileDTOList.getFirst().getRepimageYn().equals("N")) {
                        imageFileService.deleteImage(imageFileDTOList.getFirst().getIdx());
                    }
                    else {
                        imageFileService.deleteImage(imageFileDTOList.getLast().getIdx());
                    }
                    System.out.println("이미지 삭제까지 완료");
                    List<ImageFileEntity> images = imageFileService.updateImage(validImageFiles, "event", eventDTO.getIdx());
                    for (ImageFileEntity image : images) {
                        eventEntity.addImage(image);
                    }
                    eventEntity.updateImages(images);


                }*/


                // 이미지 추가 등록
                log.info("수정 이미지 추가를 진행");
                List<ImageFileEntity> images = imageFileService.updateImage(imageFiles, "event", eventEntity.getIdx());
                // 이미지 정보 추가
                // 양방향 연관관계 편의 메서드 사용
                if (images != null && !images.isEmpty()) {
                    log.info("새로운 이미지가 등록되었습니다.");
                    for (ImageFileEntity image : images) {
                        eventEntity.addImage(image);  // FK 자동 설정
                    }
                } else {
                    log.warning("이미지가 없거나 저장 실패.");
                }
                //--------------------------------------------------------------//

                // 저장
                eventRepository.save(eventEntity);

                System.out.println("게시글 저장중1");




                System.out.println("게시글 저장 완료");
            }

        } catch (Exception e) {
            throw new RuntimeException("이벤트 수정 실패: " + e.getMessage());
        }


    }


    /*-------------------------------------------------
    함수명 : delete(MemberDTO member)
    인수 : memberDTO
    출력 : 없음
    설명 : 이벤트 정보를 삭제할때 사용
    ---------------------------------------------------*/
    public void delete(Integer idx) {
        try {

            List<ImageFileDTO> imageFileDTOList = imageFileService.readImage(idx, "event");

            if (!imageFileDTOList.isEmpty()) {
                imageFileService.deleteImage(imageFileDTOList.getFirst().getIdx());
                imageFileService.deleteImage(imageFileDTOList.getLast().getIdx());
                eventRepository.deleteById(idx);
                System.out.println("게시글삭제");
            } else if (imageFileDTOList.isEmpty()) {
                System.out.println("사진없는게시글삭제");
                eventRepository.deleteById(idx);

            } else if (!imageFileDTOList.getFirst().getUrl().isEmpty() && imageFileDTOList.getLast().getUrl().isEmpty()) {
                imageFileService.deleteImage(imageFileDTOList.getFirst().getIdx());
                System.out.println("사진삭제1");
                eventRepository.deleteById(idx);
                System.out.println("게시글삭제");
            } else if (!imageFileDTOList.getLast().getUrl().isEmpty() && imageFileDTOList.getFirst().getUrl().isEmpty()) {
                imageFileService.deleteImage(imageFileDTOList.getLast().getIdx());
                System.out.println("사진삭제2");
                eventRepository.deleteById(idx);
                System.out.println("게시글삭제");
            }


        } catch (Exception e) {
            throw new RuntimeException("이벤트 삭제 실패: " + e.getMessage());
        }
    }

    /*-------------------------------------------------
    함수명 : list(String keyword, String type, LocalDateTime startDate, LocalDateTime endDate, Pageable page)
    인수 :  keyword, type, startDate, endDate, page
    출력 : 목록
    설명 : 포인트 정보들을 목록을 출력할때 사용
    ---------------------------------------------------*/
    public Page<EventDTO> list(String keyword, String type, LocalDateTime startDate, LocalDateTime endDate, Pageable page) {
        System.out.println("Service = page: "+page+",type:"+type+",keyword:"+keyword+",StartDate:"+startDate+",endDate:"+endDate);
        //List<eventDTO> eventDTOlist = Arrays.asList(modelMapper.map(eventlist, eventDTO[].class));
        Pageable pageable= PageRequest.of(Math.max(page.getPageNumber()-1,0),10);
        System.out.println("실행중:"+pageable.getPageNumber());
        Page<EventEntity> eventEntities;
        if(!type.isEmpty()){
            switch (type) {
                case "1":
                    log.info("이벤트 내용으로 검색하는 중");
                    if(keyword!=null){
                        eventEntities = eventRepository.findByContents(keyword, pageable);
                        break;
                    }

                case "2":
                    log.info("이벤트 활성화로 검색하는 중");
                    if(keyword!=null){
                        eventEntities = eventRepository.findByActiveYn(keyword, pageable);
                        break;
                    }

                case "3":
                    log.info("이벤트 기간으로 검색중 중");
                    if(startDate!=null&&endDate!=null) {
                        eventEntities = eventRepository.findByDateRange(startDate, endDate, pageable);
                        break;
                    }

                default: eventEntities = eventRepository.findByContents(keyword, pageable);
            }
        }
        else{
            log.info("모든 대상으로 검색");
            eventEntities=eventRepository.findAll(pageable);
        }
        Page<EventDTO> eventDTOS=eventEntities.map(entity -> modelMapper.map(entity,EventDTO.class));
        System.out.println("실행중2:"+eventDTOS.getSize());

        return eventDTOS;
    }


    
    /*-------------------------------------------------
    함수명 : memberlist(Pageable page)
    인수 : Pageable
    출력 : 목록
    설명 : 회원별 포인트 목록을 출력할때 사용

    /*-------------------------------------------------


    /* 회원별 포인트 등록 */
    /* 회원별 포인트 수정 */
    /* 회원별 포인트 삭제 */
}