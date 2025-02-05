package com.sixcandoit.roomservice.util;


import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.UUID;

@Component
public class FileUpload {
    /*------------------------------
      함수명 : String FileUpload(String imgLocation, MultipartFile imageFile)
      인수 : 저장될 위치, 이미지파일
      출력 : 저장후 생성된 새로운 파일명
      설명 : 이미지파일을 새로운이름으로 지정된 폴더에 저장하고 새로운이름을 전달
       ------------------------------*/
    public String ImageUpload(String imgLocation, MultipartFile imageFile) {


            String originalFilename = imageFile.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            UUID uuid = UUID.randomUUID();
            String fileName = uuid.toString() + extension;

            String path = imgLocation + fileName;


            try {
                File foloder = new File(imgLocation);
                if (!foloder.exists()) {
                    boolean result = foloder.mkdir();
                }
                byte[] filedData = imageFile.getBytes();

                FileOutputStream fos = new FileOutputStream(path);
                fos.write(filedData);
                fos.close();

            } catch (Exception e) {

               System.err.println("이미지 저장에서 문재발생"+e.getMessage());
                return null;

            }
            return fileName;

    }






     /*------------------------------
    함수명 : void FileDelete(String imgLocation, String imageFileName)
    인수 : 저장될 위치, 이미지파일
    출력 : 저장후 생성된 새로운 파일명
    설명 : 이미지파일을 새로운이름으로 지정된 폴더에 저장하고 새로운이름을 전달
     ------------------------------*/
    public  void FileDelete(String imgLocation, String imageFileName){
        String deleteFileNmae = imgLocation+imageFileName;

        try {
            File deleteFile= new File(deleteFileNmae);
            if(deleteFile.exists()){
                deleteFile.delete();
            }
        } catch (Exception e){
            System.err.println("이미지 삭제에서 문제 발생");
        }
    }



}
