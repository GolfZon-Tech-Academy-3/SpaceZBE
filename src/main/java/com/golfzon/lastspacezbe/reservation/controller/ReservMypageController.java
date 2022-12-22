package com.golfzon.lastspacezbe.reservation.controller;

import com.golfzon.lastspacezbe.reservation.dto.ReservationResponseDto;
import com.golfzon.lastspacezbe.reservation.service.ReservMypageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Api(tags = "마이페이지 예약 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class ReservMypageController {

    private final ReservMypageService reservMypageService;
    // 예약 이력
    @ApiOperation(value = "예약 이력", notes = "예약 이력 조회 기능입니다.")
    @GetMapping("/reservation/total/{memberId}")
    public ResponseEntity<List<ReservationResponseDto>> totalReservation(@PathVariable(name="memberId") Long memberId) {

        List<ReservationResponseDto> reservationResponseDtos = reservMypageService.totalReserveSelectAll(memberId);

        return ResponseEntity.ok()
                .body(reservationResponseDtos);
    }

    // 예약 현황
    @ApiOperation(value = "예약 현황", notes = "예약 현황 조회 기능입니다.")
    @GetMapping("/reservation/proceeding/{memberId}")
    public ResponseEntity<List<ReservationResponseDto>> proceedReservation(@PathVariable(name="memberId") Long memberId) {

        List<ReservationResponseDto> reservationResponseDtos = reservMypageService.proceedReserveSelectAll(memberId);

        return ResponseEntity.ok()
                .body(reservationResponseDtos);
    }
}
