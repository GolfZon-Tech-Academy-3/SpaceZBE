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
}
