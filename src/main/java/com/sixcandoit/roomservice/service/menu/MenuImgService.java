package com.sixcandoit.roomservice.service.menu;

import com.sixcandoit.roomservice.dto.Menu.MenuDTO;
import com.sixcandoit.roomservice.entity.ImageFileEntity;
import com.sixcandoit.roomservice.entity.menu.MenuEntity;
import com.sixcandoit.roomservice.repository.FileRepository;
import com.sixcandoit.roomservice.repository.menu.MenuRepository;
import com.sixcandoit.roomservice.service.FileService;
import com.sixcandoit.roomservice.service.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MenuImgService {
    private final FileRepository fileRepository;
    @Value("${imgUploadLocation}")
    private String imgUploadLocation;

    private final MenuRepository menuRepository;
    private final S3Uploader s3Uploader;
    private final ModelMapper modelMapper;
    private final FileService fileService;

    //삽입(파일업로드-> 생성된 새로운 이름을 가지고 -> DB 저장)
    public void registerImg(Integer idx, List<MultipartFile> multipartFiles) throws Exception {
        if (multipartFiles != null) {
            // 각 파일을 처리
            for (MultipartFile menuImg : multipartFiles) {
                if (!menuImg.isEmpty()) {

                    // 물리적 저장: S3에 업로드하고 반환된 파일 이름들
                    List<ImageFileEntity> savedFiles =
                            fileService.saveImages(Arrays.asList(menuImg));

                    // MenuEntity 불러오기
                    MenuEntity menuEntity
                            = menuRepository.findById(idx).orElseThrow(() -> new RuntimeException("Menu not found"));

                    // 대표 이미지 설정
                    String imgUrl = "/images/menu/" + savedFiles.get(0).getName();  // S3에서 저장된 파일 이름 사용

                    ImageFileEntity imageFileEntity = savedFiles.get(0);  // 첫 번째 이미지 파일을 사용
                    imageFileEntity.setMenuJoin(menuEntity); // 본문//이미지가 달릴 메뉴
                    imageFileEntity.setUrl(imgUrl); //경로
                    imageFileEntity.setOriginName(menuImg.getOriginalFilename());   //원래 이름

                    // 대표 이미지 여부 확인
                    if (multipartFiles.indexOf(menuImg) == 0) {
                        imageFileEntity.setRepimageYn("Y"); // 첫 번째 이미지가 대표 이미지
                    } else {
                        imageFileEntity.setRepimageYn("N"); // 나머지는 대표 이미지 아님
                    }

                    // DB에 이미지 정보 저장
                    fileRepository.save(imageFileEntity);
                }
            }
        }

    }
    //수정(업로드 파일 존재여부 -> 기존파일 삭제 -> 파일 업로드 -> 생성된 파일명 -> DB저장)
    public void modifyImg(MenuDTO menuDTO, MultipartFile file) throws Exception {
        MenuEntity read = menuRepository.findById(menuDTO.getIdx()).orElseThrow();

        String deleteFile = read.getMenuImg();  //이전 이미지파일 읽기
        String originalFileName = file.getOriginalFilename();   //저장할 파일명
        String newFileName = "";    //저장후 생성된 새로운 파일명

        //값이 존재하는지 판별하기
        if (originalFileName.length() != 0) {   //작업할 파일이 존재하면
            if (deleteFile.length() != 0) { //기존 파일이 존재하면
                s3Uploader.deleteFile(deleteFile, imgUploadLocation);   //기존파일 삭제
            }
            newFileName = s3Uploader.upload(file, imgUploadLocation);   //새로운 파일을 S3에 업로드
            menuDTO.setMenuImg(newFileName);    //업로드한 새로운 파일명으로 변경
        }
        menuDTO.setIdx(read.getIdx());  //검증처리(읽어온 idx를 다시 저장해서 정확하게 수정)

        MenuEntity menuEntity = modelMapper.map(menuDTO, MenuEntity.class);

        menuRepository.save(menuEntity);    //DB저장
    }
    //삭제(레코드 삭제 : 등록된 파일 삭제 -> 레코드 삭제)
    public void deleteImg(MenuDTO menuDTO, MultipartFile file) throws Exception {
        //해당 idx로 조회시 없으면 예외처리 발생 처리
        MenuEntity read = menuRepository.findById(menuDTO.getIdx()).orElseThrow();
        //관련 파일 삭제
        s3Uploader.deleteFile(read.getMenuImg(), imgUploadLocation);

        menuRepository.deleteById(menuDTO.getIdx());    //레코드 삭제
    }
    //개별조회
    public MenuDTO detailImg(MenuDTO menuDTO) throws Exception {
        MenuEntity read = menuRepository.findById(menuDTO.getIdx()).orElseThrow();

        return modelMapper.map(read, MenuDTO.class);
    }
    //전체조회
    public List<MenuDTO> listImg() throws Exception {
        List<MenuEntity> read = menuRepository.findAll();
        //{}, (), . 구성시 가로형보다는 세로형으로 작성
        //페이지 처리 추가
        //검색(if문 또는 Switch문)
        List<MenuDTO> menuDTOS = Arrays.asList(
                modelMapper.map(read, MenuDTO[].class)
        );
        return menuDTOS;
    }
}
