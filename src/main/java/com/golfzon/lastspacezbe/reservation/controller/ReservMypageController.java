package com.golfzon.lastspacezbe.reservation.controller;

import com.golfzon.lastspacezbe.reservation.dto.ReservationResponseDto;
import com.golfzon.lastspacezbe.reservation.service.ReservBackOfficeService;
import com.golfzon.lastspacezbe.reservation.service.ReservMypageService;
import com.golfzon.lastspacezbe.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class ReservMypageController {

    private final ReservMypageService reservMypageService;
    // 예약 이력
    @GetMapping("/reservation/total/{memberId}")
    public ResponseEntity<List<ReservationResponseDto>> totalReservation(@PathVariable(name="memberId") Long memberId) {

        List<ReservationResponseDto> reservationResponseDtos = reservMypageService.totalReserveSelectAll(memberId);

        return ResponseEntity.ok()
                .body(reservationResponseDtos);
    }

    // 예약 현황
    @GetMapping("/reservation/proceeding/{memberId}")
    public ResponseEntity<List<ReservationResponseDto>> proceedReservation(@PathVariable(name="memberId") Long memberId) {

        List<ReservationResponseDto> reservationResponseDtos = reservMypageService.proceedReserveSelectAll(memberId);

        return ResponseEntity.ok()
                .body(reservationResponseDtos);
    }
}
