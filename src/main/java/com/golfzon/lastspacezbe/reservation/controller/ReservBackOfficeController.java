package com.golfzon.lastspacezbe.reservation.controller;

import com.golfzon.lastspacezbe.reservation.dto.ReservationRequestDto;
import com.golfzon.lastspacezbe.reservation.dto.ReservationResponseDto;
import com.golfzon.lastspacezbe.reservation.service.ReservBackOfficeService;
import com.golfzon.lastspacezbe.reservation.service.ReservationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@Api(tags = "백오피스 예약 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/back-office")
public class ReservBackOfficeController {

    private final ReservationService reservationService;
    private final ReservBackOfficeService reservBackOfficeService;

    // 오늘 업체 예약 수
    @ApiOperation(value = "오늘 업체 예약 수", notes = "오늘 업체 예약 수 조회 기능입니다.")
    @GetMapping("/reservation/count/{companyId}")
    public ResponseEntity<Integer> todayReserve(@PathVariable(name="companyId") Long companyId) {

        // 예약 수
        int reserveCount = reservBackOfficeService.todayReserve(companyId);

        return ResponseEntity.ok()
                .body(reserveCount);
    }

    // 오늘 업체 예약 취소 수
    @ApiOperation(value = "오늘 업체 취소 수", notes = "오늘 업체 예약 취소 수 조회 기능입니다.")
    @GetMapping("/cancel/count/{companyId}")
    public ResponseEntity<Integer> todayCancel(@PathVariable(name="companyId") Long companyId) {

        // 예약 취소 수
        int cancelCount = reservBackOfficeService.todayCancel(companyId);

        return ResponseEntity.ok()
                .body(cancelCount);
    }

    // 예약 현황
    @ApiOperation(value = "예약 현황", notes = "예약 현황 조회 기능입니다.")
    @GetMapping("/reservation/total/{companyId}")
    public ResponseEntity<List<ReservationResponseDto>> totalReservation(@PathVariable(name="companyId") Long companyId) {

        List<ReservationResponseDto> reservationResponseDtos = reservBackOfficeService.totalReserveSelectAll(companyId);

        return ResponseEntity.ok()
                .body(reservationResponseDtos);
    }


    // 기간별 대여 금액 검색
    @ApiOperation(value = "기간별 대여 금액, 예약 현황", notes = "기간별 대여 금액, 예약 현황 조회 기능입니다.")
    @PostMapping("/total-incomes/{companyId}")
    public ResponseEntity<Map<String, Object>> totalIncome(@PathVariable(name="companyId") Long companyId, @RequestBody ReservationRequestDto dto) {

        return ResponseEntity.ok()
                .body(reservBackOfficeService.totalIncomes(companyId, dto));
    }

    // 예약 이용완료 처리
    @ApiOperation(value = "예약 이용완료", notes = "예약 이용완료 처리입니다.")
    @PutMapping("/reservation/done/{reservationId}")
    public ResponseEntity<String> doneReservation(
            @PathVariable(name = "reservationId") Long reservationId) {

        reservationService.doneReservation(reservationId);

        return ResponseEntity.ok()
                .body("result : 이용완료 처리 완료");
    }
}
