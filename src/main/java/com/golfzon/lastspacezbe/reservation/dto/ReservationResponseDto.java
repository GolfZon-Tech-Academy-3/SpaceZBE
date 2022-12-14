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
    String type; // 공간타입
    String spaceName; // 공간이름
    String payStatus; // 결제 상태
    String status; // 예약상태
    String reserveTime; // 예약했을 때 시간
    String imageName; // 공간이미지
    String location; // 장소 주소
    String details; // 상세주소

    ReviewDto review; // 작성한 리뷰, 없을시 null

    Long spaceId; // 공간번호
    Long companyId; // 업체번호
}
