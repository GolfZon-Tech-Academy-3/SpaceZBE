package com.golfzon.lastspacezbe.inquiry.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InquiryRequestDto {

    String inquiries; // 문의 내용
    String answers; // 답변 내용
}
