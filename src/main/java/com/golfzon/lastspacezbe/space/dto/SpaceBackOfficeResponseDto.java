package com.golfzon.lastspacezbe.space.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SpaceBackOfficeResponseDto {

    Long spaceId; // 공간번호
    String spaceName; // 사무공간 이름
    String type; // 공간형태
    String openTime; // 오픈시간
    String closeTime; // 마감시간
    String breakOpen; // 청소 시작 시간
    String breakClose; // 청소 마감 시간
    String facilities; // 편의시설
    int price; // 가격

}
