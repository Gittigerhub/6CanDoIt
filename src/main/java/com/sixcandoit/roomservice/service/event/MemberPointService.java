package com.sixcandoit.roomservice.service.event;

import com.sixcandoit.roomservice.dto.event.MemberPointDTO;
import com.sixcandoit.roomservice.entity.event.MemberPointEntity;
import com.sixcandoit.roomservice.entity.member.MemberEntity;
import com.sixcandoit.roomservice.repository.event.MemberPointRepository;
import com.sixcandoit.roomservice.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberPointService {
    private  final MemberPointRepository memberPointRepository;
    private  final MemberRepository memberRepository;
    private  final ModelMapper modelMapper;
    /*-------------------------------------------------
    함수명 : register(MemberPointDTO memberPointDTO)
    인수 : memberPointDTO
    출력 : 없음
    설명 : 포인트를 등록할때 사용
    ---------------------------------------------------*/
    public void register(MemberPointDTO memberPointDTO){
        try {
            Optional<MemberEntity> memberEntity = memberRepository.findByMemberEmail(
                    memberPointDTO.getMemberJoin().getMemberEmail()
            );
            if(!memberEntity.isPresent()){
                throw new RuntimeException("회원 조회 실패");
            }

            //Optional<MemberPointEntity> read = memberPointRepository.findById(memberPointDTO.getIdx());
            MemberPointEntity memberPointEntity = modelMapper.map(memberPointDTO, MemberPointEntity.class);
            memberPointEntity.setMemberJoin(memberEntity.get());
            
            memberPointRepository.save(memberPointEntity);
        } catch (Exception e) {
            throw new RuntimeException("포인트 저장 실패 : "+e.getMessage());
        }
    }

    /*-------------------------------------------------
    함수명 : read(int idx)
    인수 : 조회할 매장 idx
    출력 : MemberPointDTO
    설명 : 포인트를 조회할 때 사용
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

    /*-------------------------------------------------
    함수명 : update(MemberPointDTO memberPoint)
    인수 : memberPointDTO
    출력 : 없음
    설명 : 포인트 정보를 수정할때 사용;
  ---------------------------------------------------*/
    public void update(MemberPointDTO memberPointDTO){
        try {
            Optional<MemberEntity> memberEntity = memberRepository.findByMemberEmail(
                    memberPointDTO.getMemberJoin().getMemberEmail()
            );

            Optional<MemberPointEntity> read = memberPointRepository.findById(memberPointDTO.getIdx());
            if(!read.isPresent()){
                throw new RuntimeException("포인트 조회 실패");
            }
            else {
                MemberPointEntity memberPointEntity =modelMapper.map(memberPointDTO,MemberPointEntity.class);
                memberPointEntity.setMemberJoin(memberEntity.get());
                memberPointRepository.save(memberPointEntity);
            }
        } catch (Exception e){
            throw new RuntimeException("포인트 수정 실패: "+e.getMessage());
        }
    }

    /*-------------------------------------------------
    함수명 : delete(MemberPointDTO memberPoint)
    인수 : memberPointDTO
    출력 : 없음
    설명 : 포인트 정보를 삭제할때 사용
    ---------------------------------------------------*/
    public void delete(Integer idx){
        try {
            memberPointRepository.deleteById(idx);
        } catch (Exception e){
            throw new RuntimeException("포인트 삭제 실패: "+e.getMessage());
        }
    }

    /*-------------------------------------------------
    함수명 : list(Pageable page)
    인수 : Pageable
    출력 : 목록
    설명 : 포인트 정보들을 목록을 출력할때 사용
    ---------------------------------------------------*/
    public Page<MemberPointDTO> list(Pageable page, String type, String keyword){
        List<MemberPointEntity> memberPointlist = memberPointRepository.findAll();
        System.out.println("Service = page: "+page+",type:"+type+",keyword:"+keyword);
        //List<MemberPointDTO> memberPointDTOlist = Arrays.asList(modelMapper.map(memberPointlist, MemberPointDTO[].class));
        Pageable pageable= PageRequest.of(Math.max(page.getPageNumber()-1,0),10);
        System.out.println("실행중:"+pageable.getPageNumber());
        Page<MemberPointEntity> memberPointEntities;
        if(!keyword.isEmpty()){
            if(type.equals("1")){
                log.info("이메일로 검색을 하는 중");
                memberPointEntities = memberPointRepository.findByEmail(keyword,pageable);
            }else if(type.equals("2")){
                log.info("회원이름으로 검색하는 중");
                memberPointEntities=memberPointRepository.findByName(keyword,pageable);
            }else {
                log.info("회원 이름과 이메일로 검색하는 중");
                memberPointEntities = memberPointRepository.findByNameOrEmail(keyword,pageable);
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

    
    /*-------------------------------------------------
    함수명 : memberlist(Pageable page)
    인수 : Pageable
    출력 : 목록
    설명 : 회원별 포인트 목록을 출력할때 사용
    ---------------------------------------------------*/
    public List<MemberPointDTO> memberlist(Integer idx){
        List<MemberPointEntity> memberPointlist = memberPointRepository.findByMemberJoinIdx(idx);

        List<MemberPointDTO> memberPointDTOlist = Arrays.asList(modelMapper.map(memberPointlist, MemberPointDTO[].class));

        return memberPointDTOlist;
    }
    /*-------------------------------------------------


    /* 회원별 포인트 등록 */
    /* 회원별 포인트 수정 */
    /* 회원별 포인트 삭제 */
}