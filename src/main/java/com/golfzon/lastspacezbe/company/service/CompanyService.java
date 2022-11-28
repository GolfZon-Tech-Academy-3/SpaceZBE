package com.golfzon.lastspacezbe.company.service;

import com.golfzon.lastspacezbe.company.Dto.CompanyResponseDto;
import com.golfzon.lastspacezbe.company.Dto.MainResponseDto;
import com.golfzon.lastspacezbe.company.dto.CompanyJoinResponseDto;
import com.golfzon.lastspacezbe.company.dto.CompanyRequestDto;
import com.golfzon.lastspacezbe.company.entity.Company;
import com.golfzon.lastspacezbe.company.entity.CompanyLike;
import com.golfzon.lastspacezbe.company.repository.CompanyLikeRepository;
import com.golfzon.lastspacezbe.company.repository.CompanyRepository;
import com.golfzon.lastspacezbe.member.entity.Member;
import com.golfzon.lastspacezbe.member.repository.MemberRepository;
import com.golfzon.lastspacezbe.review.entity.Review;
import com.golfzon.lastspacezbe.review.repository.ReviewRepository;
import com.golfzon.lastspacezbe.space.entity.Space;
import com.golfzon.lastspacezbe.space.entity.SpaceImage;
import com.golfzon.lastspacezbe.space.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final SpaceRepository spaceRepository;
    private final ReviewRepository reviewRepository;
    private final CompanyLikeRepository companyLikeRepository;

    private final MemberRepository memberRepository;

    private final CompanyS3Service companyS3Service;

    // 업체 등록 (신청)
    public void companyPost(CompanyRequestDto companyRequestDto, Member member) {

        Company company = new Company(
                member,companyRequestDto.getCompanyName(),companyRequestDto.getInfo(),companyRequestDto.getRules(),
                companyRequestDto.getLocation(),companyRequestDto.getDetails(),companyRequestDto.getSummary(),"000"
        );

        if(companyRequestDto.getMultipartFile() != null){
            String imageUrl = companyS3Service.upload(companyRequestDto.getMultipartFile());
            company.setImageName(imageUrl);
        }

        companyRepository.save(company);

    }
    // 업체 정보 가져오기
    @Transactional
    public CompanyResponseDto getCompanyInfo(Long companyId, Long memberId) {
        // 업체 정보
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당하는 companyId가 없습니다."));
        // 업체에 등록된 공간
        List<Space> spaces = spaceRepository.findAllByCompanyId(companyId);
        // 공간에 등록된 사진들
        List<String> spaceImages = new ArrayList<>();
        for (Space space : spaces) {
            for (SpaceImage image : space.getSpaceImages()) {
                spaceImages.add(image.getSpaceImage());
                space.setSpaceImages(null);
            }
        }
        // 관심등록 여부
        Boolean likeCheck = checkLike(company, memberId);

        return new CompanyResponseDto(company, spaceImages, spaces, likeCheck);
    }

    // 업체 관심 등록/취소
    public Boolean companyLike(Long companyId, Long memberId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당하는 companyId가 없습니다."));
        CompanyLike companyLike = companyLikeRepository.findByCompanyAndMemberId(company, memberId);

        if (companyLike != null) {
            companyLikeRepository.deleteById(companyLike.getCompanyLikeId());
            company.setLikeCount(company.getLikeCount() - 1);
            companyRepository.save(company);
            return false;
        } else {
            companyLikeRepository.save(new CompanyLike(company, memberId));
            company.setLikeCount(company.getLikeCount() + 1);
            companyRepository.save(company);
            return true;
        }
    }

    // 업체 관심 등록 여부 확인
    public Boolean checkLike(Company company, Long memberId) {
        CompanyLike companyLike = companyLikeRepository.findByCompanyAndMemberId(company, memberId);
        boolean likeCheck = false;
        if (companyLike != null) {
            likeCheck = true;
        }
        return likeCheck;
    }

    // 인기장소 8개 가져오기
    @Transactional
    public List<MainResponseDto> getHotCompany(Long memberId) {
        List<Company> companyList = companyRepository.findTop8ByOrderByLikeCountDesc();
        log.info("companyList.size:{}",companyList.size());
        return getCompanyInfo(companyList, memberId);
    }
    // 최신등록 장소 4개 가져오기
    @Transactional
    public List<MainResponseDto> getNewCompany(Long memberId) {
        List<Company> companyList = companyRepository.findTop4ByOrderByCreatedTimeDesc();
        return getCompanyInfo(companyList, memberId);
    }

    // return될 업체 정보들 반환
    public List<MainResponseDto> getCompanyInfo(List<Company> companyList, Long memberId) {
        List<MainResponseDto> companyInfo = new ArrayList<>();


        int lowPrice = 0; // 최저가격

        for (Company company : companyList) {
            MainResponseDto dto = new MainResponseDto();
            Set<String> types = new HashSet<>(); // 등록된 type들

            dto.setCompanyId(company.getCompanyId());
            dto.setCompanyName(company.getCompanyName());
            dto.setCompanyLike(checkLike(company, memberId));
            dto.setLocation(company.getLocation().split(" ")[1]);

            List<Space> spaces = spaceRepository.findAllByCompanyId(company.getCompanyId());
            if (!spaces.isEmpty()) {
                dto.setFirstImage(spaces.get(0).getSpaceImages().get(0).getSpaceImage()); // 업체에 등록된 사진
                lowPrice = spaces.get(0).getPrice();
                log.info("price:{}", lowPrice);
                for (Space space : spaces) {
                    types.add(space.getType()); // 업체에 등록된 타입
                    // 업체에 등록된 최저 가격
                    if (space.getPrice() < lowPrice) {
                        lowPrice = space.getPrice();
                    }
                    // 공간에 등록된 리뷰
                    List<Review> reviews = reviewRepository.findAllBySpaceId(space.getSpaceId());
                    if (!reviews.isEmpty()) {
                        dto.setReviewSize(reviews.size());
                        double sum = 0;
                        for (Review review : reviews) {
                            sum += review.getRating();
                        }
                        dto.setAvgReview(Math.floor(sum / reviews.size() * 10) / 10);
                    }
                }

            }
            dto.setLowPrice(lowPrice);
            dto.setTypes(types);
            companyInfo.add(dto);
        }
        return companyInfo;
    }

    // 업체 신청 목록 보기
    public List<CompanyJoinResponseDto> companySelectAll(Member member) {

//        Member member1 = memberRepository.findById(member.getMemberId()).orElseThrow(){
//
//        }
        List<CompanyJoinResponseDto> companyJoinResponseDtos = new ArrayList<>();

        List<Company> companies = companyRepository.findAll();
        for (Company data: companies
             ) {
            CompanyJoinResponseDto responseDto = new CompanyJoinResponseDto();
            responseDto.setCompanyName(data.getCompanyName()); // 업체명
            responseDto.setInfo(data.getInfo()); // 업체 정보
            responseDto.setLocation(data.getLocation()); // 업체 위치
            responseDto.setDetails(data.getDetails()); // 업체 상세정보
            responseDto.setApproveStatus(data.getApproveStatus()); // 업체 활동 상태
            responseDto.setImageName(data.getImageName()); // 업체 이미지

            responseDto.setEmail(member.getEmail()); // 회원 이메일
            responseDto.setMemberName(member.getMemberName()); // 회원 이름
            responseDto.setProfileImage(member.getImgName()); // 프로필 이미지

            companyJoinResponseDtos.add(responseDto);
        }

        return companyJoinResponseDtos;
    }

    // 업체관리자로 승인하기
    public void approve(Long companyId) {
        Company company = companyRepository.findByCompanyId(companyId);

        company.setApproveStatus("001"); // 활동상태 활동중으로 변경

        companyRepository.save(company); // 저장.

    }

    // 업체관리자 승인 거부
    public void disapprove(Long companyId) {
        Company company = companyRepository.findByCompanyId(companyId);

        company.setApproveStatus("002"); // 활동상태 활동 정지로 변경

        companyRepository.save(company); // 저장.
    }
    
}
