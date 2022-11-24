package com.golfzon.lastspacezbe.company.controller;

import com.golfzon.lastspacezbe.company.Dto.CompanyResponseDto;
import com.golfzon.lastspacezbe.company.service.CompanyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@Slf4j
@Api(tags = "Company 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/company")
public class CompanyController {

    private final CompanyService companyService;

    @ApiOperation(value = "업체정보 조회", notes = "업체 상세페이지 조회기능입니다.")
    @GetMapping(value = "/details")
    public ResponseEntity<CompanyResponseDto> space(@RequestParam(name = "companyId") Long companyId) {
        // 업체 상세보기 service
        return ResponseEntity.ok()
                .contentType(new MediaType("application", "json", StandardCharsets.UTF_8))
                .body(companyService.getCompanyInfo(companyId));
    }

}
