package com.golfzon.lastspacezbe.payment.controller;

import com.golfzon.lastspacezbe.payment.service.PaymentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Api(tags = "예약테스트")
@Controller
@RequiredArgsConstructor
public class PaymentTestController {

    private final PaymentService paymentService;

    // 예약 결제 완료 후, 콜백
    @ApiOperation(value = "예약하기 폼페이지", notes = "예약하기 폼 페이지")
    @GetMapping(value = "/member/paytest")
    public String payTest(){

        return "test";
    }
}

