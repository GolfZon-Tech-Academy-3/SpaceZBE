package com.golfzon.lastspacezbe.payment.controller;

import com.golfzon.lastspacezbe.payment.entity.RefundVO;
import com.golfzon.lastspacezbe.payment.service.PaymentService;
import com.golfzon.lastspacezbe.reservation.dto.ReservationRequestDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 결제시스템
 */
@Controller
@RequiredArgsConstructor
public class PaymentController {

	private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

	private final PaymentService paymentService;


	// 결제 상세페이지 -> 창현님과 합칠 부분
//	@RequestMapping(value = "/reservation/selectOne", method = RequestMethod.GET)
//	public String pay(Model model) {
//
//		// 주문번호 고유값 설정 위해, 난수생성 -> 이것은 공간 등록 시 생성되어 추가되어야 한다.
//		char random_alphabet = (char) ((Math.random() * 26) + 97);
//		// merchant_uid : 고유값 -> 프론트에서 받아서 결제완료 버튼 클릭 시, VO Data로 다시 넘어와야 함.
//		String merchant_uid = String.valueOf(random_alphabet) + System.currentTimeMillis();
//		logger.info("merchantid:{}", merchant_uid);
//
//		model.addAttribute("merchant_uid", merchant_uid);
//
//		return "testPayment";
//	}

	// 선결제, 결제완료 버튼 클릭 -> 현민님과 합칠 부분
	@RequestMapping(value = "/reservation/payOK", method = RequestMethod.POST)
	@ResponseBody
	public int prepayOK(@RequestBody ReservationRequestDto vo) {
		// 매개변수값으로 imp_uid, merchant_uid가 들어와야한다.
		logger.info("VO:{}", vo);

		int flag = paymentService.verifyPayInfo(vo);
		logger.info("result: {}", flag);

		return flag;
	}
	
//	// 보증금 결제, 결제완료 버튼 클릭 -> 현민님과 합칠 부분
	@RequestMapping(value = "/reservation/depositOK", method = RequestMethod.POST)
	@ResponseBody
	public int depositOK(@RequestBody ReservationRequestDto vo) {
		// 매개변수값으로 imp_uid, merchant_uid가 들어와야한다.
		logger.info("VO:{}", vo);
		
//		int flag = service.verifyPayInfo(vo);
//		logger.info("result: {}", flag);
		
		int flag = paymentService.depositOK(vo);
		logger.info("depositOK result: {}", flag);
//		
//		ReservationVO vo2 = service.reserve(vo);
//		logger.info("reserve result: {}", vo2);
		
		return flag;
	}

//	// 보증금 결제, 결제완료 버튼 클릭 -> 현민님과 합칠 부분
	@RequestMapping(value = "/reservation/reservePayOK", method = RequestMethod.POST)
	@ResponseBody
	public int reservePayOK(@RequestBody ReservationRequestDto vo) {
		// 매개변수값으로 imp_uid, merchant_uid가 들어와야한다.
		logger.info("VO:{}", vo);
		
		int flag = paymentService.reserve(vo);
		logger.info("result: {}", flag);
		
		return flag;
	}
	
//	// 결제취소 버튼 클릭 -> 현민님과 합칠 부분
//	@RequestMapping(value = "/reservation/refund", method = RequestMethod.POST, produces = "application/json; charset=utf8")
//	public String refund(@RequestBody RefundVO vo) {
//		// 매개변수값으로 custom_uid, merchant_uid가 들어와야한다.
//		vo.setMemberId(1);
//		logger.info("VO:{}", vo);
//
//		System.out.println(paymentService.refund(vo));
//
//		return "testPayment";
//	}

}
