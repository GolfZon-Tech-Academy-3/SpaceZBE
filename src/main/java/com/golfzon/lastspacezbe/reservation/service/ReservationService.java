package com.golfzon.lastspacezbe.reservation.service;

import com.golfzon.lastspacezbe.member.entity.Member;
import com.golfzon.lastspacezbe.mileage.service.MileageService;
import com.golfzon.lastspacezbe.payment.dto.RefundDto;
import com.golfzon.lastspacezbe.payment.service.PaymentService;
import com.golfzon.lastspacezbe.redis.service.RedisService;
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

import static com.golfzon.lastspacezbe.payment.service.TossPaymentService.tossRefund;

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
    private final RedisService redisService;

    // 예약하기
    public void reserve(ReservationRequestDto requestDto, Member member) {
        requestDto.setMemberId(member.getMemberId());
        log.info("requestDto : {}", requestDto);

        // companyId 조회
        Space space = spaceRepository.findById(requestDto.getSpaceId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 spaceId는 존재하지 않습니다."));

        // 예약된 시간 있는지 확인
        int isReserved = checkReservedTimes(requestDto);
        if(isReserved == 0){
            // 일치하지 않을 시, 오류로 계산된 금액 환불처리
            if(requestDto.getPrepay().equals("002")){
                //오피스 후불
                paymentService.refund(new RefundDto(requestDto.getPostpayUid(), "후결제 예약취소", 0, member.getMemberId()));
            } else {
                //선불, 보증금
                int price = Integer.parseInt( paymentService.getPaymentInfo(requestDto));
                paymentService.refund(new RefundDto(requestDto.getPrepayUid(), "이미 결제된 금액 환불", price, member.getMemberId()));
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 예약이 이루어진 건입니다.");
        }

        Reservation reservation = new Reservation(member.getMemberId(),
                requestDto.getReservationName(), requestDto.getStartDate(), requestDto.getEndDate(),
                "001", "002", requestDto.getPrice(), requestDto.getPrepay(),
                requestDto.getImpUid(), "prepay", "postPay", requestDto.getMileage(),
                requestDto.getSpaceId(), space.getCompanyId(), false, null);

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
                reservation.setPrice(requestDto.getPrice()); //전체가격에서 마일리지를 제한 실제 결제된 총 가격
                log.info("reservation:{}", reservation);
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
                int originalPrice = requestDto.getPrice(); //전체가격에서 마일리지를 제한 실제 결제되고, 결제될 총 가격
                flag = paymentService.depositOK(requestDto);
                reservation.setPrice(originalPrice);
                reservation.setPrepayUid(requestDto.getPrepayUid());
                reservation.setPostpayUid(requestDto.getPostpayUid());
                //보증금 결제완료 003
                reservation.setPayStatus("003");
                // 저장
                if (flag == 1) {
                    // 마일리지 사용
                    if (requestDto.getMileage() > 0) {
                        mileageService.updateMileage(requestDto);
                        log.info("마일리지 사용 완료");
                    }
                }
                break;
            //후결제
            case "002":
                paymentService.verifyReserveDate(requestDto);
                flag = paymentService.reserve(requestDto);
                reservation.setPrepayUid(requestDto.getPrepayUid());
                reservation.setPostpayUid(requestDto.getPostpayUid());
                // 결제 전 001
                reservation.setPayStatus("001");
                reservation.setPrice(requestDto.getPrice()); //전체가격에서 마일리지를 제한 실제 결제될 총 가격
                // 저장
                if (flag == 1) {
                    // 마일리지 사용
                    if (requestDto.getMileage() > 0) {
                        mileageService.updateMileage(requestDto);
                        log.info("마일리지 사용 완료");
                    }
                }
                break;
        }
        // 저장
        if (flag == 1) {
            // 예약 저장
            reservationRepository.save(reservation);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "결제가 제대로 이루어지지 않았습니다.");
        }
    }

    // 오피스 예약 취소하기
    public void officeCancel(Long reservationId) {
        log.info("reservationId:{}", reservationId);
        Reservation reservation = reservationRepository.findAllByReservationId(reservationId);
        // 예약 상태 확인
        if (!reservation.getStatus().equals("001")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "취소가능한 상태가 아닙니다.");
        } else {
            // 예약된 결제 취소하기
            String postpayUid = reservation.getPostpayUid();
            if(!reservation.getToss()){
                paymentService.refund(new RefundDto(postpayUid, "후결제 예약취소", 0, reservation.getMemberId()));
            }
            reservation.setStatus("002"); // 예약상태를 예약 취소로 바꾸기
            reservationRepository.save(reservation); // 저장
            // 사용한 마일리지 환급
            mileageService.refundMileage(reservation);
        }
    }

    // 데스크/회의실 예약 취소하기
    public void deskCancel(Long reservationId) {

        Reservation reservation = reservationRepository.findAllByReservationId(reservationId);
        // 예약한 시간
        LocalDateTime reserveTime = reservationRepository.findByReservationId(reservationId).getReserveTime();
        // 현재 시간
        LocalDateTime currentDateTime = LocalDateTime.now();
        // 예약한 시간 현재시간 차이
        Duration duration = Duration.between(reserveTime, currentDateTime);

        log.info("reserveTime : {}", reserveTime);
        log.info("currentTime : {}", currentDateTime);
        log.info("Time check : {}초", duration.getSeconds());

        // 예약 상태 확인
        if (!reservation.getStatus().equals("001")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "취소가능한 상태가 아닙니다.");
        } else {

            // 선결제인 경우, 시간에 관계없이 전체 환불
            if (reservation.getPrepay().equals("000")) {
                String prepayUid = reservation.getPrepayUid();
                if(reservation.getToss()){
                    tossRefund(new RefundDto(reservation.getImpUid(), "선결제 결제취소", reservation.getPrice(), reservation.getMemberId()));
                } else {
                    paymentService.refund(new RefundDto(prepayUid, "선결제 결제취소", reservation.getPrice(), reservation.getMemberId()));
                }
                reservation.setPayStatus("000");  //000 결제 취소
                // 마일리지 취소
                mileageService.cancelMileage(reservation);
                log.info("마일리지 취소, 회수 완료");
                // 사용한 마일리지 환급
                mileageService.refundMileage(reservation);

                // 후결제인 경우, 1시간이내만 보증금 전부 환불
            } else {
                // 예약한지 1시간 이내
                if (duration.getSeconds() <= 3600) {
                    log.info("{} : 예약한지 1시간이 지나지 않았습니다. (전체 환불 가능)", duration);
                    String prepayUid = reservation.getPrepayUid();
                    if(reservation.getToss()){
                        tossRefund(new RefundDto(reservation.getImpUid(), "보증금결제 취소", (int) ((reservation.getPrice() + reservation.getMileage()) * 0.2), reservation.getMemberId()));
                    } else {
                        paymentService.refund(
                                new RefundDto(prepayUid, "보증금결제 취소", (int) ((reservation.getPrice() + reservation.getMileage()) * 0.2), reservation.getMemberId()));
                        String postpayUid = reservation.getPostpayUid();
                        paymentService.refund(new RefundDto(postpayUid, "후결제 예약취소", 0, reservation.getMemberId()));
                    }
                    reservation.setPayStatus("004"); //004 보증금 결제취소
                    // 사용한 마일리지 환급
                    mileageService.refundMileage(reservation);
                    log.info("마일리지 환급 완료");
                }
                // 예약한지 1시간 이후
                else if (duration.getSeconds() > 3600) {
                    log.info("{} : 예약한지 1시간이 지났습니다. (선결제만 전체 환불 가능)", duration);
                    String postpayUid = reservation.getPostpayUid();
                    if(reservation.getToss()){
                        tossRefund(new RefundDto(reservation.getImpUid(), "선결제 결제취소", reservation.getPrice(), reservation.getMemberId()));
                    } else {
                        paymentService.refund(new RefundDto(postpayUid, "후결제 예약취소", 0, reservation.getMemberId()));
                    }
                    // 사용한 마일리지 환급
                    mileageService.refundMileage(reservation);
                    log.info("마일리지 환급 완료");
                }
            }
            reservation.setStatus("002"); // 예약상태를 예약 취소로 바꾸기
            reservationRepository.save(reservation); // 002 예약상태 저장
        }
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
    public ReservationSpaceDto getDetails(Long spaceId, Member member, String userAgent) {
        log.info("spaceId:{}", spaceId);
        log.info("userAgent:{}", userAgent);

        boolean bot = false;
        // 봇 프로그램 작동중
        if(userAgent.contains("Headless")){
            bot = true;
        }

        // 1. 공간 정보 찾기
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 spaceId는 존재하지 않습니다."));
        // 2. 예약된 시간 찾기
        List<String> reservedTimes = getReservedTimes(space);
        // 3. 총 마일리지 찾기
        int totalMileage = mileageService.getTotalScore(member.getMemberId());

        return new ReservationSpaceDto(space, reservedTimes, totalMileage, paymentService.getMerchantUid(),bot);
    }

    // 예약된 시간 찾기
    public List<String> getReservedTimes(Space space) {
        log.info("getReservedTimes");
        List<String> reservedTimes = new ArrayList<>();
        List<Reservation> reservations = reservationRepository.findReservedTime(space.getSpaceId());
        log.info("reservation.size:{}", reservations.size());
        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();
        // 포맷변경 (년월일 시분)
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if (space.getType().equals("오피스")) {
            // 예약된 날짜 하루 간격으로 더하기
            for (Reservation reservation : reservations) {
                log.info(reservation.getStartDate());
                log.info(reservation.getEndDate());
                try {
                    Date startDate = formatter.parse(reservation.getStartDate());
                    startCal.setTime(startDate);
                    String time;
                    Date endDate = formatter.parse(reservation.getEndDate());
                    endCal.setTime(endDate);
                    // 이용시작 시간부터 이용종료 시간 전까지 시간 더하기
                    while (startCal.before(endCal)) {
                        time = formatter.format(startCal.getTime());
                        log.info("time:{}",time);
                        reservedTimes.add(time.split(" ")[0]);
                        startCal.add(Calendar.DATE, +1);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } else {
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
        }
        //오름차순으로 정렬
        Collections.sort(reservedTimes);
        log.info("RESERVEDTIME:{}",reservedTimes);
        return reservedTimes;
    }

    // 예약하는 시간이 이미 예약이 되어있는지 확인
    public int checkReservedTimes(ReservationRequestDto requestDto) {
        log.info("requestDto : {}", requestDto);
        int flag = 1;
        // companyId 조회
        Space space = spaceRepository.findById(requestDto.getSpaceId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 spaceId는 존재하지 않습니다."));
        List<String> reservedTimes = getReservedTimes(space);
        log.info("size가 0인지?:{}",reservedTimes.size()==0);
        if(reservedTimes.size()==0) return flag;
        List<String> checktimes = new ArrayList<>();
        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();
        // 포맷변경 (년월일 시분)
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if (space.getType().equals("오피스")) {
            // 예약된 날짜 하루 간격으로 더하기
            try {
                Date startDate = formatter.parse(requestDto.getStartDate());
                startCal.setTime(startDate);
                String time;
                Date endDate = formatter.parse(requestDto.getEndDate());
                endCal.setTime(endDate);
                // 이용시작 시간부터 이용종료 시간 전까지 시간 더하기
                while (startCal.before(endCal)) {
                    time = formatter.format(startCal.getTime());
                    if (reservedTimes.contains(time.split(" ")[0])) {
                        flag =0;
                    }
                    checktimes.add(time.split(" ")[0]);
                    startCal.add(Calendar.DATE, +1);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            // 예약된 시간 1시간 간격으로 더하기
            try {
                Date startDate = formatter.parse(requestDto.getStartDate());
                startCal.setTime(startDate);
                String time;
                Date endDate = formatter.parse(requestDto.getEndDate());
                endCal.setTime(endDate);
                // 이용시작 시간부터 이용종료 시간 전까지 시간 더하기
                while (startCal.before(endCal)) {
                    time = formatter.format(startCal.getTime());
                    if (reservedTimes.contains(time)) {
                        flag =0;
                    }
                    checktimes.add(time);
                    startCal.add(Calendar.HOUR, +1);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    //이용완료로 변경
    public void doneReservation(Long reservationId) {

        log.info("reservationId:{}", reservationId);
        Reservation reservation = reservationRepository.findAllByReservationId(reservationId);
        // 예약 상태 확인
        if (!reservation.getStatus().equals("001")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이용완료 가능한 상태가 아닙니다.");
        } else {
            reservation.setStatus("004"); // 예약상태를 이용 완료로 바꾸기
            reservationRepository.save(reservation); // 004 이용 완료상태 저장
        }
    }

    //예약된 시간 찾기
    public List<String> getTimes(Long spaceId) {
        // 1. 공간 정보 찾기
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 spaceId는 존재하지 않습니다."));
        // 2. 예약된 시간 찾기
        return getReservedTimes(space);
    }

    //결제 전, 중복 예약 확인
    public void checkDoubleReservation(ReservationRequestDto requestDto) {
        log.info("request:{}",requestDto);
        //이미 예약완료인지 확인
        int flag = checkReservedTimes(requestDto);
        if(flag==0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 예약이 완료되었습니다.");
        //현재 예약 중인지 확인
        //1) 예약할 날짜 확인
        List<String> checktimes = new ArrayList<>();
        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();
        // 포맷변경 (년월일 시분)
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if (requestDto.getPrepay().equals("002")) {
            // 예약된 날짜 하루 간격으로 더하기
            try {
                Date startDate = formatter.parse(requestDto.getStartDate());
                startCal.setTime(startDate);
                String time;
                Date endDate = formatter.parse(requestDto.getEndDate());
                endCal.setTime(endDate);
                // 이용시작 시간부터 이용종료 시간 전까지 시간 더하기
                while (startCal.before(endCal)) {
                    time = formatter.format(startCal.getTime());
                    checktimes.add(time.split(" ")[0]);
                    startCal.add(Calendar.DATE, +1);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            // 예약된 시간 1시간 간격으로 더하기
            try {
                Date startDate = formatter.parse(requestDto.getStartDate());
                startCal.setTime(startDate);
                String time;
                Date endDate = formatter.parse(requestDto.getEndDate());
                endCal.setTime(endDate);
                // 이용시작 시간부터 이용종료 시간 전까지 시간 더하기
                while (startCal.before(endCal)) {
                    time = formatter.format(startCal.getTime());
                    checktimes.add(time);
                    startCal.add(Calendar.HOUR, +1);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        log.info("checkTimes:{}",checktimes);

        //2) Redis에 예약중인 날짜 저장
        redisService.checkReservation(requestDto, checktimes);
    }
}
