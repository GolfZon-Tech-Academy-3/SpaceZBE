package com.golfzon.lastspacezbe.inquiry.service;

import com.golfzon.lastspacezbe.member.entity.Member;
import com.golfzon.lastspacezbe.inquiry.dto.InquiryRequestDto;
import com.golfzon.lastspacezbe.inquiry.entity.Inquiry;
import com.golfzon.lastspacezbe.inquiry.repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class InquiryService {

    private final InquiryRepository inquiryRepository;

    // 문의 작성하기
    public void inquiry(Long spaceId, InquiryRequestDto requestDto, Member member){
        Inquiry inquiry = new Inquiry(spaceId,member,requestDto.getInquiries());

        inquiryRepository.save(inquiry); // 문의내용 저장
    }

    // 문의 삭제하기
    public void inquiryDelete(Long inquiryId, Member member) {

        // 문의 삭제 예외처리
        Inquiry inquiry = inquiryRepository.findById(inquiryId).orElseThrow(
                () -> new IllegalArgumentException("해당하는 문의글이 없습니다.")
        );

        Long inquiryMemberId = inquiry.getMember().getMemberId();
        if(!member.getMemberId().equals(inquiryMemberId)){
            throw new IllegalArgumentException("작성자가 아닙니다.");
        }
        // 삭제
        inquiryRepository.deleteById(inquiryId);
    }
}
