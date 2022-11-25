package com.golfzon.lastspacezbe.company.controller;

import com.golfzon.lastspacezbe.company.Dto.CompanyResponseDto;
import com.golfzon.lastspacezbe.company.Dto.MainResponseDto;
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

@Slf4j
@Api(tags = "Company 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/company")
public class CompanyController {

    private final CompanyService companyService;

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

}
