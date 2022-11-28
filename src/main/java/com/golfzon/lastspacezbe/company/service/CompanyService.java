package com.golfzon.lastspacezbe.company.service;

import com.golfzon.lastspacezbe.company.Dto.CompanyResponseDto;
import com.golfzon.lastspacezbe.company.Dto.MainResponseDto;
import com.golfzon.lastspacezbe.company.Dto.SearchRequestDto;
import com.golfzon.lastspacezbe.company.entity.Company;
import com.golfzon.lastspacezbe.company.entity.CompanyLike;
import com.golfzon.lastspacezbe.company.repository.CompanyLikeRepository;
import com.golfzon.lastspacezbe.company.repository.CompanyRepository;
import com.golfzon.lastspacezbe.reservation.service.ReservationService;
import com.golfzon.lastspacezbe.review.entity.Review;
import com.golfzon.lastspacezbe.review.repository.ReviewRepository;
import com.golfzon.lastspacezbe.space.dto.SpaceResponseDto;
import com.golfzon.lastspacezbe.space.entity.Space;
import com.golfzon.lastspacezbe.space.entity.SpaceImage;
import com.golfzon.lastspacezbe.space.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final SpaceRepository spaceRepository;
    private final ReviewRepository reviewRepository;
    private final CompanyLikeRepository companyLikeRepository;
    private final ReservationService reservationService;

    // 업체 정보 가져오기
    public CompanyResponseDto getCompanyInfo(Long companyId, Long memberId) {
        // 업체 정보
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당하는 companyId가 없습니다."));
        // 업체에 등록된 공간
        List<Space> spaces = spaceRepository.findAllByCompanyId(companyId);
        // 공간에 등록된 사진들
        List<String> spaceImages = new ArrayList<>();
        List<SpaceResponseDto> dtos = new ArrayList<>();
        for (Space space : spaces) {
            for (SpaceImage image : space.getSpaceImages()) {
                spaceImages.add(image.getSpaceImage());
            }
            dtos.add(new SpaceResponseDto(space, space.getSpaceImages().get(0).getSpaceImage()));
        }
        // 관심등록 여부
        Boolean likeCheck = checkLike(company, memberId);

        return new CompanyResponseDto(company, spaceImages, dtos, likeCheck);
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
        log.info("companyList.size:{}", companyList.size());
        return getCompanyInfo(companyList, memberId, "hotCompany");
    }

    // 최신등록 장소 4개 가져오기
    public List<MainResponseDto> getNewCompany(Long memberId) {
        List<Company> companyList = companyRepository.findTop4ByOrderByCreatedTimeDesc();
        log.info("companyList.size:{}", companyList.size());
        return getCompanyInfo(companyList, memberId, "newCompany");
    }

    // return될 업체 정보들 반환
    public List<MainResponseDto> getCompanyInfo(List<Company> companyList, Long memberId, String type) {
        List<MainResponseDto> companyInfo = new ArrayList<>();


        int lowPrice; // 최저가격


        for (Company company : companyList) {
            MainResponseDto dto = new MainResponseDto();
            Set<String> types = new HashSet<>(); // 등록된 type들

            List<Space> spaces = spaceRepository.findAllByCompanyId(company.getCompanyId());
            if (!spaces.isEmpty()) {
                dto.setFirstImage(spaces.get(0).getSpaceImages().get(0).getSpaceImage()); // 업체에 등록된 사진
                lowPrice = spaces.get(0).getPrice();
                log.info("price:{}", lowPrice);
                for (Space space : spaces) {
                    types.add(space.getType().split(" ")[0]); // 업체에 등록된 타입
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
                if (type.equals("hotCompany") & dto.getAvgReview() == 0) continue; //리뷰 점수가 0이면 continue
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

    // 전체 업체 정보가져오기
    public Map<String, Object> getTotalCompany(Optional<SearchRequestDto> searchDto, int page, Long memberId) {
        log.info("searchDto:{}", searchDto);
        log.info("memberId:{}", memberId);
        Set<Long> companyIds = new HashSet<>();
        if (searchDto.isPresent()) {
            if (!searchDto.get().getLocation().isEmpty()) {
                log.info("지역 검색:{}",searchDto.get().getLocation());
                companyIds = companyRepository.findAllByLocation("%" + searchDto.get().getLocation() + "%");
            }
            if (!searchDto.get().getTime().isEmpty()) {
                log.info("날짜+시간 검색:{}",searchDto.get().getDate()+" "+searchDto.get().getTime());
                List<Space> spaces = spaceRepository.findAll();
                for (Space space : spaces) {
                    // 예약 가능한 시간 구하기
                    if (getTimes(space, searchDto).contains(searchDto.get().getTime())) {
                        companyIds.add(space.getCompanyId());
                    } else companyIds.remove(space.getCompanyId());
                }
            }
            if (!searchDto.get().getDate().isEmpty() & searchDto.get().getTime().isEmpty()) {
                log.info("날짜 검색:{}",searchDto.get().getDate());
                List<Space> spaces = spaceRepository.findAll();
                for (Space space : spaces) {
                    // 예약 가능한 시간 구하기
                    List<String> times = getTimes(space, searchDto);
                    if (times.size() > 0) companyIds.add(space.getCompanyId());
                }
            }
        }
        log.info("companyIds:{}",companyIds);
        Map<String, Object> map = new HashMap<>();
        Pageable pageable = PageRequest.of(page - 1, 9);
        List<Company> companyList = companyRepository.findAllByCompanyIdOrderByCreatedTimeDesc(pageable, companyIds);

        log.info("companyList.size:{}", companyList.size());
        List<MainResponseDto> dtos = getCompanyInfo(companyList, memberId, "totalCompany");
        map.put("totalSize", dtos.size());
        map.put("totalCompany", dtos);
        return map;
    }

    // 예약 가능한 시간 조회(같은 날짜)
    public List<String> getTimes(Space space, Optional<SearchRequestDto> searchDto) {
        List<String> times = new ArrayList<>();
        // 포맷변경 (년월일 시분)
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();
        // 시간 1시간 간격으로 더하기
        Date startDate;
        try {
            startDate = formatter.parse(space.getOpenTime());
            startCal.setTime(startDate);
            String time;
            Date endDate = formatter.parse(space.getCloseTime());
            endCal.setTime(endDate);
            // 오픈시작 시간부터 종료 시간 전까지 시간 더하기
            while (startCal.before(endCal)) {
                time = formatter.format(startCal.getTime());
                times.add(time);
                startCal.add(Calendar.HOUR, +1);
            }
            // break time 은 없애기
            startDate = formatter.parse(space.getBreakOpen());
            startCal.setTime(startDate);
            endDate = formatter.parse(space.getBreakClose());
            endCal.setTime(endDate);
            while (startCal.before(endCal)) {
                time = formatter.format(startCal.getTime());
                times.remove(time);
                startCal.add(Calendar.HOUR, +1);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // 2. 예약된 시간 구하기
        List<String> reservedTimes = reservationService.getReservedTimes(space.getSpaceId());

        // 3. 예약 가능한 시간 있는지 확인
        for (String reservedTime : reservedTimes) {
            if (reservedTime.split(" ")[0].equals(searchDto.get().getDate())) {
                times.remove(reservedTime.split(" ")[1]);
            }
        }
        log.info("예약 가능한 시간들:{}",times);
        return times;
    }


    // 전체 오피스 조회
    public Map<String, Object> getTotalOffice(Optional<SearchRequestDto> searchDto, int page, Long memberId) {
        log.info("searchDto:{}", searchDto);
        log.info("memberId:{}", memberId);

        Map<String, Object> map = new HashMap<>();
        Pageable pageable = PageRequest.of(page - 1, 9);
        List<Long> companyIdList = spaceRepository.findAllByOfficeOrderByCompanyIdDesc(pageable);

        log.info("companyIdList:{}", companyIdList);
        List<Company> companyList = companyRepository.findAllByCompanyId(companyIdList);
        List<MainResponseDto> dtos = getCompanyInfo(companyList, memberId, "officeCompany");
        map.put("totalSize", dtos.size());
        map.put("officeCompany", dtos);
        return map;
    }

    // 전체 데스크 조회
    public Map<String, Object> getTotalDesk(Optional<SearchRequestDto> searchDto, int page, Long memberId) {
        log.info("searchDto:{}", searchDto);
        log.info("memberId:{}", memberId);

        Map<String, Object> map = new HashMap<>();
        Pageable pageable = PageRequest.of(page - 1, 9);
        List<Long> companyIdList = spaceRepository.findAllByDeskOrderByCompanyIdDesc(pageable);

        log.info("companyIdList:{}", companyIdList);
        List<Company> companyList = companyRepository.findAllByCompanyId(companyIdList);
        List<MainResponseDto> dtos = getCompanyInfo(companyList, memberId, "deskCompany");
        map.put("totalSize", dtos.size());
        map.put("deskCompany", dtos);
        return map;
    }

    // 전체 회의실 조회
    public Map<String, Object> getTotalMeetingRoom(Optional<SearchRequestDto> searchDto, int page, Long memberId) {
        log.info("searchDto:{}", searchDto);
        log.info("memberId:{}", memberId);

        Map<String, Object> map = new HashMap<>();
        Pageable pageable = PageRequest.of(page - 1, 9);
        List<Long> companyIdList = spaceRepository.findAllByMeetingRoomOrderByCompanyIdDesc(pageable);

        log.info("companyIdList:{}", companyIdList);
        List<Company> companyList = companyRepository.findAllByCompanyId(companyIdList);
        List<MainResponseDto> dtos = getCompanyInfo(companyList, memberId, "meetingRoomCompany");
        map.put("totalSize", dtos.size());
        map.put("meetingRoomCompany", dtos);
        return map;
    }

}
