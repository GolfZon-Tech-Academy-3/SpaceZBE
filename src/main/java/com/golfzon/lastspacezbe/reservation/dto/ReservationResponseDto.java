package com.golfzon.lastspacezbe.reservation.dto;

import com.golfzon.lastspacezbe.review.dto.ReviewDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// 예약 조회용 responsedto
public class ReservationResponseDto {
    Long reservationId; // 예약 번호
    String reservationName; // 예약자 이름
    String startDate; // 이용 시간 날짜
    String endDate; // 이용 마감 날짜
    int price; // 가격
    String payStatus; // 결제 상태
    String status; // 예약상태
    String reserveTime; // 예약했을 때 시간
    ReviewDto review; // 작성한 리뷰, 없을시 null
}
