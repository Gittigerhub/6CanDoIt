package com.sixcandoit.roomservice.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
// DTO <-> Entity <-> Repository와 관련 있는 클래스는 @Service
// 관련 없는 클래스는 @Compoment

// S3에 저장과정
//              파일이 메모리에 존재             파일이 물리적 존재
// HTML(이미지추가) -> Controller -> Service -> 이미지를 서버에 임시저장 -> S3에 임시저장 파일 업로드
// -> 임시 저장된 파일을 삭제
@Service
@Transactional
@Log4j2
@RequiredArgsConstructor
public class S3Uploader {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket")
    public String bucket;

    // 서비스에서 Upload요청 메소드
    public String upload(MultipartFile file, String dirName) throws IOException {
        File uploadFile = convert(file).orElseThrow(() -> new IllegalArgumentException("변환할 파일이 없습니다."));
        return upload(uploadFile, dirName);
    }

    // S3에 파일삭제 메소드(파일명, 버킷에 저장된 폴더면)
    public void deleteFile(String deleteFile, String dirName) throws IOException {
//        String fileName = dirName + File.separator + deleteFile;
        String fileName = dirName + "/" + deleteFile;   // S3서버는 구분자 "/"로 직접 작성 가능
        try {
            amazonS3Client.deleteObject(bucket, fileName);
        } catch (SdkClientException e) {
            throw new IOException("S3에서 파일삭제를 실패하였습니다.");
        }
    }

    // S3에 파일업로드 메소드(파일, 버킷에 저장할 폴더명) - UUID로 이름생성
    private String upload(File uploadFile, String dirName) {
        String newFilename = UUID.randomUUID() + uploadFile.getName();  // 난수 + 기존파일명
        // 직접 구분자 "/" 사용 시, OS 따라서 문제발생
        // 때문에 함수 이용
        String fileName = dirName + "/" + newFilename;
        String uploadImageUrl = putS3(uploadFile, fileName);    // S3에 저장
        removeNewFile(uploadFile);                              // 임시파일 삭제
        return newFilename;                                     // 데이터베이스에 저장할 S3에 저장된 새로운 파일명
    }

    // S3에 파일 전송(파일, 새로운 UUID 이름)
    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile)
                .withCannedAcl(CannedAccessControlList.PublicRead));    // 버킷에 파일 저장, 파일은 전역에서 읽기용으로 설정

        return amazonS3Client.getUrl(bucket, fileName).toString();      // 저장된 정보 전달
    }

    // 로컬(서버)에 저장된 임시파일 삭제메소드(파일)
    private void removeNewFile(File targetFile) {
        if (targetFile.exists()) {      // 파일이 존재하면
            targetFile.delete();        // 파일 삭제
        }
    }

    // 업로드할 파일을 byte로 변환 후, 로컬에 저장(html에서 받은 파일)
    private Optional<File> convert(MultipartFile multipartFile) throws IOException {

        // 로컬에 저장되는 위치
        System.out.println(System.getProperty("user.id") + File.separator + multipartFile.getOriginalFilename());

        File convertedFile = new File(System.getProperty("user.id") + File.separator + multipartFile.getOriginalFilename());
        removeNewFile(convertedFile);           // 기존파일 삭제

        if (convertedFile.createNewFile()) {    // 파일을 생성
            try(FileOutputStream fos = new FileOutputStream(convertedFile)) {   // 저장 실패시 오류발생
                fos.write(multipartFile.getBytes());    // 파일저장
            }
            return Optional.of(convertedFile);          // 저장된 파일명을 전달
        }
        return Optional.empty();        // 저장 실패시 빈값을 전달
    }

}