package com.golfzon.lastspacezbe.mileage.controller;

import com.golfzon.lastspacezbe.member.entity.Member;
import com.golfzon.lastspacezbe.mileage.service.MileageService;
import com.golfzon.lastspacezbe.security.UserDetailsImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@Api(tags = "마이페이지 마일리지 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MileageMypageController {

    private final MileageService mileageService;
    // 마일리지 조회
    @ApiOperation(value = "마이페이지 마일리지 조회", notes = "마일리지 조회 기능입니다.")
    @GetMapping("/mileage")
            public ResponseEntity<Map<String, Object>> getMileageInfo(@RequestParam(name="type") String type){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("principal:{}",principal);
        Member member = ((UserDetailsImpl)principal).getMember();
        log.info("member?:{}",member);

        return ResponseEntity.ok()
                .body(mileageService.getMileageInfo(member.getMemberId(),type));
    }

}
