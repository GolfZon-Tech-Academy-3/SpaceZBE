package com.golfzon.lastspacezbe.company.controller;

import com.golfzon.lastspacezbe.company.dto.CompanyResponseDto;
import com.golfzon.lastspacezbe.company.dto.MainResponseDto;
import com.golfzon.lastspacezbe.company.dto.CompanyJoinResponseDto;
import com.golfzon.lastspacezbe.company.dto.CompanyRequestDto;
import com.golfzon.lastspacezbe.company.dto.SearchRequestDto;
import com.golfzon.lastspacezbe.company.service.CompanyService;
import com.golfzon.lastspacezbe.member.entity.Member;
import com.golfzon.lastspacezbe.security.UserDetailsImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Api(tags = "Company 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/company")
public class CompanyController {

    private final CompanyService companyService;

    // 업체 등록 (신청)
    @ApiOperation(value = "업체 등록", notes = "업체 등록이 가능합니다.")
    @PostMapping(value = "/post")
    public ResponseEntity<String> companyPost(CompanyRequestDto companyRequestDto) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("principal:{}",principal);
        Member member = ((UserDetailsImpl)principal).getMember();

        // 업체 등록 service
        companyService.companyPost(companyRequestDto,member);


        return ResponseEntity.ok()
                .contentType(new MediaType("application", "json", StandardCharsets.UTF_8))
                .body("result : 업체 관리자로 신청 완료");
    }

    // 업체 신청 목록보기
    @ApiOperation(value = "업체 신청 목록 조회", notes = "업체 신청 목록 조회기능입니다.")
    @GetMapping(value = "/manager/list")
    public ResponseEntity<List<CompanyJoinResponseDto>> companySelectAll() {


        List<CompanyJoinResponseDto> responseDtos = companyService.companySelectAll();
        // 업체 신청 목록보기
        return ResponseEntity.ok()
                .body(responseDtos);
    }

    @ApiOperation(value = "업체 관리자로 승인", notes = "업체관리자로 승인 기능입니다.")
    @PutMapping(value = "/approve/{companyId}")
    public ResponseEntity<String> approve(@PathVariable(name = "companyId") Long companyId) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("principal:{}",principal);
        Member member = ((UserDetailsImpl)principal).getMember();

        companyService.approve(companyId);

        // 업체 신청 목록보기
        return ResponseEntity.ok()
                .body("result : 승인완료");
    }

    @ApiOperation(value = "업체 관리자로 승인 거부 ", notes = "업체관리자 신청 거부 기능입니다.")
    @PutMapping(value = "/disapprove/{companyId}")
    public ResponseEntity<String> disapprove(@PathVariable(name = "companyId") Long companyId) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("principal:{}",principal);
        Member member = ((UserDetailsImpl)principal).getMember();

        companyService.disapprove(companyId);

        // 업체 신청 목록보기
        return ResponseEntity.ok()
                .body("result : 승인거부");
    }



    // 메인페이지
    @ApiOperation(value = "업체정보 조회", notes = "업체 상세페이지 조회기능입니다.")
    @GetMapping(value = "/main")
    public ResponseEntity<Map<String, List<MainResponseDto>>> main() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("principal:{}",principal);
        Member member = ((UserDetailsImpl)principal).getMember();

        Map<String, List<MainResponseDto>> map = new HashMap<>();
        map.put("hotCompany", companyService.getHotCompany(member.getMemberId()));
        map.put("newCompany", companyService.getNewCompany(member.getMemberId()));

        // 업체 상세보기 service
        return ResponseEntity.ok()
                .contentType(new MediaType("application", "json", StandardCharsets.UTF_8))
                .body(map);
    }

    // 업체 상세페이지
    @ApiOperation(value = "업체정보 조회", notes = "업체 상세페이지 조회기능입니다.")
    @GetMapping(value = "/details/{companyId}")
    public ResponseEntity<CompanyResponseDto> companyInfo(@PathVariable(name = "companyId") Long companyId) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("principal:{}",principal);
        Member member = ((UserDetailsImpl)principal).getMember();
        // 업체 상세보기 service
        return ResponseEntity.ok()
                .contentType(new MediaType("application", "json", StandardCharsets.UTF_8))
                .body(companyService.getCompanyInfo(companyId, member.getMemberId()));
    }

    // 업체 북마크
    @ApiOperation(value = "업체관심등록", notes = "업체 관심등록/취소 기능입니다.")
    @PostMapping(value = "/like/{companyId}")
    public ResponseEntity<Boolean> companyLike(@PathVariable(name = "companyId") Long companyId) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("principal:{}",principal);
        Member member = ((UserDetailsImpl)principal).getMember();
        // 업체 상세보기 service
        return ResponseEntity.ok()
                .contentType(new MediaType("application", "json", StandardCharsets.UTF_8))
                .body(companyService.companyLike(companyId, member.getMemberId()));
    }

    // 업체 전체조회(최신 등록순)
    //1. 타입별로 검색->지역(native query, like="%?1%")
    //2. company의 space loop를 돌면서, 해당 spaceId의 resveration을 가져오는데 해당 날짜에 해당하는 예약만 get
    //3. 예약 가능한 시간의 리스트에서 reservation된 시간을 뺌/ size>0 이면 add
    //4. 날짜+시간을 검색 시, isExsist 이면 add
    // 메인페이지
    @ApiOperation(value = "업체정보 전체조회", notes = "업체 전체보기 조회기능입니다.")
    @PostMapping(value = "/total")
    public ResponseEntity<Map<String, Object>> totalCompany(@RequestBody Optional<SearchRequestDto> searchDto, @RequestParam(name = "page") int page) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("principal:{}",principal);
        Member member = ((UserDetailsImpl)principal).getMember();

        // 업체 상세보기 service
        return ResponseEntity.ok()
                .contentType(new MediaType("application", "json", StandardCharsets.UTF_8))
                .body(companyService.getTotalCompany(searchDto, page, member.getMemberId()));
    }

    @ApiOperation(value = "업체정보 오피스 조회", notes = "오피스를 등록한 업체 전체보기 조회기능입니다.")
    @PostMapping(value = "/office")
    public ResponseEntity<Map<String, Object>> totalOffice(@RequestBody Optional<SearchRequestDto> searchDto, @RequestParam(name = "page") int page) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("principal:{}",principal);
        Member member = ((UserDetailsImpl)principal).getMember();

        // 업체 상세보기 service
        return ResponseEntity.ok()
                .contentType(new MediaType("application", "json", StandardCharsets.UTF_8))
                .body(companyService.getTotalOffice(searchDto, page, member.getMemberId()));
    }


    @ApiOperation(value = "업체정보 데스크 조회", notes = "데스크를 등록한 업체 전체보기 조회기능입니다.")
    @PostMapping(value = "/desk")
    public ResponseEntity<Map<String, Object>> totalDesk(@RequestBody Optional<SearchRequestDto> searchDto, @RequestParam(name = "page") int page) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("principal:{}",principal);
        Member member = ((UserDetailsImpl)principal).getMember();

        // 업체 상세보기 service
        return ResponseEntity.ok()
                .contentType(new MediaType("application", "json", StandardCharsets.UTF_8))
                .body(companyService.getTotalDesk(searchDto, page, member.getMemberId()));
    }

    @ApiOperation(value = "업체정보 회의실 조회", notes = "회의실를 등록한 업체 전체보기 조회기능입니다.")
    @PostMapping(value = "/meeting-room")
    public ResponseEntity<Map<String, Object>> totalMeetingRoom(@RequestBody Optional<SearchRequestDto> searchDto, @RequestParam(name = "page") int page) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("principal:{}",principal);
        Member member = ((UserDetailsImpl)principal).getMember();

        // 업체 상세보기 service
        return ResponseEntity.ok()
                .contentType(new MediaType("application", "json", StandardCharsets.UTF_8))
                .body(companyService.getTotalMeetingRoom(searchDto, page, member.getMemberId()));
    }



    //공간이 등록된 업체 모두 조회
    @GetMapping("/space/list")
    public ResponseEntity<List<MainResponseDto>> companyList() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("principal:{}",principal);
        Member member = ((UserDetailsImpl)principal).getMember();

        return ResponseEntity.ok()
                .contentType(new MediaType("application", "json", StandardCharsets.UTF_8))
                .body(companyService.companyList(member.getMemberId()));
    }
}
