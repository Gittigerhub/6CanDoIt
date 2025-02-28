package com.sixcandoit.roomservice.service.event;


import com.sixcandoit.roomservice.dto.event.MemberPointDTO;
import com.sixcandoit.roomservice.entity.event.MemberPointEntity;
import com.sixcandoit.roomservice.repository.event.MemberPointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
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

    public List<MemberPointDTO> memberlist(Integer idx){

        try {
            List<MemberPointEntity> memberPointlist = memberPointRepository.findByMemberJoinIdx(idx);
            if(!memberPointlist.isEmpty()) {
                List<MemberPointDTO> memberPointDTOlist = Arrays.asList(modelMapper.map(memberPointlist, MemberPointDTO[].class));

                return memberPointDTOlist;
            }
            else{
                throw new Exception("리스트 조회 실패");
            }
        }catch (Exception e){
            throw new RuntimeException("리스트 조회 실패입니다:"+e.getMessage());

            
        }


    }

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
