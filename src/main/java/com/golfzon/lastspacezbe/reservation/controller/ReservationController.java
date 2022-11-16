package com.golfzon.lastspacezbe.reservation.controller;

import com.golfzon.lastspacezbe.reservation.dto.ReservationRequestDto;
import com.golfzon.lastspacezbe.reservation.service.ReservationService;
import com.golfzon.lastspacezbe.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/reservation")
public class ReservationController {

    private final ReservationService reservationService;

    // 예약하기
    @PostMapping("/post")
    public ResponseEntity<String> reserve(
            @RequestBody ReservationRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

         reservationService.reserve(requestDto);
//         log.info("result : {}",result);
         return ResponseEntity.ok()
                .body("result : 예약완료");
    }
    // 예약 취소하기
    @PutMapping("/cancel")
    public ResponseEntity<String> cancel(
            @RequestParam Long reservationId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        reservationService.cancel(reservationId);

        return ResponseEntity.ok()
                .body("result : 예약취소 완료");
    }

}

