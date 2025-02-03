package com.sixcandoit.roomservice.service.notice;

import com.sixcandoit.roomservice.dto.notice.NoticeDTO;
import com.sixcandoit.roomservice.entity.NoticeEntity;

import com.sixcandoit.roomservice.repository.NoticeRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class NoticeService {
    //@Autowired
    //private NoticeRepository noticeRepostiory;
    private final NoticeRepository noticeRepository;
    private final ModelMapper modelMapper;
    //Controller와 Service는 DTO로 전달
    public void insert(NoticeDTO noticeDTO) {//삽입,입력폼에서 입력받은 내용을 데이터베이스 저장
        //DTO로 Entity변환
        //map은 변수,값으로 구성된 배열
        //noticeDTO변수들을 NoticeEntity에 변수에 맞게 변한
        NoticeEntity noticeEntity = modelMapper.map(noticeDTO, NoticeEntity.class);
        noticeRepository.save(noticeEntity);
    }
    public void update(NoticeDTO noticeDTO) {//수정,수정폼에서 수정한 내용을 데이터베이스 저장
        //해당 데이터의 id로 조회해서
        Optional<NoticeEntity> noticeEntity = noticeRepository.findById(noticeDTO.getIdx());

        if (noticeEntity.isPresent()) {//존재하면 수정
            NoticeEntity noticeEntity1 = modelMapper.map(noticeDTO, NoticeEntity.class);
            noticeRepository.save(noticeEntity1);
        }
    }
    public void delete(Integer idx) {//삭제,게시글번호로 해당 자료를 데이터베이스에서 삭제
        noticeRepository.deleteById(idx);
    }
    public List<NoticeDTO> list() {//전체목록,데이터베이스에서 모든 데이터를 화면에 출력
        List<NoticeEntity> noticeEntities = noticeRepository.findAll(); //모두조회
        //Repository<->Service Entity<-> DTO Controller
        //여러개의 레코드를 하나씩 DTO로 변환해서 다시 배열에 저장
        List<NoticeDTO> noticeDTOS = Arrays.asList(modelMapper.map(noticeEntities, NoticeDTO[].class));
        return noticeDTOS;
    }
    public NoticeDTO read(Integer id) {//개별정보,게시글번호의 데이터를 화면에 출력
        Optional<NoticeEntity> noticeEntity = noticeRepository.findById(id);

        NoticeDTO noticeDTO = modelMapper.map(noticeEntity, NoticeDTO.class);
        return noticeDTO;
    }


    public void register(NoticeDTO noticeDTO) {
    }

    public NoticeDTO noticeRead(Integer idx) {
        return null;
    }

    public void noticeUpdate(@Valid NoticeDTO noticeDTO) {

    }
}
