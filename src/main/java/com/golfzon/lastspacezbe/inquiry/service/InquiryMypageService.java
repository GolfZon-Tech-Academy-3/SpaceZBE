package com.golfzon.lastspacezbe.inquiry.service;

import com.golfzon.lastspacezbe.company.entity.Company;
import com.golfzon.lastspacezbe.company.repository.CompanyRepository;
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
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class InquiryMypageService {

    private final InquiryRepository inquiryRepository;
    private final SpaceRepository spaceRepository;
    private final MemberRepository memberRepository;
    private final CompanyRepository companyRepository;

    // 마이페이지 문의 내역
    public List<InquiryResponseDto> inquirySelectAll(Member member) {

        List<InquiryResponseDto> responseDtos = new ArrayList<>();

        List<Inquiry> inquiries = inquiryRepository.findAllByMember(member);

        for (Inquiry data: inquiries
             ) {
            Company company = companyRepository.findByCompanyId(data.getCompanyId());
            InquiryResponseDto responseDto = new InquiryResponseDto();
            responseDto.setCompanyName(company.getCompanyName()); // 업체이름
            responseDto.setInquiryId(data.getInquiryId());// 문의 번호
            responseDto.setInquiries(data.getInquiries()); // 문의내용
            responseDto.setInquiryTime(data.getInquiriesTime().toString().substring(0,10)+" "+
                                      data.getInquiriesTime().toString().substring(11,16)); // 문의 날짜
            responseDto.setAnswers(data.getAnswers()); // 답변 내용
            responseDto.setIsAnswer(data.getAnswers()!=null ? "true" : "false"); // 답변 유무

            responseDtos.add(responseDto);
        }

        return responseDtos;
    }
}
