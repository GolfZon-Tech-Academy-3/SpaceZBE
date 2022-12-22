package com.golfzon.lastspacezbe.member.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.golfzon.lastspacezbe.member.entity.Member;
import com.golfzon.lastspacezbe.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static com.golfzon.lastspacezbe.member.service.MemberService.profileImages;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberS3Service {

    private AmazonS3 s3Client;

    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    private final String bucket = "spacez3";

    private final MemberRepository memberRepository;

    @PostConstruct
    public void setS3Client() {
        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);

        s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(this.region)
                .build();
    }

    // S3 bucket에 image upload 후, Image name으로 반환
    public String upload(MultipartFile file) {
        String imageUrl;
        String fileName = createFileName(file.getOriginalFilename());
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        try (InputStream inputStream = file.getInputStream()) {
            s3Client.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            imageUrl = s3Client.getUrl(bucket, fileName).toString();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패하셨습니다");
        }
        return imageUrl;
    }


    //기존 s3에 있는 기존 이미지 정보 삭제 후 저장
    public String update(Long memberId, MultipartFile file) {
        String imageUrl;
        List<String> imageUrls = new ArrayList<>(Arrays.asList(profileImages));
        Optional<Member> member = memberRepository.findById(memberId);
        if (member.isPresent()) {
            String savedImage = member.get().getImgName().replace("https://spacez3.s3.ap-northeast-2.amazonaws.com/", "");
            log.info("지울사진:{}", savedImage);
            boolean isExistObject = s3Client.doesObjectExist(bucket, savedImage);
            log.info("사진 S3존재 여부:{}", isExistObject);
            if (isExistObject & !imageUrls.contains(savedImage)) { //기본이미지가 아니거나 이전 사진이 존재한다면, S3에서 이미지를 삭제한다.
                s3Client.deleteObject(bucket, savedImage);
            }
            //S3에 파일 업로드 후, imageUrl 반환
            imageUrl = upload(file);
        } else{
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "존재하지 않는 사용자입니다.");
        }
        return imageUrl;
    }

    //파일명 난수화(unique)
    private String createFileName(String fileName) {
        // 먼저 파일 업로드 시, 파일명을 난수화하기 위해 random으로 돌립니다.
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    //.png, .img 파일 형식 제거 후 반환
    private String getFileExtension(String fileName) {
        // file 형식이 잘못된 경우를 확인하기 위해 만들어진 로직이며,
        // 파일 타입과 상관없이 업로드할 수 있게 하기 위해 .의 존재 유무만 판단
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일(" + fileName + ") 입니다.");
        }
    }
}