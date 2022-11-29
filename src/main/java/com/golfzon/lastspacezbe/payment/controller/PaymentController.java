package com.golfzon.lastspacezbe.payment.controller;

import com.golfzon.lastspacezbe.member.entity.Member;
import com.golfzon.lastspacezbe.payment.service.PaymentService;
import com.golfzon.lastspacezbe.reservation.dto.ReservationRequestDto;
import com.golfzon.lastspacezbe.reservation.dto.ReservationSpaceDto;
import com.golfzon.lastspacezbe.reservation.service.ReservationService;
import com.golfzon.lastspacezbe.security.UserDetailsImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Slf4j
@Api(tags = "예약완료 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    // 예약 결제 완료 후, 콜백
    @ApiOperation(value = "예약폼 페이지", notes = "예약하기 폼 페이지입니다.")
    @PostMapping(value = "/callback")
    public ResponseEntity<String> getDetails(
            @RequestBody HashMap<String, Object> map){

        return ResponseEntity.ok()
                .body(paymentService.changeStatus(map));
    }

}

