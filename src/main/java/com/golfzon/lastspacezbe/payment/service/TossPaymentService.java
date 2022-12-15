package com.golfzon.lastspacezbe.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.golfzon.lastspacezbe.member.entity.Member;
import com.golfzon.lastspacezbe.mileage.service.MileageService;
import com.golfzon.lastspacezbe.payment.dto.RefundDto;
import com.golfzon.lastspacezbe.reservation.dto.ReservationRequestDto;
import com.golfzon.lastspacezbe.reservation.entity.Reservation;
import com.golfzon.lastspacezbe.reservation.repository.ReservationRepository;
import com.golfzon.lastspacezbe.reservation.service.ReservationService;
import com.golfzon.lastspacezbe.space.entity.Space;
import com.golfzon.lastspacezbe.space.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class TossPaymentService {

    private static String toss_secret;
    private final ReservationService reservationService;
    private final PaymentService paymentService;
    private final SpaceRepository spaceRepository;
    private final MileageService mileageService;
    private final ReservationRepository reservationRepository;

    @Value("${toss.secret_key}")
    public void setKey(String value){
        toss_secret = value;
    }

    public String getTossAccessToken(String code, String customerKey) {
        RestTemplate rt = new RestTemplate();
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        log.info("Basic " + Base64.getEncoder().encodeToString(toss_secret.getBytes(StandardCharsets.UTF_8)));
        headers.add("Authorization", "Basic " + Base64.getEncoder().encodeToString(toss_secret.getBytes(StandardCharsets.UTF_8)));
        // Request body 생성
        JSONObject body = new JSONObject();
        body.put("grantType", "AuthorizationCode");
        body.put("code", code);
        body.put("customerKey", customerKey);

        // HTTP 요청 보내기
        HttpEntity<JSONObject> entity = new HttpEntity<>(body, headers);
        ResponseEntity<JSONObject> response = rt.postForEntity("https://api.tosspayments.com/v1/brandpay/authorizations/access-token", entity, JSONObject.class);

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = Objects.requireNonNull(response.getBody()).toString();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(responseBody);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if (jsonNode == null) {
            throw new NullPointerException("jsonNode가 null입니다.");
        }
        log.info(jsonNode.asText());
        log.info("accessToken:{}", jsonNode.get("accessToken").asText());
        return jsonNode.get("accessToken").asText();
    }

    public String getMethods(ReservationRequestDto requestDto) {
        RestTemplate rt = new RestTemplate();
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        //headers.setContentType(MediaType.APPLICATION_JSON);
        log.info("Basic " + Base64.getEncoder().encodeToString(toss_secret.getBytes(StandardCharsets.UTF_8)));
        headers.add("Authorization", "Basic " + Base64.getEncoder().encodeToString(toss_secret.getBytes(StandardCharsets.UTF_8)));

        // HTTP 요청 보내기
        HttpEntity<JSONObject> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = rt.exchange(
                "https://api.tosspayments.com/v1/brandpay/payments/methods/"+requestDto.getMemberId(),
                HttpMethod.GET,
                entity, String.class);

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        log.info(responseBody);

        //String responseBody = Objects.requireNonNull(response.getBody()).toString();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(responseBody);
            log.info(requestDto.getMethodId());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if (jsonNode == null) {
            throw new NullPointerException("jsonNode가 null입니다.");
        } else {
            int index = 0;
            while (jsonNode.get("cards").get(index) != null) {
                log.info(String.valueOf(jsonNode.get("cards").get(index).get("id").toString().replaceAll("\"", "").equals(requestDto.getMethodId())));
                if (jsonNode.get("cards").get(index).get("id").toString().replaceAll("\"", "").equals(requestDto.getMethodId())) {
                    log.info("methodKey:{}",jsonNode.get("cards").get(index).get("methodKey").toString());
                    return jsonNode.get("cards").get(index).get("methodKey").toString().replaceAll("\"","");
                } else index++;
            }
            index = 0;
            while (jsonNode.get("accounts").get(index) != null) {
                if (jsonNode.get("accounts").get(index).get("id").toString().replaceAll("\"","").equals(requestDto.getMethodId())) {
                    log.info("methodKey:{}",jsonNode.get("accounts").get(index).get("methodKey").toString());
                    return jsonNode.get("accounts").get(index).get("methodKey").toString();
                } else index++;
            }
            throw new NullPointerException("methodKey가 null입니다.");
        }
    }

    public void tossReserve(ReservationRequestDto requestDto) {
        log.info("requestDto : {}", requestDto);
        //중복예약 확인
        reservationService.checkDoubleReservation(requestDto);
        //멤버 셋팅
        requestDto.setMemberId(requestDto.getMemberId());
        // companyId 조회
        Space space = spaceRepository.findById(requestDto.getSpaceId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 spaceId는 존재하지 않습니다."));

        Reservation reservation = new Reservation(requestDto.getMemberId(),
                requestDto.getReservationName(), requestDto.getStartDate(), requestDto.getEndDate(),
                "001", "002", requestDto.getPrice(), requestDto.getPrepay(),
                requestDto.getImpUid(), "prepay", "postPay", requestDto.getMileage(),
                requestDto.getSpaceId(), space.getCompanyId(), true, requestDto.getMethodId());

        // 선결제(000) or 보증금결제(001) or 후결제(002)
        switch (requestDto.getPrepay()) {
            //선결제
            case "000":
                //결제내역 확인
                verifySpacePayInfo(requestDto);
                //결제요청
                tossPreReserve(requestDto);
                // 구매 아이디(merchantUid), 선결제는 후결제를 위한 구매 아이디가 필요 없으므로, 000으로 저장.
                reservation.setPrepayUid(requestDto.getPrepayUid());
                reservation.setPostpayUid("000");
                // payStatus: 결제 전 001, 결제 완료 002, 결제 취소 000, 보증금 결제완료 003, 보증금 결제취소 004
                reservation.setPayStatus("002");
                reservation.setPrice(requestDto.getPrice());
                log.info("reservation:{}", reservation);
                // 마일리지 사용 및 적립
                if (requestDto.getMileage() > 0) {
                    mileageService.updateMileage(requestDto);
                    log.info("마일리지 사용 완료");
                }
                // 마일리지 적립
                mileageService.insertMileage(requestDto);
                log.info("마일리지 적립 완료");
                break;
            //보증금결제
            case "001":
                int originalPrice = requestDto.getPrice();
                tossDepositOK(requestDto);
                reservation.setPrice(originalPrice);
                reservation.setPrepayUid(requestDto.getPrepayUid());
                reservation.setPostpayUid(paymentService.getMerchantUid());
                //보증금 결제완료 003
                reservation.setPayStatus("003");
                // 마일리지 사용
                if (requestDto.getMileage() > 0) {
                    mileageService.updateMileage(requestDto);
                    log.info("마일리지 사용 완료");
                }
                break;
            //후결제
            case "002":
                paymentService.verifyReserveDate(requestDto);
                reservation.setPrepayUid(requestDto.getPrepayUid());
                reservation.setPostpayUid(paymentService.getMerchantUid());
                // 결제 전 001
                reservation.setPayStatus("001");
                reservation.setPrice(requestDto.getPrice());
                // 마일리지 사용
                if (requestDto.getMileage() > 0) {
                    mileageService.updateMileage(requestDto);
                    log.info("마일리지 사용 완료");
                }
                break;
        }
        // 저장
        reservationRepository.save(reservation);
    }

    //선결제 및 보증금 결제 요청
    public void tossPreReserve(ReservationRequestDto requestDto) {
        RestTemplate rt = new RestTemplate();
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        log.info("Basic " + Base64.getEncoder().encodeToString(toss_secret.getBytes(StandardCharsets.UTF_8)));
        headers.add("Authorization", "Basic " + Base64.getEncoder().encodeToString(toss_secret.getBytes(StandardCharsets.UTF_8)));
        // Request body 생성
        JSONObject body = new JSONObject();
        body.put("paymentKey", requestDto.getImpUid()); // 결제 고유키
        body.put("amount", requestDto.getPrice()); // 결제될 가격
        body.put("customerKey", requestDto.getMemberId()); // USER 번호
        body.put("orderId", requestDto.getPrepayUid()); // 주문아이디

        // HTTP 요청 보내기
        HttpEntity<JSONObject> entity = new HttpEntity<>(body, headers);
        ResponseEntity<JSONObject> response = rt.postForEntity("https://api.tosspayments.com/v1/brandpay/payments/confirm", entity, JSONObject.class);

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = Objects.requireNonNull(response.getBody()).toString();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(responseBody);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if (jsonNode == null) {
            throw new NullPointerException("jsonNode가 null입니다.");
        }
        log.info(jsonNode.asText());
        log.info("status:{}", jsonNode.get("status").asText());
        log.info("amount:{}", jsonNode.get("card").get("amount").asText());
    }

    //후결제 예약 요청
    public void tossPostReserve(ReservationRequestDto requestDto) {
        requestDto.setPostpayUid(paymentService.getMerchantUid());
        RestTemplate rt = new RestTemplate();
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        log.info("후결제예약 요청 -> Basic " + Base64.getEncoder().encodeToString(toss_secret.getBytes(StandardCharsets.UTF_8)));
        headers.add("Authorization", "Basic " + Base64.getEncoder().encodeToString(toss_secret.getBytes(StandardCharsets.UTF_8)));
        // Request body 생성
        JSONObject body = new JSONObject();
        body.put("methodKey", getMethods(requestDto)); // 결제 수단
        body.put("amount", requestDto.getPrice()); // 결제될 가격
        body.put("customerKey", requestDto.getMemberId()); // USER 번호
        body.put("orderId", requestDto.getPostpayUid()); // 주문아이디 merchant_uid
        body.put("orderName", requestDto.getOrderName()); // 주문내용
        log.info("body:{}",body);

        // HTTP 요청 보내기
        HttpEntity<JSONObject> entity = new HttpEntity<>(body, headers);
        ResponseEntity<JSONObject> response = rt.postForEntity("https://api.tosspayments.com/v1/brandpay/payments", entity, JSONObject.class);

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = Objects.requireNonNull(response.getBody()).toString();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(responseBody);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if (jsonNode == null) {
            throw new NullPointerException("jsonNode가 null입니다.");
        }
        log.info(jsonNode.asText());
        log.info("status:{}", jsonNode.get("status").asText());
        log.info("amount:{}", jsonNode.get("card").get("amount").asText());
        log.info("paymentKey:{}", jsonNode.get("paymentKey").asText());
        requestDto.setImpUid(jsonNode.get("paymentKey").asText());
    }


    // 결제정보와 DB 정보와 일치여부 확인(선결제)
    public void verifySpacePayInfo(ReservationRequestDto vo) {
        // 실제 결제된 가격 확인(인증토큰 발급)
        int price = vo.getPrice();
        log.info("계산요청된 돈: {}", price);
        // 계산되어야할 가격 확인
        Optional<Space> vo2 = spaceRepository.findById(vo.getSpaceId());
        if (vo2.isPresent()) {
            int price2 = vo2.get().getPrice() * paymentService.getReserveTime(vo.getStartDate(), vo.getEndDate()) - vo.getMileage();
            log.info("계산되어야할 돈: {}", price2);
            // 가격 비교
            if (price != price2) {
                // 일치하지 않을 시, 에러반환
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "결제요청금액이 올바르지 않습니다.");
            }
        }
    }

    // 결제정보와 DB 정보와 일치여부 확인(보증금 결제)
    public void tossDepositOK(ReservationRequestDto vo) {

        // 계산되어야 할 값과 실제 계산된 값이 맞는지 확인.
        int price = (int) ((vo.getPrice() + vo.getMileage()) * 0.2);
        log.info("실제 보증금 계산 요청된 돈: {}", price);
        Optional<Space> vo2 = spaceRepository.findById(vo.getSpaceId());
        if (vo2.isPresent()) {
            int price2 = vo2.get().getPrice() * paymentService.getReserveTime(vo.getStartDate(), vo.getEndDate());
            int depositPrice = (int) (price2 * 0.2);
            log.info("price2:{}", price2);
            log.info("계산되어야할 depositPrice: {}", depositPrice);
            if (price == depositPrice) {
                vo.setPrice(depositPrice);
                log.info("보증금 결제될 가격:{}", vo.getPrice());
                tossPreReserve(vo);
                vo.setPrice(price2 - depositPrice - vo.getMileage());
                log.info("예약될 가격:{}", vo.getPrice());
                //tossPostReserve(vo);
            } else {
                // 일치하지 않을 시, 에러반환
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "결제요청금액이 올바르지 않습니다.");
            }
        }
    }

    public static void tossRefund(RefundDto refundDto) {
        RestTemplate rt = new RestTemplate();
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        log.info("toss refund : Basic " + Base64.getEncoder().encodeToString(toss_secret.getBytes(StandardCharsets.UTF_8)));
        headers.add("Authorization", "Basic " + Base64.getEncoder().encodeToString(toss_secret.getBytes(StandardCharsets.UTF_8)));
        headers.add("Idempotency-Key", String.valueOf(UUID.randomUUID()));
        // Request body 생성
        JSONObject body = new JSONObject();
        body.put("cancelReason", refundDto.getReason()); // 취소 사유

        // HTTP 요청 보내기
        String url = "https://api.tosspayments.com/v1/payments/"+refundDto.getMerchant_uid()+"/cancel";
        log.info(url);
        HttpEntity<JSONObject> entity = new HttpEntity<>(body, headers);
        ResponseEntity<JSONObject> response = rt.postForEntity(url, entity, JSONObject.class);

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = Objects.requireNonNull(response.getBody()).toString();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(responseBody);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if (jsonNode == null) {
            throw new NullPointerException("jsonNode가 null입니다.");
        }
        log.info(jsonNode.asText());
        log.info("status:{}", jsonNode.get("status").asText());
        log.info("amount:{}", jsonNode.get("card").get("amount").asText());
    }
}