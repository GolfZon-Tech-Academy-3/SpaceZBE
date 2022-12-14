package com.golfzon.lastspacezbe.company.service;

import com.golfzon.lastspacezbe.chat.entity.ChatRoom;
import com.golfzon.lastspacezbe.chat.repository.ChatRoomRepository;
import com.golfzon.lastspacezbe.chat.service.ChatRoomService;
import com.golfzon.lastspacezbe.chat.repository.ChatRoomsRepository;
import com.golfzon.lastspacezbe.company.dto.*;
import com.golfzon.lastspacezbe.company.entity.Company;
import com.golfzon.lastspacezbe.company.entity.CompanyLike;
import com.golfzon.lastspacezbe.company.repository.CompanyLikeRepository;
import com.golfzon.lastspacezbe.company.repository.CompanyRepository;
import com.golfzon.lastspacezbe.member.entity.Member;
import com.golfzon.lastspacezbe.member.repository.MemberRepository;
import com.golfzon.lastspacezbe.reservation.service.ReservationService;
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
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
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
    private final ChatRoomsRepository chatRoomsRepository;
    private final ChatRoomRepository chatRoomRepository;

    private final CompanyS3Service companyS3Service;

    private final ChatRoomService chatRoomService;

    private static final String CHAT_ROOMS = "CHAT_ROOM";
    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, ChatRoom> opsHashChatRoom;
    private Map<String, ChannelTopic> topics;
    @PostConstruct
    private void init() {
        opsHashChatRoom = redisTemplate.opsForHash();
        topics = new HashMap<>();
    }

    // ?????? ?????? (??????)
    public void companyPost(CompanyRequestDto companyRequestDto, Member member) {

        //????????????
        Company isCompany = companyRepository.findByMember(member);
        if (isCompany != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "?????? ???????????? ????????? ?????? ?????????????????????.");
        }
        //?????? ??????
        Company company = new Company(
                member, companyRequestDto.getCompanyName(), companyRequestDto.getInfo(), companyRequestDto.getRules(),
                companyRequestDto.getLocation(), companyRequestDto.getDetails(), companyRequestDto.getSummary(), "000", 0
        );

        if (companyRequestDto.getMultipartFile() != null) {
            String imageUrl = companyS3Service.upload(companyRequestDto.getMultipartFile());
            company.setImageName(imageUrl);
        }

        companyRepository.save(company);
    }

    // ?????? ?????? ????????????
    public CompanyResponseDto getCompanyInfo(Long companyId, Long memberId) {
        // ?????? ??????
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "???????????? companyId??? ????????????."));
        // ????????? ????????? ??????
        List<Space> spaces = spaceRepository.findAllByCompanyId(companyId);
        // ????????? ????????? ?????????
        List<String> spaceImages = new ArrayList<>();
        spaceImages.add(company.getImageName());
        List<SpaceResponseDto> dtos = new ArrayList<>();
        for (Space space : spaces) {
            for (SpaceImage image : space.getSpaceImages()) {
                spaceImages.add(image.getSpaceImage());
            }
            dtos.add(new SpaceResponseDto(space, space.getSpaceImages().get(0).getSpaceImage()));
        }
        // ???????????? ??????
        Boolean likeCheck = checkLike(company, memberId);

        return new CompanyResponseDto(company, spaceImages, dtos, likeCheck);
    }

    // ?????? ?????? ??????/??????
    public Boolean companyLike(Long companyId, Long memberId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "???????????? companyId??? ????????????."));
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

    // ?????? ?????? ?????? ?????? ??????
    public Boolean checkLike(Company company, Long memberId) {
        CompanyLike companyLike = companyLikeRepository.findByCompanyAndMemberId(company, memberId);
        boolean likeCheck = false;
        if (companyLike != null) {
            likeCheck = true;
        }
        return likeCheck;
    }

    // ???????????? 8??? ????????????
    @Transactional
    public List<MainResponseDto> getHotCompany(Long memberId) {
        List<Company> companyList = companyRepository.findTop8ByApproveStatusOrderByReviewAvgDesc("001");
        log.info("companyList.size:{}", companyList.size());
        return getCompanyInfo(companyList, memberId, "hotCompany");
    }

    // ???????????? ?????? 4??? ????????????
    public List<MainResponseDto> getNewCompany(Long memberId) {
        List<Company> companyList = companyRepository.findTop4ByApproveStatusOrderByCreatedTimeDesc("001");
        log.info("companyList.size:{}", companyList.size());
        return getCompanyInfo(companyList, memberId, "newCompany");
    }

    // ?????? ?????? ??????
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

    // ?????? ????????? ??????
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

    // ?????? ????????? ??????
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

    // ?????? ????????? ??????
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

    // return??? ?????? ????????? ??????
    public List<MainResponseDto> getCompanyInfo(List<Company> companyList, Long memberId, String type) {
        List<MainResponseDto> companyInfo = new ArrayList<>();

        int lowPrice = 0; // ????????????

        for (Company company : companyList) {
            if (type.equals("hotCompany") && company.getReviewAvg()==0.0) continue; //?????? ????????? 0?????? continue
            MainResponseDto dto = new MainResponseDto();
            Set<String> types = new HashSet<>(); // ????????? type???

            List<Space> spaces = spaceRepository.findAllByCompanyId(company.getCompanyId());
            if (!spaces.isEmpty()) {
                //dto.setFirstImage(spaces.get(0).getSpaceImages().get(0).getSpaceImage()); // ????????? ????????? ??????
                lowPrice = spaces.get(0).getPrice();
                log.info("price:{}", lowPrice);
                for (Space space : spaces) {
                    types.add(space.getType().split(" ")[0]); // ????????? ????????? ??????
                    // ????????? ????????? ?????? ??????
                    if (space.getPrice() < lowPrice) {
                        lowPrice = space.getPrice();
                    }
                }
                int reviewSize = reviewRepository.countByCompanyId(company.getCompanyId());
                dto.setReviewSize(reviewSize);
            } //else continue; //????????? ???????????? ?????? ?????????, continue
            dto.setFirstImage(company.getImageName()); //???????????????
            dto.setCompanyId(company.getCompanyId()); //????????????
            dto.setCompanyName(company.getCompanyName()); //????????????
            dto.setCompanyLike(checkLike(company, memberId)); //?????? ???????????????
            dto.setLocation(company.getLocation().split(" ")[1]); //?????? ??????
            dto.setLowPrice(lowPrice); //?????? ?????????
            dto.setTypes(types); //?????? ?????????
            dto.setDetails(company.getDetails()); // ????????????
            dto.setAddress(company.getLocation()); // ?????? ??????
            dto.setAvgReview(company.getReviewAvg()); // ????????? ????????? ??????
            companyInfo.add(dto);
        }
        return companyInfo;
    }

    // ?????? ????????? ?????? ??????(?????? ??????)
    public List<String> getTimes(Space space, Optional<SearchRequestDto> searchDto) {
        List<String> times = new ArrayList<>();
        if(!space.getType().equals("?????????")){
            // ???????????? (????????? ??????)
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
            Calendar startCal = Calendar.getInstance();
            Calendar endCal = Calendar.getInstance();
            // ?????? 1?????? ???????????? ?????????
            Date startDate;
            try {
                startDate = formatter.parse(space.getOpenTime());
                startCal.setTime(startDate);
                String time;
                Date endDate = formatter.parse(space.getCloseTime());
                endCal.setTime(endDate);
                // ???????????? ???????????? ?????? ?????? ????????? ?????? ?????????
                while (startCal.before(endCal)) {
                    time = formatter.format(startCal.getTime());
                    times.add(time);
                    startCal.add(Calendar.HOUR, +1);
                }
                // break time ??? ?????????
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

            // 2. ????????? ?????? ?????????
            List<String> reservedTimes = reservationService.getReservedTimes(space);

            // 3. ?????? ????????? ?????? ????????? ??????
            for (String reservedTime : reservedTimes) {
                if (reservedTime.split(" ")[0].equals(searchDto.get().getDate())) {
                    times.remove(reservedTime.split(" ")[1]);
                }
            }
        } else {
            // 2. ????????? ?????? ?????????
            List<String> reservedTimes = reservationService.getReservedTimes(space);

            // 3. ?????? ????????? ?????? ????????? ??????
            for (String reservedTime : reservedTimes) {
                if (reservedTime.split(" ")[0].equals(searchDto.get().getDate())) {
                    return new ArrayList<>();
                } else {
                    times.add("????????????");
                }
            }
        }
        log.info("?????? ????????? ?????????:{}", times);
        return times;
    }

    // ??????
    public Page<Company> searchCompany(Optional<SearchRequestDto> searchDto, Set<Long> companyIds, int page) {
        if (searchDto.get().getLocation() != null) {
            log.info("?????? ??????:{}", searchDto.get().getLocation());
            log.info("companyIds:{}", companyIds);
            Set<Long> companyIds2 = companyRepository.findAllByLocation("%" + searchDto.get().getLocation() + "%");
            log.info("companyIds2:{}", companyIds2);
            //????????? ???????????? ????????? ??? ?????? ??? ??????
            Set<Long> finalCompanyIds = companyIds;
            companyIds2.removeIf(companyId -> !finalCompanyIds.contains(companyId));
            companyIds = companyIds2;
            log.info("companyIds2:{}", companyIds2);
        }
        if (searchDto.get().getTime() != null) {
            log.info("??????+?????? ??????:{}", searchDto.get().getDate() + " " + searchDto.get().getTime());
            Set<Long> companyIds2 = new HashSet<>();
            List<Space> spaces = spaceRepository.findAll();
            for (Space space : spaces) {
                // ?????? ????????? ?????? ?????????
                if (getTimes(space, searchDto).contains(searchDto.get().getTime())) {
                    if (searchDto.get().getLocation() == null) { //??????, ????????? ?????? ??? ?????? add
                        companyIds2.add(space.getCompanyId());
                    } else if (companyIds.contains(space.getCompanyId())) { //???????????? ???, ?????? ??? ??????, ?????? ??????
                        companyIds2.add(space.getCompanyId());
                    }
                }
            }
            companyIds = companyIds2;
        } else if (searchDto.get().getDate() != null & searchDto.get().getTime() == null) {
            log.info("?????? ??????:{}", searchDto.get().getDate());
            List<Space> spaces = spaceRepository.findAll();
            Set<Long> companyIds2 = new HashSet<>();
            for (Space space : spaces) {
                // ?????? ????????? ?????? ?????????
                List<String> times = getTimes(space, searchDto);
                if (times.size() > 0) {
                    if (searchDto.get().getLocation() == null) { //????????? ?????? ??? ?????? add
                        companyIds2.add(space.getCompanyId());
                    } else if (companyIds.contains(space.getCompanyId())) { //???????????? ???, ?????? ??? ?????? ??????
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

    // ?????? ?????? ?????? ??????
    public List<CompanyJoinResponseDto> companySelectAll() {

        List<CompanyJoinResponseDto> companyJoinResponseDtos = new ArrayList<>();

        List<Company> companies = companyRepository.findAll();
        for (Company data : companies
        ) {
            CompanyJoinResponseDto responseDto = new CompanyJoinResponseDto();
            responseDto.setCompanyId(data.getCompanyId());
            responseDto.setCompanyName(data.getCompanyName()); // ?????????
            responseDto.setInfo(data.getInfo()); // ?????? ??????
            responseDto.setLocation(data.getLocation()); // ?????? ??????
            responseDto.setDetails(data.getDetails()); // ?????? ????????????
            responseDto.setApproveStatus(data.getApproveStatus()); // ?????? ?????? ??????
            responseDto.setImageName(data.getImageName()); // ?????? ?????????

            responseDto.setEmail(data.getMember().getUsername()); // ?????? ?????????
            responseDto.setMemberName(data.getMember().getMemberName()); // ?????? ??????
            responseDto.setProfileImage(data.getMember().getImgName()); // ????????? ?????????

            companyJoinResponseDtos.add(responseDto);
        }

        return companyJoinResponseDtos;
    }

    // ?????????????????? ????????????
    public void approve(Long companyId) {
        Company company = companyRepository.findByCompanyId(companyId);
        company.setApproveStatus("001"); // ???????????? ??????????????? ??????
        companyRepository.save(company); // ??????.

        Member member = memberRepository.findByMemberId(company.getMember().getMemberId());
        member.setAuthority("manager"); // ????????? ???????????? ??????
        memberRepository.save(member);

        // ??????????????? ????????? ??????
        ChatRoom chatRoom = createChatRoom(company.getCompanyName()+"?????? ???");
        chatRoom.setMember(member);
        chatRoomsRepository.save(chatRoom);

    }

    // ????????? ??????
    public ChatRoom createChatRoom(String name) {
        ChatRoom chatRoom = ChatRoom.create(name);
        opsHashChatRoom.put(CHAT_ROOMS, chatRoom.getRoomId(), chatRoom);
        return chatRoom;
    }

    // ??????????????? ?????? ??????
    public void disapprove(Long companyId) {
        Company company = companyRepository.findByCompanyId(companyId);
        company.setApproveStatus("002"); // ???????????? ?????? ????????? ??????
        companyRepository.save(company); // ??????.

        Member member = memberRepository.findByMemberId(company.getMember().getMemberId());
        member.setAuthority("member"); // ????????? ????????? ??????
        memberRepository.save(member); //??????.
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

    // ?????? ?????? ??????
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

    // ?????? ?????? ????????????
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
