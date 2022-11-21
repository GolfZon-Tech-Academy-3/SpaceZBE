package com.golfzon.lastspacezbe.reservation.service;

import com.golfzon.lastspacezbe.member.entity.Member;
import com.golfzon.lastspacezbe.reservation.dto.ReservationRequestDto;
import com.golfzon.lastspacezbe.reservation.entity.Reservation;
import com.golfzon.lastspacezbe.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReservationService {

    @Value("${service.message.apiKey}")
    private String api_key;

    @Value("${service.message.secretKey}")
    private String api_secret;

    @Value("${service.message.sender}")
    private String sender;

    private final ReservationRepository reservationRepository;

    // 예약하기
    public void reserve(ReservationRequestDto requestDto, Member member){
        log.info("member : {}",member);
        // 선결제
        Reservation reservation = new Reservation(member.getMemberId(),
                requestDto.getReservationName(),requestDto.getStartDate(),requestDto.getEndDate(),
        "001", "002", requestDto.getPrice(),"000","imp","prepay","postPay");
        // 저장
        reservationRepository.save(reservation);
    }

    // 오피스 예약 취소하기
    public void officeCancel(Long reservationId){

        Reservation reservation = reservationRepository.findAllByReservationId(reservationId);
        reservation.setStatus("002"); // 예약상태를 예약 취소로 바꾸기
        reservationRepository.save(reservation); // 002 예약상태 저장

    }
    // 데스크/회의실 예약 취소하기
    public void deskCancel(Long reservationId){

        Reservation reservation = reservationRepository.findAllByReservationId(reservationId);
        reservation.setStatus("002"); // 예약상태를 예약 취소로 바꾸기
        // 예약한 시간
        LocalDateTime reserveTime = reservationRepository.findByReservationId(reservationId).getReserveTime();
        // 현재 시간
        LocalDateTime currentDateTime = LocalDateTime.now();
        // 예약한 시간 현재시간 차이
        Duration duration = Duration.between(reserveTime, currentDateTime);

        log.info("reserveTime : {}",reserveTime);
        log.info("currentTime : {}",currentDateTime);
        log.info("Time check : {}초",duration.getSeconds());

        // 예약한지 1시간 이내
        if(duration.getSeconds() <= 3600){

        }
        // 예약한지 1시간 이후
        else if(duration.getSeconds() > 3600){

        }

        reservationRepository.save(reservation); // 002 예약상태 저장

    }

    public void certifiedPhoneNumber(String userPhoneNumber, int randomNumber) {
        Message coolsms = new Message(api_key, api_secret);

        // 4 params(to, from, type, text) are mandatory. must be filled
        HashMap<String, String> params = new HashMap<>();
        params.put("to", userPhoneNumber);    // 수신전화번호
        params.put("from", sender);    // 발신전화번호. 테스트시에는 발신,수신 둘다 본인 번호로 하면 됨
        params.put("type", "SMS");
        params.put("text", "[TEST] 인증번호는" + "["+randomNumber+"]" + "입니다."); // 문자 내용 입력
        params.put("app_version", "spacez app 1.0"); // application name and version

        try {
            JSONObject obj = coolsms.send(params);
            System.out.println(obj.toString());
        } catch (CoolsmsException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCode());
        }
    }

}
