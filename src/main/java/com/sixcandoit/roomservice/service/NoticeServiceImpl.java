//package com.sixcandoit.roomservice.service;
//
//import com.sixcandoit.roomservice.dto.NoticeDTO;
//import com.sixcandoit.roomservice.entity.NoticeEntity;
//import com.sixcandoit.roomservice.repository.NoticeRepository;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j2;
//import org.modelmapper.ModelMapper;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@Transactional
//@Log4j2
//@RequiredArgsConstructor
//public class NoticeServiceImpl implements NoticeService {
//
//    private final NoticeRepository noticeRepository;
//    private ModelMapper modelMapper = new ModelMapper();
//
//    @Override
//    public NoticeDTO register(NoticeDTO noticeDTO, String email) {
//        // controller에서 입력받은 noticeDTO를 entity로 변환하고 변환된 값을 저장
//        // noticeDTO에는 로그인된 그 값을 통해 값을 찾아옴
//
//        //현재 로그인된 관리자의 email로 entity 받아오기
////                Admin admin=
////                        adminRepository.findByEmail(email);
////                log.info(admin);
////                //noticeDTO를 엔티티로 변환하여 저장한다.
////
////        NoticeEntity noticeEntity=
////                modelMapper.map(noticeDTO,NoticeEntity.class);
////
////        //저장하기 전 notice에는 현재 admin 객체가 없이->부모없이 저장된다.
////        //그래서 entity를 같이 넣어준다. 참조가 가능하도록 포링키는 null일 수 있다.
////
////        log.info(noticeEntity);
////        //저장
////        NoticeEntity result=
////                noticeRepository.save(noticeEntity);
////        log.info(result);
////        return modelMapper.map(result,NoticeDTO.class);
////
////    }
////
////    @Override
////    public List<NoticeDTO> list() {
////        return List.of();
////    }
////
////
////
////
////
////
////
////
////
////
////
////    @Override
////    public NoticeDTO read(Long num) {
////        return null;
////    }
//
//
////
////
////
////
////
////    @Override
////    public NoticeDTO update(NoticeDTO noticeDTO) {
////        return null;
////    }
////}
//    }
//}