package com.golfzon.lastspacezbe.inquiry.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InquiryResponseDto {

    Long inquiryId; // 문의 번호
    String inquiries; // 문의 내용
    String answers; // 답변 내용
    String inquiryTime; //문의 날짜
    String type; // 공간타입
    String companyName; // 업체 이름

    Long memberId; // 회원번호
    String memberName; // 회원 닉네임

    String isAnswer; // 답변 유무

}
