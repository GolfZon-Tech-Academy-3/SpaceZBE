package com.golfzon.lastspacezbe.reservation.service;

import com.golfzon.lastspacezbe.member.entity.Member;
import com.golfzon.lastspacezbe.mileage.entity.Mileage;
import com.golfzon.lastspacezbe.mileage.service.MileageService;
import com.golfzon.lastspacezbe.payment.service.PaymentService;
import com.golfzon.lastspacezbe.reservation.dto.ReservationRequestDto;
import com.golfzon.lastspacezbe.reservation.dto.ReservationSpaceDto;
import com.golfzon.lastspacezbe.reservation.entity.Reservation;
import com.golfzon.lastspacezbe.reservation.repository.ReservationRepository;
import com.golfzon.lastspacezbe.space.entity.Space;
import com.golfzon.lastspacezbe.space.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

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
    private final SpaceRepository spaceRepository;
    private final MileageService mileageService;
    private final PaymentService paymentService;

    // 예약하기
    public void reserve(ReservationRequestDto requestDto, Member member) {
        requestDto.setMemberId(member.getMemberId());
        log.info("requestDto : {}", requestDto);

        // companyId 조회
        Space space = spaceRepository.findById(requestDto.getSpaceId())
                .orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,"해당 spaceId는 존재하지 않습니다."));

        Reservation reservation = new Reservation(member.getMemberId(),
                requestDto.getReservationName(), requestDto.getStartDate(), requestDto.getEndDate(),
                "001", "002", requestDto.getPrice(), "000", requestDto.getImpUid(), "prepay", "postPay", requestDto.getSpaceId(), space.getCompanyId());

        int flag = 0;
        // 선결제(000) or 보증금결제(001) or 후결제(002)
        switch (requestDto.getPrepay()) {
            //선결제
            case "000":
                flag = paymentService.verifyPayInfo(requestDto);
                // 구매 아이디(merchantUid), 선결제는 후결제를 위한 구매 아이디가 필요 없으므로, 000으로 저장.
                reservation.setPrepayUid(requestDto.getPrepayUid());
                reservation.setPostpayUid("000");
                // payStatus: 결제 전 001, 결제 완료 002, 결제 취소 000, 보증금 결제완료 003, 보증금 결제취소 004
                reservation.setPayStatus("002");
                reservation.setPrice(requestDto.getPrice());
                log.info("reservation:{}",reservation);
                // 마일리지 사용 및 적립
                if (flag == 1) {
                    // 마일리지 사용
                    if (requestDto.getMileage() > 0) {
                        mileageService.updateMileage(requestDto);
                        log.info("마일리지 사용 완료");
                    }
                    // 마일리지 적립
                    mileageService.insertMileage(requestDto);
                    log.info("마일리지 적립 완료");
                }
                break;
            //보증금결제
            case "001":
                flag = paymentService.depositOK(requestDto);
                reservation.setPrepayUid(requestDto.getPrepayUid());
                reservation.setPostpayUid(requestDto.getPostpayUid());
                //보증금 결제완료 003
                reservation.setPayStatus("003");
                reservation.setPrice(requestDto.getPrice()-requestDto.getMileage());
                // 마일리지 사용
                if (requestDto.getMileage() > 0) {
                    mileageService.updateMileage(requestDto);
                    log.info("마일리지 사용 완료");
                }
                break;
            //후결제
            case "002":
                flag = paymentService.reserve(requestDto);
                reservation.setPrepayUid(requestDto.getPrepayUid());
                reservation.setPostpayUid(requestDto.getPostpayUid());
                // 결제 전 001
                reservation.setPayStatus("001");
                reservation.setPrice(requestDto.getPrice()-requestDto.getMileage());
                // 마일리지 사용
                if (requestDto.getMileage() > 0) {
                    mileageService.updateMileage(requestDto);
                    log.info("마일리지 사용 완료");
                }
                break;
        }
        // 저장
        if(flag==1){
            reservationRepository.save(reservation);
        }
    }

    // 오피스 예약 취소하기
    public void officeCancel(Long reservationId) {

        Reservation reservation = reservationRepository.findAllByReservationId(reservationId);
        reservation.setStatus("002"); // 예약상태를 예약 취소로 바꾸기
        reservationRepository.save(reservation); // 002 예약상태 저장

    }

    // 데스크/회의실 예약 취소하기
    public void deskCancel(Long reservationId) {

        Reservation reservation = reservationRepository.findAllByReservationId(reservationId);
        reservation.setStatus("002"); // 예약상태를 예약 취소로 바꾸기
        // 예약한 시간
        LocalDateTime reserveTime = reservationRepository.findByReservationId(reservationId).getReserveTime();
        // 현재 시간
        LocalDateTime currentDateTime = LocalDateTime.now();
        // 예약한 시간 현재시간 차이
        Duration duration = Duration.between(reserveTime, currentDateTime);

        log.info("reserveTime : {}", reserveTime);
        log.info("currentTime : {}", currentDateTime);
        log.info("Time check : {}초", duration.getSeconds());

        // 예약한지 1시간 이내
        if (duration.getSeconds() <= 3600) {

        }
        // 예약한지 1시간 이후
        else if (duration.getSeconds() > 3600) {

        }

        reservationRepository.save(reservation); // 002 예약상태 저장

    }

    // 핸드폰 인증번호 보내기
    public void certifiedPhoneNumber(String userPhoneNumber, int randomNumber) {
        Message coolsms = new Message(api_key, api_secret);

        // 4 params(to, from, type, text) are mandatory. must be filled
        HashMap<String, String> params = new HashMap<>();
        params.put("to", userPhoneNumber);    // 수신전화번호
        params.put("from", sender);    // 발신전화번호. 테스트시에는 발신,수신 둘다 본인 번호로 하면 됨
        params.put("type", "SMS");
        params.put("text", "[TEST] 인증번호는" + "[" + randomNumber + "]" + "입니다."); // 문자 내용 입력
        params.put("app_version", "spacez app 1.0"); // application name and version

        try {
            JSONObject obj = coolsms.send(params);
            System.out.println(obj.toString());
        } catch (CoolsmsException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCode());
        }
    }

    // 공간 예약 상세페이지
    public ReservationSpaceDto getDetails(Long spaceId, Member member) {
        log.info("spaceId:{}", spaceId);

        // 1. 공간 정보 찾기
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 spaceId는 존재하지 않습니다."));

        // 2. 예약된 시간 찾기
        List<String> reservedTimes = new ArrayList<>();
        List<Reservation> reservations = reservationRepository.findReservedTime(spaceId);
        // 포맷변경 (년월일 시분)
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();
        // 예약된 시간 1시간 간격으로 더하기
        for (Reservation reservation : reservations) {
            try {
                Date startDate = formatter.parse(reservation.getStartDate());
                startCal.setTime(startDate);
                String time;
                Date endDate = formatter.parse(reservation.getEndDate());
                endCal.setTime(endDate);
                // 이용시작 시간부터 이용종료 시간 전까지 시간 더하기
                while (startCal.before(endCal)) {
                    time = formatter.format(startCal.getTime());
                    reservedTimes.add(time);
                    startCal.add(Calendar.HOUR, +1);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        //오름차순으로 정렬
        Collections.sort(reservedTimes);

        // 3. 총 마일리지 찾기
        int totalMileage = mileageService.getTotalScore(member.getMemberId());

        return new ReservationSpaceDto(space, reservedTimes, totalMileage);
    }
}
