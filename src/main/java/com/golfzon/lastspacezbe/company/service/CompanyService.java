package com.golfzon.lastspacezbe.company.service;

import com.golfzon.lastspacezbe.company.dto.*;
import com.golfzon.lastspacezbe.company.entity.Company;
import com.golfzon.lastspacezbe.company.entity.CompanyLike;
import com.golfzon.lastspacezbe.company.repository.CompanyLikeRepository;
import com.golfzon.lastspacezbe.company.repository.CompanyRepository;
import com.golfzon.lastspacezbe.member.entity.Member;
import com.golfzon.lastspacezbe.member.repository.MemberRepository;
import com.golfzon.lastspacezbe.reservation.service.ReservationService;
import com.golfzon.lastspacezbe.review.entity.Review;
import com.golfzon.lastspacezbe.review.repository.ReviewRepository;
import com.golfzon.lastspacezbe.space.dto.SpaceResponseDto;
import com.golfzon.lastspacezbe.space.entity.Space;
import com.golfzon.lastspacezbe.space.entity.SpaceImage;
import com.golfzon.lastspacezbe.space.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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

    private final MemberRepository memberRepository;

    private final CompanyS3Service companyS3Service;

    // 업체 등록 (신청)
    public void companyPost(CompanyRequestDto companyRequestDto, Member member) {

        //예외처리
        Company isCompany = companyRepository.findByMember(member);
        if (isCompany != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 아이디로 업체를 이미 등록하였습니다.");
        }
        //업체 등록
        Company company = new Company(
                member, companyRequestDto.getCompanyName(), companyRequestDto.getInfo(), companyRequestDto.getRules(),
                companyRequestDto.getLocation(), companyRequestDto.getDetails(), companyRequestDto.getSummary(), "000"
        );

        if (companyRequestDto.getMultipartFile() != null) {
            String imageUrl = companyS3Service.upload(companyRequestDto.getMultipartFile());
            company.setImageName(imageUrl);
        }

        companyRepository.save(company);

    }

    // 업체 정보 가져오기
    public CompanyResponseDto getCompanyInfo(Long companyId, Long memberId) {
        // 업체 정보
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당하는 companyId가 없습니다."));
        // 업체에 등록된 공간
        List<Space> spaces = spaceRepository.findAllByCompanyId(companyId);
        // 공간에 등록된 사진들
        List<String> spaceImages = new ArrayList<>();
        spaceImages.add(company.getImageName());
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

    // 전체 업체 조회
    public Map<String, Object> getTotalCompany(Optional<SearchRequestDto> searchDto, int page, Long memberId) {
        log.info("searchDto:{}", searchDto);
        log.info("memberId:{}", memberId);

        Page<Company> companyList;
        Set<Long> companyIdList = spaceRepository.findAllCompanyIds();
        log.info("companyIdList:{}", companyIdList);
        if (searchDto.isPresent()) {
            companyList = searchCompany(searchDto, companyIdList, page);
        } else {
            Pageable pageable = PageRequest.of(page - 1, 9);
            companyList = companyRepository.findAllByOrderByCreatedTimeDesc(pageable, companyIdList);
            log.info("companyList:{}", companyList.getSize());
        }

        log.info("companyList.size:{}", companyList.getTotalElements());
        List<MainResponseDto> dtos = getCompanyInfo(companyList.toList(), memberId, "totalCompany");
        log.info("dtos.size():{}", dtos.size());
        Map<String, Object> map = new HashMap<>();
        map.put("totalSize", companyList.getTotalElements());
        map.put("totalPage", companyList.getTotalPages());
        map.put("company", dtos);
        return map;
    }

    // 전체 오피스 조회
    public Map<String, Object> getTotalOffice(Optional<SearchRequestDto> searchDto, int page, Long memberId) {
        log.info("searchDto:{}", searchDto);
        log.info("memberId:{}", memberId);

        Page<Company> companyList;
        if (searchDto.isPresent()) {
            Set<Long> companyIdList = spaceRepository.findAllCompanyIdByOffice();
            companyList = searchCompany(searchDto, companyIdList, page);
            log.info("companyIdList:{}", companyIdList);
        } else {
            Pageable pageable = PageRequest.of(page - 1, 9);
            List<Long> companyIdList = spaceRepository.findAllOfficeCompany();
            companyList = companyRepository.findAllCompany(companyIdList, pageable);
            log.info("companyList:{}", companyList);
        }
        log.info("companyList.size:{}", companyList.getTotalElements());
        List<MainResponseDto> dtos = getCompanyInfo(companyList.toList(), memberId, "officeCompany");
        Map<String, Object> map = new HashMap<>();
        map.put("totalSize", companyList.getTotalElements());
        map.put("totalPage", companyList.getTotalPages());
        map.put("company", dtos);
        return map;
    }

    // 전체 데스크 조회
    public Map<String, Object> getTotalDesk(Optional<SearchRequestDto> searchDto, int page, Long memberId) {
        log.info("searchDto:{}", searchDto);
        log.info("memberId:{}", memberId);

        Page<Company> companyList;
        if (searchDto.isPresent()) {
            Set<Long> companyIdList = spaceRepository.findAllCompanyIdByDesk();
            companyList = searchCompany(searchDto, companyIdList, page);
            log.info("companyIdList:{}", companyIdList);
        } else {
            Pageable pageable = PageRequest.of(page - 1, 9);
            List<Long> companyIdList = spaceRepository.findAllByDeskOrderByCompanyIdDesc();
            companyList = companyRepository.findAllCompany(companyIdList, pageable);
            log.info("companyList:{}", companyList);
        }
        log.info("companyList.size:{}", companyList.getTotalElements());
        Map<String, Object> map = new HashMap<>();
        List<MainResponseDto> dtos = getCompanyInfo(companyList.toList(), memberId, "deskCompany");
        map.put("totalSize", companyList.getTotalElements());
        map.put("totalPage", companyList.getTotalPages());
        map.put("company", dtos);
        return map;
    }

    // 전체 회의실 조회
    public Map<String, Object> getTotalMeetingRoom(Optional<SearchRequestDto> searchDto, int page, Long memberId) {
        log.info("searchDto:{}", searchDto);
        log.info("memberId:{}", memberId);

        Page<Company> companyList;
        if (searchDto.isPresent()) {
            Set<Long> companyIdList = spaceRepository.findAllCompanyIdByMeetingRoom();
            companyList = searchCompany(searchDto, companyIdList, page);
            log.info("companyIdList:{}", companyIdList);
        } else {
            Pageable pageable = PageRequest.of(page - 1, 9);
            List<Long> companyIdList = spaceRepository.findAllByMeetingRoomOrderByCompanyIdDesc();
            companyList = companyRepository.findAllCompany(companyIdList, pageable);
            log.info("companyList:{}", companyList);
        }
        log.info("companyList.size:{}", companyList.getTotalElements());
        Map<String, Object> map = new HashMap<>();
        List<MainResponseDto> dtos = getCompanyInfo(companyList.toList(), memberId, "meetingRoomCompany");
        map.put("totalSize", companyList.getTotalElements());
        map.put("totalPage", companyList.getTotalPages());
        map.put("company", dtos);
        return map;
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
                //dto.setFirstImage(spaces.get(0).getSpaceImages().get(0).getSpaceImage()); // 업체에 등록된 사진
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
            } //else continue; //공간이 등록되어 있지 않으면, continue
            dto.setFirstImage(company.getImageName()); //업체이미지
            dto.setCompanyId(company.getCompanyId()); //업체번호
            dto.setCompanyName(company.getCompanyName()); //업체이름
            dto.setCompanyLike(checkLike(company, memberId)); //업체 관심등록수
            dto.setLocation(company.getLocation().split(" ")[1]); //업체 위치
            dto.setLowPrice(lowPrice); //업체 최저가
            dto.setTypes(types); //업체 타입들
            dto.setDetails(company.getDetails()); // 상세주소
            dto.setAddress(company.getLocation()); // 업체 주소
            companyInfo.add(dto);
        }
        return companyInfo;
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
        List<String> reservedTimes = reservationService.getReservedTimes(space);

        // 3. 예약 가능한 시간 있는지 확인
        for (String reservedTime : reservedTimes) {
            if (reservedTime.split(" ")[0].equals(searchDto.get().getDate())) {
                times.remove(reservedTime.split(" ")[1]);
            }
        }
        log.info("예약 가능한 시간들:{}", times);
        return times;
    }

    // 검색
    public Page<Company> searchCompany(Optional<SearchRequestDto> searchDto, Set<Long> companyIds, int page) {
        if (searchDto.get().getLocation() != null) {
            log.info("지역 검색:{}", searchDto.get().getLocation());
            log.info("companyIds:{}", companyIds);
            Set<Long> companyIds2 = companyRepository.findAllByLocation("%" + searchDto.get().getLocation() + "%");
            log.info("companyIds2:{}", companyIds2);
            //타입별 공간에서 지역이 안 맞을 시 제거
            Set<Long> finalCompanyIds = companyIds;
            companyIds2.removeIf(companyId -> !finalCompanyIds.contains(companyId));
            companyIds = companyIds2;
            log.info("companyIds2:{}", companyIds2);
        }
        if (searchDto.get().getTime() != null) {
            log.info("날짜+시간 검색:{}", searchDto.get().getDate() + " " + searchDto.get().getTime());
            Set<Long> companyIds2 = new HashSet<>();
            List<Space> spaces = spaceRepository.findAll();
            for (Space space : spaces) {
                // 예약 가능한 시간 구하기
                if (getTimes(space, searchDto).contains(searchDto.get().getTime())) {
                    if (searchDto.get().getLocation() == null) { //시간, 날짜만 검색 시 모두 add
                        companyIds2.add(space.getCompanyId());
                    } else if (companyIds.contains(space.getCompanyId())) { //지역검색 시, 지역 내 날짜, 시간 검색
                        companyIds2.add(space.getCompanyId());
                    }
                }
            }
            companyIds = companyIds2;
        } else if (searchDto.get().getDate() != null & searchDto.get().getTime() == null) {
            log.info("날짜 검색:{}", searchDto.get().getDate());
            List<Space> spaces = spaceRepository.findAll();
            Set<Long> companyIds2 = new HashSet<>();
            for (Space space : spaces) {
                // 예약 가능한 시간 구하기
                List<String> times = getTimes(space, searchDto);
                if (times.size() > 0) {
                    if (searchDto.get().getLocation() == null) { //날짜만 검색 시 모두 add
                        companyIds2.add(space.getCompanyId());
                    } else if (companyIds.contains(space.getCompanyId())) { //지역검색 시, 지역 내 날짜 검색
                        companyIds2.add(space.getCompanyId());
                    }
                }
            }
            companyIds = companyIds2;
        }
        log.info("companyIds:{}", companyIds);
        Pageable pageable = PageRequest.of(page - 1, 9);
        return companyRepository.findAllByCompanyIdOrderByCreatedTimeDesc(pageable, companyIds);
    }

    // 업체 신청 목록 보기
    public List<CompanyJoinResponseDto> companySelectAll() {

//        Member member1 = memberRepository.findById(member.getMemberId()).orElseThrow(){
//
//        }
        List<CompanyJoinResponseDto> companyJoinResponseDtos = new ArrayList<>();

        List<Company> companies = companyRepository.findAll();
        for (Company data : companies
        ) {
            CompanyJoinResponseDto responseDto = new CompanyJoinResponseDto();
            responseDto.setCompanyId(data.getCompanyId());
            responseDto.setCompanyName(data.getCompanyName()); // 업체명
            responseDto.setInfo(data.getInfo()); // 업체 정보
            responseDto.setLocation(data.getLocation()); // 업체 위치
            responseDto.setDetails(data.getDetails()); // 업체 상세정보
            responseDto.setApproveStatus(data.getApproveStatus()); // 업체 활동 상태
            responseDto.setImageName(data.getImageName()); // 업체 이미지

            responseDto.setEmail(data.getMember().getEmail()); // 회원 이메일
            responseDto.setMemberName(data.getMember().getMemberName()); // 회원 이름
            responseDto.setProfileImage(data.getMember().getImgName()); // 프로필 이미지

            companyJoinResponseDtos.add(responseDto);
        }

        return companyJoinResponseDtos;
    }

    // 업체관리자로 승인하기
    public void approve(Long companyId) {
        Company company = companyRepository.findByCompanyId(companyId);
        company.setApproveStatus("001"); // 활동상태 활동중으로 변경
        companyRepository.save(company); // 저장.

        Member member = memberRepository.findByMemberId(company.getMember().getMemberId());
        member.setAuthority("manager"); // 권한을 매니저로 변경
        memberRepository.save(member); //저장.

    }

    // 업체관리자 승인 거부
    public void disapprove(Long companyId) {
        Company company = companyRepository.findByCompanyId(companyId);
        company.setApproveStatus("002"); // 활동상태 활동 정지로 변경
        companyRepository.save(company); // 저장.

        Member member = memberRepository.findByMemberId(company.getMember().getMemberId());
        member.setAuthority("member"); // 권한을 멤버로 변경
        memberRepository.save(member); //저장.
    }

    public List<MainResponseDto> companyList(Long memberId) {
        Set<Long> companyIdList = spaceRepository.findAllCompanyIds();
        log.info("companyIdList:{}", companyIdList);

        List<Company> companyList = companyRepository.findAllByCompanyIds(companyIdList);
        log.info("companyList:{}", companyList.size());

        List<MainResponseDto> dtos = getCompanyInfo(companyList, memberId, "totalCompany");
        log.info("dtos.size():{}", dtos.size());
        return dtos;
    }

    // 업체 정보 조회
    public CompanyInfoResponseDto getCompany(Long companyId){

        Company company = companyRepository.findByCompanyId(companyId);
        CompanyInfoResponseDto companyInfoResponseDto = new CompanyInfoResponseDto();
        companyInfoResponseDto.setCompanyId(companyId);
        companyInfoResponseDto.setCompanyName(company.getCompanyName());
        companyInfoResponseDto.setLocation(company.getLocation());
        companyInfoResponseDto.setDetails(company.getDetails());
        companyInfoResponseDto.setInfo(company.getInfo());
        companyInfoResponseDto.setRules(company.getRules());
        companyInfoResponseDto.setSummary(company.getSummary());
        companyInfoResponseDto.setImageName(company.getImageName());

        return companyInfoResponseDto;
    }

    // 업체 정보 수정하기
    public void update(CompanyInfoRequestDto companyInfoRequestDto, Long companyId) {
        Company company = companyRepository.findByCompanyId(companyId);

        company.setCompanyName(companyInfoRequestDto.getCompanyName());
        company.setLocation(companyInfoRequestDto.getLocation());
        company.setDetails(companyInfoRequestDto.getDetails());
        company.setInfo(companyInfoRequestDto.getInfo());
        company.setSummary(companyInfoRequestDto.getSummary());
        company.setRules(companyInfoRequestDto.getRules());

        if(companyInfoRequestDto.getMultipartFile() != null){
            String imageUrl = companyS3Service.update(company.getCompanyId(), companyInfoRequestDto.getMultipartFile());
            company.setImageName(imageUrl);
        }

        companyRepository.save(company);
    }
}
