package com.golfzon.lastspacezbe.reservation.dto;

import com.golfzon.lastspacezbe.space.entity.Space;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// 유저 예약 조회용 responsedto
public class ReservationSpaceDto {
    Long spaceId; // 공간 아이디
    String spaceName; // 공간 이름
    String type; // 공간 형태
    int price; // 가격
    String openTime; // 오픈시간
    String closeTime; // 마감시간
    String breakOpen; // 쉬는 시작시간 (청소)
    String breakClose; // 쉬는 마감시간
    List<String> reservedTime; // 예약된 시간들(오늘 기준)
    int mileage; //유저의 사용가능한 마일리지
    String merchantUid; //상품 고유번호

    boolean bot; // 예약 봇 프로그램 여부

    public ReservationSpaceDto(Space space, List<String> reservedTimes, int mileage, String merchantUid , boolean bot) {
        this.spaceId = space.getSpaceId();
        this.spaceName = space.getSpaceName();
        this.type = space.getType();
        this.price = space.getPrice();
        this.openTime = space.getOpenTime();
        this.closeTime = space.getCloseTime();
        this.breakOpen = space.getBreakOpen();
        this.breakClose = space.getBreakClose();
        this.reservedTime = reservedTimes;
        this.mileage = mileage;
        this.merchantUid = merchantUid;
        this.bot = bot;
    }
}
