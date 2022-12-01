package com.golfzon.lastspacezbe.member.controller;

import com.golfzon.lastspacezbe.member.dto.SignupRequestDto;
import com.golfzon.lastspacezbe.member.entity.Member;
import com.golfzon.lastspacezbe.member.service.MemberService;
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
import java.util.List;

@Slf4j
@Api(tags = "마스터 컨트롤러")
@RequestMapping(value = "/master")
@RestController
@RequiredArgsConstructor
public class MasterController {

    private final MemberService memberService;

    //마스터 목록보기
    @ApiOperation(value = "마스터 목록조회", notes = "마스터로 등록된 회원 목록 조회 처리입니다.")
    @GetMapping("/list")
    public ResponseEntity<List<SignupRequestDto>> masterList() {

        return ResponseEntity.ok()
                .contentType(new MediaType("application", "json", StandardCharsets.UTF_8))
                .body(memberService.masterList());
    }

    //마스터 목록보기
    @ApiOperation(value = "마스터 목록조회", notes = "마스터로 등록된 회원 목록 조회 처리입니다.")
    @GetMapping("/member/list")
    public ResponseEntity<List<SignupRequestDto>> memberList(@RequestParam(value = "searchWord") String searchWord) {

        return ResponseEntity.ok()
                .contentType(new MediaType("application", "json", StandardCharsets.UTF_8))
                .body(memberService.memberList(searchWord));
    }
}
