package com.golfzon.lastspacezbe.mileage.controller;

import javax.servlet.http.HttpSession;

import com.golfzon.lastspacezbe.mileage.service.MileageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * 회원가입, 회원수정, (회원정보조회), 로그인, 로그아웃, 이메일인증(아이디 중복확인)
 */
@Controller
public class MileageController {

	private static final Logger logger = LoggerFactory.getLogger(MileageController.class);

//	@Autowired
//	MileageService service;
//
//	@Autowired
//	HttpSession session;
//
//	// 프로필조회, 마일리지조회
//	@ResponseBody
//	@RequestMapping(value = "/mypage/profile", method = RequestMethod.GET, produces = "application/json; charset=utf8")
//	public ProfileDTO profileMileage(@RequestParam(value = "memberId") String memberId) {
//
//		logger.info("profileMileage..");
//		logger.info("memberId : {}", memberId);
//
//		ProfileDTO dto = service.selectAll(Long.parseLong(memberId));
//
//		return dto;
//	}

}
