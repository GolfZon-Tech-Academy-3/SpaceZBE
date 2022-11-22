package com.golfzon.lastspacezbe.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// 예약 하기  requestdto
public class ReservationRequestDto {
    String reservationName; // 예약자 이름
    String startDate; // 이용 시간 날짜
    String endDate; // 이용 마감 날짜
    int price; // 가격
    String prepay; // 선결제 or 보증금결제 or 후결제 000 001 002
    Long spaceId; //사무공간 번호
    int mileage; //사용할 마일리지

    String impUid; //import 결제 고유아이디
    String prepayUid; // 선결제, 보증금으로 결제한 서버가 제공한 merchant_uid(구매번호)
    String postpayUid; // 후결제 예약한 서버가 제공한 merchant_uid(구매번호)

    Long memberId;

    @Override
    public String toString() {
        return "ReservationRequestDto{" +
                "reservationName='" + reservationName + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", price=" + price +
                ", prepay='" + prepay + '\'' +
                ", spaceId=" + spaceId +
                ", mileage=" + mileage +
                ", impUid='" + impUid + '\'' +
                ", prepayUid='" + prepayUid + '\'' +
                ", postpayUid='" + postpayUid + '\'' +
                ", memberId=" + memberId +
                '}';
    }
}
