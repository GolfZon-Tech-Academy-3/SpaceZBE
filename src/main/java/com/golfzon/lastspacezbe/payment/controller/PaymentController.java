package com.golfzon.lastspacezbe.payment.controller;

import com.golfzon.lastspacezbe.payment.service.PaymentService;
import com.golfzon.lastspacezbe.payment.service.TossPaymentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Slf4j
@Api(tags = "예약완료 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final TossPaymentService tossPaymentService;

    // 예약 결제 완료 후, 콜백
    @ApiOperation(value = "예약결제 완료", notes = "예약결제 완료 후, 상태변경 기능입니다.")
    @PostMapping(value = "/callback")
    public ResponseEntity<String> getDetails(
            @RequestBody HashMap<String, Object> map){

        return ResponseEntity.ok()
                .body(paymentService.changeStatus(map));
    }

    // AccessToken 발급
    @ApiOperation(value = "예약하기 폼페이지", notes = "예약하기 폼 페이지")
    @GetMapping(value = "/callback-auth")
    public ResponseEntity<String> getTossAccessToken(@RequestParam("code") String code, @RequestParam("customerKey") String customerKey){

        log.info("code:{}",code);
        log.info("customerKey:{}",customerKey);
        return ResponseEntity.ok()
                .body(tossPaymentService.getTossAccessToken(code, customerKey));
    }

}

