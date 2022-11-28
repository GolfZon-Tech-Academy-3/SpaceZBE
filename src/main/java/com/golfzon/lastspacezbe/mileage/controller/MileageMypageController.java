package com.golfzon.lastspacezbe.mileage.controller;

import com.golfzon.lastspacezbe.member.entity.Member;
import com.golfzon.lastspacezbe.mileage.service.MileageService;
import com.golfzon.lastspacezbe.security.UserDetailsImpl;
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
@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MileageMypageController {

    private final MileageService mileageService;
    // 마일리지 조회
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
