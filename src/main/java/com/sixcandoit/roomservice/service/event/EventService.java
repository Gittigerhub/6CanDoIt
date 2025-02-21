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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
    public void update(EventDTO eventDTO) {
        try {
            Optional<EventEntity> read = eventRepository.findById(eventDTO.getIdx());
            List<ImageFileDTO> imageFileDTOList = imageFileService.readImage(eventDTO.getIdx(), "event");

            if (!read.isPresent()) {
                throw new RuntimeException("이벤트 조회 실패");
            } else {



                EventEntity eventEntity = modelMapper.map(eventDTO, EventEntity.class);

                imageFileService.deleteImage(imageFileDTOList.getFirst().getIdx());
                imageFileService.deleteImage(imageFileDTOList.getLast().getIdx());

                List<ImageFileEntity> images = imageFileService.updateImage(eventDTO.getFiles(), "event", eventDTO.getIdx());



                eventEntity.updateImages(images);  // FK 자동 설정




                eventRepository.save(eventEntity);
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

            imageFileService.deleteImage(imageFileDTOList.getFirst().getIdx());
            imageFileService.deleteImage(imageFileDTOList.getLast().getIdx());
            eventRepository.deleteById(idx);


        } catch (Exception e) {
            throw new RuntimeException("이벤트 삭제 실패: " + e.getMessage());
        }
    }

    /*-------------------------------------------------
    함수명 : list(Pageable page)
    인수 : Pageable
    출력 : 목록
    설명 : 포인트 정보들을 목록을 출력할때 사용
    ---------------------------------------------------*/
    public List<EventDTO> list() {
        List<EventEntity> eventlist = eventRepository.findAll();

        List<EventDTO> eventDTOlist = Arrays.asList(modelMapper.map(eventlist, EventDTO[].class));

        return eventDTOlist;
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