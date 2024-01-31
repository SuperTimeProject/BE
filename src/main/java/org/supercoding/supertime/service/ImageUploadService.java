package org.supercoding.supertime.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.internal.Mimetypes;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.supercoding.supertime.repository.PostImageRepository;
import org.supercoding.supertime.web.entity.board.PostImageEntity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageUploadService {
    private final PostImageRepository postImageRepository;
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    // 이미지 중복 방지를 위한 랜덤 이름 생성 ( s3에 올라가는데만 사용 )
    private String changedImageName(String ext){
        String random = UUID.randomUUID().toString();
        return random+ext;
    }

    private String uploadImageToS3(MultipartFile image, String folderName) {
        String originName = image.getOriginalFilename();
        String ext = originName.substring(originName.lastIndexOf("."));
        String changedName = folderName + "/" + changedImageName(ext);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(Mimetypes.getInstance().getMimetype(changedName));

        try{
            byte[] bytes = IOUtils.toByteArray(image.getInputStream());
            metadata.setContentLength(bytes.length);
            ByteArrayInputStream byteArrayIs = new ByteArrayInputStream(bytes);

            PutObjectResult putObjectResult = amazonS3.putObject(new PutObjectRequest(
                    bucketName, changedName, byteArrayIs, metadata
            ).withCannedAcl(CannedAccessControlList.PublicRead));
            log.info("[UploadToS3] s3에 이미지가 업로드 되었습니다. resultUrl = " + putObjectResult);
        } catch (IOException e){
            throw new ImageUploadExeception();
        }

        return amazonS3.getUrl(bucketName, changedName).toString();
    }

    public List<PostImageEntity> uploadImages(List<MultipartFile> images, String folderName) {

        List<PostImageEntity> uploadedImages = new ArrayList<>();

        for(MultipartFile image : images){
            String originName = image.getOriginalFilename();
            String storedImagedPath = uploadImageToS3(image, folderName);

            log.info("[uploadImage] 이미지가 s3업데이트 메서드로 넘어갈 예정입니다. originName = " + originName);

            PostImageEntity newProductImage = PostImageEntity.builder()
                    .postImageFileName(originName)
                    .postImageFilePath(storedImagedPath)
                    .build();

            postImageRepository.save(newProductImage);

            uploadedImages.add(newProductImage);
        }

        return uploadedImages;
    }

    public void deleteImage(String productImagePath) {
        String key = extractKey(productImagePath);
        log.info("[test]" + key);
        DeleteObjectRequest deleteRequest = new DeleteObjectRequest(bucketName, key);
        amazonS3.deleteObject(deleteRequest);
    }

    private String extractKey(String productImagePath){
        String baseUrl = "https://" + bucketName + ".s3.ap-northeast-2.amazonaws.com/";
        return productImagePath.substring(baseUrl.length());
    }

    public class ImageUploadExeception extends RuntimeException{
        public ImageUploadExeception(){
            super("이미지 업로드 오류가 발생하였습니다.");
        }
    }
}
