package com.golfzon.lastspacezbe.inquiry.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InquiryTotalResponseDto {

    Long inquiryId; // 문의 번호
    String memberName; // 회원 닉네임
    String imagePath; // 이미지
    String inquiries; // 문의 내용
    String answers; // 답변 내용
    String inquiryTime; //문의 날짜

}
