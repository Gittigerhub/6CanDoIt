package com.sixcandoit.roomservice.service;

import com.sixcandoit.roomservice.dto.ImageFileDTO;
import com.sixcandoit.roomservice.entity.ImageFileEntity;
import com.sixcandoit.roomservice.repository.ImageFileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class ImageFileService {

    private final ModelMapper modelMapper;
    @Value("${imgUploadLocation}")              // 이미지가 저장될 위치
    private String imgUploadLocation;

    @Value("${aws.s3.bucket}")              // 버킷 이름
    private String bucket;

    @Value("${aws.region}")              // 리전 이름
    private String region;

    private final S3Uploader s3Uploader;        //파일업로드 클래스 상속
    private final ImageFileRepository imageFileRepository;

    // 이미지 등록
    /* --------------------------------------------------------------------
       1. 이미지 등록시에는 다중 이미지 파일들만 받습니다.
       2. 각 연관관계 테이블에 이미지 등록시 자동으로 FK값이 들어가도록 메서드를
            추가하였습니다.
       3. 각 연관 서비스에서
            // DTO -> Entity로 변환
            OrganizationEntity organ = modelMapper.map(organizationDTO, OrganizationEntity.class);

            // ActiveYn의 최초 기본값은 "Y"
            organ.setActiveYn("Y");

            // 이미지 등록
            List<ImageFileEntity> images = fileService.saveImages(imageFiles);

            // 이미지 정보 추가
            // 양방향 연관관계 편의 메서드 사용
            for (ImageFileEntity image : images) {
                organ.addImage(image);  // FK 자동 설정
            }
            System.out.println("FK 자동 등록");

            // Entity 테이블에 저장
            organizationRepository.save(organ);
            System.out.println("저장 최최최종");

            위와 같은 형태로 사용해주시면 됩니다.
    -------------------------------------------------------------------- */
    public List<ImageFileEntity> saveImages(List<MultipartFile> imageFiles) throws Exception {

        System.out.println("이지미 파일스 사이즈 : " + imageFiles.size());

        // 유효한 이미지만 모아서 반환할 리스트
        List<ImageFileEntity> images = new ArrayList<>();

        if (imageFiles != null && !imageFiles.isEmpty()) {

            for (MultipartFile imagefile : imageFiles) {

                // 빈 파일인지 확인
                if (imagefile.isEmpty()) {
                    log.warn("빈 파일이 업로드 되었습니다.:{}", imagefile.getOriginalFilename());

                } else {

                    // FileEntity에 저장할 오리지널네임
                    String originalFilename = imagefile.getOriginalFilename();

                    // S3 업로드 성공 시, 생성된 파일 이름
                    String newFileName = "";

                    // FileEntity에 저장할 url
                    if (originalFilename != null) { // 작업 할 파일이 존재하면
                        newFileName = s3Uploader.upload(imagefile, imgUploadLocation);   // S3업로드
                    }

                    // 엔티티 셋
                    ImageFileEntity fileEntity = new ImageFileEntity();
                    fileEntity.setName(newFileName);
                    fileEntity.setOriginName(originalFilename);
                    fileEntity.setUrl("https://" + bucket + ".s3." +
                            region + ".amazonaws.com/" + imgUploadLocation + "/" + newFileName);

                    // 대표이미지 여부 확인
                    if (imageFiles.indexOf(imagefile) == 0) {
                        fileEntity.setRepimageYn("Y");      // 대표이미지 O
                    } else {
                        fileEntity.setRepimageYn("N");      // 대표이미지 X
                    }

                    images.add(fileEntity);

                }

            }

        }

        // 반환
        return images;
    }

    // 이미지 수정
    /* --------------------------------------------------------------------
       1. 이미지 수정시에는 join값과 해당 join의 idx값을 받습니다.
       2. 대표이미지는 기존 이미지들중에 대표이미지가 없을경우 자동으로 새로등록되는
        이미지들중에 인덱스[0]이 대표이미지가 됩니다.
    -------------------------------------------------------------------- */
    public List<ImageFileEntity> updateImage(List<MultipartFile> imageFiles, String join, Integer idx) throws Exception {

        System.out.println("imageFiles : " + imageFiles.toString());
        System.out.println("join : " + join);
        System.out.println("idx : " + idx);

        // 기존에 존재하던 이미지 리스트
        List<ImageFileEntity> existingImages = new ArrayList<>();
        System.out.println("기존에 존재하던 이미지 리스트를 담을 리스트 existingImages 생성");

        if (join.equals("organ")) {
            existingImages = imageFileRepository.organizationJoin(idx);
            System.out.println("organ 기본이미지 리스트 : " + existingImages.toString());
        } else if (join.equals("room")) {
            existingImages = imageFileRepository.roomJoin(idx);
            System.out.println("room 기본이미지 리스트 : " + existingImages.toString());
        } else if (join.equals("notice")) {
            existingImages = imageFileRepository.noticeJoin(idx);
            System.out.println("notice 기본이미지 리스트 : " + existingImages.toString());
        } else if (join.equals("qna")) {
            existingImages = imageFileRepository.qnaJoin(idx);
            System.out.println("qna 기본이미지 리스트 : " + existingImages.toString());
        } else if (join.equals("event")) {
            existingImages = imageFileRepository.eventJoin(idx);
            System.out.println("event 기본이미지 리스트 : " + existingImages.toString());
        } else if (join.equals("adver")) {
            existingImages = imageFileRepository.advertisementJoin(idx);
            System.out.println("adver 기본이미지 리스트 : " + existingImages.toString());
        } else if (join.equals("menu")) {
            existingImages = imageFileRepository.menuJoin(idx);
            System.out.println("menu 기본이미지 리스트 : " + existingImages.toString());
        }

        System.out.println("이미지 서비스 - 기존 이미지 리스트 조회완료");

        if (imageFiles != null && !imageFiles.isEmpty()) {

            for (MultipartFile imagefile : imageFiles) {

                // 빈 파일인지 확인
                if (imagefile.isEmpty()) {
                    log.warn("빈 파일이 업로드 되었습니다.:{}", imagefile.getOriginalFilename());
                    continue;
                }
                System.out.println("이미지 서비스 - 빈파일인지 확인");

                // FileEntity에 저장할 오리지널네임
                String originalFilename = imagefile.getOriginalFilename();

                // S3 업로드 성공 시, 생성된 파일 이름
                String newFileName = "";

                // FileEntity에 저장할 url
                if (originalFilename != null) { // 작업 할 파일이 존재하면
                    newFileName = s3Uploader.upload(imagefile, imgUploadLocation);   // S3업로드
                }

                System.out.println("이미지 서비스 - S3까지 업로드");

                // 엔티티 셋
                ImageFileEntity fileEntity = new ImageFileEntity();
                fileEntity.setName(newFileName);
                fileEntity.setOriginName(originalFilename);
                fileEntity.setUrl("https://" + bucket + ".s3." +
                        region + ".amazonaws.com/" + imgUploadLocation + "/" + newFileName);

                // 기존 이미지 리스트에 대표이미지가 없을경우 대표이미지 추가
                // 기존 이미지 리스트중에 repimageYn값이 Y가 없을경우 true 반환
                boolean hasNoRepImage = existingImages.stream().noneMatch(image -> "Y".equals(image.getRepimageYn()));

                if (hasNoRepImage) {
                    if (imageFiles.indexOf(imagefile) == 0) {
                        fileEntity.setRepimageYn("Y");      // 대표이미지 O
                    } else {
                        fileEntity.setRepimageYn("N");      // 대표이미지 X
                    }
                } else {
                    fileEntity.setRepimageYn("N");          // 대표이미지 X
                }

                existingImages.add(fileEntity);

            }

        }

        // 반환
        return existingImages;

    }

    // 이미지 삭제
    /* --------------------------------------------------------------------
       1. 이미지 삭제시에는 이미지의 idx를 받습니다.
    -------------------------------------------------------------------- */
    public void deleteImage(Integer idx) throws Exception {

        // idx로 이미지 데이터 조회
        ImageFileEntity fileEntity = imageFileRepository.findById(idx)
                .orElseThrow(() -> new IllegalArgumentException("해당 이미지를 찾을 수 없습니다."));

        // 레코드 삭제하기 전, 관련 파일 삭제
        s3Uploader.deleteFile(fileEntity.getName(), imgUploadLocation);

        // DB 정보 삭제
        imageFileRepository.deleteById(idx);
    }

    // 이미지 조회
    /* --------------------------------------------------------------------
       1. 이미지 조회시에는 연관 테이블의 idx를 받습니다.
    -------------------------------------------------------------------- */
    public List<ImageFileDTO> readImage(Integer idx, String join) {

        try {
            // 연관관계 idx로 이미 리스트 조회
            List<ImageFileEntity> imageFileEntitys = new ArrayList<>();

            if (join.equals("organ")) {
                imageFileEntitys = imageFileRepository.organizationJoin(idx);
            } else if (join.equals("room")) {
                imageFileEntitys = imageFileRepository.roomJoin(idx);
            } else if (join.equals("notice")) {
                imageFileEntitys = imageFileRepository.noticeJoin(idx);
            } else if (join.equals("qna")) {
                imageFileEntitys = imageFileRepository.qnaJoin(idx);
            } else if (join.equals("event")) {
                imageFileEntitys = imageFileRepository.eventJoin(idx);
            } else if (join.equals("adver")) {
                imageFileEntitys = imageFileRepository.advertisementJoin(idx);
            } else if (join.equals("menu")) {
                imageFileEntitys = imageFileRepository.menuJoin(idx);
            }

            // DTO로 변환
            List<ImageFileDTO> imageFileDTOS = imageFileEntitys.stream()
                    .map(imageFileEntity -> modelMapper.map(imageFileEntity, ImageFileDTO.class))
                    .collect(Collectors.toList());

            // 반환
            return imageFileDTOS;

        } catch (Exception e) {
            throw new RuntimeException("이미지 조회 오류");
        }

    }

}