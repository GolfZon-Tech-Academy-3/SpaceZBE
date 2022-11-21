package com.golfzon.lastspacezbe.reservation.controller;

import com.golfzon.lastspacezbe.reservation.dto.ReservationRequestDto;
import com.golfzon.lastspacezbe.reservation.service.ReservationService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/back-office")
public class ReservBackOfficeController {

    private final ReservationService reservationService;

    // 오늘 업체 예약 수
    @GetMapping("/reservation/count/{companyId}")
    public ResponseEntity<Integer> todayReserve(@PathVariable(name="companyId") Long companyId) {

        // 예약 수
        int reserveCount = reservationService.todayReserve(companyId);

        return ResponseEntity.ok()
                .body(reserveCount);
    }

    // 오늘 업체 예약 취소 수
    @GetMapping("/cancel/count/{companyId}")
    public ResponseEntity<Integer> todayCancel(@PathVariable(name="companyId") Long companyId) {

        // 예약 취소 수
        int cancelCount = reservationService.todayCancel(companyId);

        return ResponseEntity.ok()
                .body(cancelCount);
    }
}
