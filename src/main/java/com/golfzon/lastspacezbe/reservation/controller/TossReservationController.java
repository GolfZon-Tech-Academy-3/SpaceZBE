package com.golfzon.lastspacezbe.reservation.controller;

import com.golfzon.lastspacezbe.payment.service.TossPaymentService;
import com.golfzon.lastspacezbe.reservation.dto.ReservationRequestDto;
import com.golfzon.lastspacezbe.reservation.service.ReservationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = "토스예약 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/reservation")
public class TossReservationController {

    private final ReservationService reservationService;
    private final TossPaymentService tossReservationService;


    // 토스예약하기
    @ApiOperation(value = "토스예약", notes = "토스예약 처리입니다.")
    @PostMapping(value = "/toss-post")
    public ResponseEntity<String> tossReserve(
            @RequestBody ReservationRequestDto requestDto){

         tossReservationService.tossReserve(requestDto);
         return ResponseEntity.ok()
                .body("result : 예약완료");
    }
}

