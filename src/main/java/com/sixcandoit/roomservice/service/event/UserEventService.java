package com.sixcandoit.roomservice.service.event;

import com.sixcandoit.roomservice.dto.ImageFileDTO;
import com.sixcandoit.roomservice.dto.event.EventDTO;
import com.sixcandoit.roomservice.entity.event.EventEntity;
import com.sixcandoit.roomservice.repository.event.EventRepository;
import com.sixcandoit.roomservice.service.ImageFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserEventService {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final ImageFileService imageFileService;

    /*-------------------------------------------------
    함수명 : list(Pageable page)
    인수 : Pageable
    출력 : 목록
    설명 : 포인트 정보들을 목록을 출력할때 사용
    ---------------------------------------------------*/
    public Page<EventDTO> list(String keyword, String type, LocalDateTime StartDate, LocalDateTime endDate, Pageable page) {
       // List<EventEntity> eventlist = eventRepository.findAll();
        System.out.println("Service = page: "+page+",type:"+type+",keyword:"+keyword);
        //List<eventDTO> eventDTOlist = Arrays.asList(modelMapper.map(eventlist, eventDTO[].class));
        Pageable pageable= PageRequest.of(Math.max(page.getPageNumber()-1,0),10);
        System.out.println("실행중:"+pageable.getPageNumber());
        Page<EventEntity> eventEntities;
        if(!keyword.isEmpty()){
            if(type.equals("1")){
                log.info("이벤트 내용으로 검색하는 중");
                eventEntities = eventRepository.findByContents(keyword,pageable);
            }else if(type.equals("2")){
                log.info("이벤트 활성화로 검색하는 중");
                eventEntities=eventRepository.findByActiveYn(keyword,pageable);
            }else {
                log.info("이벤트 기간으로 검색중 중");
                eventEntities = eventRepository.findByDateRange(StartDate,endDate,pageable);
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
}
