package com.golfzon.lastspacezbe.inquiry.service;

import com.golfzon.lastspacezbe.inquiry.dto.InquiryRequestDto;
import com.golfzon.lastspacezbe.inquiry.dto.InquiryResponseDto;
import com.golfzon.lastspacezbe.inquiry.entity.Inquiry;
import com.golfzon.lastspacezbe.inquiry.repository.InquiryRepository;
import com.golfzon.lastspacezbe.member.entity.Member;
import com.golfzon.lastspacezbe.member.repository.MemberRepository;
import com.golfzon.lastspacezbe.space.entity.Space;
import com.golfzon.lastspacezbe.space.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class InquiryBackOfficeService {

    private final InquiryRepository inquiryRepository;
    private final SpaceRepository spaceRepository;
    private final MemberRepository memberRepository;

    // 백오피스 문의 내역
    public List<InquiryResponseDto> inquirySelectAll(Long companyId) {

        List<InquiryResponseDto> responseDtos = new ArrayList<>();

        // 업체의 사무공간 조회
        List<Space> spaces = spaceRepository.findAllByCompanyId(companyId);
        for (Space dataSpace : spaces
        ) {
            List<Inquiry> inquiries = inquiryRepository.findAllBySpaceId(dataSpace.getSpaceId());
            for (Inquiry dataInquiry: inquiries
                 ) {
//                // 문의한 회원의 닉네임
//                Optional<Member> member = memberRepository.findById(dataInquiry.getMember().getMemberId());
//                log.info("memberId : {}", dataInquiry.getMember().getMemberId());

                InquiryResponseDto inquiryResponseDto = new InquiryResponseDto();
                inquiryResponseDto.setInquiryId(dataInquiry.getInquiryId()); // 문의 번호
                inquiryResponseDto.setInquiries(dataInquiry.getInquiries()); // 문의내용
                inquiryResponseDto.setAnswers(dataInquiry.getAnswers()); // 답변 내용
                inquiryResponseDto.setInquiryTime(dataInquiry.getInquiriesTime().toString().substring(0,10)+" "+
                        dataInquiry.getInquiriesTime().toString().substring(11,16)); // 문의 날짜
                inquiryResponseDto.setType(dataSpace.getType()); // 사무공간타입
                inquiryResponseDto.setSpaceName(dataSpace.getSpaceName()); // 사무공간 이름
                inquiryResponseDto.setMemberId(dataInquiry.getMember().getMemberId());
                inquiryResponseDto.setMemberName(dataInquiry.getMember().getMemberName()); // 회원 닉네임
                inquiryResponseDto.setIsAnswer(dataInquiry.getAnswers()!=null ? "true" : "false"); // 답변 유무

                responseDtos.add(inquiryResponseDto);
            }
        }
        return responseDtos;
    }
}
