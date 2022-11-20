package com.golfzon.lastspacezbe.space.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SpaceRequestDto {

    String spaceName; // 사무공간 이름
    String facilities; // 편의시설
    int price; // 가격
    String type; // 공간형태
    String openTime; // 오픈시간
    String closeTime; // 마감시간
    String breakOpen; // 쉬는 시작시간 (청소)
    String breakClose; // 쉬는 마감시간 (청소)

    @Override
    public String toString() {
        return "SpaceRequestDto{" +
                "spaceName='" + spaceName + '\'' +
                ", facilities='" + facilities + '\'' +
                ", price=" + price +
                ", type='" + type + '\'' +
                ", openTime='" + openTime + '\'' +
                ", closeTime='" + closeTime + '\'' +
                ", breakOpen='" + breakOpen + '\'' +
                ", breakClose='" + breakClose + '\'' +
                '}';
    }
}
