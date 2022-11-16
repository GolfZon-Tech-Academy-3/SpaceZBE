package com.golfzon.lastspacezbe.member.controller;

import com.golfzon.lastspacezbe.member.dto.SignupRequestDto;
import com.golfzon.lastspacezbe.member.service.MemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags="멤버 컨트롤러")
@RequestMapping(value="/member", produces = "application/json; charset=utf8")
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    //회원가입
    @ApiOperation(value="회원가입", notes="회원가입 처리입니다.")
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequestDto signupRequestDto) {
        memberService.signup(signupRequestDto);
        return ResponseEntity.ok()
                .body("회원가입 완료");
    }
    //이메일 인증 및 ID 중복체크
    @ApiOperation(value="이메일인증 및 중복체크", notes="이메일 인증, 이메일 중복체크 처리입니다.")
    @PostMapping("/mail")
    public ResponseEntity<Integer> sendEmail(@RequestBody SignupRequestDto signupRequestDto){
        int num = memberService.sendEmail(signupRequestDto);
        return ResponseEntity.ok()
                .body(num);
    }

    //이메일 인증 및 ID 중복체크
    @ApiOperation(value="닉네임 중복체크", notes="닉네임 중복체크 처리입니다.")
    @PostMapping("/name")
    public ResponseEntity<Integer> checkMemberName(@RequestBody SignupRequestDto signupRequestDto){
        int result = memberService.checkMemberName(signupRequestDto);
        return ResponseEntity.ok()
                .body(result);
    }

}
