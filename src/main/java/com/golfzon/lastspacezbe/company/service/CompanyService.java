package com.golfzon.lastspacezbe.company.service;

import com.golfzon.lastspacezbe.company.Dto.CompanyResponseDto;
import com.golfzon.lastspacezbe.company.Dto.MainResponseDto;
import com.golfzon.lastspacezbe.company.entity.Company;
import com.golfzon.lastspacezbe.company.entity.CompanyLike;
import com.golfzon.lastspacezbe.company.repository.CompanyLikeRepository;
import com.golfzon.lastspacezbe.company.repository.CompanyRepository;
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
        return getCompanyInfo(companyList, memberId, "hotCompany");
    }
    // 최신등록 장소 4개 가져오기
    @Transactional
    public List<MainResponseDto> getNewCompany(Long memberId) {
        List<Company> companyList = companyRepository.findTop4ByOrderByCreatedTimeDesc();
        return getCompanyInfo(companyList, memberId, "newCompany");
    }

    // return될 업체 정보들 반환
    public List<MainResponseDto> getCompanyInfo(List<Company> companyList, Long memberId, String type) {
        List<MainResponseDto> companyInfo = new ArrayList<>();


        int lowPrice = 0; // 최저가격


        for (Company company : companyList) {
            MainResponseDto dto = new MainResponseDto();
            Set<String> types = new HashSet<>(); // 등록된 type들

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
                if(type.equals("hotCompany") & dto.getAvgReview()==0) continue; //리뷰 점수가 0이면 continue
            } else continue; //공간이 등록되어 있지 않으면, continue

            dto.setCompanyId(company.getCompanyId()); //업체번호
            dto.setCompanyName(company.getCompanyName()); //업체이름
            dto.setCompanyLike(checkLike(company, memberId)); //업체 관심등록수
            dto.setLocation(company.getLocation().split(" ")[1]); //업체 위치
            dto.setLowPrice(lowPrice); //업체 최저가
            dto.setTypes(types); //업체 타입들
            companyInfo.add(dto);
        }
        return companyInfo;
    }
}
