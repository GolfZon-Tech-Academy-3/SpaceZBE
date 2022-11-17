package com.golfzon.lastspacezbe.reservation.controller;

import com.golfzon.lastspacezbe.reservation.dto.ReservationRequestDto;
import com.golfzon.lastspacezbe.reservation.service.ReservationService;
import com.golfzon.lastspacezbe.security.UserDetailsImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(tags = "예약 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/reservation")
public class ReservationController {

    private final ReservationService reservationService;

    // 예약하기
    @ApiOperation(value = "예약", notes = "예약 처리입니다.")
    @PostMapping(value = "/post",produces="text/plain;charset=UTF-8")
    public ResponseEntity<String> reserve(
            @RequestBody ReservationRequestDto requestDto) {

         reservationService.reserve(requestDto);
//         log.info("result : {}",result);
         return ResponseEntity.ok()
                .body("result : 예약완료");
    }
    // 오피스 예약 취소하기
    @ApiOperation(value = "예약취소", notes = "예약취소 처리입니다.")
    @PutMapping("/office-cancel")
    public ResponseEntity<String> officeCancel(
            @RequestParam Long reservationId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        reservationService.officeCancel(reservationId);

        return ResponseEntity.ok()
                .body("result : 예약취소 완료");
    }

    // 데스크 회의실 예약 취소하기
    @PutMapping("/desk-cancel")
    public ResponseEntity<String> deskCancel(
            @RequestParam Long reservationId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        reservationService.deskCancel(reservationId);

        return ResponseEntity.ok()
                .body("result : 예약취소 완료");
    }

}

