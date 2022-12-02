package com.golfzon.lastspacezbe.inquiry.service;

import com.golfzon.lastspacezbe.inquiry.dto.InquiryResponseDto;
import com.golfzon.lastspacezbe.inquiry.dto.InquiryTotalResponseDto;
import com.golfzon.lastspacezbe.member.entity.Member;
import com.golfzon.lastspacezbe.inquiry.dto.InquiryRequestDto;
import com.golfzon.lastspacezbe.inquiry.entity.Inquiry;
import com.golfzon.lastspacezbe.inquiry.repository.InquiryRepository;
import com.golfzon.lastspacezbe.member.repository.MemberRepository;
import com.golfzon.lastspacezbe.space.entity.Space;
import com.golfzon.lastspacezbe.space.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final SpaceRepository spaceRepository;
    private final MemberRepository memberRepository;

    // 문의 작성하기
    public void inquiry(Long companyId, InquiryRequestDto requestDto, Member member) {
        Inquiry inquiry = new Inquiry(companyId, member, requestDto.getInquiries());

        inquiryRepository.save(inquiry); // 문의내용 저장
    }

    // 문의 삭제하기
    public void inquiryDelete(Long inquiryId, Member member) {

        // 문의 삭제 예외처리
        Inquiry inquiry = inquiryRepository.findById(inquiryId).orElseThrow(
                () -> new IllegalArgumentException("해당하는 문의글이 없습니다.")
        );

        Long inquiryMemberId = inquiry.getMember().getMemberId();
        if (!member.getMemberId().equals(inquiryMemberId)) {
            throw new IllegalArgumentException("작성자가 아닙니다.");
        }
        // 삭제
        inquiryRepository.deleteById(inquiryId);
    }

    // 답변 작성하기 / 수정하기
    public void answerPost(Long inquiryId, InquiryRequestDto requestDto, Member member) {

        Inquiry inquiry = inquiryRepository.findById(inquiryId).orElseThrow(
                () -> new IllegalArgumentException("해당하는 문의글이 없습니다.")
        );

        inquiry.setAnswers(requestDto.getAnswers()); // 답변 넣기
        inquiryRepository.save(inquiry); // 변경사항 저장
    }

    // 답변삭제
    public void answerDelete(Long inquiryId, Member member) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId).orElseThrow(
                () -> new IllegalArgumentException("해당하는 문의글이 없습니다.")
        );

        inquiry.setAnswers(null); // 답변 초기화
        inquiryRepository.save(inquiry); // 변경사항 저장
    }

    // 문의 내역
    public List<InquiryTotalResponseDto> totalInquiry(Long companyId) {

        List<Inquiry> inquiries = inquiryRepository.findAllByCompanyId(companyId);
        List<InquiryTotalResponseDto> responseDtos = new ArrayList<>();

        for (Inquiry data : inquiries
        ) {
            Optional<Member> member = memberRepository.findById(data.getMember().getMemberId());

            InquiryTotalResponseDto totalResponseDto = new InquiryTotalResponseDto();
            totalResponseDto.setInquiryId(data.getInquiryId()); // 문의 번호
            totalResponseDto.setInquiries(data.getInquiries()); // 문의내용
            totalResponseDto.setAnswers(data.getAnswers()); // 답변내용
            totalResponseDto.setInquiryTime(data.getInquiriesTime().toString().substring(0,10)+" "
            +data.getInquiriesTime().toString().substring(11,16)); // 문의 날짜
            totalResponseDto.setImagePath(member.get().getImgName()); // 프로필 이미지
            totalResponseDto.setMemberName(member.get().getMemberName()); // 회원 이름

            responseDtos.add(totalResponseDto);
        }

        return responseDtos;
    }
}
