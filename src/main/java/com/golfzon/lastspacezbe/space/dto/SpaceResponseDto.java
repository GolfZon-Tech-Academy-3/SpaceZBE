package com.golfzon.lastspacezbe.space.dto;

import com.golfzon.lastspacezbe.space.entity.Space;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SpaceResponseDto {

    Long spaceId;
    String spaceName; // 사무공간 이름
    String facilities; // 편의시설
    String type; // 공간형태
    int price; //가격
    String openTime; // 오픈시간
    String closeTime; // 마감시간
    String breakOpen; // 쉬는 시작시간 (청소)
    String breakClose; // 쉬는 마감시간 (청소)
    String spaceImage; // 대표이미지 1개


    public SpaceResponseDto(Space space, String spaceImage) {
        this.spaceId = space.getSpaceId();
        this.spaceName = space.getSpaceName();
        this.facilities = space.getFacilities();
        this.type = space.getType();
        this.price = space.getPrice();
        this.openTime = space.getOpenTime();
        this.closeTime = space.getCloseTime();
        this.breakOpen = space.getBreakOpen();
        this.breakClose = space.getBreakClose();
        this.spaceImage = spaceImage;
    }

    @Override
    public String toString() {
        return "SpaceResponseDto{" +
                "spaceId=" + spaceId +
                ", spaceName='" + spaceName + '\'' +
                ", facilities='" + facilities + '\'' +
                ", type='" + type + '\'' +
                ", price=" + price +
                ", openTime='" + openTime + '\'' +
                ", closeTime='" + closeTime + '\'' +
                ", breakOpen='" + breakOpen + '\'' +
                ", breakClose='" + breakClose + '\'' +
                ", spaceImage='" + spaceImage + '\'' +
                '}';
    }
}
