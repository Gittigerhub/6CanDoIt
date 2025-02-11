package com.sixcandoit.roomservice.service;

import com.sixcandoit.roomservice.entity.FileEntity;
import com.sixcandoit.roomservice.repository.FileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class FileService {

    @Value("${imgUploadLocation}")              // 이미지가 저장될 위치
    private String imgUploadLocation;

    private final S3Uploader s3Uploader;        //파일업로드 클래스 상속
    private final FileRepository fileRepository;

    // 이미지 등록
    public List<FileEntity> saveImage(List<MultipartFile> imageFiles) throws Exception {

        // 모아서 반환할 리스트
        List<FileEntity> images = new ArrayList<>();

        if (imageFiles != null && !imageFiles.isEmpty()) {

            for (MultipartFile imagefile : imageFiles) {

                // 빈 파일인지 확인
                if (imagefile.isEmpty()) {
                    log.warn("빈 파일이 업로드 되었습니다.:{}", imagefile.getOriginalFilename());
                    continue;
                }

                // FileEntity에 저장할 오리지널네임
                String originalFilename = imagefile.getOriginalFilename();
                // S3 업로드 성공 시, 생성된 파일 이름
                String newFileName = "";

                // FileEntity에 저장할 url
                if (originalFilename != null) { // 작업 할 파일이 존재하면
                    newFileName = s3Uploader.upload(imagefile, imgUploadLocation);   // S3업로드
                }

                // 엔티티 셋
                FileEntity fileEntity = new FileEntity();
                fileEntity.setName(newFileName);
                fileEntity.setOriginName(originalFilename);

                // 대표이미지 여부 확인
                if (imageFiles.indexOf(imagefile) == 0) {
                    fileEntity.setRepimageYn("Y");      // 대표이미지 O
                } else {
                    fileEntity.setRepimageYn("N");      // 대표이미지 X
                }

                images.add(fileEntity);

            }

        }

        // 반환
        return images;
    }


    // 이미지 수정
    public void updateImage(List<MultipartFile> imageFiles) {



    }

    // 이미지 삭제
    public void deleteImage(Integer idx) throws Exception {

        // idx로 이미지 데이터 조회
        FileEntity fileEntity = fileRepository.findById(idx)
                .orElseThrow(() -> new IllegalArgumentException("해당 이미지를 찾을 수 없습니다."));

        // 레코드 삭제하기 전, 관련 파일 삭제
        s3Uploader.deleteFile(fileEntity.getName(), imgUploadLocation);

        // DB 정보 삭제
        fileRepository.deleteById(idx);
    }

}