package com.sixcandoit.roomservice.service.event;


import com.sixcandoit.roomservice.dto.event.MemberPointDTO;
import com.sixcandoit.roomservice.entity.event.MemberPointEntity;
import com.sixcandoit.roomservice.repository.event.MemberPointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Log4j2

public class UserMemberPointService {
    private final MemberPointRepository memberPointRepository;
    private final ModelMapper modelMapper;

    /*-------------------------------------------------
    함수명 : memberlist(Integer idx,Pageable page, String type, String keyword)
    인수 : Integer idx,Pageable page, String type, String keyword
    출력 : list
    설명 : 유져가 포인트 목록 볼때 사용
    ---------------------------------------------------*/
    public Page<MemberPointDTO> memberlist(Integer idx,Pageable page, String type, String keyword){

        try {
            List<MemberPointEntity> memberPointlist = memberPointRepository.findByMemberJoinIdx(idx);
            if(!memberPointlist.isEmpty()) {
               // List<MemberPointDTO> memberPointDTOlist = Arrays.asList(modelMapper.map(memberPointlist, MemberPointDTO[].class));
                Pageable pageable= PageRequest.of(Math.max(page.getPageNumber()-1,0),10);
                System.out.println("실행중:"+pageable.getPageNumber());
                Page<MemberPointEntity> memberPointEntities;
                if(!keyword.isEmpty()){
                    if(type.equals("1")){
                        log.info("포인트 내용으로 검색하는 중");
                        memberPointEntities = memberPointRepository.findByContents(keyword,pageable);
                    }else if(type.equals("2")){
                        log.info("포인트 사용여부로 검색하는 중");
                        memberPointEntities=memberPointRepository.findByPointOperationYn(keyword,pageable);
                    }else {
                        log.info("포인트 내용과 사용여부로 검색하는 중");
                        memberPointEntities = memberPointRepository.findByContentsOrOperationYn(keyword,pageable);
                    }
                }
                else{
                    log.info("모든 대상으로 검색");
                    memberPointEntities=memberPointRepository.findAll(pageable);
                }
                Page<MemberPointDTO> memberPointDTOS=memberPointEntities.map(entity -> modelMapper.map(entity,MemberPointDTO.class));
                System.out.println("실행중2:"+memberPointDTOS.getSize());

                return memberPointDTOS;
            }
            else{
                throw new Exception("리스트 조회 실패");
            }
        }catch (Exception e){
            throw new RuntimeException("리스트 조회 실패입니다:"+e.getMessage());

            
        }


    }

    /*-------------------------------------------------
    함수명 : read(Integer idx)
    인수 : Integer idx
    출력 : 없음
    설명 : 유저가 포인트를 자세히 보기할 때
    ---------------------------------------------------*/
    public MemberPointDTO read(Integer idx){
        try {
            Optional<MemberPointEntity> read = memberPointRepository.findById(idx);
            if(!read.isPresent()){
                throw new RuntimeException("포인트 개별 조회 실패");
            }
            else {
                MemberPointDTO memberPointDTO = modelMapper.map(read,MemberPointDTO.class);
                return  memberPointDTO;
            }
        } catch (Exception e){
            throw new RuntimeException("포인트 개별 조회 실패: "+e.getMessage());
        }
    }

}
