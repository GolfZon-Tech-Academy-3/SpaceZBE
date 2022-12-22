package com.golfzon.lastspacezbe.member.service;

import com.golfzon.lastspacezbe.member.dto.SignupRequestDto;
import com.golfzon.lastspacezbe.member.entity.Member;
import com.golfzon.lastspacezbe.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor

public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;
    private final MemberS3Service memberS3Service;

    public static String[] profileImages = new String[]{"img_0001.png", "img_0002.png", "img_0003.png"};

    //회원가입
    public void signup(SignupRequestDto signupRequestDto) {
        log.info("signup()...");
        log.info("{}", signupRequestDto);


        // 아이디 중복확인
        checkEmail(signupRequestDto.getEmail());
        // 기본이미지 저장(1,2,3 중 랜덤으로 저장)
        signupRequestDto.setImgName("https://spacez3.s3.ap-northeast-2.amazonaws.com/"+profileImages[new Random().nextInt(2)]);
        // 비밀번호 암호화
        signupRequestDto.setPassword(passwordEncoder.encode(signupRequestDto.getPassword()));
        memberRepository.save(new Member(signupRequestDto));
    }

    //이메일 인증
    public int sendEmail(SignupRequestDto signupRequestDto) {
        log.info("sendEmail()...");
        log.info("{}", signupRequestDto);
        // 아이디 중복확인
        checkEmail(signupRequestDto.getEmail());
        // 이메일 인증번호 -> 난수생성
        int num = ThreadLocalRandom.current().nextInt(100000, 1000000);
        log.info("certificate num:{}", num);
        // createMimeMessage 로 초기화
        MimeMessage msg = javaMailSender.createMimeMessage();
        // 내용을 setting
        try {
            msg.setSubject("[SpaceZ] 인증번호를 입력해주세요.");
            msg.setText("인증번호 [" + num + "]를 인증 칸에 입력하세요.");
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(signupRequestDto.getEmail()));

            javaMailSender.send(msg);

        } catch (MessagingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이메일 전송 실패");
        }

        return num;
    }

    //아이디 중복확인
    private void checkEmail(String email) {
        log.info("checkEmail()...");
        log.info("checkEmail:{}", email);
        Optional<Member> foundByEmail = memberRepository.findByUsername(email);
        log.info("foundByEmail:{}", foundByEmail);
        if (foundByEmail.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 존재하는 아이디입니다.");
        }
        Pattern emailPattern = Pattern.compile("\\w+@\\w+\\.\\w+(\\.\\w+)?");
        Matcher emailMatcher = emailPattern.matcher(email);
        if (email.length() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이메일을 입력해주세요.");
        }
        if (!emailMatcher.matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이메일형식이 아닙니다.");
        }
    }

    //닉네임 중복확인
    public int checkMemberName(SignupRequestDto signupRequestDto) {
        log.info("checkMemberName()...");
        log.info("{}", signupRequestDto);
        int flag = 0;
        Optional<Member> member = memberRepository.findByMemberName(signupRequestDto.getMemberName());
        log.info("{}", member);
        if (member.isEmpty()) {
            flag = 1;
        }
        log.info("flag:{}", flag);
        return flag;
    }

    //회원정보 수정
    public void updateMember(SignupRequestDto signupRequestDto, Member member) {
        log.info("updateMember()...");
        log.info("signupRequestDto:{}", signupRequestDto);
        log.info("member:{}", member);

        // 프로필이미지 S3에 저장
        if(signupRequestDto.getMultipartFile() != null){
            String imageUrl = memberS3Service.update(member.getMemberId(), signupRequestDto.getMultipartFile());
            member.setImgName(imageUrl);
        }
        member.setMemberName(signupRequestDto.getMemberName());
        // Update member info.
        memberRepository.save(member);
    }

    //마스터 목록 조회
    public List<SignupRequestDto> masterList() {
        List<Member> members = memberRepository.findAllByAuthority("master");
        List<SignupRequestDto> masters = new ArrayList<>();
        for (Member member:members) {
            masters.add(new SignupRequestDto(member));
        }
        return masters;
    }

    //마스터로 승급될 회원 조회
    public List<SignupRequestDto> memberList(String searchWord) {
        log.info("searchWord:{}",searchWord);
        List<Member> members = memberRepository.findMembers("%"+searchWord+"%");
        log.info("member:{}",members.size());
        List<SignupRequestDto> dtos = new ArrayList<>();
        for (Member member:members) {
            dtos.add(new SignupRequestDto(member));
        }
        return dtos;
    }

    public void approve(Long memberId) {

        Member member = memberRepository.findByMemberId(memberId);
        member.setAuthority("master"); // 권한 변경
        memberRepository.save(member); // 저장.

    }

    // 업체관리자 승인 거부
    public void disapprove(Long memberId) {

        Member member = memberRepository.findByMemberId(memberId);
        member.setAuthority("member"); // 권한 변경
        memberRepository.save(member); // 저장.
    }
}
